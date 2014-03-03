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
package com.kakao.sample.usermgmt;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;

/**
 * 추가로 받고자 하는 사용자 정보를 나타내는 layout
 * 이름, 나이, 성별을 입력할 수 있다.
 * @author MJ
 */
public class ExtraUserPropertyLayout extends FrameLayout {
    // property key
    private  static final String NAME_KEY = "name";
    private  static final String AGE_KEY = "age";
    private  static final String GENDER_KEY = "gender";

    private EditText name;
    private EditText age;
    private Spinner gender;

    public ExtraUserPropertyLayout(Context context) {
        super(context);
    }

    public ExtraUserPropertyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtraUserPropertyLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow () {
        super.onAttachedToWindow();
        final View view = inflate(getContext(), R.layout.extra_user_property, this);
        name = (EditText) view.findViewById(R.id.name);
        age = (EditText) view.findViewById(R.id.age);
        gender = (Spinner) view.findViewById(R.id.gender);
    }

    Map<String, String> getProperties(){
        final String nickNameValue = name.getText().toString();
        final String ageValue = age.getText().toString();
        final String genderValue = String.valueOf(gender.getSelectedItem());

        Map<String, String> properties = new HashMap<String, String>();
        if(nickNameValue != null)
            properties.put(NAME_KEY, nickNameValue);
        if(ageValue != null)
            properties.put(AGE_KEY, ageValue);
        if(genderValue != null)
            properties.put(GENDER_KEY, genderValue);

        return properties;
    }

    void showProperties(final Map<String, String> properties) {
        final String nameValue = properties.get(NAME_KEY);
        if (nameValue != null)
            name.setText(nameValue);

        final String ageValue = properties.get(AGE_KEY);
        if (ageValue != null)
            age.setText(ageValue);

        final String genderValue = properties.get(GENDER_KEY);
        if (genderValue != null) {
            ArrayAdapter<String> myAdap = (ArrayAdapter<String>) gender.getAdapter(); //cast to an ArrayAdapter
            int spinnerPosition = myAdap.getPosition(genderValue);
            gender.setSelection(spinnerPosition);
        }
    }


}
