package com.suyang.utils;

import com.suyang.domain.FileItem;
import com.suyang.settings.OfficeSettings;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class OfficeManager {

    private static Logger logger = LoggerFactory.getLogger(OfficeManager.class);

    @Autowired
    private OfficeSettings officeSettings;

    public OfficeManager(OfficeSettings officeSettings) {
        this.officeSettings = officeSettings;
    }

    public boolean checkFileSize(long size) {
        return size <= officeSettings.getMaxSize() && size >= 0;
    }

    public boolean checkExtname(String fileName) {
        String extName = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        if (getFileExts().contains(extName)) {
            return true;
        }
        return false;
    }

    public void save(InputStream inputStream, String fileName) {
        File dir = new File(officeSettings.getStore());
        if (!dir.exists()) {
            dir.mkdir();
        }
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(new File(dir, fileName));
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public void read(OutputStream outputStream, String fileName) {
        File dir = new File(officeSettings.getStore());
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(dir, fileName));
            IOUtils.copy(fileInputStream, outputStream);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public List<FileItem> getFiles() {
        List<FileItem> result = new ArrayList<>();
        File dir = new File(officeSettings.getStore());
        if (dir.exists()) {
            for (File f : dir.listFiles()) {
                result.add(convet(f));
            }
        }
        return result;
    }

    public FileItem findById(String id) {
        FileItem result = null;
        for (FileItem item : getFiles()) {
            if (item.getId().equals(id)) {
                result = item;
                break;
            }
        }
        return result;
    }

    public List<String> getViewedExts() {
        String exts = officeSettings.getViewDocs();
        return Arrays.asList(exts.split("\\|"));
    }

    public List<String> getEditedExts() {
        String exts = officeSettings.getEditDocs();
        return Arrays.asList(exts.split("\\|"));
    }

    public List<String> getConvertExts() {
        String exts = officeSettings.getConvertDocs();
        return Arrays.asList(exts.split("\\|"));
    }

    public List<String> getFileExts() {
        List<String> res = new ArrayList<>();
        res.addAll(getViewedExts());
        res.addAll(getEditedExts());
        res.addAll(getConvertExts());
        return res;
    }

    public OfficeSettings getOfficeSettings() {
        return this.officeSettings;
    }

    private static FileItem convet(File file) {
        FileItem result = new FileItem();
        result.setId(Md5Utils.getMD5fromString(file.getPath()));
        result.setFileName(file.getName());
        result.setFileSize(file.length());
        String fileType = "";
        if (file.getName().lastIndexOf(".") > 0) {
            fileType = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        }
        result.setFileType(fileType.toLowerCase());
        result.setPath(file.getPath());
        return result;
    }
}
