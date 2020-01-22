package com.lmxdawn.api.admin.controller;

import com.lmxdawn.api.admin.entity.Developer;
import com.lmxdawn.api.admin.entity.Equipment;
import com.lmxdawn.api.admin.entity.app.AppInfo;
import com.lmxdawn.api.admin.service.AppInfoService;
import com.lmxdawn.api.admin.service.DeveloperService;
import com.lmxdawn.api.admin.service.UDIDService;
import com.lmxdawn.api.admin.util.BeanToXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

@Controller
public class UDIDController {

    private Logger log = LoggerFactory.getLogger(UDIDController.class);
    @Autowired
    private UDIDService udidService;
    @Autowired
    private AppInfoService appInfoService;
    @Autowired
    private DeveloperService developerService;

    @Value("${app.appServer}")
    private String appServer;


    @PostMapping("/udid/{id}/install")
    public String udid(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") Integer id) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        //获取HTTP请求的输入流
        InputStream is = request.getInputStream();
        //已HTTP请求输入流建立一个BufferedReader对象
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();

        //读取HTTP请求内容
        String buffer = null;
        while ((buffer = br.readLine()) != null) {
            sb.append(buffer);
        }
        String content = sb.toString().substring(sb.toString().indexOf("<?xml"), sb.toString().indexOf("</plist>") + 8);

        try {
            Map<String, String> udidMap = BeanToXml.xmlToMap(content);
            if (udidMap != null) {
                Equipment equipment = udidService.getEquipment(udidMap.get("UDID"), udidMap);
                if (!appInfoService.inTranscocdQueue(id, equipment)) {
                    AppInfo appInfo = appInfoService.getAppInfo(id);
                    Developer developer = developerService.consume(appInfo, equipment);
                    appInfoService.sendJob(appInfo, equipment, developer);
                }
                String url = String.format("%s/app/%s?udid=%s", appServer, id, equipment.getUdid());
                System.out.println(url);
                response.setHeader("Location", url);
                response.setStatus(301);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("获取udid失败");
        }

        return "ping";
    }


}
