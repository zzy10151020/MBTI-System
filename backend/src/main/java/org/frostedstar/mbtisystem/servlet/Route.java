package org.frostedstar.mbtisystem.servlet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 路由注解，用于标记控制器方法的访问路径
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Route {
    /**
     * 路由路径
     */
    String value() default "";
    
    /**
     * HTTP 方法
     */
    String method() default "GET";
}
