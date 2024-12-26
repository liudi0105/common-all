package common.module.webmvc;

import common.module.util.errors.AppError;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@ToString
public class R<T> {
    private String code;
    private String msg;
    private T data;
    private Boolean success;

    public static R<Boolean> success() {
        return success(true);
    }

    public static R<Boolean> fail(String msg) {
        return R.<Boolean>builder()
                .code("UNKNOWN_ERROR")
                .success(false)
                .msg(msg)
                .build();
    }

    public static R<Boolean> fail(String code, String msg) {
        return R.<Boolean>builder()
                .success(false)
                .code(code)
                .msg(msg)
                .build();
    }

    public static R<Boolean> fail(AppError appError) {
        return R.<Boolean>builder()
                .code(appError.getCode())
                .success(false)
                .msg(appError.getMsg())
                .build();
    }

    public static <T> R<T> success(T data) {
        return R.<T>builder()
                .code("SUCCESS")
                .data(data)
                .success(true)
                .build();
    }

    public T check() {
        if (!success) {
            throw new AppError("获取响应数据失败");
        }
        return data;
    }

}
