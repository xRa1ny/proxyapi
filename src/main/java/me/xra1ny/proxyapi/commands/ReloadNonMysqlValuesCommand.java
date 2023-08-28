package me.xra1ny.proxyapi.commands;

import me.xra1ny.proxyapi.RPlugin;
import me.xra1ny.proxyapi.exceptions.ClassNotAnnotatedException;
import me.xra1ny.proxyapi.models.command.CommandReturnState;
import me.xra1ny.proxyapi.models.command.RCommand;
import net.md_5.bungee.api.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public class ReloadNonMysqlValuesCommand extends RCommand {

    public ReloadNonMysqlValuesCommand() throws ClassNotAnnotatedException {
        super("reloadproxynonmysqlvalues", "pluginapi.command.reloadnonmysqlvalues", false, false);
    }

    @Override
    protected @NotNull CommandReturnState executeBaseCommand(@NotNull CommandSender sender) throws Exception {
        RPlugin.sendMessage(sender, "Attempting to reload non mysql values...");
        RPlugin.getInstance().reloadNonMySqlValues();
        RPlugin.sendMessage(sender, "Successfully reloaded non mysql values!");

        return CommandReturnState.SUCCESS;
    }

    @Override
    protected @NotNull @Unmodifiable List<String> help(@NotNull CommandSender sender) {
        return List.of();
    }

    @Override
    public @NotNull List<String> onCommandTabComplete(@NotNull CommandSender sender, @NotNull String args) {
        return List.of();
    }
}
