/**
 * Copyright 2014 Minyoung Jeong <kkungkkung@gmail.com>
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
package com.kakao.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author kkung
 */
public class JsonHelper {

    public static Map<String, Object> MapFromJson(JSONObject obj) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator it = obj.keys();
        while (it.hasNext()) {
            String key = (String)it.next();
            map.put(key, fromJson(obj.get(key)));
        }

        return map;
    }

    public static List ListFromJson(JSONArray obj) throws JSONException {
        List list = new ArrayList();
        for (int i = 0; i < obj.length(); i++) {
            list.add(fromJson(obj.get(i)));
        }
        return list;
    }

    private static Object fromJson(Object obj) throws JSONException {
        if (obj == JSONObject.NULL) {
            return null;
        } else if (obj instanceof JSONObject) {
            return MapFromJson((JSONObject) obj);
        } else if (obj instanceof JSONArray) {
            return ListFromJson((JSONArray) obj);
        } else {
            return obj;
        }
    }

}
