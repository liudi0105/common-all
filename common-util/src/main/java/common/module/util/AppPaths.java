package common.module.util;

public class AppPaths {
    public static String nameOfPath(String path) {
        return AppStrings.tailExclude(path, "/");
    }
}
