package common.module.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * AppIOUtils 工具类，提供文件和流操作功能。
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppIOUtils {

    /**
     * 从指定路径读取文件内容并以字符串形式返回。
     *
     * @param path 文件路径
     * @return 文件内容的字符串表示
     */
    public static String readPathAsString(String path) {
        File file = new File(path);
        try {
            return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("读取文件失败：" + path, e);
        }
    }

    /**
     * 从类路径读取文件的输入流。
     *
     * @param path 类路径文件的相对路径
     */
    public static InputStream classpathInputStream(String path) {
        try {
            File file = new File(AppIOUtils.class.getClassLoader().getResource(path).toURI());
            return new FileInputStream(file);
        } catch (Exception e) {
        throw new RuntimeException("加载类路径文件失败：" + path, e);
        }
    }

    /**
     * 将一个输出流转换为输入流，并通过消费者处理。
     *
     * @param oconsumer 用于生成输出流内容的消费者
     * @param iconsumer 用于处理输入流内容的消费者
     */
    public static void o2i(Consumer<OutputStream> oconsumer, Consumer<InputStream> iconsumer) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            // 使用消费者写入输出流内容
            oconsumer.accept(byteArrayOutputStream);

            // 将输出流内容转换为输入流
            try (InputStream inputStream = IOUtils.toInputStream(byteArrayOutputStream.toString(StandardCharsets.UTF_8), StandardCharsets.UTF_8)) {
                // 使用消费者处理输入流
                iconsumer.accept(inputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException("转换流时发生错误", e);
        }
    }
}