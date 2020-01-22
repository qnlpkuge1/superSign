package com.lmxdawn.api.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {

    @GetMapping("/")
    public String defaultIndex() {
        return "index";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/ping")
    public String ping() {
        return "/ping";
    }

    @GetMapping("/404")
    public String error404() {
        return "/404";
    }


}
