package com.lmxdawn.api.admin.controller;

import com.github.pagehelper.PageInfo;
import com.lmxdawn.api.admin.annotation.AuthRuleAnnotation;
import com.lmxdawn.api.admin.entity.Developer;
import com.lmxdawn.api.admin.entity.DeveloperPingLog;
import com.lmxdawn.api.admin.req.AllocatDeveloperReq;
import com.lmxdawn.api.admin.req.appInfo.AppInfoQueryRequest;
import com.lmxdawn.api.admin.res.DeveloperDTO;
import com.lmxdawn.api.admin.res.PageSimpleResponse;
import com.lmxdawn.api.admin.service.DeveloperPingLogService;
import com.lmxdawn.api.admin.service.DeveloperService;
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
public class DeveloperController {

    @Autowired
    private DeveloperService developerService;

    @Autowired
    private DeveloperPingLogService developerPingLogService;

    @AuthRuleAnnotation("admin/developer")
    @GetMapping("/admin/developer/list")
    public BaseResponse list(@Valid AppInfoQueryRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultVOUtils.error(ResultEnum.PARAM_VERIFY_FALL, bindingResult.getFieldError().getDefaultMessage());
        }
        List<Developer> developers = developerService.list(request);

        List<DeveloperDTO> developerDTOs = developers.stream().map(d -> {
            DeveloperDTO developerDTO = new DeveloperDTO();
            BeanUtils.copyProperties(d, developerDTO);
            developerDTO.setPassword("");
            developerDTO.setBalance(developerService.getTicketLen(d));
            return developerDTO;
        }).collect(Collectors.toList());
        PageInfo<Developer> pageInfo = new PageInfo<>(developers);
        PageSimpleResponse<DeveloperDTO> pageSimpleResponse = new PageSimpleResponse<>();
        pageSimpleResponse.setTotal(pageInfo.getTotal());
        pageSimpleResponse.setList(developerDTOs);
        return ResultVOUtils.success(pageSimpleResponse);
    }

    /**
     * 新增
     *
     * @return
     */
    @AuthRuleAnnotation("admin/developer/create")
    @PostMapping("/admin/developer/add")
    public BaseResponse add(@RequestBody Developer developer, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultVOUtils.error(ResultEnum.PARAM_VERIFY_FALL, bindingResult.getFieldError().getDefaultMessage());
        }

        Developer saveDeveloper = developerService.add(developer);
        saveDeveloper.setPassword("");
        return ResultVOUtils.success(saveDeveloper);
    }

    /**
     * 新增
     *
     * @return
     */
    @AuthRuleAnnotation("admin/developer/allocat")
    @PostMapping("/admin/developer/allocat")
    public BaseResponse allocat(@RequestBody AllocatDeveloperReq req, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultVOUtils.error(ResultEnum.PARAM_VERIFY_FALL, bindingResult.getFieldError().getDefaultMessage());
        }

        developerService.allocats(req.getAppInfoId(), req.getDeveloperIds());
        return ResultVOUtils.success("success");
    }

    @AuthRuleAnnotation("admin/developer/resetInstall")
    @PostMapping("/admin/developer/resetInstall")
    public BaseResponse resetInstall(@RequestParam("id") Integer id, @RequestParam("installLimit") Integer installLimit) {
        developerService.resetInstall(id, installLimit);
        return ResultVOUtils.success("success");
    }

    @AuthRuleAnnotation("admin/developer")
    @GetMapping("/admin/developer/list/unallocat")
    public BaseResponse findUnallocat() {
        return ResultVOUtils.success(developerService.findUnallocat());
    }

    @AuthRuleAnnotation("admin/developer/pingLog")
    @GetMapping("/admin/developer/pingLog")
    public BaseResponse pingLogList(@Valid AppInfoQueryRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultVOUtils.error(ResultEnum.PARAM_VERIFY_FALL, bindingResult.getFieldError().getDefaultMessage());
        }
        List<DeveloperPingLog> developers = developerPingLogService.list(request);
        PageInfo<DeveloperPingLog> pageInfo = new PageInfo<>(developers);
        PageSimpleResponse<DeveloperPingLog> pageSimpleResponse = new PageSimpleResponse<>();
        pageSimpleResponse.setTotal(pageInfo.getTotal());
        pageSimpleResponse.setList(developers);
        return ResultVOUtils.success(pageSimpleResponse);
    }
}
