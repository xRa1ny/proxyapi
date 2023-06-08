package me.xra1ny.proxyapi.models.party;

import me.xra1ny.proxyapi.RPlugin;
import me.xra1ny.proxyapi.models.user.RUser;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

public class PartyManager {
    /**
     * all registered parties
     */
    @Getter(onMethod = @__({@NotNull, @Unmodifiable}))
    private final List<Party> parties = new ArrayList<>();

    /**
     * creates a party with the user specified
     * @param leader the user
     * @return the party created
     */
    @NotNull
    public Party createParty(@NotNull RUser leader) {
        final Party party = new Party(leader);

        this.parties.add(party);
        leader.sendMessage("Party erstellt!");

        return party;
    }

    /**
     * retrieves the party associated with the user specified
     * @param user the user
     * @return the party associated with the user specified
     */
    @Nullable
    public Party getParty(@NotNull RUser user) {
        return this.parties.stream()
                .filter(party -> party.getMembers().contains(user))
                .findAny()
                .orElse(null);
    }
}
