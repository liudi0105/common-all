package common.module.util;

public class AppFiles {

    public String formatFileSize(long sizeInBytes) {
        if (sizeInBytes < 0) {
            throw new IllegalArgumentException("Size cannot be negative");
        }

        // 定义文件大小单位
        String[] units = {"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};

        int unitIndex = 0;
        double size = sizeInBytes;

        // 根据大小调整单位
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        // 返回格式化后的文件大小
        return String.format("%.2f %s", size, units[unitIndex]);
    }

}
