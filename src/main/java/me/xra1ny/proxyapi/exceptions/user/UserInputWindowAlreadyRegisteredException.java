package me.xra1ny.proxyapi.exceptions.user;

import me.xra1ny.proxyapi.models.exception.RPluginException;
import me.xra1ny.proxyapi.models.user.UserInputWindow;
import org.jetbrains.annotations.NotNull;

public class UserInputWindowAlreadyRegisteredException extends RPluginException {
    public UserInputWindowAlreadyRegisteredException(@NotNull UserInputWindow userInputWindow) {
        super("user input window " + userInputWindow + " is already registered!");
    }
}
