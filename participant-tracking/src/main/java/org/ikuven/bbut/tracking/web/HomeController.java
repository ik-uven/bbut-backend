package org.ikuven.bbut.tracking.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
public class HomeController {
 
    @RequestMapping(value = {"/","/admin", "/results/**"})
    public String index() {
        return "/index.html";
    }
}
