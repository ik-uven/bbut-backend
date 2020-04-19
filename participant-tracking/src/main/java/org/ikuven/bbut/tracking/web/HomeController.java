package org.ikuven.bbut.tracking.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
 
    @RequestMapping(value = {"/","/admin"})
    public String index() {
        return "index.html";
    }
}
