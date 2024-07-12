package com.streaming.aop.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 이 애노테이션은 메소드에 적용할 수 있습니다
@Target(ElementType.METHOD)
// 이 애노테이션은 런타임 동안 유지됩니다
@Retention(RetentionPolicy.RUNTIME)
public @interface ReadOnly {
}
