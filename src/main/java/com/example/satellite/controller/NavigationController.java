package com.example.satellite.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *   控制前端所有页面到后端的映射
 * Description:
 * Created by Gaoxinwen on 2016/12/6.
 */
@Controller
public class NavigationController {



    //获取登录的用户名  （用于显示在所有的页面导航栏的右上角上，显示用户的姓名）
    public void getUsername(ModelMap model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username 获得登录用户名
        /*将前端获取的“用户名”传给导航栏内的"${username}"标签*/
        model.addAttribute("username", name);

    }

    /*《登录》页面的【登录】按钮提交的地址是"/login"，进行登录的判断，
       如果登录了的话，直接重定向到index页面，否则转到Login页面
     */

    @RequestMapping("/login")
    public String login() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // The user is logged in
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";   /*重定向到@RequestMapping("/")*/
        } else {
            return "login";     /*返回《登录》页面*/
        }
    }

    @RequestMapping("/")
    public String index(ModelMap model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName(); //get logged in username
        model.addAttribute("username", name);
        return "index";    /*返回《欢迎登录》页面*/
    }

    /*《卫星信息查询》的七个子页面的跳转*/
    @RequestMapping("/satellitequery")
    public String zvCurve(ModelMap model) {
        getUsername(model);  /*运行这个方法来获取“用户名”来显示在页面上*/
        return "data/SatelliteQuery";
    }

    @RequestMapping("/thruster")
    public String zqCurve(ModelMap model) {
        getUsername(model);
        return "data/thruster";
    }

    @RequestMapping("/communication")
    public String nhqCurve(ModelMap model) {
        getUsername(model);
        return "data/communication";
    }
    @RequestMapping("/camera")
    public String info(ModelMap model) {
        getUsername(model);
        return "data/camera";
    }

    @RequestMapping("/location")
    public String location(ModelMap model) {
        getUsername(model);
        return "data/location";
    }

    @RequestMapping("/energy")
    public String heoCurve(ModelMap model) {
        getUsername(model);
        return "data/energy";
    }

    @RequestMapping("/girder")
    public String girder(ModelMap model) {
        getUsername(model);
        return "data/girder";
    }

    @RequestMapping("/extend")
    public String extend(ModelMap model) {
        getUsername(model);
        return "data/extend";
    }

    /*《厂内经济运行》两个子页面的跳转*/
    @GetMapping("/economic_operation")
    public String economic(ModelMap model) {
        getUsername(model);
        return "economicaloperation/economic";
    }

    @GetMapping("/report")
    public String report(ModelMap model) {
        getUsername(model);
        model.addAttribute("dateCriteria", new DateCriteria());
        return "economicaloperation/report";
    }

    /*《欢迎登陆》页面的跳转*/
    @RequestMapping("/index")
    public String ui(ModelMap model) {
        getUsername(model);
        return "index";
    }

}