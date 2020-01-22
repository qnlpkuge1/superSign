package com.lmxdawn.api.admin.service;

import com.lmxdawn.api.admin.entity.Equipment;

import java.util.Map;

public interface UDIDService {

    Equipment getEquipment(String udid, Map<String, String> udidMap);

    String getMbconfigTemp(int index);

    String getMbconfigTemp(String type);

    String getPlistTemp(int index);

}
