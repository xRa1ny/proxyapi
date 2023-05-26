package me.xra1ny.proxyapi.listeners;

import me.xra1ny.proxyapi.RPlugin;
import me.xra1ny.proxyapi.models.user.RUser;
import me.xra1ny.proxyapi.models.user.UserInputWindow;
import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

@Slf4j
public final class DefaultPluginListener implements Listener {
    @EventHandler
    public void onPlayerSendChatMessage(@NotNull ChatEvent e) {
        try {
            if(!(e.getSender() instanceof ProxiedPlayer player)) {
                return;
            }

            final RUser user = RPlugin.getInstance().getUserManager().get(player);
            final UserInputWindow userInputWindow = RPlugin.getInstance().getUserInputWindowManager().get(user);

            if(userInputWindow == null) {
                return;
            }

            e.setCancelled(true);
            userInputWindow.getInputWindowHandler().onUserSendChatMessage(user, e.getMessage());
        }catch(Exception ex) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default async chat event handler!", ex);
        }
    }
}