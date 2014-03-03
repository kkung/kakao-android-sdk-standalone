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
package com.kakao.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SharedPreferencesCache {
    private static final String TAG = SharedPreferencesCache.class.getSimpleName();

    private static final String JSON_VALUE_TYPE = "valueType";
    private static final String JSON_VALUE = "value";
    private static final String JSON_VALUE_ENUM_TYPE = "enumType";

    private static final String TYPE_BOOLEAN = "bool";
    private static final String TYPE_BOOLEAN_ARRAY = "bool[]";
    private static final String TYPE_BYTE = "byte";
    private static final String TYPE_BYTE_ARRAY = "byte[]";
    private static final String TYPE_SHORT = "short";
    private static final String TYPE_SHORT_ARRAY = "short[]";
    private static final String TYPE_INTEGER = "int";
    private static final String TYPE_INTEGER_ARRAY = "int[]";
    private static final String TYPE_LONG = "long";
    private static final String TYPE_LONG_ARRAY = "long[]";
    private static final String TYPE_FLOAT = "float";
    private static final String TYPE_FLOAT_ARRAY = "float[]";
    private static final String TYPE_DOUBLE = "double";
    private static final String TYPE_DOUBLE_ARRAY = "double[]";
    private static final String TYPE_CHAR = "char";
    private static final String TYPE_CHAR_ARRAY = "char[]";
    private static final String TYPE_STRING = "string";
    private static final String TYPE_STRING_LIST = "stringList";
    private static final String TYPE_ENUM = "enum";
    private static final long INVALID_BUNDLE_MILLISECONDS = Long.MIN_VALUE;

    private final String cacheName;
    private final SharedPreferences cache;

    public SharedPreferencesCache(Context context, String cacheName) {
        Utility.notNull(context, "context");
        Utility.notNull(cacheName, "cacheName");
        this.cacheName = cacheName;

        Context applicationContext = context.getApplicationContext();
        context = applicationContext != null ? applicationContext : context;
        this.cache = context.getSharedPreferences(
                this.cacheName,
                Context.MODE_PRIVATE);
    }

    public Bundle load()  {
        Bundle settings = new Bundle();

        Map<String, ?> allCachedEntries = cache.getAll();

        for (String key : allCachedEntries.keySet()) {
            try {
                deserializeKey(key, settings);
            } catch (JSONException e) {
                Logger.getInstance().w(TAG, "Error reading cached value for key: '" + key + "' -- " + e);
                return null;
            }
        }

        return settings;
    }

    public void save(Bundle bundle) {
        Utility.notNull(bundle, "bundle");

        SharedPreferences.Editor editor = cache.edit();

        for (String key : bundle.keySet()) {
            try {
                serializeKey(key, bundle, editor);
            } catch (JSONException e) {
                // Error in the bundle. Don't store a partial cache.
                Logger.getInstance().w(TAG, "Error processing value for key: '" + key + "' -- " + e);
                // Bypass the commit and just return. This cancels the entire edit transaction
                return;
            }
        }

        boolean successfulCommit = editor.commit();
        if (!successfulCommit) {
            Logger.getInstance().w(TAG, "SharedPreferences.Editor.commit() was not successful");
        }
    }

    public void clearAll() {
        cache.edit().clear().commit();
    }

    public void clear(final List<String> keysToClear) {
        SharedPreferences.Editor cacheEditor = cache.edit();
        for(String key : keysToClear){
            cacheEditor.remove(key);
        }
        cacheEditor.commit();
    }

    public static Date getDate(Bundle bundle, String key) {
        if (bundle == null) {
            return null;
        }

        long n = bundle.getLong(key, INVALID_BUNDLE_MILLISECONDS);
        if (n == INVALID_BUNDLE_MILLISECONDS) {
            return null;
        }

        return new Date(n);
    }

    public static void putDate(Bundle bundle, String key, Date date) {
        bundle.putLong(key, date.getTime());
    }

    private void serializeKey(String key, Bundle bundle, SharedPreferences.Editor editor)
        throws JSONException {
        Object value = bundle.get(key);
        if (value == null) {
            // Cannot serialize null values.
            return;
        }

        String supportedType = null;
        JSONArray jsonArray = null;
        JSONObject json = new JSONObject();

        if (value instanceof Byte) {
            supportedType = TYPE_BYTE;
            json.put(JSON_VALUE, ((Byte)value).intValue());
        } else if (value instanceof Short) {
            supportedType = TYPE_SHORT;
            json.put(JSON_VALUE, ((Short)value).intValue());
        } else if (value instanceof Integer) {
            supportedType = TYPE_INTEGER;
            json.put(JSON_VALUE, ((Integer)value).intValue());
        } else if (value instanceof Long) {
            supportedType = TYPE_LONG;
            json.put(JSON_VALUE, ((Long)value).longValue());
        } else if (value instanceof Float) {
            supportedType = TYPE_FLOAT;
            json.put(JSON_VALUE, ((Float)value).doubleValue());
        } else if (value instanceof Double) {
            supportedType = TYPE_DOUBLE;
            json.put(JSON_VALUE, ((Double)value).doubleValue());
        } else if (value instanceof Boolean) {
            supportedType = TYPE_BOOLEAN;
            json.put(JSON_VALUE, ((Boolean)value).booleanValue());
        } else if (value instanceof Character) {
            supportedType = TYPE_CHAR;
            json.put(JSON_VALUE, value.toString());
        } else if (value instanceof String) {
            supportedType = TYPE_STRING;
            json.put(JSON_VALUE, (String)value);
        } else if (value instanceof Enum<?>) {
            supportedType = TYPE_ENUM;
            json.put(JSON_VALUE, value.toString());
            json.put(JSON_VALUE_ENUM_TYPE, value.getClass().getName());
        } else {
            // Optimistically create a JSONArray. If not an array type, we can null
            // it out later
            jsonArray = new JSONArray();
            if (value instanceof byte[]) {
                supportedType = TYPE_BYTE_ARRAY;
                for (byte v : (byte[])value) {
                    jsonArray.put((int)v);
                }
            } else if (value instanceof short[]) {
                supportedType = TYPE_SHORT_ARRAY;
                for (short v : (short[])value) {
                    jsonArray.put((int)v);
                }
            } else if (value instanceof int[]) {
                supportedType = TYPE_INTEGER_ARRAY;
                for (int v : (int[])value) {
                    jsonArray.put(v);
                }
            } else if (value instanceof long[]) {
                supportedType = TYPE_LONG_ARRAY;
                for (long v : (long[])value) {
                    jsonArray.put(v);
                }
            } else if (value instanceof float[]) {
                supportedType = TYPE_FLOAT_ARRAY;
                for (float v : (float[])value) {
                    jsonArray.put((double)v);
                }
            } else if (value instanceof double[]) {
                supportedType = TYPE_DOUBLE_ARRAY;
                for (double v : (double[])value) {
                    jsonArray.put(v);
                }
            } else if (value instanceof boolean[]) {
                supportedType = TYPE_BOOLEAN_ARRAY;
                for (boolean v : (boolean[])value) {
                    jsonArray.put(v);
                }
            } else if (value instanceof char[]) {
                supportedType = TYPE_CHAR_ARRAY;
                for (char v : (char[])value) {
                    jsonArray.put(String.valueOf(v));
                }
            } else if (value instanceof List<?>) {
                supportedType = TYPE_STRING_LIST;
                @SuppressWarnings("unchecked")
                List<String> stringList = (List<String>)value;
                for (String v : stringList) {
                    jsonArray.put((v == null) ? JSONObject.NULL : v);
                }
            } else {
                // Unsupported type. Clear out the array as a precaution even though
                // it is redundant with the null supportedType.
                jsonArray = null;
            }
        }

        if (supportedType != null) {
            json.put(JSON_VALUE_TYPE, supportedType);
            if (jsonArray != null) {
                // If we have an array, it has already been converted to JSON. So use
                // that instead.
                json.putOpt(JSON_VALUE, jsonArray);
            }

            String jsonString = json.toString();
            editor.putString(key, jsonString);
        }
    }

    private void deserializeKey(String key, Bundle bundle)
            throws JSONException {
        String jsonString = cache.getString(key, "{}");
        JSONObject json = new JSONObject(jsonString);

        String valueType = json.getString(JSON_VALUE_TYPE);

        if (valueType.equals(TYPE_BOOLEAN)) {
            bundle.putBoolean(key, json.getBoolean(JSON_VALUE));
        } else if (valueType.equals(TYPE_BOOLEAN_ARRAY)) {
            JSONArray jsonArray = json.getJSONArray(JSON_VALUE);
            boolean[] array = new boolean[jsonArray.length()];
            for (int i = 0; i < array.length; i++) {
                array[i] = jsonArray.getBoolean(i);
            }
            bundle.putBooleanArray(key, array);
        } else if (valueType.equals(TYPE_BYTE)) {
            bundle.putByte(key, (byte)json.getInt(JSON_VALUE));
        } else if (valueType.equals(TYPE_BYTE_ARRAY)) {
            JSONArray jsonArray = json.getJSONArray(JSON_VALUE);
            byte[] array = new byte[jsonArray.length()];
            for (int i = 0; i < array.length; i++) {
                array[i] = (byte)jsonArray.getInt(i);
            }
            bundle.putByteArray(key, array);
        } else if (valueType.equals(TYPE_SHORT)) {
            bundle.putShort(key, (short)json.getInt(JSON_VALUE));
        } else if (valueType.equals(TYPE_SHORT_ARRAY)) {
            JSONArray jsonArray = json.getJSONArray(JSON_VALUE);
            short[] array = new short[jsonArray.length()];
            for (int i = 0; i < array.length; i++) {
                array[i] = (short)jsonArray.getInt(i);
            }
            bundle.putShortArray(key, array);
        } else if (valueType.equals(TYPE_INTEGER)) {
            bundle.putInt(key, json.getInt(JSON_VALUE));
        } else if (valueType.equals(TYPE_INTEGER_ARRAY)) {
            JSONArray jsonArray = json.getJSONArray(JSON_VALUE);
            int[] array = new int[jsonArray.length()];
            for (int i = 0; i < array.length; i++) {
                array[i] = jsonArray.getInt(i);
            }
            bundle.putIntArray(key, array);
        } else if (valueType.equals(TYPE_LONG)) {
            bundle.putLong(key, json.getLong(JSON_VALUE));
        } else if (valueType.equals(TYPE_LONG_ARRAY)) {
            JSONArray jsonArray = json.getJSONArray(JSON_VALUE);
            long[] array = new long[jsonArray.length()];
            for (int i = 0; i < array.length; i++) {
                array[i] = jsonArray.getLong(i);
            }
            bundle.putLongArray(key, array);
        } else if (valueType.equals(TYPE_FLOAT)) {
            bundle.putFloat(key, (float)json.getDouble(JSON_VALUE));
        } else if (valueType.equals(TYPE_FLOAT_ARRAY)) {
            JSONArray jsonArray = json.getJSONArray(JSON_VALUE);
            float[] array = new float[jsonArray.length()];
            for (int i = 0; i < array.length; i++) {
                array[i] = (float)jsonArray.getDouble(i);
            }
            bundle.putFloatArray(key, array);
        } else if (valueType.equals(TYPE_DOUBLE)) {
            bundle.putDouble(key, json.getDouble(JSON_VALUE));
        } else if (valueType.equals(TYPE_DOUBLE_ARRAY)) {
            JSONArray jsonArray = json.getJSONArray(JSON_VALUE);
            double[] array = new double[jsonArray.length()];
            for (int i = 0; i < array.length; i++) {
                array[i] = jsonArray.getDouble(i);
            }
            bundle.putDoubleArray(key, array);
        } else if (valueType.equals(TYPE_CHAR)) {
            String charString = json.getString(JSON_VALUE);
            if (charString != null && charString.length() == 1) {
                bundle.putChar(key, charString.charAt(0));
            }
        } else if (valueType.equals(TYPE_CHAR_ARRAY)) {
            JSONArray jsonArray = json.getJSONArray(JSON_VALUE);
            char[] array = new char[jsonArray.length()];
            for (int i = 0; i < array.length; i++) {
                String charString = jsonArray.getString(i);
                if (charString != null && charString.length() == 1) {
                    array[i] = charString.charAt(0);
                }
            }
            bundle.putCharArray(key, array);
        } else if (valueType.equals(TYPE_STRING)) {
            bundle.putString(key, json.getString(JSON_VALUE));
        } else if (valueType.equals(TYPE_STRING_LIST)) {
            JSONArray jsonArray = json.getJSONArray(JSON_VALUE);
            int numStrings = jsonArray.length();
            ArrayList<String> stringList = new ArrayList<String>(numStrings);
            for (int i = 0; i < numStrings; i++) {
                Object jsonStringValue = jsonArray.get(i);
                stringList.add(i, jsonStringValue == JSONObject.NULL ? null : (String)jsonStringValue);
            }
            bundle.putStringArrayList(key, stringList);
        } else if (valueType.equals(TYPE_ENUM)) {
            try {
                String enumType = json.getString(JSON_VALUE_ENUM_TYPE);
                @SuppressWarnings({ "unchecked", "rawtypes" })
                Class<? extends Enum> enumClass = (Class<? extends Enum>) Class.forName(enumType);
                @SuppressWarnings("unchecked")
                Enum<?> enumValue = Enum.valueOf(enumClass, json.getString(JSON_VALUE));
                bundle.putSerializable(key, enumValue);
            } catch (ClassNotFoundException e) {
                Logger.getInstance().w(TAG, "Error deserializing key '" + key + "' -- " + e);
            } catch (IllegalArgumentException e) {
                Logger.getInstance().w(TAG, "Error deserializing key '" + key + "' -- " + e);
            }
        }
    }
}
