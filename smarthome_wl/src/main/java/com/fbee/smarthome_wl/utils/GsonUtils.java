package com.fbee.smarthome_wl.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by wl on 2017/3/23.
 */

public class GsonUtils {
    public static <T> ArrayList<T> jsonToArrayList(String json, Class<T> clazz)
    {
        Type type = new TypeToken<ArrayList<JsonObject>>()
        {}.getType();
        ArrayList<JsonObject> jsonObjects = new Gson().fromJson(json, type);

        ArrayList<T> arrayList = new ArrayList<>();
        for (JsonObject jsonObject : jsonObjects)
        {
            arrayList.add(new Gson().fromJson(jsonObject, clazz));
        }
        return arrayList;
    }


    public class StringConverter implements JsonSerializer<String>,
            JsonDeserializer<String> {
        public JsonElement serialize(String src, Type typeOfSrc,
                                     JsonSerializationContext context) {
            if (src == null) {
                return new JsonPrimitive("");
            } else {
                return new JsonPrimitive(src.toString());
            }
        }

        public String deserialize(JsonElement json, Type typeOfT,
                                  JsonDeserializationContext context)
                throws JsonParseException {
            return json.getAsJsonPrimitive().getAsString();
        }
    }

    /**
     * 转成json字符串
     *
     * @param object
     * @return
     */
    public static String GsonString(Object object) {
        String gsonString = null;
        GsonBuilder gb = new GsonBuilder();
        Gson gson = gb.create();
        if (gson != null) {
            gsonString = gson.toJson(object);
        }
        return gsonString;
    }
    public Gson getGson(){
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(String.class, new StringConverter());
        Gson gson = gb.create();
        return gson;
    }



}
