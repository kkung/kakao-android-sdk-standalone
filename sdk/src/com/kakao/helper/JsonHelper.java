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
