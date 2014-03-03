/**
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
package com.kakao.sample.kakaolink;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import com.kakao.AppActionBuilder;
import com.kakao.KakaoLink;
import com.kakao.KakaoLinkParseException;
import com.kakao.KakaoTalkLinkMessageBuilder;
import com.kakao.sample.kakaolink.R.string;

/**
 * 텍스트, 이미지, 링크, 버튼 타입으로 메시지를 구성하여 카카오톡으로 전송한다.
 */
public class KakaoLinkMainActivity extends Activity {
    private KakaoLink kakaoLink;
    private Spinner text, link, image, button;
    private KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder;
    private final String imageSrc = "http://dn.api1.kage.kakao.co.kr/14/dn/btqaWmFftyx/tBbQPH764Maw2R6IBhXd6K/o.jpg";
    private final String weblink = "http://www.kakao.com/services/8";

    /**
     * 메시지를 구성할 텍스트, 이미지, 링크, 버튼을 위한 spinner를 구성한다.
     * 메시지 전송 버튼과 메시지 다시 구성하기 버튼을 만든다.
     * @param savedInstanceState activity 내려가지 전에 저장한 객체
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        try {
            kakaoLink = KakaoLink.getKakaoLink(this);
            kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

            text = (Spinner) findViewById(R.id.text);
            image = (Spinner) findViewById(R.id.image);
            link = (Spinner) findViewById(R.id.link);
            button = (Spinner) findViewById(R.id.button);

            addListenerOnSendButton();
            addListenerOnClearButton();
        } catch (KakaoLinkParseException e) {
            alert(e.getMessage());
        }
    }

    // get the selected dropdown list value
    void addListenerOnSendButton() {
        Button sendButton = (Button) findViewById(R.id.send);

        sendButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String textType = String.valueOf(text.getSelectedItem());
                final String linkType = String.valueOf(link.getSelectedItem());
                final String imageType = String.valueOf(image.getSelectedItem());
                final String buttonType = String.valueOf(button.getSelectedItem());

                final String message = "Text : " + textType +
                    "\nLink : " + linkType +
                    "\nImage : " + imageType +
                    "\nButton : " + buttonType;

                new AlertDialog.Builder(KakaoLinkMainActivity.this)
                    .setTitle(R.string.send_message)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendKakaoTalkLink(textType, linkType, imageType, buttonType);
                            kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
            }

        });
    }

    private void addListenerOnClearButton() {
        Button clearButton = (Button) findViewById(R.id.clear);
        clearButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(KakaoLinkMainActivity.this)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.reset_message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
            }
        });
    }

    private void sendKakaoTalkLink(String textType, String linkType, String imageType, String buttonType) {
        try {
            if (textType.equals(getString(string.use_text)))
                kakaoTalkLinkMessageBuilder.addText(getString(string.kakaolink_text));

            if (imageType.equals(getString(string.use_image)))
                kakaoTalkLinkMessageBuilder.addImage(imageSrc, 300, 200);

            if (linkType.equals(getString(string.use_applink))){
                kakaoTalkLinkMessageBuilder.addAppLink(getString(string.kakaolink_applink),
                    new AppActionBuilder()
                    .setAndroidExecuteURLParam("target=main")
                    .setIOSExecuteURLParam("target=main", AppActionBuilder.DEVICE_TYPE.PHONE).build());
            }
            else if (linkType.equals(getString(string.use_weblink)))
                kakaoTalkLinkMessageBuilder.addWebLink(getString(string.kakaolink_weblink), weblink);   //웹싸이트에 등록한 "http://www.kakao.com"을 overwrite함. overwrite는 같은 도메인만 가능.

            if (buttonType.equals(getString(string.use_appbutton)))
                kakaoTalkLinkMessageBuilder.addAppButton(getString(string.kakaolink_appbutton));
            else if (buttonType.equals(getString(string.use_webbutton)))
                kakaoTalkLinkMessageBuilder.addWebButton(getString(string.kakaolink_webbutton), null); // 웹싸이트에 등록한 "http://www.kakao.com"으로 이동.

            kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder.build(), this);
        } catch (KakaoLinkParseException e) {
            alert(e.getMessage());
        }
    }

    private void alert(String message) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .create().show();
    }

}
