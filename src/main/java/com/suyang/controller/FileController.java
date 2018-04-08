package com.suyang.controller;

import com.suyang.domain.FileItem;
import com.suyang.exceptions.APIException;
import com.suyang.exceptions.APIExceptionType;
import com.suyang.utils.OfficeManager;
import com.suyang.utils.OfficeUtils;
import com.suyang.utils.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/file/**")
public class FileController {

    private static Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private OfficeManager officeManager;

    @RequestMapping("/list")
    @ResponseBody
    public List<FileItem> getFiles() {
        return officeManager.getFiles();
    }

    @RequestMapping("upload")
    @ResponseBody
    public void upload(@RequestParam("file") MultipartFile file) throws APIException, IOException {
        if(!file.isEmpty()){
            if(!officeManager.checkExtname(file.getOriginalFilename())){
                throw new APIException(APIExceptionType.FileExtError);
            }
            if(!officeManager.checkFileSize(file.getSize())){
                throw new APIException(APIExceptionType.FileTooLarge);
            }
            officeManager.save(file.getInputStream(), file.getOriginalFilename());
        }
    }

    @RequestMapping("download")
    public void download(String id, HttpServletResponse response) throws IOException {
        FileItem fileItem = officeManager.findById(id);
        if(fileItem != null){
            response.setHeader("Content-Disposition", "attachment; filename=" + fileItem.getFileName());
            officeManager.read(response.getOutputStream(), fileItem.getFileName());
        }
    }

    @RequestMapping("edit")
    public String onlineEdit(Map<String, Object> map,
                             @RequestParam(name = "id", required = true) String id,
                             @RequestParam(name = "edit", required = false, defaultValue = "false") boolean edit,
                             String userId,
                             String userName,
                             HttpServletRequest request) throws UnsupportedEncodingException {
        FileItem file = officeManager.findById(id);
        if (file != null) {
            map.put("api", officeManager.getOfficeSettings().getApiUrl());
            map.put("fileName", file.getFileName());
            map.put("fileType", file.getFileType());
            map.put("docType", OfficeUtils.GetFileType(file.getFileName()));
            map.put("downloadUrl", RequestUtils.getPrefix(request) + "/api/file/download?id=" + file.getId());
            String callbackQuery = "?type=track&fileName=" + URLEncoder.encode(file.getFileName(), "UTF-8") + "&userId" + URLEncoder.encode(userId, "UTF-8") + "&userName=" + URLEncoder.encode(userName, "UTF-8");
            map.put("configCallback", RequestUtils.getPrefix(request) + "/api/online/callback" + callbackQuery);
            map.put("callback", RequestUtils.getPrefix(request) + "/api/online/callback");
            map.put("id", file.getId());
            map.put("userId", userId);
            map.put("userName", userName);
            map.put("mode", edit ? "edit" : "view");
            return "/online";
        }
        return "";
    }
}
