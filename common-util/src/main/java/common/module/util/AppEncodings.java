package common.module.util;

import common.module.util.errors.AppError;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppEncodings {

    public static String encodeUrl(String url) {
        try {
            return URLEncoder.encode(url, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new AppError(e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String md5(String value) {
        return DigestUtils.md5Hex(value);
    }

    public static String encodeBase64(String code) {
        if (StringUtils.isBlank(code)) {
            return code;
        }
        return new String(Base64.encodeBase64(code.getBytes(StandardCharsets.UTF_8)));
    }

    public static String decodeBase64(String code) {
        if (StringUtils.isBlank(code)) {
            return code;
        }
        return new String(Base64.decodeBase64(code.getBytes(StandardCharsets.UTF_8)));
    }

    public static byte[] decodeBase64Bytes(String code) {
        if (code == null) {
            return null;
        }
        return Base64.decodeBase64(code.getBytes(StandardCharsets.UTF_8));
    }
}
