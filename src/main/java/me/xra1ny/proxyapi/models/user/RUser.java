package me.xra1ny.proxyapi.models.user;

import lombok.Getter;
import lombok.Setter;
import me.xra1ny.proxyapi.RPlugin;
import me.xra1ny.proxyapi.models.config.RConfig;
import me.xra1ny.proxyapi.models.localisation.Replacement;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RUser {
    /**
     * the player of this user (might be null after player disconnected)
     */
    @Getter(onMethod = @__(@Nullable))
    private ProxiedPlayer player;

    /**
     * the date of creation of this user instance
     */
    @Getter(onMethod = @__(@NotNull))
    private final Date creation = Date.from(Instant.now());

    /**
     * the timeout of this user (unregisters this user when 0)
     */
    @Getter
    @Setter
    private long timeout;

    /**
     * the list of all users this user ignores
     */
    @Getter(onMethod = @__(@NotNull))
    private final List<RUser> ignored = new ArrayList<>();

    @Getter(onMethod = @__(@Nullable))
    @Setter(onParam = @__(@NotNull))
    private Class<? extends RConfig> localisationConfigClass;

    public RUser(@NotNull ProxiedPlayer player) {
        this.player = player;
        this.timeout = RPlugin.getInstance().getUserManager().getUserTimeoutHandler().getUserTimeout();
    }

    /**
     * updates this user
     */
    public void update() {
        if(this.player == null) {
            return;
        }

        this.player = ProxyServer.getInstance().getPlayer(this.player.getUniqueId());
    }

    public void sendMessage(@NotNull String... message) {
        RPlugin.sendMessage(this.player, message);
    }

    public void sendTranslatedMessage(@NotNull Class<? extends RConfig> localisationConfigClass, @NotNull String key, @NotNull Replacement... replacements) {
        sendMessage(RPlugin.getInstance().getLocalisationManager().get(localisationConfigClass, key, replacements));
    }

    public void sendTranslatedMessage(@NotNull String key, @NotNull Replacement... replacements) {
        sendTranslatedMessage(this.localisationConfigClass, key, replacements);
    }
}
