package me.xra1ny.proxyapi.models.maintenance;

import me.xra1ny.proxyapi.RPlugin;
import me.xra1ny.proxyapi.models.user.RUser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class MaintenanceManager {
    /**
     * the message to display when a user gets kicked in result of ongoing maintenance
     */
    @Getter(onMethod = @__(@NotNull))
    private String message;

    @Getter
    private boolean enabled;

    /**
     * all uuids ignored by the maintenance system
     */
    @Getter(onMethod = @__({ @NotNull, @Unmodifiable}))
    private final List<UUID> ignoredUsers = new ArrayList<>();

    public MaintenanceManager() {
        this.message = RPlugin.getInstance().getConfig().getString("maintenance.message");
        this.enabled = RPlugin.getInstance().getConfig().getBoolean("maintenance.enabled");

        final List<UUID> ignoredUsers = RPlugin.getInstance().getConfig().getStringList("maintenance.ignored").stream().map(UUID::fromString).toList();

        if(ignoredUsers != null) {
            this.ignoredUsers.addAll(ignoredUsers);
        }
    }

    /**
     * sets the message of the maintenance and updates it in the config
     * @param message the message
     */
    public void setMessage(@NotNull String message) {
        if(this.message != null && this.message.equals(message)) {
            return;
        }

        RPlugin.getInstance().getConfig().set("maintenance.message", message);
        RPlugin.getInstance().saveConfig();

        for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            player.sendMessage(
                    TextComponent.fromLegacyText(
                            RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getChatColor() + "Die Wartungsarbeiten Nachricht wurde angepasst!"
                    )
            );
        }

        this.message = message;
    }

    /**
     * updates the enabled status of the maintenance system
     * @param enabled true or false
     */
    public void setEnabled(boolean enabled) {
        if(this.enabled == enabled) {
            return;
        }

        RPlugin.getInstance().getConfig().set("maintenance.enabled", enabled);
        RPlugin.getInstance().saveConfig();

        // Kick all Users not permitted...
        for(RUser user : RPlugin.getInstance().getUserManager().getUsers()) {
            if(!this.ignoredUsers.contains(user.getPlayer().getUniqueId())) {
                user.getPlayer().disconnect(
                        TextComponent.fromLegacyText(
                                this.message
                        )
                );
            }
        }

        for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            player.sendMessage(
                    TextComponent.fromLegacyText(
                            RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getChatColor() + "Die Wartungen wurden " + String.valueOf(enabled)
                                    .replace("true", ChatColor.GREEN + "aktiviert!")
                                    .replace("false", ChatColor.RED + "deaktiviert!"
                                    )
                    )
            );
        }

        this.enabled = enabled;
    }

    private void updateConfig() {
        RPlugin.getInstance().getConfig().set("maintenance.ignored", this.ignoredUsers.stream().map(UUID::toString).toList());
        RPlugin.getInstance().saveConfig();
    }

    /**
     * adds the uuid specified to be ignored by the maintenance system on join
     * @param uuid the uuid
     */
    public void add(@NotNull UUID uuid) {
        if(this.ignoredUsers.contains(uuid)) {
            return;
        }

        this.ignoredUsers.add(uuid);

        // TODO: Add filter (Send Message only to permitted Players)
        for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            player.sendMessage(
                    TextComponent.fromLegacyText(
                            RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getChatColor() + ChatColor.YELLOW + uuid + RPlugin.getInstance().getChatColor() + " wurde als Wartungsarbeiten Ausnahme " + ChatColor.GREEN + "hinzugefügt!"
                    )
            );
        }

        updateConfig();
    }

    /**
     * removes the uuid specified from the whitelist of ignored uuids on join
     * @param uuid the uuid
     */
    public void remove(@NotNull UUID uuid) {
        if(!this.ignoredUsers.contains(uuid)) {
            return;
        }

        this.ignoredUsers.remove(uuid);

        // TODO: Add filter (Send Message only to permitted Players)
        for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            player.sendMessage(
                    TextComponent.fromLegacyText(
                            RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getChatColor() + ChatColor.YELLOW + uuid + RPlugin.getInstance().getChatColor() + " wurde als Wartungsarbeiten Ausnahme " + ChatColor.RED + "entfernt!"
                    )
            );
        }

        updateConfig();
    }
}
