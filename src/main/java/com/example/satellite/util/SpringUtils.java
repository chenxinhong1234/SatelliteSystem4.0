package com.example.satellite.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Description:spring容器的初始化与获取spring容器
 * Created by Gaoxinwen on 2016/4/28.
 */
public class SpringUtils {

    private static ApplicationContext applicationContext;

    static {
        applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
