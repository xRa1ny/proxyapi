package me.xra1ny.proxyapi.models.config;

import lombok.Getter;
import me.xra1ny.proxyapi.RPlugin;
import me.xra1ny.proxyapi.exceptions.config.ConfigAlreadyRegisteredException;
import me.xra1ny.proxyapi.exceptions.config.ConfigNotRegisteredException;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ConfigManager {
    @Getter(onMethod = @__(@NotNull))
    private final List<RConfig> configs = new ArrayList<>();

    public void register(@NotNull RConfig config) throws ConfigAlreadyRegisteredException {
        if(this.configs.contains(config)) {
            throw new ConfigAlreadyRegisteredException(config);
        }

        this.configs.add(config);
    }

    public void registerAll(@NotNull String packageName) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ConfigAlreadyRegisteredException {
        RPlugin.getInstance().getLogger().log(Level.INFO, "attempting to register all configs in package " + packageName + "...");

        for(Class<? extends RConfig> configClass : new Reflections(packageName).getSubTypesOf(RConfig.class)) {
            register(configClass.getDeclaredConstructor().newInstance());
        }
    }

    public void unregister(@NotNull RConfig config) throws ConfigNotRegisteredException {
        if(!this.configs.contains(config)) {
            throw new ConfigNotRegisteredException(config);
        }

        this.configs.remove(config);
    }

    public <T extends RConfig> T get(@NotNull Class<T> type) {
        return (T) this.configs.stream()
                .filter(config -> config.getClass().equals(type))
                .findFirst()
                .orElse(null);
    }
}
