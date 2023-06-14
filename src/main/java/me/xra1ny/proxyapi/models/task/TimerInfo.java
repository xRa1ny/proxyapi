package me.xra1ny.proxyapi.models.task;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TimerInfo {
    /**
     * defines the countdown of this timer
     */
    int countdown();
}
