package me.xra1ny.proxyapi.models.localisation;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class Replacement {
    @Getter(onMethod = @__(@NotNull))
    private final String key, value;

    public Replacement(@NotNull String key, @NotNull String value) {
        this.key = key;
        this.value = value;
    }
}
