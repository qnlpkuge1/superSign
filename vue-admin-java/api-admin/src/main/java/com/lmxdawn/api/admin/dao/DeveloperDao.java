package com.lmxdawn.api.admin.dao;

import com.lmxdawn.api.admin.entity.Developer;
import com.lmxdawn.api.admin.req.appInfo.AppInfoQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeveloperDao {

    Developer get(Integer id);

    boolean insert(Developer developer);

    List<Developer> list(AppInfoQueryRequest appInfoQueryRequest);

    boolean updateAllocat(Developer developer);

    List<Developer> findByIds(@Param("ids") List<Integer> ids);

    List<Developer> findUnallocat();

    void resetInstall(Integer id, Integer installLimit);

    List<Developer> findAll();
}
