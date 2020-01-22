package com.goosun.apiconsumer.service.impl;

import com.goosun.apiconsumer.service.UDIDService;
import com.lmxdawn.api.admin.entity.Equipment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

@Service
public class UDIDServiceImpl implements UDIDService {

    @Value("${app.sourcePath}")
    private String sourcePath;

    @Value("${app.mbconfigPath}")
    private String mbconfigPath;

    @Override
    @Cacheable(value = "equipment", key = "#p0")
    public Equipment getEquipment(String udid, Map<String, String> udidMap) {
        if (udid == null) {
            return null;
        }
        Equipment equipment = new Equipment();
        equipment.setUdid(udid);
        equipment.setProduct(udidMap.get("PRODUCT"));
        return equipment;
    }

    @Override
    @Cacheable(value = "mbconfig", key = "#p0")
    public String getMbconfigTemp(int index) {
        StringBuilder context = new StringBuilder();
        try (FileReader reader = new FileReader(sourcePath + "app/udid.mobileconfig");
             BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                context.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return context.toString();
    }

    @Override
    @Cacheable(value = "mbconfig", key = "#p0")
    public String getMbconfigTemp(String type) {
        StringBuilder context = new StringBuilder();
        try (FileReader reader = new FileReader(mbconfigPath + type + ".mobileconfig");
             BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                context.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return context.toString();
    }


    @Override
    @Cacheable(value = "plist", key = "#p0")
    public String getPlistTemp(int index) {
        StringBuilder context = new StringBuilder();
        try (FileReader reader = new FileReader(sourcePath + "app/dist.plist");
             BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                context.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return context.toString();
    }

}
