package common.module.util;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.reader.UnicodeReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * AppYamls 工具类，用于处理 YAML 文件。
 */
public class AppYamls {

    /**
     * 从类路径加载 YAML 文件并转换为指定类型的对象。
     *
     * @param filePath YAML 文件路径
     * @param cls      目标对象类型
     * @param <T>      类型参数
     * @return 转换后的对象
     */
    public static <T> T loadClasspath(String filePath, Class<T> cls) {
        return AppJsons.convert(loadClasspath(filePath), cls);
    }

    /**
     * 从类路径加载 YAML 文件并解析为 Map。
     *
     * @param filePath YAML 文件路径
     * @return 解析后的 Map
     */
    public static Map<String, Object> loadClasspath(String filePath) {
        try (InputStream inputStream = AppIOUtils.classpathInputStream(filePath)) {
            return yamlHandler(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load YAML file: " + filePath, e);
        }
    }

    /**
     * 处理单个 YAML 文件流。
     *
     * @param inputStream YAML 文件流
     * @return 解析后的 Map
     */
    public static Map<String, Object> yamlHandler(InputStream inputStream) {
        Map<String, Object> result = new LinkedHashMap<>();
        try (UnicodeReader reader = new UnicodeReader(inputStream)) {
            Yaml yaml = new Yaml();
            Object object = yaml.load(reader);

            if (object instanceof Map) {
                buildFlattenedMap(result, (Map<String, Object>) object, null);
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Failed to process YAML input stream", e);
        }
    }

    /**
     * 处理多个 YAML 文件流。
     *
     * @param inputStreams YAML 文件流列表
     * @return 合并后的 Map
     */
    public static Map<String, Object> yamlHandler(List<InputStream> inputStreams) {
        Map<String, Object> result = new LinkedHashMap<>();
        Yaml yaml = new Yaml();

        for (InputStream inputStream : inputStreams) {
            try (UnicodeReader reader = new UnicodeReader(inputStream)) {
                Object object = yaml.load(reader);

                if (object instanceof Map) {
                    buildFlattenedMap(result, (Map<String, Object>) object, null);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to process YAML input stream", e);
            }
        }
        return result;
    }

    /**
     * 将 YAML 文件中的嵌套 Map 扁平化为单层 Map。
     *
     * @param result 扁平化后的结果 Map
     * @param source 原始嵌套 Map
     * @param path   当前路径前缀
     */
    private static void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, String path) {
        source.forEach((key, value) -> {
            String fullPath = path != null ? path + (key.startsWith("[") ? key : "." + key) : key;

            if (value instanceof Map) {
                // 如果是嵌套 Map，则递归处理
                buildFlattenedMap(result, (Map<String, Object>) value, fullPath);
            } else if (value instanceof Collection) {
                // 如果是集合类型，则处理每个元素
                Collection<?> collection = (Collection<?>) value;
                if (collection.isEmpty()) {
                    result.put(fullPath, "");
                } else {
                    int index = 0;
                    for (Object item : collection) {
                        buildFlattenedMap(result, Collections.singletonMap("[" + index++ + "]", item), fullPath);
                    }
                }
            } else {
                // 处理基础类型或其他类型
                result.put(fullPath, value != null ? value : "");
            }
        });
    }
}