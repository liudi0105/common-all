package common.module.jpa;

import com.google.common.collect.Lists;
import common.module.util.AppJsons;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
@Accessors(chain = true)
public class AppPageResult<T> {

    private Long totalElements;
    private List<T> content;

    public static <T> AppPageResult<T> of(Long totalElements, List<T> content) {
        return new AppPageResult<T>()
                .setTotalElements(totalElements)
                .setContent(content);
    }

    public static <T> AppPageResult<T> of(Page<T> page) {
        return new AppPageResult<T>()
                .setContent(page.getContent())
                .setTotalElements(page.getTotalElements());
    }

    public <C> AppPageResult<C> convert(Class<C> tClass) {
        return new AppPageResult<C>()
                .setContent(AppJsons.convertList(this.content, tClass))
                .setTotalElements(totalElements);
    }

    public AppPageResult<T> handle(Consumer<List<T>> consumer) {
        consumer.accept(this.content);
        return this;
    }

    public <C> AppPageResult<C> map(Function<T, C> func) {
        List<C> collect = this.content.stream().map(func).collect(Collectors.toList());
        return new AppPageResult<C>()
                .setContent(collect)
                .setTotalElements(totalElements);
    }

    public static <C> AppPageResult<C> empty() {
        return AppPageResult.of(0L, Lists.newArrayList());
    }

    public AppPageResult<T> wrapContent(Consumer<List<T>> consumer) {
        consumer.accept(content);
        return this;
    }

}
