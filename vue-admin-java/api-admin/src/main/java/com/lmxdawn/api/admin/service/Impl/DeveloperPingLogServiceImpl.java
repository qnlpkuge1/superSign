package com.lmxdawn.api.admin.service.Impl;

import com.github.pagehelper.PageHelper;
import com.lmxdawn.api.admin.dao.DeveloperPingLogDao;
import com.lmxdawn.api.admin.entity.Developer;
import com.lmxdawn.api.admin.entity.DeveloperPingLog;
import com.lmxdawn.api.admin.req.appInfo.AppInfoQueryRequest;
import com.lmxdawn.api.admin.service.DeveloperPingLogService;
import com.lmxdawn.api.admin.service.DeveloperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class DeveloperPingLogServiceImpl implements DeveloperPingLogService {

    @Autowired
    private DeveloperPingLogDao developerPingLogDao;

    @Autowired
    private DeveloperService developerService;


    @Value("${app.developerPing}")
    private String developerPing;

    @Override
    public void pingAll() {
        List<Developer> developers = developerService.findAll();

        developers.forEach(developer -> {
            String cmd = String.format("ruby %s %s %s", developerPing, developer.getUsername(), developer.getPassword());
            System.out.println(cmd);
            String result = exeCmd(cmd);
            DeveloperPingLog developerPingLog = new DeveloperPingLog();
            developerPingLog.setCreateTime(new Date());
            developerPingLog.setDeveloperId(developer.getId());
            developerPingLog.setDeveloperName(developer.getUsername());
            if (!StringUtils.isEmpty(result)) {
                developerPingLog.setResult(result.substring(0, result.length() > 100 ? 100 : result.length()));
                developerPingLog.setStatus(result.contains("Cert id"));
            }
            developerPingLogDao.insert(developerPingLog);
        });
    }

    @Override
    public List<DeveloperPingLog> list(AppInfoQueryRequest request) {
        if (request == null) {
            return Collections.emptyList();
        }
        int offset = (request.getPage() - 1) * request.getLimit();
        PageHelper.offsetPage(offset, request.getLimit());
        return developerPingLogDao.list(request);
    }

    private String exeCmd(String commandStr) {
        BufferedReader br = null;
        try {
            Process p = Runtime.getRuntime().exec(commandStr);
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }
}
