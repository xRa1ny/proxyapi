package me.xra1ny.proxyapi.models.command;

import lombok.Getter;
import me.xra1ny.proxyapi.RPlugin;
import me.xra1ny.proxyapi.exceptions.CommandAlreadyRegisteredException;
import net.md_5.bungee.api.ProxyServer;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class CommandManager {
    /**
     * all currently registered commands
     */
    @Getter(onMethod = @__(@NotNull))
    private final List<RCommand> commands = new ArrayList<>();

    /**
     * checks if the command specified is registered or not
     * @param command the command
     * @return true if the command specified is registered, false otherwise
     */
    public boolean isRegistered(@NotNull RCommand command) {
        return this.commands.contains(command);
    }

    /**
     * registers the specified command
     * @param command the command
     * @throws CommandAlreadyRegisteredException if the command specified is already registered
     */
    public void register(@NotNull RCommand command) throws CommandAlreadyRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to register command " + command + "...");

        if(isRegistered(command)) {
            throw new CommandAlreadyRegisteredException(command);
        }

        ProxyServer.getInstance().getPluginManager().registerCommand(RPlugin.getInstance(), command);
        this.commands.add(command);
        RPlugin.getInstance().getLogger().log(Level.INFO, "command " + command + " successfully registered!");
    }

    /**
     * registers all command within the package specified
     * @param packageName the package name
     * @throws NoSuchMethodException if any command found within the package specified does not have a constructor of signature ()
     * @throws InvocationTargetException if an exception occurs while constructing any command found within the package specified
     * @throws InstantiationException if any command found within the package specified could not be instantiated (abstract)
     * @throws IllegalAccessException if any command found within the package specified constructor is inaccessible
     * @throws CommandAlreadyRegisteredException if any command found within the package specified is already registered
     */
    public void registerAll(@NotNull String packageName) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, CommandAlreadyRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to register all commands in package " + packageName + "...");

        for(Class<? extends RCommand> commandClass : new Reflections(packageName).getSubTypesOf(RCommand.class)) {
            register(commandClass.getDeclaredConstructor().newInstance());
        }
    }
}
