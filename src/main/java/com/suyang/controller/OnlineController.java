package com.suyang.controller;

import com.suyang.domain.FileItem;
import com.suyang.settings.OfficeSettings;
import com.suyang.utils.OfficeManager;
import com.suyang.utils.RequestUtils;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@RequestMapping("/api/online/**")
@Controller
public class OnlineController {

    private static Logger logger = LoggerFactory.getLogger(OnlineController.class);

    @Autowired
    private OfficeManager officeManager;

    @RequestMapping("callback")
    public void callback(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("type");

        if (action == null) {
            request.getRequestDispatcher("/").forward(request, response);
            return;
        }
        System.out.println(action);

        PrintWriter writer = response.getWriter();

        switch (action.toLowerCase())
        {
            case "upload":
                Upload(request, response, writer);
                break;
            case "convert":
                Convert(request, response, writer);
                break;
            case "track":
                Track(request, response, writer);
                break;
        }
    }

    private void Upload(HttpServletRequest request, HttpServletResponse response, PrintWriter writer) throws IOException, ServletException {
        response.setContentType("text/plain");
        Part httpPostedFile = request.getPart("file");
        String fileName = "";
        for (String content : httpPostedFile.getHeader("content-disposition").split(";"))
        {
            if (content.trim().startsWith("filename"))
            {
                fileName = content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }

        long curSize = httpPostedFile.getSize();
        if(!officeManager.checkFileSize(curSize)){
            writer.write("{ \"error\": \"File size is incorrect\"}");
            return;
        }
        if(!officeManager.checkExtname(fileName)){
            writer.write("{ \"error\": \"File type is not supported\"}");
            return;
        }

        officeManager.save(httpPostedFile.getInputStream(), fileName);
        writer.write("{ \"filename\": \"" + fileName + "\"}");
    }

    private void Convert(HttpServletRequest request, HttpServletResponse response, PrintWriter writer) {
        response.setContentType("text/plain");

        try
        {
            String fileName = request.getParameter("filename");
//            String fileUri = RequestUtils.getPrefix(request) + "/api/file/download?id="+
//            String fileExt = FileUtility.GetFileExtension(fileName);
//            FileType fileType = FileUtility.GetFileType(fileName);
//            String internalFileExt = DocumentManager.GetInternalExtension(fileType);
//
//            if (DocumentManager.GetConvertExts().contains(fileExt))
//            {
//                String key = ServiceConverter.GenerateRevisionId(fileUri);
//
//                String newFileUri = ServiceConverter.GetConvertedUri(fileUri, fileExt, internalFileExt, key, true);
//
//                if (newFileUri == "")
//                {
//                    writer.write("{ \"step\" : \"0\", \"filename\" : \"" + fileName + "\"}");
//                    return;
//                }
//
//                String correctName = DocumentManager.GetCorrectName(FileUtility.GetFileNameWithoutExtension(fileName) + internalFileExt);
//
//                URL url = new URL(newFileUri);
//                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
//                InputStream stream = connection.getInputStream();
//
//                if (stream == null)
//                {
//                    throw new Exception("Stream is null");
//                }
//
//                File convertedFile = new File(DocumentManager.StoragePath(correctName, null));
//                try (FileOutputStream out = new FileOutputStream(convertedFile))
//                {
//                    int read;
//                    final byte[] bytes = new byte[1024];
//                    while ((read = stream.read(bytes)) != -1)
//                    {
//                        out.write(bytes, 0, read);
//                    }
//
//                    out.flush();
//                }
//
//                connection.disconnect();
//
//                //remove source file ?
//                //File sourceFile = new File(DocumentManager.StoragePath(fileName, null));
//                //sourceFile.delete();
//
//                fileName = correctName;
//            }

            writer.write("{ \"filename\" : \"" + fileName + "\"}");

        }
        catch (Exception ex)
        {
            writer.write("{ \"error\": \"" + ex.getMessage() + "\"}");
        }
    }

    private void Track(HttpServletRequest request, HttpServletResponse response, PrintWriter writer) {

        String userAddress = request.getParameter("userAddress");
        String fileName = request.getParameter("fileName");
        String body = "";

        try
        {
            Scanner scanner = new Scanner(request.getInputStream());
            scanner.useDelimiter("\\A");
            body = scanner.hasNext() ? scanner.next() : "";
            scanner.close();
        }
        catch (Exception ex)
        {
            writer.write("get request.getInputStream error:" + ex.getMessage());
            return;
        }

        if (body.isEmpty())
        {
            writer.write("empty request.getInputStream");
            return;
        }

        JSONParser parser = new JSONParser();
        JSONObject jsonObj;

        try
        {
            Object obj = parser.parse(body);
            jsonObj = (JSONObject) obj;
        }
        catch (Exception ex)
        {
            writer.write("JSONParser.parse error:" + ex.getMessage());
            return;
        }

        long status = (long) jsonObj.get("status");

        int saved = 0;
        if(status == 2 || status == 3)//MustSave, Corrupted
        {
            String downloadUri = (String) jsonObj.get("url");
            String id = (String) jsonObj.get("key");
            String[] userIds = (String[]) jsonObj.get("users");

            FileItem file = officeManager.findById(id);

            try
            {
                URL url = new URL(downloadUri);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                InputStream stream = connection.getInputStream();

                if (stream == null)
                {
                    throw new Exception("Stream is null");
                }

                officeManager.save(stream, file.getFileName());

                connection.disconnect();

            }
            catch (Exception ex)
            {
                saved = 1;
            }
        }

        writer.write("{\"error\":" + saved + "}");
    }
}
