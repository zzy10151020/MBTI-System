package org.frostedstar.mbtisystem.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库连接工具类
 */
@Slf4j
public class DatabaseUtil {
    
    private static HikariDataSource dataSource;
    
    static {
        init();
    }
    
    /**
     * 初始化数据源
     */
    private static void init() {
        try {
            Properties props = new Properties();
            InputStream inputStream = DatabaseUtil.class.getClassLoader()
                    .getResourceAsStream("application.properties");
            
            if (inputStream != null) {
                props.load(inputStream);
                inputStream.close();
            }
            
            HikariConfig config = new HikariConfig();
            config.setDriverClassName(props.getProperty("db.driver"));
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.username"));
            config.setPassword(props.getProperty("db.password"));
            
            // 连接池配置
            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.maxActive", "20")));
            config.setMinimumIdle(Integer.parseInt(props.getProperty("db.pool.minIdle", "2")));
            config.setConnectionTimeout(Long.parseLong(props.getProperty("db.pool.maxWait", "60000")));
            config.setValidationTimeout(5000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            config.setLeakDetectionThreshold(60000);
            
            dataSource = new HikariDataSource(config);
            log.info("数据库连接池初始化成功");
            
        } catch (IOException e) {
            log.error("读取数据库配置失败", e);
            throw new RuntimeException("数据库连接池初始化失败", e);
        }
    }
    
    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    /**
     * 获取数据源
     */
    public static DataSource getDataSource() {
        return dataSource;
    }
    
    /**
     * 关闭数据源
     */
    public static void close() {
        if (dataSource != null) {
            dataSource.close();
            log.info("数据库连接池已关闭");
        }
    }
    
    /**
     * 关闭数据库连接资源
     */
    public static void closeQuietly(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    log.warn("关闭资源时发生异常", e);
                }
            }
        }
    }
}
