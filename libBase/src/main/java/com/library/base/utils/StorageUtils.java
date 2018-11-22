package com.library.base.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 存储路径
 * Provides application storage paths
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public final class StorageUtils {

    public static final String ROOT_PATH = "HealthTalkDoc";
    public static final String IMAGE_PATH = "images";
    public static final String VIDEO_PATH = "videos";

    private StorageUtils() {
    }

    public static File createMapsFile() {
        if (isSDMounted()) {
            String path = getMapsPath();
            File dir = createRootFileDir(path);
            try {
                UUID uid = UUID.randomUUID();
                dir = File.createTempFile(uid.toString(), ".jpg", dir);
                return dir;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * 创建视频文件
     * @throws IOException
     */
    public static File createVideoFile() {
        String path = getVideoPath();
        File dir = createRootFileDir(path);
        UUID uid = UUID.randomUUID();
        try {
            dir = File.createTempFile(uid.toString(), ".MP4", dir);
        } catch (IOException e) {
            e.printStackTrace();
            ToastUtils.showShort("视频获取失败");
        }
        return dir;
    }

    /**
     * 创建文件
     * @throws IOException
     */
    public static File createImageFile() {
        String path = getImagePath();
        File dir = createRootFileDir(path);
        UUID uid = UUID.randomUUID();
        try {
            dir = File.createTempFile(uid.toString(), ".jpg", dir);
        } catch (IOException e) {
            e.printStackTrace();
            ToastUtils.showShort("图片获取失败");
        }
        return dir;
    }

    /**
     * 创建图片文件
     * @throws IOException
     */
    @Deprecated
    public static File createPhotoFile() {
        return createImageFile();
    }

    /**
     * 根据名称创建图片文件
     * @param filename
     * @return
     * @throws IOException
     */
    public static File createImageFileByName(String filename) {
        String path = getImagePath();
        File dir = createRootFileDir(path);
        try {
            dir = File.createTempFile(filename, ".jpg", dir);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return dir;
    }

    /**
     * 照相默认的存储路径
     * @return
     */
    public static File createCameraFile() {
        if (isSDMounted()) {
            String path = getCameraPath();
            File dir = createRootFileDir(path);
            try {
                UUID uid = UUID.randomUUID();
                dir = File.createTempFile(uid.toString(), ".jpg", dir);
                return dir;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 二维码默认地址
     * @return
     */
    public static File createQrFile() {
        if (isSDMounted()) {
            String path = getQrPath();
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try {
                UUID uid = UUID.randomUUID();
                dir = File.createTempFile(uid.toString(), ".png", dir);
                return dir;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 头像
     * @return
     */
    public static File createHeaderFile() throws Exception {
        String path = getHeadersPath();
        File dir = createRootFileDir(path);
        UUID uid = UUID.randomUUID();
        dir = File.createTempFile(uid.toString(), ".jpg", dir);
        return dir;
    }

    /**
     * 语音
     * @return
     * @throws Exception
     */
    public static File createVoiceFile() {
        String path = getVoicePath();
        String armName = System.currentTimeMillis() + ".arm";
        return new File(path, armName);
    }

    /**
     * 创建语音文件
     * @param name
     * @return
     */
    public static File createVoiceFile(String name) {
        if (isSDMounted()) {
            String path = getVoicePath();
            File dir = createRootFileDir(path);
            File file = new File(dir, name);
            try {
                boolean bl = file.createNewFile();
                if (bl) return file;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 创建聊天背景文件
     * @return
     */
    public static File createThemeFile() {
        if (isSDMounted()) {
            try {
                String path = getThemeCachePath();
                UUID uid = UUID.randomUUID();
                File file = File.createTempFile(uid.toString(), ".jpg", new File(path));
                return file;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 保存bitmap
     * @param bitmap
     * @param tagFile
     * @return
     */
    public static boolean saveImageOnImagsDir(Bitmap bitmap, File tagFile) {
        FileOutputStream fileOutputStream = null;
        if (bitmap != null && tagFile != null) {
            try {
                if (!tagFile.exists()) {
                    tagFile.mkdirs();
                    tagFile.delete();
                }
                fileOutputStream = new FileOutputStream(tagFile.getAbsolutePath());
                bitmap.compress(CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {

            } finally {
                if (fileOutputStream != null)
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
        return false;
    }

    /**
     * 删除文件
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        return file != null && (!file.exists() || file.isFile() && file.delete());
    }

    /**
     * 删除文件
     * @param path
     * @return
     */
    public static boolean deleteFile(String path) {
        return deleteFile(new File(path));
    }

    /**
     * Returns specified application cache directory. Cache directory will be created on SD card by defined path if card
     * is mounted. Else - Android defines cache directory on device's file system.
     * @param context  Application context
     * @param cacheDir Cache directory path (e.g.: "AppCacheDir", "AppDir/cache/images")
     * @return Cache {@link File directory}
     */
    public static File getOwnCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = null;
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
        }
        if (appCacheDir == null || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }

    private static File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "universal"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
        if (!appCacheDir.exists()) {
            try {
                new File(dataDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                LogUtils.e(e, "Can't create \".nomedia\" file in application external cache directory");
            }
            if (!appCacheDir.mkdirs()) {
                LogUtils.w("Unable to create external cache directory");
                return null;
            }
        }
        return appCacheDir;
    }

    /**
     * 获得项目存储的根目录
     * @return
     */
    public static File getHealthTalkExternalCacheDir(String name) {
        String dirName = name;
        File dataDir = new File(Environment.getExternalStorageDirectory(), "consultation");
        if (dirName == null) dirName = "union_test";
        File appCachDir = new File(dataDir, dirName);
        if (!dataDir.exists()) {
            try {
                new File(dataDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                LogUtils.e(e, "Can't create \".nomedia\" file in application external cache directory");
            }
        }
        boolean b = appCachDir.mkdirs();
        if (b) {
            try {
                new File(appCachDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                LogUtils.e(e, "Can't create \".nomedia\" file in application external cache directory");
            }
        }
        return appCachDir;
    }

    public static File createRootFileDir(String path) {
        File dataDir = new File(path);
        if (!dataDir.exists()) {
            boolean b = dataDir.mkdirs();
            if (b) {
                try {
                    new File(dataDir, ".nomedia").createNewFile();
                } catch (IOException e) {
                    LogUtils.e(e, "Can't create \".nomedia\" file in application external cache directory");
                }
            }
        }
        return dataDir;
    }


    /**
     * sd卡状态
     * @return
     */
    public static boolean isSDMounted() {
        return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED) ? true : false;
    }

    public static String getRootPath() {
        String rootPath = new StringBuffer(Environment.getExternalStorageDirectory().getAbsolutePath())
                .append(File.separator)
                .append(ROOT_PATH)
                .toString();
        File rootFile = new File(rootPath);
        if (!rootFile.exists()) {
            rootFile.mkdir();
        }
        return rootPath;
    }

    public static String getRootPath(String typeName) {
        return getRootPath() + File.separator + typeName + File.separator;
    }

    /**
     * 获取im缓存路径
     * @return
     */
    public static String getImPath() {
        return getRootPath("nim");
    }

    /**
     * 缓存图片
     * @return
     */
    public static String getImagePath() {
        return getRootPath(IMAGE_PATH);
    }

    /**
     * 剪裁视频地址
     * @return
     */
    public static String getVideoPath() {
        return getRootPath(VIDEO_PATH);
    }

    /**
     * 用户操作图片
     * @return
     */
    public static String getPhotoPath() {
        return getRootPath("photo");
    }

    /**
     * 语音
     * @return
     */
    public static String getVoicePath() {
        return getRootPath("voices");
    }

    /**
     * 头像
     * @return
     */
    public static String getHeadersPath() {
        return getRootPath("headers");
    }

    public static File getHeaderFileDir() {
        File file = new File(getHeadersPath());
        if (!file.exists()) {
            createRootFileDir(file.getAbsolutePath());
        }
        return file;
    }

    /**
     * 相机
     * @return
     */
    public static String getCameraPath() {
        return getRootPath("camera");
    }

    /**
     * 二维码
     * @return
     */
    public static String getQrPath() {
        return Environment.getExternalStorageDirectory().toString() + "/QrCode/";
    }


    /**
     * 地图
     * @return
     */
    public static String getMapsPath() {
        return getRootPath("maps");
    }

    /**
     * 删除语音文件
     * @param name
     */
    public static void deleteVoiceFile(String name) {
        try {
            name = getFileName(name);
            File file = new File(getVoicePath(), name);
            file.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除图片文件
     * @param name
     */
    public static void deleteImageFile(String name) {
        try {
            name = getFileName(name);
            File file = new File(getImagePath(), name);
            file.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除地图文件
     * @param name
     */
    public static void deleteMapFile(String name) {
        try {
            name = getFileName(name);
            File file = new File(getMapsPath(), name);
            file.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        path = (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
        return path;
    }

    /**
     * @return
     */
    public static String getDownCachePath() {
        String path = getRootPath("downloads");
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
            createRootFileDir(path);
        }
        return path;
    }

    public static String getThemeCachePath() {
        String path = getRootPath("themes");
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
            createRootFileDir(path);
        }
        return path;
    }

    public static String[][] getMIMEFile() {
        String[][] MIME_MapTable = {
                //{后缀名，    MIME类型}
                {".3gp", "video/3gpp"},
                {".apk", "application/vnd.android.package-archive"},
                {".asf", "video/x-ms-asf"},
                {".avi", "video/x-msvideo"},
                {".bin", "application/octet-stream"},
                {".bmp", "image/bmp"},
                {".c", "text/plain"},
                {".class", "application/octet-stream"},
                {".conf", "text/plain"},
                {".cpp", "text/plain"},
                {".doc", "application/msword"},
                {".exe", "application/octet-stream"},
                {".gif", "image/gif"},
                {".gtar", "application/x-gtar"},
                {".gz", "application/x-gzip"},
                {".h", "text/plain"},
                {".htm", "text/html"},
                {".html", "text/html"},
                {".jar", "application/java-archive"},
                {".java", "text/plain"},
                {".jpeg", "image/jpeg"},
                {".jpg", "image/jpeg"},
                {".js", "application/x-javascript"},
                {".log", "text/plain"},
                {".m3u", "audio/x-mpegurl"},
                {".m4a", "audio/mp4a-latm"},
                {".m4b", "audio/mp4a-latm"},
                {".m4p", "audio/mp4a-latm"},
                {".m4u", "video/vnd.mpegurl"},
                {".m4v", "video/x-m4v"},
                {".mov", "video/quicktime"},
                {".mp2", "audio/x-mpeg"},
                {".mp3", "audio/x-mpeg"},
                {".mp4", "video/mp4"},
                {".mpc", "application/vnd.mpohun.certificate"},
                {".mpe", "video/mpeg"},
                {".mpeg", "video/mpeg"},
                {".mpg", "video/mpeg"},
                {".mpg4", "video/mp4"},
                {".mpga", "audio/mpeg"},
                {".msg", "application/vnd.ms-outlook"},
                {".ogg", "audio/ogg"},
                {".pdf", "application/pdf"},
                {".png", "image/png"},
                {".pps", "application/vnd.ms-powerpoint"},
                {".ppt", "application/vnd.ms-powerpoint"},
                {".prop", "text/plain"},
                {".rar", "application/x-rar-compressed"},
                {".rc", "text/plain"},
                {".rmvb", "audio/x-pn-realaudio"},
                {".rtf", "application/rtf"},
                {".sh", "text/plain"},
                {".tar", "application/x-tar"},
                {".tgz", "application/x-compressed"},
                {".txt", "text/plain"},
                {".wav", "audio/x-wav"},
                {".wma", "audio/x-ms-wma"},
                {".wmv", "audio/x-ms-wmv"},
                {".wps", "application/vnd.ms-works"},
                //{".xml",    "text/xml"},
                {".xml", "text/plain"},
                {".z", "application/x-compress"},
                {".zip", "application/zip"},
                {"", "*/*"}
        };
        return MIME_MapTable;
    }


    /**
     * 缓存图片
     * @return
     */
    public static String getUserImagePath() {
        return getRootPath(IMAGE_PATH);
    }

}
