package me.xra1ny.proxyapi.models.maintenance;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.proxyapi.RPlugin;
import me.xra1ny.proxyapi.models.user.RUser;
import me.xra1ny.proxyapi.utils.ConfigKeys;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class ProxyMaintenanceManager {
    @Getter
    private boolean enabled;

    /**
     * the message to display when a user gets kicked in result of ongoing maintenance
     */
    @Getter(onMethod = @__(@NotNull))
    private String message;

    /**
     * all uuids ignored by the maintenance system
     */
    @Getter(onMethod = @__({ @NotNull, @Unmodifiable }))
    private final List<UUID> ignoredUsers = new ArrayList<>();

    public ProxyMaintenanceManager() {
        Configuration maintenance = RPlugin.getInstance().getConfig().getSection(ConfigKeys.MAINTENANCE);

        if(maintenance == null) {
            RPlugin.getInstance().getConfig().set(ConfigKeys.MAINTENANCE, "");
            maintenance = RPlugin.getInstance().getConfig().getSection(ConfigKeys.MAINTENANCE);
        }

        this.enabled = maintenance.getBoolean(ConfigKeys.MAINTENANCE_ENABLED);
        this.message = maintenance.getString(ConfigKeys.MAINTENANCE_MESSAGE);

        final List<UUID> ignoredUsers = maintenance.getStringList(ConfigKeys.MAINTENANCE_IGNORED).stream().map(UUID::fromString).toList();

        if(ignoredUsers != null) {
            this.ignoredUsers.addAll(ignoredUsers);
        }
    }

    /**
     * sets the message of the maintenance and updates it in the config
     * @param message the message
     */
    public void setMessage(@NotNull String message) {
        if(this.message.equals(message)) {
            return;
        }

        final Configuration maintenance = RPlugin.getInstance().getConfig().getSection(ConfigKeys.MAINTENANCE);

        maintenance.set(ConfigKeys.MAINTENANCE_MESSAGE, message);

        RPlugin.getInstance().saveConfig();

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

        final Configuration maintenance = RPlugin.getInstance().getConfig().getSection(ConfigKeys.MAINTENANCE);

        maintenance.set(ConfigKeys.MAINTENANCE_ENABLED, enabled);

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

        this.enabled = enabled;
    }

    private void updateConfig() {
        final Configuration maintenance = RPlugin.getInstance().getConfig().getSection(ConfigKeys.MAINTENANCE);

        maintenance.set(ConfigKeys.MAINTENANCE_IGNORED, this.ignoredUsers.stream().map(UUID::toString).toList());

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

        updateConfig();
    }
}
