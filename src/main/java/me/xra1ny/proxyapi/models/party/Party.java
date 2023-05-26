package me.xra1ny.proxyapi.models.party;

import me.xra1ny.proxyapi.RPlugin;
import me.xra1ny.proxyapi.models.user.RUser;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Party {
    /**
     * the members of this party
     */
    @Getter(onMethod = @__({@NotNull, @Unmodifiable}))
    private final List<RUser> members = new ArrayList<>();

    /**
     * the leader of this party
     */
    @Getter(onMethod = @__(@NotNull))
    private RUser leader;

    public Party(@NotNull RUser leader) {
        this.leader = leader;
        this.members.add(leader);
    }

    /**
     * adds the user specified to this party
     * @param user the user
     */
    public void add(@NotNull RUser user) {
        if(this.members.contains(user)) {
            return;
        }

        this.members.add(user);

        for(RUser member : this.members) {
            member.getPlayer().sendMessage(
                    TextComponent.fromLegacyText(
                            RPlugin.getInstance().getPrefix() + ChatColor.YELLOW + member.getPlayer().getName() + ChatColor.GRAY + " ist der Party " + ChatColor.GREEN + "beigetreten!"
                    )
            );
        }
    }

    /**
     * removes the user specified from this party
     * @param user the user
     */
    public void removeMember(@NotNull RUser user) {
        if(!this.members.contains(user)) {
            return;
        }

        for(RUser member : this.members) {
            member.getPlayer().sendMessage(
                    TextComponent.fromLegacyText(
                            RPlugin.getInstance().getPrefix() + ChatColor.YELLOW + user.getPlayer().getName() + ChatColor.GRAY + " hat die Party " + ChatColor.RED + "verlassen!"
                    )
            );
        }

        this.members.remove(user);

        if(this.members.size() == 1) {
            final RUser last = this.members.get(0);

            this.members.remove(last);
            last.getPlayer().sendMessage(
                    TextComponent.fromLegacyText(
                            RPlugin.getInstance().getPrefix() + "Die Party wurde aufgrund unzureichender Spieler aufgelöst!"
                    )
            );

            return;
        }

        if(this.leader.equals(user)) {
            final RUser leader = this.members.get(new Random().nextInt(this.members.size()));

            setLeader(leader);
        }
    }

    /**
     * moves this party to the server specified
     * @param targetServerInfo the server
     */
    public void move(@NotNull final ServerInfo targetServerInfo) {
        for(RUser member : this.members) {
            member.getPlayer().connect(targetServerInfo);
            member.getPlayer().sendMessage(
                    TextComponent.fromLegacyText(
                            RPlugin.getInstance().getPrefix() + "Die Party wechselt auf " + ChatColor.YELLOW + targetServerInfo.getName()
                    )
            );
        }
    }

    /**
     * sets the leader of this party to the user specified
     * @param leader the user
     */
    public void setLeader(@NotNull RUser leader) {
        this.leader = leader;

        for(RUser member : this.members) {
            member.getPlayer().sendMessage(
                    TextComponent.fromLegacyText(
                            RPlugin.getInstance().getPrefix() + ChatColor.YELLOW + leader.getPlayer().getName() + ChatColor.GRAY + " ist der neue Partyleader!"
                    )
            );
        }
    }
}
