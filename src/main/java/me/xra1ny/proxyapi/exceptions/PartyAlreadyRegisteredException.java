package me.xra1ny.proxyapi.exceptions;

import me.xra1ny.proxyapi.models.exception.RPluginException;
import me.xra1ny.proxyapi.models.party.Party;
import org.jetbrains.annotations.NotNull;

public class PartyAlreadyRegisteredException extends RPluginException {
    public PartyAlreadyRegisteredException(@NotNull Party party) {
        super("party " + party + " is already registered!");
    }
}
