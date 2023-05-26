package me.xra1ny.proxyapi;

import me.xra1ny.proxyapi.models.user.RUser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PluginInfo {
    /***
     * the user class to use for this plugin
     * @return the user class
     */
    Class<? extends RUser> userClass();
}
