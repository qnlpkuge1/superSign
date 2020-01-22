package com.lmxdawn.api.admin.controller;

import com.lmxdawn.api.admin.entity.PkgApp;
import com.lmxdawn.api.admin.service.PkgAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PkgAppController {

    @Autowired
    private PkgAppService pkgAppService;

    @GetMapping("/pkgApp/{id}")
    public ModelAndView app(@PathVariable("id") Integer id) {
        PkgApp pkgApp = pkgAppService.get(id);
        ModelAndView mv = new ModelAndView();
        mv.addObject("pkgApp", pkgApp);
        mv.setViewName("pkgApp.html");
        return mv;
    }
}
