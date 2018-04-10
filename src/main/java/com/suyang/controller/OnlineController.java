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
    public void callback(String type,
                        String userId,
                        String userName,
                        HttpServletRequest request,
                        HttpServletResponse response) throws ServletException, IOException {
        if (type == null || !"callback".equals(type)) {
            request.getRequestDispatcher("/").forward(request, response);
            return;
        }
        PrintWriter writer = response.getWriter();

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
