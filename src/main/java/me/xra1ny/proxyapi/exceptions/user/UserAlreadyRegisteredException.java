package me.xra1ny.proxyapi.exceptions.user;

import me.xra1ny.proxyapi.models.exception.RPluginException;
import me.xra1ny.proxyapi.models.user.RUser;
import org.jetbrains.annotations.NotNull;

public class UserAlreadyRegisteredException extends RPluginException {
    public UserAlreadyRegisteredException(@NotNull RUser user) {
        super("user " + user + " is already registered!");
    }
}
