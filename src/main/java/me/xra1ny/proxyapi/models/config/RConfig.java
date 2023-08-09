package me.xra1ny.proxyapi.models.config;

import lombok.Getter;
import lombok.SneakyThrows;
import me.xra1ny.proxyapi.exceptions.ClassNotAnnotatedException;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.reflections.ReflectionUtils;

import java.io.File;
import java.lang.reflect.Field;

public abstract class RConfig {
    @Getter(onMethod = @__(@NotNull))
    private final String name;

    @Getter(onMethod = @__(@NotNull))
    private final File configFile;

    @Getter(onMethod = @__(@NotNull))
    private final Configuration config;

    @SneakyThrows
    public RConfig() {
        final ConfigInfo info = getClass().getDeclaredAnnotation(ConfigInfo.class);

        if (info == null) {
            throw new ClassNotAnnotatedException(getClass(), ConfigInfo.class);
        }

        this.configFile = new File(info.value());
        this.configFile.createNewFile();
        this.name = this.configFile.getName();
        this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.configFile);
    }

    @SneakyThrows
    public RConfig(@NotNull String configFileName) {
        this.configFile = new File(configFileName);
        this.configFile.createNewFile();
        this.name = this.configFile.getName();
        this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.configFile);
    }

    public <T> T get(@NotNull Class<T> type, @NotNull String key) {
        final Field field = ReflectionUtils.getAllFields(getClass()).stream()
                .filter(_field -> _field.getDeclaredAnnotation(Path.class) != null)
                .filter(_field -> _field.getType().equals(type))
                .filter(_field -> _field.getDeclaredAnnotation(Path.class).value().equalsIgnoreCase(key))
                .findFirst().orElse(null);

        if (field == null) {
            return null;
        }

        final Path path = field.getAnnotation(Path.class);

        return (T) this.config.get(path.value());
    }

    @SneakyThrows
    public void save() {
        for (Field field : ReflectionUtils.getAllFields(getClass())) {
            final Path path = field.getAnnotation(Path.class);

            if (path == null) {
                continue;
            }

            field.setAccessible(true);
            this.config.set(path.value(), field.get(this));
        }

        ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.config, this.configFile);
    }

    @SneakyThrows
    public void update() {
        for (Field field : ReflectionUtils.getAllFields(getClass())) {
            final Path path = field.getAnnotation(Path.class);

            if (path == null) {
                continue;
            }

            field.setAccessible(true);
            field.set(this, this.config.get(path.value()));
        }
    }
}
