package common.module.util;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import common.module.util.errors.AppError;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppStrings {

    private static final Pattern humpPattern = Pattern.compile("[A-Z]");

    public static String sprintf(String template, Object... value) {
        String result = template;
        for (Object v : value) {
            result = result.replaceFirst("\\{}", String.valueOf(v));
        }
        return result;
    }

    public static String substitute(String template, Map<String, ?> params) {
        return new StringSubstitutor(params).replace(template);
    }

    public static Set<String> splitToSet(String value, String separator) {
        if (value == null) {
            return Sets.newLinkedHashSet();
        }
        return Arrays.stream(value.split(separator))
                .collect(Collectors.toCollection(Sets::newLinkedHashSet));
    }

    public static List<String> split(String value, String separator) {
        if (value == null) {
            return Lists.newArrayList();
        }
        return Arrays.stream(value.split(separator)).collect(Collectors.toList());
    }

    public static String join(Collection<String> collection, String delimiter) {
        if (CollectionUtils.isEmpty(collection)) {
            return "";
        }
        return StringUtils.join(collection, delimiter);
    }

    public static String tail(String template, String occur) {
        return template.substring(template.lastIndexOf(occur));
    }

    public static String tailExclude(String template, String occur) {
        int i = template.lastIndexOf(occur);
        if (i < 0) {
            throw new AppError("string [{}] not include [{}]", template, occur);
        }
        return template.substring(+1);
    }

    public static String append(String template, String prefix) {
        if (template == null) {
            return prefix;
        }
        if (template.startsWith(prefix)) {
            return template;
        }
        return prefix + template;
    }

    public static String absolutePath(String path) {
        return append(path, "/");
    }

    public static String joinPath(String... template) {
        String delimiter = "/";
        if (ArrayUtils.isEmpty(template)) {
            return "";
        }
        List<String> list = Arrays.stream(template).filter(StringUtils::isNotBlank)
                .map(v -> strip(v, delimiter))
                .toList();

        return list.stream().reduce((a, b) -> a.concat(delimiter).concat(b)).orElse("");
    }

    public static String strip(String template, String suffix) {
        String s = stripStart(template, suffix);
        return stripEnd(s, suffix);
    }

    public static String stripEnd(String template, String suffix) {
        if (StringUtils.isEmpty(suffix) || !template.endsWith(suffix)) {
            return template;
        }
        return template.substring(0, template.length() - suffix.length());
    }

    public static String stripStart(String template, String prefix) {
        if (StringUtils.isEmpty(prefix) || !template.startsWith(prefix)) {
            return template;
        }
        return template.substring(prefix.length());
    }

    private static final Pattern compile = Pattern.compile("-?[0-9]+.?[0-9]+");

    public static boolean isNumber(String value) {
        return compile.matcher(value).matches();
    }

    // return true if obj is null or obj is a blank string
    public static boolean isBlank(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            String s = (String) obj;
            return StringUtils.isBlank(s);
        }
        return false;
    }

    public static boolean isNotBlank(Object obj) {
        return !isBlank(obj);
    }

    public static boolean anyBlank(Object... objects) {
        if (objects == null) {
            return true;
        }
        return Arrays.stream(objects).anyMatch(AppStrings::isBlank);
    }

    public static boolean anyNotBlank(Object... objects) {
        if (objects == null) {
            return true;
        }
        return Arrays.stream(objects).anyMatch(AppStrings::isNotBlank);
    }


    public static List<String> params(String str, String pattern) {
        Matcher matcher = Pattern.compile(pattern).matcher(str);
        List<String> list = Lists.newArrayList();
        if (!matcher.matches())
            return list;
        for (int i = 1; i <= matcher.groupCount(); i++) {
            list.add(matcher.group(i));
        }
        return list;
    }

    /**
     * 驼峰转下划线
     */
    public static String underline(String str) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, str);
    }

    private static final Pattern linePattern = Pattern.compile("_(\\w)");

    /**
     * 下划线转驼峰
     */
    public static String hump(String str) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str);
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static boolean isEnglish(String str) {
        if (StringUtils.isBlank(str)) {
            return true;
        }

        for (char c : str.toCharArray()) {
            if (String.valueOf(c).getBytes().length != 1) {
                return false;
            }
        }
        return true;
    }

    public static Set<String> toSet(String str) {
        return toSet(str, ",");
    }

    public static Set<String> toSet(String str, String splitter) {
        return Sets.newHashSet(toList(str, splitter));
    }

    public static List<String> toList(String str) {
        return toList(str, ",");
    }

    public static List<String> toList(String str, String splitter) {
        if (StringUtils.isBlank(str))
            return Lists.newArrayList();
        return Lists.newArrayList(str.split(splitter));
    }

    public static String fromIterable(Iterable<String> strList) {
        return fromIterable(strList, ",");
    }

    public static String fromIterable(Iterable<String> strList, String delimiter) {
        if (strList == null || Iterables.isEmpty(strList)) {
            return null;
        }
        return String.join(delimiter, strList);
    }
}
