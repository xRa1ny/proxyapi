package me.xra1ny.proxyapi.exceptions;

import me.xra1ny.proxyapi.models.exception.RPluginException;
import me.xra1ny.proxyapi.models.user.RUser;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UserNotRegisteredException extends RPluginException {
    public UserNotRegisteredException(@NotNull RUser user) {
        super("user " + user + " is not yet registered!");
    }

    public UserNotRegisteredException(@NotNull ProxiedPlayer player) {
        super("user of player " + player + " is not yet registered!");
    }

    public UserNotRegisteredException(@NotNull UUID uuid) {
        super("user of player uuid " + uuid + " is not yet registered!");
    }

    public UserNotRegisteredException(@NotNull String name) {
        super("user of player name " + name + " is not yet registered!");
    }
}
