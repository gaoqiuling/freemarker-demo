package com.itisacat.freemarker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

/**
 * Created by gaoqiuling on 2017/7/7.
 */
@Controller
@RequestMapping(value = "index/")
public class IndexController {
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public ModelAndView getCarouselPage(ModelMap modelMap) {
        modelMap.addAttribute("date", new Date());
        return new ModelAndView("index_list");
    }
}
