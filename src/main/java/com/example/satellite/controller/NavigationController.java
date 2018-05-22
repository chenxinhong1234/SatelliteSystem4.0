package com.example.satellite.controller;

import com.example.satellite.domain.Report;
import com.example.satellite.service.ReportService;
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

    @Autowired
    private ReportService reportService;

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

    /*《电站信息查询》的五个子页面的跳转*/
    @RequestMapping("/satellitequery")
    public String zvCurve(ModelMap model) {
        getUsername(model);  /*运行这个方法来获取“用户名”来显示在页面上*/
        return "data/SatelliteQuery";
    }

    @RequestMapping("/zqcurve")
    public String zqCurve(ModelMap model) {
        getUsername(model);
        return "data/zqcurve";
    }


    @RequestMapping("/nhqcurve")
    public String nhqCurve(ModelMap model) {
        getUsername(model);
        return "data/nhqcurve";
    }
    @RequestMapping("/info")
    public String info(ModelMap model) {
        getUsername(model);
        return "data/info";
    }

    @RequestMapping("/heocurve")
    public String heoCurve(ModelMap model) {
        getUsername(model);
        return "data/heocurve";
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

    @PostMapping("/report")
    public String reportSearch(@ModelAttribute DateCriteria dateCriteria, RedirectAttributes redirectAttributes) throws
            ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = df.parse(dateCriteria.getStart());
        Date endDate = df.parse(dateCriteria.getEnd());
        System.out.println(startDate);
        System.out.println(endDate);
        // 结束日期加1天
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.add(Calendar.DATE, 1);
        endDate = calendar.getTime();
        List<Report> reportList = reportService.getReportFromDate(startDate, endDate);
        System.out.println(reportList);
        redirectAttributes.addFlashAttribute("reportList", reportList);
        return "redirect:/report";
    }

    @RequestMapping(value = "/download/{id}", method = RequestMethod.GET)
    public void reportDownload(@PathVariable("id") Integer id,  HttpServletResponse response) {
        try {
            Report report = reportService.getReport(id);
            byte[] bytes = report.getFile();
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

            System.out.println(report.getName());

            String fileName = report.getName();
            fileName = URLEncoder.encode(fileName,"UTF-8");

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            // copy it to response's OutputStream
            org.apache.commons.io.IOUtils.copy(bis, response.getOutputStream());
            bis.close();
            response.flushBuffer();
        } catch (IOException ex) {
            throw new RuntimeException("IOError writing file to output stream");
        }

    }
}