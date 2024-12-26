package common.module.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppJsons {

    private static final ObjectMapper OBJECT_MAPPER = newObjectMapper();

    private static final ObjectMapper OBJECT_MAPPER_WITH_NULL_FIELDS = newObjectMapperWithNullFields();

    public static ObjectMapper newObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        JavaTimeModule timeModule = new JavaTimeModule();
        timeModule.addSerializer(LocalDateTime.class, new JsonSerializers.LocalDateTimeSerializer());
        timeModule.addDeserializer(LocalDateTime.class, new JsonSerializers.LocalDateTimeDeserializer());

        timeModule.addSerializer(Instant.class, new JsonSerializers.InstantSerializer());
        timeModule.addDeserializer(Instant.class, new JsonSerializers.InstantDeserializer());

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(BigDecimal.class, new JsonSerializers.BigDecimalSerializer());
        simpleModule.addDeserializer(BigDecimal.class, new JsonSerializers.BigDecimalDeserializer());

        objectMapper.registerModules(timeModule, simpleModule);
        return objectMapper;
    }

    public static ObjectMapper objectMapper() {
        return OBJECT_MAPPER;
    }

    public static ObjectMapper newObjectMapperWithNullFields() {
        ObjectMapper objectMapper = newObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        return objectMapper;
    }

    public static <T> Map<String, T> nonNullMap(Map<String, T> map) {
        return map.entrySet()
                .stream()
                .filter(v -> v.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> toMap(String json) {
        return fromJson(json, Map.class);
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> toMap(Object obj) {
        if (obj == null) {
            return Maps.newHashMap();
        }
        if (obj instanceof Map) {
            return (Map<String, T>) obj;
        }
        return convert(obj, HashMap.class);
    }

    public static <T> Map<String, T> toUnderlineMap(Object obj) {
        Map<String, T> map = toMap(obj);
        return map.entrySet().stream()
                .collect(Collectors.toMap(k -> AppStrings.underline(k.getKey()), Map.Entry::getValue));
    }

    public static <T> Map<String, T> fromUnderlineMap(Map<String, T> map) {
        Map<String, T> resMap = new HashMap<>();
        map.forEach((k, v) -> resMap.put(AppStrings.hump(k), v));
        return resMap;
    }

    public static <T> T convertUnderlineMap(Map<String, ?> map, Class<T> clazz) {
        return convert(fromUnderlineMap(map), clazz);
    }

    public static <T> List<T> convertUnderlineMapList(List<Map<String, ?>> map, Class<T> clazz) {
        return map
                .stream()
                .map(v -> convertUnderlineMap(v, clazz))
                .collect(Collectors.toList());
    }

    // fix sonar bug
    public static <T> List<T> convertUnderlineMapListObj(List<Map<String, Object>> map, Class<T> clazz) {
        return map
                .stream()
                .map(v -> convertUnderlineMap(v, clazz))
                .collect(Collectors.toList());
    }

    public static <T> T convert(Object o, Class<T> clazz) {
        if (clazz.isAssignableFrom(o.getClass())) {
            return (T) o;
        }
        return objectMapper().convertValue(o, clazz);
    }

    public static <T> List<T> convertList(Collection<?> collection, Class<T> clazz) {
        return collection.stream().map(v -> convert(v, clazz)).collect(Collectors.toList());
    }

    /**
     * convert object to json with null fields reserved
     */
    public static String json(Object obj) {
        return toJsonInternal(obj, OBJECT_MAPPER_WITH_NULL_FIELDS);
    }

    private static String toJsonInternal(Object obj, ObjectMapper objectMapper) {
        if (obj == null) {
            return null;
        }
        String jsonInternal = "";
        try {
            jsonInternal = objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            if (log.isDebugEnabled()) {
                throw new RuntimeException("JSON序列化失败");
            }
        }
        return jsonInternal;
    }

    /**
     * convert to json without null fields
     */
    public static String toJson(Object obj) {
        return toJsonInternal(obj, OBJECT_MAPPER);
    }

    public static <T> List<T> fromJSONArray(String json, Class<T> clazz) {
        if (json == null) {
            return Lists.newArrayList();
        }
        try {
            List<T> list = objectMapper().readValue(json, new TypeReference<List<T>>() {
            });
            return convertList(list, clazz);
        } catch (JsonProcessingException e) {
            log.error("fromJSONArray generic execute failed", e);
            throw new RuntimeException("fromJSONArray generic execute failed");
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper().readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("fromJSON generic execute failed");
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        try {
            return objectMapper().readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("fromJSON execute failed, \njson: {}, \nclass: {}", json, clazz.getName());
            throw new RuntimeException(e);
        }
    }

}
