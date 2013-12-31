package xmu.swordbearer.smallraccoon.cache;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 缓存工具类
 *
 * @author swordbearer
 */
public class CacheUtil {
    /**
     * 保存缓存文件：建议缓存的数据是可序列化的，便于读取时进行反序列化操作
     *
     * @param context
     * @param key     缓存文件名
     * @param data    可以是Serializable对象
     * @return
     */
    public static boolean saveCache(Context context, String key, Object data) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        File file = context.getFileStreamPath(key);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = context.openFileOutput(key, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                oos.close();
                fos.close();
            } catch (IOException e) {
            }
        }
        return false;
    }

    /**
     * 读取缓存文件
     *
     * @param context
     * @param key     文件名
     * @return 返回Object对象:如果返回的实体可以被反序列化，可以使用Serializable进行类型转换
     */
    public static Object readCache(Context context, String key) {
        File data = context.getFileStreamPath(key);
        if (!data.exists()) {
            return null;
        }
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(key);
            ois = new ObjectInputStream(fis);
            try {
                return ois.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除缓存
     *
     * @param context
     * @param key     缓存文件名
     */
    public static void deleteCache(Context context, String key) {
        File cache = context.getFileStreamPath(key);
        if (cache.exists()) {
            cache.delete();
        }
    }

    /**
     * 检测是否存在缓存
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean isExistCache(Context context, String key) {
        File cache = context.getFileStreamPath(key);
        return cache.exists();
    }

    /****************** Manage cache date in SDCard ******************/

    /**
     * 将文件流缓存在SD卡中
     *
     * @param path     缓存路径
     * @param cacheKey 缓存文件名
     * @param inStream 文件流
     * @return
     */
    public static boolean saveCacheStream2SD(String path, String cacheKey,
                                             InputStream inStream) {
        if (!isSDCard()) {
            return false;
        }
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        File cacheFile = new File(path + File.separator + cacheKey);
        try {
            if (!cacheFile.exists()) {
                cacheFile.createNewFile();
            }
            fos = new FileOutputStream(cacheFile);
            bis = new BufferedInputStream(inStream);
            byte[] buffer = new byte[2048];
            int len = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                fos.close();
                bis.close();
                inStream.close();
            } catch (IOException e) {
            }
        }
        return true;
    }

    public static boolean saveCacheSerializable2SD(String path,
                                                   String cacheKey, Object data) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        File file = new File(path + File.separator + cacheKey);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                oos.close();
                fos.close();
            } catch (IOException e) {
            }
        }
        return false;
    }

    public static Object readCacheSerializableInSD(String path, String cacheKey) {
        if (!isSDCard()) {
            return null;
        }
        File data = new File(path + File.separator + cacheKey);
        if (!data.exists()) {
            return null;
        }
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(data);
            ois = new ObjectInputStream(fis);
            try {
                return ois.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从SDcard中读取byte[]数据
     *
     * @param path     缓存路径
     * @param cacheKey 缓存文件名
     * @return byte数组
     */
    public static byte[] readCacheBytesInSD(String path, String cacheKey) {
        if (!isSDCard()) {
            return null;
        }
        File cacheFile = new File(path + File.separator + cacheKey);

        if (!cacheFile.exists()) {
            return null;
        }
        FileInputStream fis = null;
        byte[] buffer = null;
        try {
            long dataLen = cacheFile.length();
            fis = new FileInputStream(cacheFile);
            buffer = new byte[(int) dataLen];
            int offset = 0;
            int numRead = 0;
            while (offset < buffer.length
                    && (numRead = fis.read(buffer, offset, buffer.length
                    - offset)) >= 0) {
                offset += numRead;
            }
            // 确保所有数据均被读取
            if (offset != buffer.length) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
        return buffer;
    }

    public static InputStream readCacheStreamInSD(String path, String cacheKey) {
        if (!isSDCard()) {
            return null;
        }
        File cacheFile = new File(path + File.separator + cacheKey);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(cacheFile);
        } catch (FileNotFoundException e) {
            return null;
        }
        return fis;
    }

    /**
     * 删除SD卡中的缓存文件
     *
     * @param path
     * @param cacheKey
     */
    public static void deleteCacheInSD(String path, String cacheKey) {
        File cacheFile = new File(path + File.separator + cacheKey);
        if (cacheFile.exists()) {
            cacheFile.delete();
        }
    }

    /**
     * 检测在SDCard中是否有该缓存文件
     *
     * @param path
     * @param cacheKey
     * @return
     */
    public static boolean isExistCacheInSD(String path, String cacheKey) {
        File cacheFile = new File(path + File.separator + cacheKey);
        return cacheFile.exists();
    }

    /**
     * 检测手机是否有SDCard
     *
     * @return
     */
    public static boolean isSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

}
