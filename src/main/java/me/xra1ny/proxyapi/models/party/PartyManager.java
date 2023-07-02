package me.xra1ny.proxyapi.models.party;

import lombok.Getter;
import me.xra1ny.proxyapi.RPlugin;
import me.xra1ny.proxyapi.exceptions.PartyAlreadyRegisteredException;
import me.xra1ny.proxyapi.exceptions.PartyNotRegisteredException;
import me.xra1ny.proxyapi.models.user.RUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class PartyManager {
    /**
     * all registered parties
     */
    @Getter(onMethod = @__({@NotNull, @Unmodifiable}))
    private final List<Party> parties = new ArrayList<>();

    public void register(@NotNull Party party) throws PartyAlreadyRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to register party " + party + "...");

        if(this.parties.contains(party)) {
            throw new PartyAlreadyRegisteredException(party);
        }

        this.parties.add(party);
        RPlugin.getInstance().getLogger().log(Level.INFO, "party " + party + " successfully registered!");
    }

    public void unregister(@NotNull Party party) throws PartyNotRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregistered party " + party + "...");

        if(!this.parties.contains(party)) {
            throw new PartyNotRegisteredException(party);
        }

        this.parties.remove(party);
        RPlugin.getInstance().getLogger().log(Level.INFO, "party " + party + " successfully unregistered!");
    }

    public void unregisterAll() {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregister all parties...");
        this.parties.clear();
        RPlugin.getInstance().getLogger().log(Level.INFO, "successfully unregistered all parties!");
    }

    /**
     * retrieves the party associated with the user specified
     * @param user the user
     * @return the party associated with the user specified
     */
    @Nullable
    public Party get(@NotNull RUser user) {
        return this.parties.stream()
                .filter(party -> party.getMembers().contains(user))
                .findAny()
                .orElse(null);
    }
}
