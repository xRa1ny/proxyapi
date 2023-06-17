package me.xra1ny.proxyapi.models.command;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.proxyapi.RPlugin;
import me.xra1ny.proxyapi.exceptions.ClassNotAnnotatedException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/** Used to create Commands */
@Slf4j
public abstract class RCommand extends Command implements TabExecutor {
    /**
     * the name of this command
     */
    @Getter(onMethod = @__(@NotNull))
    private final String name;

    /**
     * the permission of this command
     */
    @Getter(onMethod = @__(@NotNull))
    private final String permission;

    /**
     * the valid arguments of this command
     */
    @Getter(onMethod = @__(@NotNull))
    private final String[] args;

    @Getter(onMethod = @__(@NotNull))
    private final boolean requiresPlayer;

    public RCommand(@NotNull String name, @NotNull String permission, boolean requiresPlayer, @NotNull String... args) throws ClassNotAnnotatedException {
        super(name);

        this.name = name;
        this.permission = permission;
        this.requiresPlayer = requiresPlayer;
        this.args = args;
    }

    /**
     * called when this command is executed with only the base command (/commandname)
     * @param sender the sender
     * @return the status of this command execution
     */
    @NotNull
    protected abstract CommandReturnState executeBaseCommand(@NotNull CommandSender sender) throws Exception;

    /**
     * called when this command is executed with arguments (/command name arg1 arg2 arg3)
     * @param sender the sender
     * @param args the arguments
     * @param values the values of any unknown arguments
     * @return the status of this command execution
     */
    @NotNull
    protected abstract CommandReturnState executeWithArgs(@NotNull CommandSender sender, @NotNull String args, @NotNull String[] values) throws Exception;

    /**
     * Returns the Help Screen for this Command, excluding the Plugin Prefix
     */
    @NotNull
    @Unmodifiable
    protected abstract List<String> help(@NotNull CommandSender sender);

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if(!this.permission.isBlank()) {
            if(!sender.hasPermission(this.permission)) {
                sender.sendMessage(
                        TextComponent.fromLegacyText(
                                RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getPlayerNoPermissionErrorMessage()
                        )
                );

                return;
            }
        }

        if(this.requiresPlayer) {
            if(!(sender instanceof ProxiedPlayer)) {
                sender.sendMessage(
                        TextComponent.fromLegacyText(
                                RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getCommandOnlyPlayerErrorMessage()
                        )
                );

                return;
            }
        }

        try {
            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("help")) {
                    sendHelpScreen(sender);

                    return;
                }
            }

            // Create List of all valid Command Arguments in all lower case
            final List<String> commandArgs = new ArrayList<>(Stream.of(this.args).map(String::toLowerCase).toList());

            final List<String> commandValues = new ArrayList<>();
            final StringBuilder builder = new StringBuilder();

            for(String arg : args) {
                boolean contains = false;

                for(String commandArg : commandArgs) {
                    if(Arrays.asList(commandArg.split(" ")).contains(arg.toLowerCase())) {
                        contains = true;
                    }
                }

                if(contains) {
                    builder.append(builder.length() > 0 ? " " : "").append(arg);
                }else {
                    builder.append(builder.length() > 0 ? " " : "").append("?");
                    commandValues.add(arg);
                }

                contains = false;
            }

            final CommandReturnState commandReturnState;

            if(builder.length() > 0) {
                commandReturnState = executeWithArgs(sender, builder.toString(), commandValues.toArray(new String[0]));
            }else {
                commandReturnState = executeBaseCommand(sender);
            }

            if(commandReturnState == CommandReturnState.ERROR) {
                sender.sendMessage(
                        TextComponent.fromLegacyText(
                                RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getCommandErrorMessage()
                        )
                );
            }else if(commandReturnState == CommandReturnState.INVALID_ARGS) {
                sender.sendMessage(
                        TextComponent.fromLegacyText(
                                RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getCommandInvalidArgsErrorMessage()
                        )
                );
            }
        }catch(Exception ex) {
            sender.sendMessage(
                    TextComponent.fromLegacyText(
                            RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getCommandInternalErrorMessage()
                    )
            );
            sender.sendMessage(
                    TextComponent.fromLegacyText(
                            ChatColor.RED.toString() + ex
                    )
            );
        }
    }

    @NotNull
    public final Iterable<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        final List<String> tabCompleted = new ArrayList<>();

        if(args.length == 1) {
            tabCompleted.add("help");
        }

        for(String arg : this.args) {
            final String[] originalArgs = arg.split(" ");
            final String[] editedArgs = originalArgs.clone();

            if(originalArgs.length > args.length-1) {
                for(int i = 0; i < args.length; i++) {
                    if (!originalArgs[i].equals("%PLAYER%") &&
                            !originalArgs[i].equals("%BOOLEAN%")) {
                        continue;
                    }

                    editedArgs[i] = args[i];
                }

                final String finalArg = originalArgs[args.length-1];

                if(!String.join(" ", editedArgs).startsWith(String.join(" ", args))) {
                    continue;
                }

                if(finalArg.equalsIgnoreCase(RPlugin.getInstance().PLAYER_IDENTIFIER)) {
                    for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                        if(tabCompleted.contains(player.getName())) {
                            continue;
                        }

                        tabCompleted.add(player.getName());
                    }
                }else if(finalArg.equalsIgnoreCase("%NUMBER%")) {
                    tabCompleted.add("0");
                }else if(finalArg.equalsIgnoreCase("%BOOLEAN%")) {
                    tabCompleted.add("<true|false>");
                }else if(finalArg.startsWith("%") && finalArg.endsWith("%")) {
                    tabCompleted.add("<" + finalArg.replaceAll("%", "") + ">");
                }else {
                    tabCompleted.add(finalArg);
                }

            }
        }

        return tabCompleted;
    }

    private void sendHelpScreen(@NotNull CommandSender sender) {
        for(String line : help(sender)) {
            sender.sendMessage(
                    TextComponent.fromLegacyText(
                            RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getChatColor() + line
                    )
            );
        }
    }
}
