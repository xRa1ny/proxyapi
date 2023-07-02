package me.xra1ny.proxyapi.exceptions;

import me.xra1ny.proxyapi.models.exception.RPluginException;
import me.xra1ny.proxyapi.models.party.Party;
import org.jetbrains.annotations.NotNull;

public class PartyNotRegisteredException extends RPluginException {
    public PartyNotRegisteredException(@NotNull Party party) {
        super("party " + party + " is not yet registered!");
    }
}
