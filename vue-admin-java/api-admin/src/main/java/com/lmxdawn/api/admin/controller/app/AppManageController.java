package com.lmxdawn.api.admin.controller.app;

import com.github.pagehelper.PageInfo;
import com.lmxdawn.api.admin.annotation.AuthRuleAnnotation;
import com.lmxdawn.api.admin.entity.app.AppInfo;
import com.lmxdawn.api.admin.req.appInfo.AppInfoQueryRequest;
import com.lmxdawn.api.admin.res.AppInfoDTO;
import com.lmxdawn.api.admin.res.PageSimpleResponse;
import com.lmxdawn.api.admin.service.AppInfoService;
import com.lmxdawn.api.common.enums.ResultEnum;
import com.lmxdawn.api.common.res.BaseResponse;
import com.lmxdawn.api.common.util.ResultVOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AppManageController {

    @Autowired
    private AppInfoService appInfoService;

    @AuthRuleAnnotation("admin/appInfo")
    @GetMapping("/admin/appInfo/list")
    public BaseResponse list(@Valid AppInfoQueryRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultVOUtils.error(ResultEnum.PARAM_VERIFY_FALL, bindingResult.getFieldError().getDefaultMessage());
        }

        List<AppInfo> appInfos = appInfoService.list(request);
        List<AppInfoDTO> appInfoDTOS = appInfos.stream().map(appInfo -> {
            AppInfoDTO appInfoDTO = new AppInfoDTO();
            BeanUtils.copyProperties(appInfo, appInfoDTO);
            appInfoDTO.setBalance(appInfoService.getAppTicketLen(appInfo));
            return appInfoDTO;
        }).collect(Collectors.toList());

        PageInfo<AppInfo> pageInfo = new PageInfo<>(appInfos);
        PageSimpleResponse<AppInfoDTO> pageSimpleResponse = new PageSimpleResponse<>();
        pageSimpleResponse.setTotal(pageInfo.getTotal());
        pageSimpleResponse.setList(appInfoDTOS);
        return ResultVOUtils.success(pageSimpleResponse);
    }


    @AuthRuleAnnotation("admin/appInfo")
    @GetMapping("/admin/appInfo/allList")
    public BaseResponse allList(@Valid AppInfoQueryRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultVOUtils.error(ResultEnum.PARAM_VERIFY_FALL, bindingResult.getFieldError().getDefaultMessage());
        }

        List<AppInfo> appInfos = appInfoService.allList(request);
        List<AppInfoDTO> appInfoDTOS = appInfos.stream().map(appInfo -> {
            AppInfoDTO appInfoDTO = new AppInfoDTO();
            BeanUtils.copyProperties(appInfo, appInfoDTO);
            appInfoDTO.setBalance(appInfoService.getAppTicketLen(appInfo));
            return appInfoDTO;
        }).collect(Collectors.toList());

        PageInfo<AppInfo> pageInfo = new PageInfo<>(appInfos);
        PageSimpleResponse<AppInfoDTO> pageSimpleResponse = new PageSimpleResponse<>();
        pageSimpleResponse.setTotal(pageInfo.getTotal());
        pageSimpleResponse.setList(appInfoDTOS);
        return ResultVOUtils.success(pageSimpleResponse);
    }


    @AuthRuleAnnotation("admin/appInfo/update")
    @PostMapping("/admin/appInfo/installLimit")
    public BaseResponse installLimit(@RequestParam("id") Integer id, @RequestParam("installLimit") Integer installLimit) {
        AppInfo appInfo = appInfoService.setInstallLimit(id, installLimit);
        return ResultVOUtils.success(appInfo);
    }

    @AuthRuleAnnotation("admin/appInfo/enable")
    @PostMapping("/admin/appInfo/enable")
    public BaseResponse enableAppInfo(@RequestParam("id") Integer id) {
        return ResultVOUtils.success(appInfoService.enableAppInfo(id));
    }

    @AuthRuleAnnotation("admin/appInfo/enable")
    @PostMapping("/admin/appInfo/disable")
    public BaseResponse disableAppInfo(@RequestParam("id") Integer id) {
        return ResultVOUtils.success(appInfoService.disableAppInfo(id));
    }

    @AuthRuleAnnotation("admin/appInfo")
    @GetMapping(value = "/admin/appInfo/url")
    public BaseResponse getUrl(@RequestParam("id") Integer id) {
        return ResultVOUtils.success(appInfoService.getUrl(id));
    }
}
