package common.module.util.model;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public interface TreeModel<T extends TreeModel<T>> {

    String getTreeId();

    void setId(String nodeId);

    String getTreeParentId();

    List<T> getChildren();

    T setChildren(List<T> list);

    default void forEach(UnaryConsumer<T> operator) {
        if (CollectionUtils.isEmpty(getChildren())) {
            return;
        }
        getChildren().forEach(v -> {
            operator.consume((T) this, v);
            v.forEach(operator);
        });
    }

    default void forEach(Consumer<T> operator) {
        operator.accept((T) this);
        if (CollectionUtils.isEmpty(getChildren())) {
            return;
        }
        getChildren().forEach(v -> v.forEach(operator));
    }

    default List<T> toList() {
        return toList(Lists.newArrayList((T) this), getChildren());
    }

    default List<T> toList(List<T> parent, List<T> children) {
        if (CollectionUtils.isEmpty(children)) {
            return parent;
        }
        children.forEach(v -> {
            if (CollectionUtils.isEmpty(v.getChildren())) {
                v.setChildren(Lists.newArrayList());
            }
        });
        List<T> newChildren = children.stream()
                .flatMap(v -> v.getChildren().stream())
                .collect(Collectors.toList());
        parent.addAll(children);
        return toList(parent, newChildren);
    }

}
