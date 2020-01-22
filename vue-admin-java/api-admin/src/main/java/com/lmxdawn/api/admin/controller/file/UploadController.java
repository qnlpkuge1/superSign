package com.lmxdawn.api.admin.controller.file;

import ch.qos.logback.core.util.FileUtil;
import com.lmxdawn.api.admin.annotation.AuthRuleAnnotation;
import com.lmxdawn.api.admin.entity.app.AppInfo;
import com.lmxdawn.api.admin.service.AppInfoService;
import com.lmxdawn.api.admin.service.FileStroeService;
import com.lmxdawn.api.admin.util.ThreadVariable;
import com.lmxdawn.api.common.res.BaseResponse;
import com.lmxdawn.api.common.util.ResultVOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 上传相关
 */
@RestController
@RequestMapping("/admin/file/upload")
public class UploadController {

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private FileStroeService fileStroeService;

    @Value("${app.tmpPath}")
    private String tmpPath;

    /**
     * 上传的token
     *
     * @return
     */
    @GetMapping("/qiuNiuUpToken")
    public BaseResponse qiuNiuUpToken() {

        // TODO 这里接入 七牛云 的SDK 就可以了
        Map<String, Object> res = new HashMap<>();
        res.put("uploadUrl", "/admin/file/upload/createFile"); // 这里可以直接设置成七牛云的上传 url，不用服务端这边去post请求七牛云的上传接口
        res.put("upToken", "xxxxxxx");

        return ResultVOUtils.success(res);
    }


    /**
     * 上传文件（如果是接入的第三方的建议这个接口废弃）
     *
     * @return
     */
    @AuthRuleAnnotation("")
    @PostMapping("/icon")
    public BaseResponse uploadIcon(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResultVOUtils.error(40051, "图片上传失败，请选择文件");
        }

        String fileName = file.getOriginalFilename();
        File dest = new File(tmpPath + System.currentTimeMillis() + File.separator + fileName);
        try {
            if (!dest.exists()) {
                FileUtil.createMissingParentDirectories(dest);
            }
            file.transferTo(dest);
        } catch (IOException e) {

        }

        String url = fileStroeService.uploadFile("icon/" + System.currentTimeMillis() + "/" + fileName, dest.getPath());
        return ResultVOUtils.success(url);
    }

    /**
     * 上传文件
     *
     * @return
     */
    @AuthRuleAnnotation("")
    @PostMapping("/createFile")
    public BaseResponse createFile(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResultVOUtils.error(40051, "上传失败，请选择文件");
        }

        String fileName = file.getOriginalFilename();
        File dest = new File(tmpPath + System.currentTimeMillis() + File.separator + fileName);
        try {
            if (!dest.exists()) {
                FileUtil.createMissingParentDirectories(dest);
            }
            file.transferTo(dest);
        } catch (IOException e) {

        }

        AppInfo appInfo = appInfoService.add(dest.getPath());

        Map<String, Object> res = new HashMap<>();
        res.put("appInfo", appInfo);
        return ResultVOUtils.success(res);
    }


    /**
     * 上传文件
     *
     * @return
     */
    @AuthRuleAnnotation("")
    @PostMapping("{id}/updateFile")
    public BaseResponse updateFile(@PathVariable("id") Integer id, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResultVOUtils.error(40051, "上传失败，请选择文件");
        }

        AppInfo appInfo = appInfoService.getAppInfo(id);
        if (appInfo == null) {
            return ResultVOUtils.error(40011, "没有找到应用信息");
        }
        if(!appInfo.getOwnerId().equals(ThreadVariable.getUserId())){
            return ResultVOUtils.error(40301, "没有修改权限");
        }

        String fileName = file.getOriginalFilename();
        File dest = new File(tmpPath + System.currentTimeMillis() + File.separator + fileName);
        try {
            if (!dest.exists()) {
                FileUtil.createMissingParentDirectories(dest);
            }
            file.transferTo(dest);
        } catch (IOException e) {

        }

        appInfo = appInfoService.update(appInfo, dest.getPath());
        Map<String, Object> res = new HashMap<>();
        res.put("appInfo", appInfo);
        return ResultVOUtils.success(res);
    }

}
