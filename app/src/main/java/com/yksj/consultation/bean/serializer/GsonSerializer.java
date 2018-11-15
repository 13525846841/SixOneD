/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yksj.consultation.bean.serializer;

import com.google.gson.Gson;
import com.yksj.consultation.bean.ResponseBean;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Json GsonSerializer/Deserializer.
 */
public class GsonSerializer {

    public static final Gson GSON = new Gson();

    public static String serialize(Object object) {
        return GSON.toJson(object);
    }

    public static String serialize(Object object, Class clazz) {
        return GSON.toJson(object, clazz);
    }

    public static <T> T deserialize(String string, Class<T> clazz) {
        return GSON.fromJson(string, clazz);
    }

    public static <T> T deserialize(String string, Type type) {
        return GSON.fromJson(string, type);
    }

    public static <T> ResponseBean<List<T>> fromJsonArrar(String json, Class<T> clazz){
        try {
            ParameterizedTypeImpl listType = new ParameterizedTypeImpl(List.class, new Type[]{clazz});
            ParameterizedTypeImpl type = new ParameterizedTypeImpl(ResponseBean.class, new Type[]{listType});
            return deserialize(json, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> ResponseBean<T> fromJsonObject(String json, Class<T> clazz){
        try {
            ParameterizedTypeImpl type = new ParameterizedTypeImpl(ResponseBean.class, new Type[]{clazz});
            return deserialize(json, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
