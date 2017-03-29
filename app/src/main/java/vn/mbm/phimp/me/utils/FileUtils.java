package vn.mbm.phimp.me.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import android.content.Context;
import android.text.format.Formatter;

/**
 * Created by rohanagarwal94 on 29/3/17.
 */

public class FileUtils {
    private FileUtils() {

    }

    public static String getHash(final File file) throws NoSuchAlgorithmException, IOException {
        return md5(file) + "_" + sha1(file);
    }

    public static String md5(final File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        FileInputStream fis = new FileInputStream(file);
        byte[] dataBytes = new byte[1024];

        int nread = 0;

        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        }

        //convert the byte to hex format
        StringBuffer sb = new StringBuffer("");
        for (byte mdbyte : md.digest()) {
            sb.append(Integer.toString((mdbyte & 0xff) + 0x100, 16).substring(1));
        }
        fis.close();
        md.reset();
        return sb.toString();
    }

    public static String sha1(final File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        FileInputStream fis = new FileInputStream(file);
        byte[] dataBytes = new byte[1024];

        int nread = 0;

        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        }

        //convert the byte to hex format
        StringBuffer sb = new StringBuffer("");
        for (byte mdbyte : md.digest()) {
            sb.append(Integer.toString((mdbyte & 0xff) + 0x100, 16).substring(1));
        }
        fis.close();
        md.reset();
        return sb.toString();
    }

    public static long getFilesSize(List<File> files) {
        long byteSize = 0;
        for (File file : files) {
            byteSize += file.length();
        }
        return byteSize;
    }

    public static String getFormattedSize(Context context, long size) {
        return Formatter.formatFileSize(context, size);
    }

    public static String getFormattedFilesSize(Context context, List<File> files) {
        return Formatter.formatFileSize(context, getFilesSize(files));
    }
}
