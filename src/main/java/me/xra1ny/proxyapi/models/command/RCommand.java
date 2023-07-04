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

import java.lang.reflect.Method;
import java.util.*;
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
    private final CommandArg[] args;

    @Getter(onMethod = @__(@NotNull))
    private final boolean requiresPlayer;

    public RCommand(@NotNull String name, @NotNull String permission, boolean requiresPlayer, @NotNull CommandArg... args) throws ClassNotAnnotatedException {
        super(name);

        this.name = name;
        this.permission = permission;
        this.requiresPlayer = requiresPlayer;
        this.args = args;
    }

    /**
     * called when this command is executed with only the base command (/command)
     * @param sender the sender
     * @return the status of this command execution
     */
    @NotNull
    protected abstract CommandReturnState executeBaseCommand(@NotNull CommandSender sender) throws Exception;

    /**
     * Returns the Help Screen for this Command, excluding the Plugin Prefix
     */
    @NotNull
    @Unmodifiable
    protected abstract List<String> help(@NotNull CommandSender sender);

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if(!this.permission.isBlank() && !sender.hasPermission(this.permission)) {
            RPlugin.sendMessage(sender, RPlugin.getInstance().getPlayerNoPermissionErrorMessage());

            return;
        }

        if(this.requiresPlayer) {
            if(!(sender instanceof ProxiedPlayer)) {
                RPlugin.sendMessage(sender, RPlugin.getInstance().getCommandOnlyPlayerErrorMessage());

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

            for(CommandArg arg : this.args) {
                final String[] originalArgs = arg.getValue().split(" ");
                final String[] editedArgs = originalArgs.clone();

                if(originalArgs.length >= args.length) {
                    for(int i = 0; i < args.length; i++) {
                        if (!originalArgs[i].startsWith("%") && !originalArgs[i].endsWith("%") && !originalArgs[i].endsWith("*")) {
                            continue;
                        }

                        editedArgs[i] = args[i];
                    }
                }
            }

            boolean varArg = false;
            int varArgs = 0;
            int varArgsIndex = Integer.MIN_VALUE;
            final Map<String, CommandArg> argMap = new HashMap<>();

            for(CommandArg arg : this.args) {
                String fullArg = arg.getValue().replaceAll("%.*%", "?");
                final List<String> originalArgsList = List.of(arg.getValue().split(" "));

                if(originalArgsList.size() >= args.length || originalArgsList.get(originalArgsList.size()-1).endsWith("%*")) {
                    final String finalArg = originalArgsList.get(originalArgsList.size()-1);

                    if(List.of(args).size() >= originalArgsList.size() && finalArg.endsWith("%*")) {
                        fullArg+="*";
                        varArg = true;

                        if(varArgsIndex == Integer.MIN_VALUE) {
                            varArgsIndex = originalArgsList.size()-1;
                        }
                    }

                    argMap.put(fullArg, arg);
                }
            }

            final List<String> commandArgs = new ArrayList<>(Stream.of(this.args)
                    .map(CommandArg::getValue)
                    .map(String::toLowerCase)
                    .toList());
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
                    if(!varArg || varArgs == 0) {
                        builder.append(builder.length() > 0 ? " " : "").append("?");
                    }

                    if(List.of(args).indexOf(arg) >= varArgsIndex) {
                        builder.append(varArg && varArgs == 0 ? "*" : "");
                        varArgs++;
                    }

                    commandValues.add(arg);
                }
            }

            final CommandArg arg = argMap.get(builder.toString());

            if(arg != null) {
                if(!arg.getPermission().isBlank() && !sender.hasPermission(arg.getPermission())) {
                    RPlugin.sendMessage(sender, RPlugin.getInstance().getPlayerNoPermissionErrorMessage());

                    return;
                }
            }

            CommandReturnState commandReturnState = CommandReturnState.INVALID_ARGS;

            if(builder.length() > 0) {
                for(Method method : getClass().getMethods()) {
                    final CommandArgHandler handler = method.getDeclaredAnnotation(CommandArgHandler.class);

                    if(handler == null || !Arrays.asList(handler.value()).contains(builder.toString())) {
                        continue;
                    }

                    commandReturnState = (CommandReturnState) method.invoke(this, sender, builder.toString(), commandValues.toArray(new String[0]));

                    break;
                }
            }else {
                commandReturnState = executeBaseCommand(sender);
            }

            if(commandReturnState == CommandReturnState.ERROR) {
                sender.sendMessage(RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getCommandErrorMessage());
            }else if(commandReturnState == CommandReturnState.INVALID_ARGS) {
                sender.sendMessage(RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getCommandInvalidArgsErrorMessage());
            }

            return;
        }catch(Exception ex) {
            sender.sendMessage(RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getCommandInternalErrorMessage());
            sender.sendMessage(ChatColor.RED.toString() + ex);
        }

        return;
    }

    @NotNull
    public abstract List<String> onCommandTabComplete(@NotNull CommandSender sender, @NotNull String args);

    @NotNull
    @Override
    public final Iterable<String> onTabComplete(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        final List<String> tabCompleted = new ArrayList<>();

        if(args.length == 1) {
            tabCompleted.add("help");
        }

        for(CommandArg arg : this.args) {
            final String[] originalArgs = arg.getValue().split(" ");
            final String[] editedArgs = originalArgs.clone();

            if(originalArgs.length >= args.length || originalArgs[originalArgs.length-1].endsWith("%*")) {
                for(int i = 0; i < args.length; i++) {
                    final String originalArg = i >= originalArgs.length ? originalArgs[originalArgs.length-1] : originalArgs[i];

                    if (!originalArg.startsWith("%") && !(originalArg.endsWith("%") || originalArg.endsWith("%*"))) {
                        continue;
                    }

                    editedArgs[i >= editedArgs.length ? editedArgs.length-1 : i] = args[i];
                }

                final String finalArg = originalArgs[args.length-1 >= originalArgs.length ? originalArgs.length-1 : args.length-1];

                if(!String.join(" ", editedArgs).startsWith(String.join(" ", args))) {
                    continue;
                }

                if(finalArg.startsWith("%") && finalArg.endsWith("%*")) {
                    tabCompleted.add(finalArg.replace("%", "").replace("%*", ""));

                    break;
                }

                if(finalArg.equalsIgnoreCase(CommandArg.PLAYER)) {
                    for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                        if(tabCompleted.contains(player.getName())) {
                            continue;
                        }

                        tabCompleted.add(player.getName());
                    }
                }else if(finalArg.startsWith(CommandArg.NUMBER)) {
                    tabCompleted.add("0");
                }else if(finalArg.startsWith(CommandArg.BOOLEAN)) {
                    tabCompleted.add("true");
                    tabCompleted.add("false");
                }else if(finalArg.startsWith("%") && (finalArg.endsWith("%") || finalArg.endsWith("%*"))) {
                    tabCompleted.add(finalArg.replace("%", "").replace("%*", ""));
                }else {
                    tabCompleted.add(finalArg);
                }
            }
        }

        final List<String> commandArgs = new ArrayList<>(Stream.of(this.args)
                .map(CommandArg::getValue)
                .map(String::toLowerCase)
                .toList());
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
            }
        }

        tabCompleted.addAll(onCommandTabComplete(sender, builder.toString()));

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
