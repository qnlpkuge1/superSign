package com.lmxdawn.api.admin.controller.app;

import com.github.pagehelper.PageInfo;
import com.lmxdawn.api.admin.annotation.AuthRuleAnnotation;
import com.lmxdawn.api.admin.entity.InstallLog;
import com.lmxdawn.api.admin.req.appInfo.AppInfoQueryRequest;
import com.lmxdawn.api.admin.res.PageSimpleResponse;
import com.lmxdawn.api.admin.service.InstallLogService;
import com.lmxdawn.api.common.enums.ResultEnum;
import com.lmxdawn.api.common.res.BaseResponse;
import com.lmxdawn.api.common.util.ResultVOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class InstallLogController {

    @Autowired
    private InstallLogService installLogService;

    @AuthRuleAnnotation("admin/installLog")
    @GetMapping("/admin/installLog/list")
    public BaseResponse list(@Valid AppInfoQueryRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultVOUtils.error(ResultEnum.PARAM_VERIFY_FALL, bindingResult.getFieldError().getDefaultMessage());
        }
        List<InstallLog> installLogs = installLogService.list(request);
        PageInfo<InstallLog> pageInfo = new PageInfo<>(installLogs);
        PageSimpleResponse<InstallLog> pageSimpleResponse = new PageSimpleResponse<>();
        pageSimpleResponse.setTotal(pageInfo.getTotal());
        pageSimpleResponse.setList(installLogs);
        return ResultVOUtils.success(pageSimpleResponse);
    }
}
