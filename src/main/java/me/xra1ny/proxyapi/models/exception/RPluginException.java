package me.xra1ny.proxyapi.models.exception;

import org.jetbrains.annotations.NotNull;

public class RPluginException extends Exception {
    public RPluginException(@NotNull String message) {
        super(message);
    }
}
