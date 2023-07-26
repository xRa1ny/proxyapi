package me.xra1ny.proxyapi.exceptions.config;

import me.xra1ny.proxyapi.models.config.RConfig;
import me.xra1ny.proxyapi.models.exception.RPluginException;
import org.jetbrains.annotations.NotNull;

public class ConfigAlreadyRegisteredException extends RPluginException {
    public ConfigAlreadyRegisteredException(@NotNull RConfig config) {
        super("config " + config + " is already registered!");
    }
}
