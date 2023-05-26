package me.xra1ny.proxyapi.models.listener;

import lombok.Getter;
import me.xra1ny.proxyapi.RPlugin;
import me.xra1ny.proxyapi.exceptions.ListenerAlreadyRegisteredException;
import me.xra1ny.proxyapi.exceptions.ListenerNotRegisteredException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ListenerManager {
    /**
     * all currently registered listeners
     */
    @Getter(onMethod = @__(@NotNull))
    private final List<Listener> listeners = new ArrayList<>();

    /**
     * checks if the listener specified is registered or not
     * @param listener the listener
     * @return true if the listener specified is registered, false otherwise
     */
    public boolean isRegistered(@NotNull Listener listener) {
        return this.listeners.contains(listener);
    }

    /**
     * registers the listener specified
     * @param listener the listener
     * @throws ListenerAlreadyRegisteredException if the listener specified is already registered
     */
    public void register(@NotNull Listener listener) throws ListenerAlreadyRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to register listener " + listener + "...");

        if(isRegistered(listener)) {
            throw new ListenerAlreadyRegisteredException(listener);
        }

        ProxyServer.getInstance().getPluginManager().registerListener(RPlugin.getInstance(), listener);

        this.listeners.add(listener);

        RPlugin.getInstance().getLogger().log(Level.INFO, "listener " + listener + " successfully registered!");
    }

    /**
     * registers all listeners within the package specified
     * @param packageName the package name
     * @throws NoSuchMethodException if any listener found within the package specified does not have a constructor of signature ()
     * @throws InvocationTargetException if an exception occurs while constructing any listener found within the package specified
     * @throws InstantiationException if any listener found within the package specified could not be instantiated (abstract)
     * @throws IllegalAccessException if any listener found within the package specified constructor is inaccessible
     * @throws ListenerAlreadyRegisteredException if any listener found within the package specified is already registered
     */
    public void registerAll(@NotNull String packageName) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ListenerAlreadyRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to register all listeners in package " + packageName + "...");

        for(Class<? extends Listener> listenerClass : new Reflections(packageName).getSubTypesOf(Listener.class)) {
            register(listenerClass.getDeclaredConstructor().newInstance());
        }
    }

    /**
     * unregisters the listener specified
     * @param listener the listener
     * @throws ListenerNotRegisteredException if the listener specified is not yet registered
     */
    public void unregister(@NotNull Listener listener) throws ListenerNotRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregister listener " + listener + "...");

        if(!isRegistered(listener)) {
            throw new ListenerNotRegisteredException(listener);
        }

        ProxyServer.getInstance().getPluginManager().unregisterListener(listener);

        this.listeners.remove(listener);

        RPlugin.getInstance().getLogger().log(Level.INFO, "listener " + listener + " successfully unregistered!");
    }

    /**
     * unregisters all listeners registered within the package name specified
     * @param packageName the package name
     * @throws ListenerNotRegisteredException if any listener found within the package specified is not yet registered
     */
    public void unregisterAll(@NotNull String packageName) throws ListenerNotRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregister all listeners in package " + packageName + "...");

        for(Listener listener : this.listeners) {
            if(!listener.getClass().getPackage().getName().equals(packageName)) {
                continue;
            }

            unregister(listener);
        }
    }

    /**
     * unregisters all registered listeners
     * @throws ListenerNotRegisteredException if any listener is not yet registered
     */
    public void unregisterAll() throws ListenerNotRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to unregister all listeners...");

        for(Listener listener : this.listeners) {
            unregister(listener);
        }
    }
}
