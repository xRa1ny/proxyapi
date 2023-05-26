package me.xra1ny.proxyapi.models.command;

import me.xra1ny.proxyapi.RPlugin;
import me.xra1ny.proxyapi.exceptions.CommandAlreadyRegisteredException;
import me.xra1ny.proxyapi.exceptions.CommandNotRegisteredException;
import lombok.Getter;
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

    /**
     * unregisters the command specified
     * @param command the command
     * @throws CommandNotRegisteredException if the command specified is not yet registered
     */
    public void unregister(@NotNull RCommand command) throws CommandNotRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregister command " + command + "...");

        if(!isRegistered(command)) {
            throw new CommandNotRegisteredException(command);
        }

        this.commands.remove(command);

        RPlugin.getInstance().getLogger().log(Level.INFO, "command " + command + " successfully unregistered!");
    }

    /**
     * unregisters all command within the package specified
     * @param packageName the package name
     * @throws CommandNotRegisteredException if any command within the package specified is not yet registered
     */
    public void unregisterAll(@NotNull String packageName) throws CommandNotRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregister all commands in package " + packageName + "...");

        for(RCommand command : this.commands) {
            if(!command.getClass().getPackage().getName().equals(packageName)) {
                continue;
            }

            unregister(command);
        }
    }

    /**
     * unregisters all commands
     * @throws CommandNotRegisteredException if any command is not yet registered
     */
    public void unregisterAll() throws CommandNotRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregister all commands...");

        for(RCommand command : this.commands) {
            unregister(command);
        }
    }

    /**
     * retrieves all commands with the command name specified
     * @param commandName the command name
     * @return all commands with the command name specified
     */
    @NotNull
    public List<RCommand> getAll(@NotNull String commandName) {
        return this.commands.stream()
                .filter(command -> command.getName().equals(commandName))
                .toList();
    }
}
