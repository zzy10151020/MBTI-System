package org.frostedstar.mbtisystem;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.frostedstar.mbtisystem.filter.CharacterEncodingFilter;
import org.frostedstar.mbtisystem.filter.CorsFilter;
import org.frostedstar.mbtisystem.servlet.ApiDispatcherServlet;

import java.io.File;

/**
 * MBTI系统启动类
 */
@Slf4j
public class MBTISystemApplication {
    
    private static final int PORT = 8080;
    private static final String CONTEXT_PATH = "/mbti-system";
    
    public static void main(String[] args) {
        try {
            // 创建Tomcat实例
            Tomcat tomcat = new Tomcat();
            tomcat.setPort(PORT);
            tomcat.getConnector(); // 触发连接器创建
            
            // 设置工作目录
            String workingDir = System.getProperty("user.dir");
            tomcat.setBaseDir(workingDir);
            
            // 创建Context
            Context context = tomcat.addContext(CONTEXT_PATH, new File(workingDir).getAbsolutePath());
            
            // 添加字符编码过滤器
            FilterDef characterEncodingFilterDef = new FilterDef();
            characterEncodingFilterDef.setFilterName("CharacterEncodingFilter");
            characterEncodingFilterDef.setFilterClass(CharacterEncodingFilter.class.getName());
            context.addFilterDef(characterEncodingFilterDef);
            
            FilterMap characterEncodingFilterMap = new FilterMap();
            characterEncodingFilterMap.setFilterName("CharacterEncodingFilter");
            characterEncodingFilterMap.addURLPattern("/*");
            context.addFilterMap(characterEncodingFilterMap);
            
            // 添加CORS过滤器
            FilterDef corsFilterDef = new FilterDef();
            corsFilterDef.setFilterName("CorsFilter");
            corsFilterDef.setFilterClass(CorsFilter.class.getName());
            context.addFilterDef(corsFilterDef);
            
            FilterMap corsFilterMap = new FilterMap();
            corsFilterMap.setFilterName("CorsFilter");
            corsFilterMap.addURLPattern("/*");
            context.addFilterMap(corsFilterMap);
            
            // 添加API分发器Servlet
            tomcat.addServlet(CONTEXT_PATH, "ApiDispatcherServlet", new ApiDispatcherServlet());
            context.addServletMappingDecoded("/api/*", "ApiDispatcherServlet");
            
            // 启动Tomcat
            tomcat.start();
            
            log.info("MBTI System started successfully!");
            log.info("Access URL: http://localhost:{}{}", PORT, CONTEXT_PATH);
            log.info("API Base URL: http://localhost:{}{}/api", PORT, CONTEXT_PATH);
            
            // 等待关闭
            tomcat.getServer().await();
            
        } catch (LifecycleException e) {
            log.error("Failed to start MBTI System", e);
            System.exit(1);
        }
    }
}
