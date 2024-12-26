package common.module.jpa;

import common.module.util.errors.AppError;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppPages {
    public static int startIndex(int pageIndex, int pageSize) {
        return (pageIndex - 1) * pageSize;
    }

    public static int endIndex(int pageIndex, int pageSize, long total) {
        return Math.min((pageIndex - 1) * pageSize + pageSize, (int) total);
    }

    public static void checkPageParam(Integer pageIndex, Integer pageSize) {
        if (pageIndex == null || pageIndex < 1) {
            throw new AppError("pageIndex 不能小于 1");
        }
        if (pageSize == null || pageSize < 1) {
            throw new AppError("pageSize 不能小于 1");
        }
    }

    public static <T> AppPageResult<T> fromList(List<T> list, int pageSize, int pageIndex) {
        Comparator comparing = Comparator.comparing(Function.identity());
        return fromList(list, comparing, pageSize, pageIndex);
    }

    public static <T> AppPageResult<T> fromList(List<T> list, Comparator<T> comparator, int pageSize, int pageIndex) {

        List<T> l = list.stream().sorted(comparator).collect(Collectors.toList());

        int start = pageSize * (pageIndex - 1);

        int end = start + pageSize;

        if (end > list.size()) {
            end = list.size();
        }
        return AppPageResult.of((long) list.size(), l.subList(start, end));
    }
}
