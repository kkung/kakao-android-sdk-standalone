/**
 * Copyright 2014 Minyoung Jeong <kkungkkung@gmail.com>
 * Copyright 2014 Kakao Corp.
 *
 * Redistribution and modification in source or binary forms are not permitted without specific prior written permission. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kakao.sample.kakaostory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.kakao.APIErrorResult;
import com.kakao.KakaoStoryHttpResponseHandler;
import com.kakao.KakaoStoryPostParamBuilder;
import com.kakao.KakaoStoryPostParamBuilder.PERMISSION;
import com.kakao.KakaoStoryProfile;
import com.kakao.KakaoStoryProfile.BirthdayType;
import com.kakao.KakaoStoryService;
import com.kakao.KakaoStoryUpload;
import com.kakao.LogoutResponseCallback;
import com.kakao.UserManagement;
import com.kakao.UserProfile;
import com.kakao.helper.Logger;
import com.kakao.sample.kakaostory.R.drawable;
import com.kakao.sample.kakaostory.R.id;
import com.kakao.widget.ProfileLayout;

/**
 * 카카오스토리 API인 프로필, 포스팅(이미지 업로드)를 테스트 한다.
 * 유효한 세션이 있다는 검증을 {@link KakaoStoryLoginActivity}로 부터 받고 보여지는 로그인 된 페이지이다.
 */
public class KakaoStoryMainActivity extends Activity {
    private final String storyPostText = "This Cafe is really awesome!";
    private final String execParam = "place=1111";
    private ImageView background;
    private ProfileLayout profileLayout;
    private TextView birthdayView;
    private String imageURL;
    private UserProfile userProfile;

    /**
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeView();
    }

    protected void onResume() {
        super.onResume();
        // 로그인 하면서 caching되어 있는 profile를 그린다.
        userProfile = UserProfile.loadFromCache();
        if (userProfile != null) {
            profileLayout.setUserProfile(userProfile);
        }
    }

    private void redirectLoginActivity() {
        final Intent intent = new Intent(this, KakaoStoryLoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void onClickProfile() {
        KakaoStoryService.requestProfile(new MyKakaoStoryHttpResponseHandler<KakaoStoryProfile>() {
            @Override
            protected void onHttpSuccess(final KakaoStoryProfile storyProfile) {
                Toast.makeText(getApplicationContext(), "success to get story profile", Toast.LENGTH_SHORT).show();
                applyStoryProfileToView(storyProfile);
            }
        });
    }

    private void onClickUpload() {
        try {
            // TODO 갤러리나 카메라 촬영 후 image File을 올리도록
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawable.post_image);
            File file = new File(writeStoryImage(getApplicationContext(), bitmap));

            KakaoStoryService.requestUpload(new MyKakaoStoryHttpResponseHandler<KakaoStoryUpload>() {
                @Override
                protected void onHttpSuccess(final KakaoStoryUpload storyProfile) {
                    imageURL = storyProfile.getUrl();
                    Toast.makeText(getApplicationContext(), "success to upload image", Toast.LENGTH_SHORT).show();
                }

            }, file);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void onClickPost() {
        final KakaoStoryPostParamBuilder postParamBuilder = new KakaoStoryPostParamBuilder(storyPostText, PERMISSION.PUBLIC);
        postParamBuilder.setAndroidExecuteParam(execParam);
        postParamBuilder.setIOSExecuteParam(execParam);
        if (imageURL != null)
            postParamBuilder.setImageURL(imageURL);
        Bundle parameters = postParamBuilder.build();

        KakaoStoryService.requestPost(new MyKakaoStoryHttpResponseHandler<Void>() {
            @Override
            protected void onHttpSuccess(Void resultObj) {
                Toast.makeText(getApplicationContext(), "success to post on KakaoStory", Toast.LENGTH_SHORT).show();
            }
        }, parameters);
    }

    private void onClickLogout() {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            protected void onSuccess(final long userId) {
                redirectLoginActivity();
            }

            @Override
            protected void onFailure(final APIErrorResult apiErrorResult) {
                redirectLoginActivity();
            }
        });
    }

    private void initializeView() {
        setContentView(R.layout.main);
        initializeButtons();
        initializeProfileView();
    }

    private void initializeButtons() {
        final Button profileButton = (Button) findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickProfile();
            }
        });

        // 스토리 포스팅을 위해 이미지를 upload한다.
        Button uploadButton = (Button) findViewById(id.upload_button);
        uploadButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUpload();
            }
        });

        // image upload를 했다면 이미지와 함께 포스팅하고 image upload를 하지 않았으면 text만 upload한다.
        Button postButton = (Button) findViewById(id.post_button);
        postButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPost();
            }
        });

        Button logoutButton = (Button) findViewById(id.logout_button);
        logoutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLogout();
            }
        });
    }

    private void initializeProfileView() {
        profileLayout = (ProfileLayout) findViewById(R.id.com_kakao_user_profile);
        // background image
        background = (ImageView) findViewById(id.background);
        background.setImageResource(R.drawable.default_background);

        // extra story profile
        birthdayView = (TextView) findViewById(id.birthday);
    }

    // profile view에서 story profile을 update 한다.
    private void applyStoryProfileToView(final KakaoStoryProfile storyProfile) {
        if (profileLayout != null) {
            if (userProfile != null)
                profileLayout.setUserProfile(userProfile);

            final String nickName = storyProfile.getNickName();
            if (nickName != null)
                profileLayout.setNickname(nickName);

            final String profileImageURL = storyProfile.getProfileImageURL();
            if (profileImageURL != null)
                profileLayout.setProfileURL(profileImageURL);
        }

        final String backgroundURL = storyProfile.getBgImageURL();
        if (background != null && backgroundURL != null ) {
            new DownloadImageTask(background).execute(backgroundURL);
        }

        final Calendar birthday = storyProfile.getBirthdayCalendar();
        final BirthdayType birthDayType = storyProfile.getBirthdayType();
        if (birthdayView != null && birthday != null) {
            StringBuilder displayBirthday = new StringBuilder(8);
            displayBirthday.append(birthday.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)).append(" ").append(birthday.get(Calendar.DAY_OF_MONTH));
            if (birthDayType != null)
                displayBirthday.append(" (").append(birthDayType.getDisplaySymbol()).append(")");
            birthdayView.setText(displayBirthday.toString());
        }
    }

    private static String writeStoryImage(final Context context, final Bitmap bitmap) throws IOException {
        final File diskCacheDir = new File(context.getCacheDir(), "story");

        if (!diskCacheDir.exists())
            diskCacheDir.mkdirs();

        final String file = diskCacheDir.getAbsolutePath() + File.separator + "temp_" + System.currentTimeMillis() + ".jpg";

        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file), 8 * 1024);
            bitmap.compress(CompressFormat.JPEG, 100, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }

        return file;
    }

    private abstract class MyKakaoStoryHttpResponseHandler<T> extends KakaoStoryHttpResponseHandler<T> {

        @Override
        protected void onHttpSessionClosedFailure(final APIErrorResult errorResult) {
            redirectLoginActivity();
        }

        @Override
        protected void onNotKakaoStoryUser() {
            Toast.makeText(getApplicationContext(), "not KakaoStory user", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onFailure(final APIErrorResult errorResult) {
            final String message = "MyKakaoStoryHttpResponseHandler : failure : " + errorResult;
            Logger.getInstance().d(message);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}