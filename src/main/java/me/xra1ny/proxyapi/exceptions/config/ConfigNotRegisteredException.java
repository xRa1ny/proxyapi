package me.xra1ny.proxyapi.exceptions.config;

import me.xra1ny.proxyapi.models.config.RConfig;
import me.xra1ny.proxyapi.models.exception.RPluginException;
import org.jetbrains.annotations.NotNull;

public class ConfigNotRegisteredException extends RPluginException {
    public ConfigNotRegisteredException(@NotNull RConfig config) {
        super("config " + config + " is not yet registered!");
    }
}
