package com.lmxdawn.api.admin.controller;

import com.lmxdawn.api.admin.dto.SignProcess;
import com.lmxdawn.api.admin.entity.Developer;
import com.lmxdawn.api.admin.entity.Equipment;
import com.lmxdawn.api.admin.entity.app.AppInfo;
import com.lmxdawn.api.admin.service.AppInfoService;
import com.lmxdawn.api.admin.service.DeveloperService;
import com.lmxdawn.api.admin.service.UDIDService;
import com.lmxdawn.api.common.res.BaseResponse;
import com.lmxdawn.api.common.util.ResultVOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class AppController {


    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private UDIDService udidService;

    @Autowired
    private DeveloperService developerService;

    @Value("${app.appServer}")
    private String appServer;

    @GetMapping("/app/{id}")
    public ModelAndView app(@PathVariable("id") Integer id, @RequestParam(value = "udid", required = false) String udid) {
        AppInfo appInfo = appInfoService.getAppInfo(id);
        ModelAndView mv = new ModelAndView();
        if (appInfo == null) {
            mv.setViewName("404.html");
            return mv;
        }

        mv.setViewName("app.html");
        mv.addObject("appInfo", appInfo);
        if (!StringUtils.isEmpty(udid)) {
            mv.addObject("udid", udid);
            mv.addObject("plist", String.format("itms-services://?action=download-manifest&url=%s/app/%s/%s.plist", appServer, id, udid));
        }
        return mv;
    }

    @GetMapping("/appPre/{id}")
    public ModelAndView appPre(@PathVariable("id") Integer id, @RequestParam(value = "udid", required = false) String udid) {
        AppInfo appInfo = appInfoService.getAppInfo(id);
        ModelAndView mv = new ModelAndView();
        if (appInfo == null) {
            mv.setViewName("404.html");
            return mv;
        }

        mv.setViewName("appPre.html");
        mv.addObject("appInfo", appInfo);
        if (!StringUtils.isEmpty(udid)) {
            mv.addObject("udid", udid);
            mv.addObject("plist", String.format("itms-services://?action=download-manifest&url=%s/app/%s/%s.plist", appServer, id, udid));
        }
        return mv;
    }

    @ResponseBody
    @GetMapping(value = "/app/init/{id}/{udid}")
    public BaseResponse initSign(@PathVariable("id") Integer id, @PathVariable("udid") String udid) {
        AppInfo appInfo = appInfoService.getAppInfo(id);
        Equipment equipment = udidService.getEquipment(udid, null);
        if (appInfo == null || equipment == null) {
            return ResultVOUtils.error(20010, "参数不匹配");
        }
        if (!appInfo.getStatus().equals(1)) {
            return ResultVOUtils.error(20021, "应用未上架");
        }

        if (appInfoService.inTranscocdQueue(id, equipment)) {
            return ResultVOUtils.success(new SignProcess(3));
        }

        Developer developer = developerService.consume(appInfo, equipment);
        if (developer == null) {
            return ResultVOUtils.error(20011, "无可安装设备数量");
        }

        appInfoService.sendJob(appInfo, equipment, developer);
        return ResultVOUtils.success(new SignProcess(3));
    }


    @ResponseBody
    @GetMapping(value = "/app/process/{id}/{udid}")
    public BaseResponse signProcess(@PathVariable("id") Integer id, @PathVariable("udid") String udid) {
        return ResultVOUtils.success(appInfoService.getSignProcess(id, udid));
    }

}
