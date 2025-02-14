package common.module.jpa;

import com.google.common.collect.Lists;
import common.module.util.AppJsons;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppPageResult<T> {

    private Long totalElements;
    private List<T> content;

    public static <T> AppPageResult<T> of(Long totalElements, List<T> content) {
        return  AppPageResult.<T>builder()
                .totalElements(totalElements)
                .content(content)
                .build();
    }

    public static <T> AppPageResult<T> of(Page<T> page) {
        return AppPageResult.<T>builder()
                .content(page.getContent())
                .totalElements(page.getTotalElements())
                .build();
    }

    public <C> AppPageResult<C> convert(Class<C> tClass) {
        return  AppPageResult.<C>builder()
                .content(AppJsons.convertList(this.content, tClass))
                .totalElements(totalElements)
                .build();
    }

    public AppPageResult<T> handle(Consumer<List<T>> consumer) {
        consumer.accept(this.content);
        return this;
    }

    public <C> AppPageResult<C> map(Function<T, C> func) {
        List<C> collect = this.content.stream().map(func).collect(Collectors.toList());
        return AppPageResult.<C>builder()
                .content(collect)
                .totalElements(totalElements)
                .build();
    }

    public static <C> AppPageResult<C> empty() {
        return AppPageResult.of(0L, Lists.newArrayList());
    }

    public AppPageResult<T> wrapContent(Consumer<List<T>> consumer) {
        consumer.accept(content);
        return this;
    }

}
