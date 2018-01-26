package com.xiang.wafer;

import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.Locale;

/**获取文件的 mimeType
 * Created by ZhaoMiXiang on 2017/1/23.
 */

public class MimeUtils {
    private static String getSuffix(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return null;
        }
        String fileName = file.getName();
        if (fileName.equals("") || fileName.endsWith(".")) {
            return null;
        }
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(index + 1).toLowerCase(Locale.US);
        } else {
            return null;
        }
    }
    private static String getSuffix(Uri uri) {
        String path = uri.toString();
        if (path.equals("") || path.endsWith(".")) {
            return null;
        }
        int index = path.lastIndexOf(".");
        if (index != -1) {
            return path.substring(index + 1).toLowerCase(Locale.US);
        } else {
            return null;
        }
    }

    public static String getMimeType(File file){
        String suffix = getSuffix(file);
        if (suffix == null) {
            return "file/*";
        }
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
        if (type != null && !type.isEmpty()) {
            return type;
        }
        return "file/*";
    }
    public static String getMimeType(Uri uri){
        String suffix = getSuffix(uri);
        if (suffix == null) {
            return "*/*";
        }
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
        if (type != null && !type.isEmpty()) {
            return type;
        }
        return "*/*";
    }
}
