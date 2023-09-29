package me.xra1ny.proxyapi.models.party;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import me.xra1ny.proxyapi.RPlugin;
import me.xra1ny.proxyapi.models.user.RUser;
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
    @Setter(onParam = @__(@NotNull))
    private RUser leader;


    @SneakyThrows
    public Party(@NotNull RUser leader) {
        this.leader = leader;
        this.members.add(leader);

        RPlugin.getInstance().getPartyManager().register(this);
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
    }

    /**
     * removes the user specified from this party
     * @param user the user
     */
    public void remove(@NotNull RUser user) {
        if(!this.members.contains(user)) {
            return;
        }

        if(this.members.size() <= 2) {
            this.members.clear();

            return;
        }

        this.members.remove(user);

        if(this.leader.equals(user)) {
            this.leader = this.members.get(new Random().nextInt(this.members.size()));
        }
    }

    /**
     * moves this party to the server specified
     * @param targetServerInfo the server
     */
    public void move(@NotNull ServerInfo targetServerInfo) {
        for(RUser member : this.members) {
            member.getPlayer().connect(targetServerInfo);
        }
    }
}
