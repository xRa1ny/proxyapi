package me.xra1ny.proxyapi.listeners;

import me.xra1ny.proxyapi.RPlugin;
import me.xra1ny.proxyapi.exceptions.UserNotRegisteredException;
import me.xra1ny.proxyapi.models.user.RUser;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class DefaultPluginConnectionListener implements Listener {
    @EventHandler
    public void onPlayerLoginProxy(@NotNull LoginEvent e) {
        try {
            if(RPlugin.getInstance().getProxyMaintenanceManager().isEnabled()) {
                if(!RPlugin.getInstance().getProxyMaintenanceManager().getIgnoredUsers().stream()
                        .toList()
                        .contains(e.getConnection().getUniqueId())) {
                    e.setCancelled(true);
                    e.setCancelReason(
                            TextComponent.fromLegacyText(
                                    RPlugin.getInstance().getProxyMaintenanceManager().getMessage()
                            )
                    );
                }
            }
        }catch(Exception ex) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default login event handler!", e);
        }
    }

    @EventHandler
    public void onPlayerJoinProxy(@NotNull PostLoginEvent e) {
        try {
            RUser user;
            try {
                user = RPlugin.getInstance().getUserManager().get(e.getPlayer());
                user.setTimeout(RPlugin.getInstance().getUserManager().getUserTimeoutHandler().getUserTimeout());
                user.update();
            }catch (UserNotRegisteredException ex) {
                user = RPlugin.getInstance().getUserManager().getUserClass().getDeclaredConstructor(ProxiedPlayer.class).newInstance(e.getPlayer());
                RPlugin.getInstance().getUserManager().register(user);
            }
        }catch(Exception ex) {
            RPlugin.getInstance().getLogger().log(Level.SEVERE, "error while executing default player join event handler!", ex);
        }
    }
}
