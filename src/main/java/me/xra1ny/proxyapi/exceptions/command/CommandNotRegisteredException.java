package me.xra1ny.proxyapi.exceptions.command;

import me.xra1ny.proxyapi.models.command.RCommand;
import me.xra1ny.proxyapi.models.exception.RPluginException;
import org.jetbrains.annotations.NotNull;

public class CommandNotRegisteredException extends RPluginException {
    public CommandNotRegisteredException(@NotNull RCommand command) {
        super("command " + command + " is not yet registered!");
    }
}
