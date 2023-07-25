package me.xra1ny.proxyapi.models.localisation;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LocalisationManager {
    private final Map<String, Configuration> configs = new HashMap<>();

    public LocalisationManager(@NotNull String... configUrls) throws IOException {
        for(String configUrl : configUrls) {
            final File configFile = new File(configUrl);
            final Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

            this.configs.put(configFile.getName(), config);
        }
    }

    @Nullable
    public String get(@NotNull String configName, @NotNull String key, @NotNull Replacement... replacements) {
        String value = configs.get(configName).getString(key);

        if(value == null) {
            return null;
        }

        for(Replacement replacement : replacements) {
            value = value.replace(replacement.getKey(), replacement.getValue());
        }

        return value;
    }
}
