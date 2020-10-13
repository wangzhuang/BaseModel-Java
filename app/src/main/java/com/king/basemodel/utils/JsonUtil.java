package com.king.basemodel.utils;

import android.annotation.SuppressLint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.king.basemodel.common.GlobalKt.log;


/**
 * Gson类库的封装工具类，专门负责解析json数据</br> 内部实现了Gson对象的单例
 */
public class JsonUtil {
    private static final String TAG = "JsonUtil";
    private static Gson gson = null;

    // 静态代码块
    static {
        if (gson == null) {
            gson = new Gson();
        }
    }

    private JsonUtil() {

    }

    /**
     * 将对象转换成json格式
     *
     * @param ts
     * @return
     */
    public static String objectToJson(Object ts) {
        String jsonStr = null;
        if (gson != null) {
            jsonStr = gson.toJson(ts);
        }
        return jsonStr;
    }

    /**
     * 将对象转换成json格式(并自定义日期格式)
     *
     * @param ts
     * @return
     */
    public static String objectToJsonDateSerializer(Object ts,
                                                    final String dateformat) {
        String jsonStr = null;
        gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Date.class,
                        new JsonSerializer<Date>() {
                            @Override
                            @SuppressLint("SimpleDateFormat")
                            public JsonElement serialize(Date src,
                                                         Type typeOfSrc,
                                                         JsonSerializationContext context) {
                                SimpleDateFormat format = new SimpleDateFormat(
                                        dateformat);
                                return new JsonPrimitive(format.format(src));
                            }
                        }).setDateFormat(dateformat).create();
        if (gson != null) {
            jsonStr = gson.toJson(ts);
        }
        return jsonStr;
    }

    /**
     * @param json
     * @param clazz
     * @return
     * @Description:把jsonArray转成list [{}]
     * @modified by    @time
     * @author ZMY
     */
    public static <T> List<T> json2List(String json, Class<T> clazz) {
        List<T> list;
        try {
            list = new ArrayList<T>();
            //json != null 避免 json.length 报 nullpointer 错误
            // at java.io.StringReader.<init>(StringReader.java:50)
            //        at com.google.gson.JsonParser.parse(JsonParser.java:45)
            //        at com.qdzr.moyou.utils.JsonUtil.json2List(JsonUtil.java:107)
            if (gson != null && json != null) {
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonElement = jsonParser.parse(json); // 将json字符串转换成JsonElement
                JsonArray jsonArray = jsonElement.getAsJsonArray(); // 将JsonElement转换成JsonArray

                Iterator<JsonElement> it = jsonArray.iterator(); // Iterator处理
                while (it.hasNext()) { // 循环
                    list.add(gson.fromJson(it.next().toString(), clazz)); // 将json字符串转换为对象加入List
                }
            }
            return list;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            log(TAG, "解析json失败");
            return null;
        }
    }

    /**
     * @param response json字符串
     * @param clazz    model类型
     * @return 封装的model List
     * @Description:获取列表{ "Data": [ {"xx","xx"},{"xx","xx"}]}
     * @modified by @time
     * @author ZMY
     * 泛型的好处就是在编译期就能检测出错误
     * 前面的<T>可以理解为泛型的声明意为你指定的一种类型
     * <T>为某一确定的类型
     */
    public static <T> List<T> getJsonList(String response, Class<T> clazz, String Str) {
        try {
            JSONObject jsonObject = new JSONObject(response);//先转化为jsonObject再获取为jsonArray
            JSONArray jsonArray = jsonObject.getJSONArray(Str);

            // 2,将json数据转成arrayList;3,加载到列表ListView
            return json2List(jsonArray.toString(), clazz);//通过jsons字符串进行转换的
        } catch (JSONException e) {
            e.printStackTrace();
            log(TAG, "解析json失败");
            return null;
        }
    }

    /**
     * "xxx":[{}]
     *
     * @param object
     * @param clazz
     * @param tag
     * @return
     */
    public static <T> List<T> getJsonList(JSONObject object, Class<T> clazz,
                                          String tag) {
        try {
            JSONArray jsonArray = object.getJSONArray(tag);
            // 2,将json数据转成arrayList;3,加载到广告列表ListView
            return json2List(jsonArray.toString(), clazz);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * {"Data":{}}
     *
     * @param response
     * @param clazz
     * @return、 <T>类型声明
     */

    public static <T> Object getJsonObject(String response, Class<T> clazz, String str) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject object = jsonObject.getJSONObject(str);
            return json2Bean(object.toString(), clazz);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * {
     * "Data": true,
     * "HasErrors": false,
     * "Success": true,
     * "AllMessages": "",
     * "Messages": []
     * }
     * 获取Object 中的某个字段int类型
     */

    public static int getJsonCode(String response, String str) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            return jsonObject.optInt(str);
        } catch (JSONException e) {
            e.printStackTrace();
            return -3;
        }
    }

    public static boolean getJsonBoolean(String response, String str) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            return jsonObject.optBoolean(str);//判断是否有此属性
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static <T> Object getJsonBoolean2(String response, Class<T> clazz, String str) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean b = jsonObject.optBoolean(str);
            if (b) {
                JSONObject jsonObject1 = jsonObject.getJSONObject("Data");
                return json2Bean(jsonObject1.toString(), clazz);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * {
     * "Data": true,
     * "HasErrors": false,
     * "Success": true,
     * "AllMessages": "",
     * "Messages": []
     * }
     * 获取Object 中的某个字段String类型
     */


    public static String getJsonCodeFromString(String response, String str) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            return jsonObject.optString(str);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static <T> List<T> json2List(JsonArray jsonArray, Class<T> clazz) {
        List<T> list = new ArrayList<T>();
        if (gson != null) {
            Iterator<JsonElement> it = jsonArray.iterator(); // Iterator处理
            while (it.hasNext()) { // 循环
                JsonElement jsonElement = it.next(); // 提取JsonElement
                String json = jsonElement.toString(); // JsonElement转换成String
                list.add(gson.fromJson(json, clazz)); // 加入List
            }
        }
        return list;
    }

    /**
     * 将json格式转换成list对象，并准确指定类型
     *
     * @param jsonStr
     * @param type
     * @return
     */
    public static List<?> jsonToList(String jsonStr, Type type) {
        List<?> objList = null;
        if (gson != null) {
            objList = gson.fromJson(jsonStr, type);
        }
        return objList;
    }

    /**
     * 将json格式转换成map对象
     *
     * @param jsonStr
     * @return
     */
    public static Map<?, ?> jsonToMap(String jsonStr) {
        Map<?, ?> objMap = null;
        if (gson != null) {
            Type type = new com.google.gson.reflect.TypeToken<Map<?, ?>>() {
            }.getType();
            objMap = gson.fromJson(jsonStr, type);
        }
        return objMap;
    }

    /**
     * 将json转换成bean对象
     *
     * @param jsonStr "User" {Username:xx,Displayname:xx}
     * @return
     */
    public static Object json2Bean(String jsonStr, Class<?> cl) {
        if (jsonStr==null) {
            return null;
        }
        if (jsonStr.length()==0) {
            return null;
        }

//        jsonStr.replace("\"data\":\"\"","\"data\":{}");
//        Log.e(TAG, "json2Bean: "+jsonStr );
        Object obj;
        try {
            obj = null;
            if (gson != null) {
                obj = gson.fromJson(jsonStr, cl);
            }
            return obj;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            log(TAG, "解析json失败");
            return null;
        }

    }

    /**
     * 将json转换成bean对象
     *
     * @param jsonStr
     * @param cl
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T jsonToBeanDateSerializer(String jsonStr, Class<T> cl,
                                                 final String pattern) {
        Object obj = null;
        gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    @Override
                    @SuppressLint("SimpleDateFormat")
                    public Date deserialize(JsonElement json, Type typeOfT,
                                            JsonDeserializationContext context)
                            throws JsonParseException {
                        SimpleDateFormat format = new SimpleDateFormat(pattern);
                        String dateStr = json.getAsString();
                        try {
                            return format.parse(dateStr);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }).setDateFormat(pattern).create();
        if (gson != null) {
            obj = gson.fromJson(jsonStr, cl);
        }
        return (T) obj;
    }

    /**
     * 根据
     *
     * @param jsonStr
     * @param key
     * @return
     */
    public static Object getJsonValue(String jsonStr, String key) {
        Object rulsObj = null;
        Map<?, ?> rulsMap = jsonToMap(jsonStr);
        if (rulsMap != null && rulsMap.size() > 0) {
            rulsObj = rulsMap.get(key);
        }
        return rulsObj;
    }


}
