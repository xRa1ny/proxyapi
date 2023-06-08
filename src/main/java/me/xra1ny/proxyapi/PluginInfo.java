package me.xra1ny.proxyapi;

import me.xra1ny.proxyapi.models.maintenance.RMaintenanceManager;
import me.xra1ny.proxyapi.models.user.RUser;
import me.xra1ny.proxyapi.models.user.RUserManager;
import org.jetbrains.annotations.NotNull;

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

    /**
     * the user manager class to use for this plugin
     * @return the user manager class
     */
    @NotNull
    Class<? extends RUserManager> userManagerClass() default RUserManager.class;

    /**
     * the maintenance manager class to use for this plugin
     * @return the maintenance manager class
     */
    @NotNull
    Class<? extends RMaintenanceManager> maintenanceManagerClass() default RMaintenanceManager.class;
}
