package me.xra1ny.proxyapi.models.command;

import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Builder
public class CommandArg {
    public static final String PLAYER = "%PLAYER%";
    public static final String BOOLEAN = "%BOOLEAN%";
    public static final String NUMBER = "%NUMBER%";

    @Builder.Default
    @Getter(onMethod = @__(@NotNull))
    private String value = "";

    @Builder.Default
    @Getter(onMethod = @__(@NotNull))
    private String permission = "";

    @Builder.Default
    @Getter
    private boolean player = false;

    @NotNull
    public String getFormattedValue() {
        return this.value.replaceAll("%[A-Za-z0-9]*%", "?");
    }
}
