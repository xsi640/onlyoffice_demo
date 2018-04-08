package com.suyang.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Md5Utils {
    public static String getMD5fromFile(String path) {
        String result = "";
        if (isFile(path))
            return result;

        FileInputStream stream = null;
        try {
            stream = new FileInputStream(path);
            result = org.apache.commons.codec.digest.DigestUtils.md5Hex(stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static String getMD5fromString(String text){
        return org.apache.commons.codec.digest.DigestUtils.md5Hex(text);
    }

    public static boolean isFile(String path) {
        File f = new File(path);
        return f.isFile();
    }
}
