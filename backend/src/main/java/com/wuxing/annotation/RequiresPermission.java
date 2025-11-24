package com.wuxing.annotation;

import java.lang.annotation.*;

/**
 * 权限注解
 * 用于方法级权限控制
 * 
 * @author wuxing
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermission {
    
    /**
     * 权限编码
     */
    String[] value();
    
    /**
     * 权限关系：AND 或 OR
     * AND: 需要拥有所有权限
     * OR: 拥有任一权限即可
     */
    Logical logical() default Logical.AND;
    
    enum Logical {
        AND, OR
    }
}
