package org.jrl.doc.gradle.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

/**
* json工具
* @author JerryLong
*/
public class JrlJsonUtils {

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 如果有null字段的话,就不输出,用在节省流量的情况
     */
    private final static ObjectMapper PURE_MAPPER = new ObjectMapper();

    /**
     * null字段也输出
     */
    private final static ObjectMapper RAW_MAPPER = new ObjectMapper();

    static {
        // 初始化,这是Jackson所谓的key缓存：对JSON的字段名是否调用String#intern方法，放进字符串常量池里，以提高效率,设置为false。
        OBJECT_MAPPER.getFactory().disable(JsonFactory.Feature.INTERN_FIELD_NAMES);
        //反序列化忽略未知属性，不报错
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //默认情况下（false）parser解析器是不能解析包含控制字符的json字符串，设置为true不报错。
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //属性为NULL不被序列化，只对bean起作用，Map List不起作用
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 指定类型的序列化, 不同jackson版本对不同的类型的默认规则可能不一样，这里做强制指定
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(java.sql.Date.class, new DateSerializer(null, new SimpleDateFormat("yyyy-MM-dd")));
        OBJECT_MAPPER.registerModule(simpleModule);
        //org.json.JSONArray、org.json.JSONObject 序列化反序列化
        OBJECT_MAPPER.registerModule(new JsonOrgModule());


        // 初始化,这是Jackson所谓的key缓存：对JSON的字段名是否调用String#intern方法，放进字符串常量池里，以提高效率，默认是true。
        PURE_MAPPER.getFactory().disable(JsonFactory.Feature.INTERN_FIELD_NAMES);
        //属性为NULL不被序列化，只对bean起作用，Map List不起作用
        PURE_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //org.json.JSONArray、org.json.JSONObject 序列化反序列化
        PURE_MAPPER.registerModule(new JsonOrgModule());
    }

    // ------------- defaultMapper start -------------

    /**
     * json字符串到对象,默认配置
     *
     * @param json
     * @param valueType
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, Class<T> valueType) {
        try {
            return OBJECT_MAPPER.readValue(json, valueType);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * json字符串到对象,默认配置 {@link #OBJECT_MAPPER}
     *
     * @param json
     * @param valueTypeRef 例如:new TypeReference<Map<String, Att>>(){}
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, TypeReference<T> valueTypeRef) {
        try {
            return OBJECT_MAPPER.readValue(json, valueTypeRef);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * json流转对象
     *
     * @param stream
     * @param valueType
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T fromJson(InputStream stream, Class<T> valueType) throws IOException {
        return OBJECT_MAPPER.readValue(stream, valueType);
    }

    /**
     * 对象到json字符串,默认配置
     * - 属性为NULL不被序列化
     * - java.sql.Date format yyyy-MM-dd
     *
     * @param value
     * @return
     */
    public static String toJson(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            return null;
        }
    }

    // ------------- defaultMapper end -------------


    // ------------- pureMapper start -------------

    /**
     * json字符串到对象,如果有null字段的话,就不输出,用在节省流量的情况
     *
     * @param json
     * @param valueType
     * @param <T>
     * @return
     */
    @Deprecated
    public static <T> T fromJsonPure(String json, Class<T> valueType) {
        try {
            return PURE_MAPPER.readValue(json, valueType);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * json字符串到对象,如果有null字段的话,就不输出,用在节省流量的情况
     *
     * @param json
     * @param valueTypeRef 例如:new TypeReference<Map<String, Att>>(){}
     * @param <T>
     * @return
     */
    @Deprecated
    public static <T> T fromJsonPure(String json, TypeReference<T> valueTypeRef) {
        try {
            return PURE_MAPPER.readValue(json, valueTypeRef);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 对象到json字符串,如果有null字段的话,就不输出,用在节省流量的情况
     *
     * @param value
     * @return
     */
    public static String toJsonPure(Object value) {
        try {
            return PURE_MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            return null;
        }
    }
    // ------------- pureMapper end -------------

    /**
     * 对象到json字符串,有null字段的话也输出
     *
     * @param value
     * @return
     */
    public static String toJsonRaw(Object value) {
        try {
            return RAW_MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将 json 转成 JsonNode
     *
     * @param json
     * @return
     */
    public static JsonNode readTree(String json) {
        try {
            return OBJECT_MAPPER.readTree(json);
        } catch (IOException e) {
            return null;
        }
    }
}
