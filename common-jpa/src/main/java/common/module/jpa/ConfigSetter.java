package common.module.jpa;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import common.module.util.errors.AppError;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class ConfigSetter {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public Builder newBuilder() {
        return new Builder();
    }


    public class Builder {
        private final Map<String, Consumer<String>> params = Maps.newLinkedHashMap();

        private final Set<String> optionalKeys = Sets.newHashSet();

        public Builder appendOptional(String key, Consumer<String> consumer) {
            optionalKeys.add(key);
            params.put(key, consumer);
            return this;
        }

        public Builder append(String key, Consumer<String> consumer) {
            params.put(key, consumer);
            return this;
        }

        public void commitDatabase() {
            String sql = "select * from app_system_property where property_key in (:keys)";
            Map<String, String> map = jdbcTemplate
                    .queryForList(sql, Map.of("keys", params.keySet()))
                    .stream()
                    .collect(Collectors.toMap(v -> (String) v.get("property_key"), v -> (String) v.get("property_value")));

            Set<String> missingKeys = Sets.newHashSet(this.params.keySet());
            missingKeys.removeAll(map.keySet());
            missingKeys.removeAll(this.optionalKeys);

            if (CollectionUtils.isNotEmpty(missingKeys)) {
                throw new AppError("missing key: " + missingKeys);
            }

            this.params.forEach((k, v) -> {
                String value = map.get(k);
                if (value != null) {
                    v.accept(value);
                }
            });
        }
    }
}
