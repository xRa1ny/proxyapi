package me.xra1ny.proxyapi.models.localisation;

import me.xra1ny.proxyapi.models.config.RConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class LocalisationManager {
    private final Map<Class<? extends RConfig>, RConfig> configs = new HashMap<>();

    @SafeVarargs
    public LocalisationManager(@NotNull Class<? extends RConfig>... configClasses) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        for(Class<? extends RConfig> configClass : configClasses) {
            final RConfig config = configClass.getDeclaredConstructor().newInstance();

            config.update();
            this.configs.put(configClass, config);
        }
    }

    @Nullable
    public String get(@NotNull Class<? extends RConfig> configClass, @NotNull String key, @NotNull Replacement... replacements) {
        String value = configs.get(configClass).get(String.class, key);

        if(value == null) {
            return null;
        }

        for(Replacement replacement : replacements) {
            value = value.replace(replacement.getKey(), replacement.getValue());
        }

        return value;
    }
}
