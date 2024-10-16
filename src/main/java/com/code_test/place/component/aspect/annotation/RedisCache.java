package com.code_test.place.component.aspect.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCache {

    String prefix();

    String[] params() default {};

    long expireMinutes() default 10;

    Class<?> type() default Object.class;


}
