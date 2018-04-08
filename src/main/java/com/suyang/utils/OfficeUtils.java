package com.suyang.utils;

import com.suyang.enums.FileType;

import java.util.Arrays;
import java.util.List;

public class OfficeUtils {
    public static List<String> ExtsDocument = Arrays.asList
            (
                    ".doc", ".docx", ".docm",
                    ".dot", ".dotx", ".dotm",
                    ".odt", ".fodt", ".rtf", ".txt",
                    ".html", ".htm", ".mht",
                    ".pdf", ".djvu", ".fb2", ".epub", ".xps"
            );

    public static List<String> ExtsSpreadsheet = Arrays.asList
            (
                    ".xls", ".xlsx", ".xlsm",
                    ".xlt", ".xltx", ".xltm",
                    ".ods", ".fods", ".csv"
            );

    public static List<String> ExtsPresentation = Arrays.asList
            (
                    ".pps", ".ppsx", ".ppsm",
                    ".ppt", ".pptx", ".pptm",
                    ".pot", ".potx", ".potm",
                    ".odp", ".fodp"
            );

    public static FileType GetFileType(String fileName)
    {
        String ext = GetFileExtension(fileName).toLowerCase();

        if (ExtsDocument.contains(ext))
            return FileType.Text;

        if (ExtsSpreadsheet.contains(ext))
            return FileType.Spreadsheet;

        if (ExtsPresentation.contains(ext))
            return FileType.Presentation;

        return FileType.Text;
    }

    private static String GetFileExtension(String fileName) {
        if(fileName == null)
            return null;
        String fileExt = fileName.substring(fileName.lastIndexOf("."));
        return fileExt.toLowerCase();
    }
}
