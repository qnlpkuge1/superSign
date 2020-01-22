package com.lmxdawn.api.admin.controller.app;

import com.github.pagehelper.PageInfo;
import com.lmxdawn.api.admin.annotation.AuthRuleAnnotation;
import com.lmxdawn.api.admin.entity.PkgApp;
import com.lmxdawn.api.admin.req.appInfo.AppInfoQueryRequest;
import com.lmxdawn.api.admin.res.PageSimpleResponse;
import com.lmxdawn.api.admin.service.PkgAppService;
import com.lmxdawn.api.common.enums.ResultEnum;
import com.lmxdawn.api.common.res.BaseResponse;
import com.lmxdawn.api.common.util.ResultVOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PkgAppManageContoller {

    @Autowired
    private PkgAppService pkgAppService;

    /**
     * 新增
     *
     * @return
     */
    @AuthRuleAnnotation("admin/pkgApp/add")
    @PostMapping("/admin/pkgApp/add")
    public BaseResponse add(@RequestBody PkgApp pkgApp, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultVOUtils.error(ResultEnum.PARAM_VERIFY_FALL, bindingResult.getFieldError().getDefaultMessage());
        }

        PkgApp savedPkgApp = pkgAppService.add(pkgApp);
        savedPkgApp.setRemovePassword("");
        return ResultVOUtils.success(savedPkgApp);
    }

    @AuthRuleAnnotation("admin/pkgApp")
    @GetMapping("/admin/pkgApp/list")
    public BaseResponse list(@Valid AppInfoQueryRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultVOUtils.error(ResultEnum.PARAM_VERIFY_FALL, bindingResult.getFieldError().getDefaultMessage());
        }
        List<PkgApp> pkgApps = pkgAppService.ownerList(request);

        pkgApps = pkgApps.stream().map(d -> {
            d.setRemovePassword("");
            return d;
        }).collect(Collectors.toList());
        PageInfo<PkgApp> pageInfo = new PageInfo<>(pkgApps);
        PageSimpleResponse<PkgApp> pageSimpleResponse = new PageSimpleResponse<>();
        pageSimpleResponse.setTotal(pageInfo.getTotal());
        pageSimpleResponse.setList(pkgApps);
        return ResultVOUtils.success(pageSimpleResponse);
    }

    @AuthRuleAnnotation("admin/pkgApp")
    @GetMapping(value = "/admin/pkgApp/url")
    public BaseResponse getUrl(@RequestParam("id") Integer id) {
        return ResultVOUtils.success(pkgAppService.getUrl(id));
    }
}
