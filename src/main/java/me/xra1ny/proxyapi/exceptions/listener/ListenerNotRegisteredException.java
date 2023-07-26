package me.xra1ny.proxyapi.exceptions.listener;

import me.xra1ny.proxyapi.models.exception.RPluginException;
import net.md_5.bungee.api.plugin.Listener;
import org.jetbrains.annotations.NotNull;

public class ListenerNotRegisteredException extends RPluginException {
    public ListenerNotRegisteredException(@NotNull Listener listener) {
        super("listener " + listener + " is not yet registered!");
    }
}
