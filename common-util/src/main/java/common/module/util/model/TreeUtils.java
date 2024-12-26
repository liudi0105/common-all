package common.module.util.model;

import com.google.common.collect.Lists;
import common.module.util.AppCollections;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TreeUtils {

    public static <T extends TreeModel<T>> T from(Collection<T> collection) {

        List<T> roots = toForest(collection);

        if (roots.isEmpty()) {
            throw new RuntimeException("无此数据");
        } else if (roots.size() > 1) {
            throw new RuntimeException("树形数据错误");
        }

        return roots.get(0);
    }

    public static <T extends TreeModel<T>> List<T> toForest(Collection<T> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            return Lists.newArrayList();
        }
        Map<String, T> map = AppCollections.toMap(collection, TreeModel::getTreeId);
        collection.forEach(v -> {
            T t = map.get(v.getTreeParentId());
            if (t == null) {
                return;
            }
            if (t.getChildren() == null) {
                t.setChildren(Lists.newArrayList());
            }
            t.getChildren().add(v);
        });

        Set<String> keySet = map.keySet();

        List<T> roots = collection
                .stream()
                .filter(v -> v.getTreeParentId() == null || !keySet.contains(v.getTreeParentId()))
                .collect(Collectors.toList());

        if (roots.isEmpty()) {
            throw new RuntimeException("无此数据");
        }

        return roots;
    }

}
