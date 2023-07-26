package me.xra1ny.proxyapi;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.proxyapi.models.color.HexCodeManager;
import me.xra1ny.proxyapi.models.command.CommandManager;
import me.xra1ny.proxyapi.models.config.ConfigManager;
import me.xra1ny.proxyapi.models.config.RConfig;
import me.xra1ny.proxyapi.models.listener.ListenerManager;
import me.xra1ny.proxyapi.models.localisation.LocalisationManager;
import me.xra1ny.proxyapi.models.maintenance.ProxyMaintenanceManager;
import me.xra1ny.proxyapi.models.party.PartyManager;
import me.xra1ny.proxyapi.models.user.RUser;
import me.xra1ny.proxyapi.models.user.RUserManager;
import me.xra1ny.proxyapi.models.user.UserInputWindowManager;
import me.xra1ny.proxyapi.utils.ConfigKeys;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

/** Contains all Components to develop a RainyMC Proxy Plugin (Must be extended from in your desired Main-Class) */
@Slf4j
public abstract class RPlugin extends Plugin {
    /**
     * the singleton instance access point of this plugin
     */
    @Getter(onMethod = @__(@NotNull))
    private static RPlugin instance;

    private boolean maintenanceEnabled;

    private String maintenanceMessage;

    /**
     * the config setting whether to use mysql or not
     */
    @Getter
    private boolean mysqlEnabled;

    @Getter
    private boolean forcePrefix;

    /**
     * the global prefix of this plugin
     */
    @Getter(onMethod = @__(@NotNull))
    @Setter(value = AccessLevel.PROTECTED, onParam = @__(@NotNull))
    private String prefix;

    @Getter
    private boolean forceChatColor;

    /**
     * the global chat color of this plugin
     */
    @Getter(onMethod = @__(@NotNull))
    @Setter(value = AccessLevel.PROTECTED, onParam = @__(@NotNull))
    private ChatColor chatColor;

    @Getter
    private boolean forceCommandErrorMessage;

    /**
     * the global message to display when an error occurs while executing a command
     */
    @Getter(onMethod = @__(@NotNull))
    @Setter(value = AccessLevel.PROTECTED, onParam = @__(@NotNull))
    private String commandErrorMessage;

    @Getter
    private boolean forceCommandInternalErrorMessage;

    /**
     * the global message to display when an internal error occurs while executing a command
     */
    @Getter(onMethod = @__(@NotNull))
    @Setter(value = AccessLevel.PROTECTED, onParam = @__(@NotNull))
    private String commandInternalErrorMessage;

    @Getter
    private boolean forceCommandInvalidArgsErrorMessage;

    /**
     * the global message to display when a command has been passed invalid arguments
     */
    @Getter(onMethod = @__(@NotNull))
    @Setter(value = AccessLevel.PROTECTED, onParam = @__(@NotNull))
    private String commandInvalidArgsErrorMessage;

    @Getter
    private boolean forcePlayerNoPermissionErrorMessage;

    /**
     * the global error message displayed when a player does not have the permissions to perform an action
     */
    @Getter(onMethod = @__(@NotNull))
    @Setter(value = AccessLevel.PROTECTED, onParam = @__(@NotNull))
    private String playerNoPermissionErrorMessage;

    @Getter
    private boolean forcePlayerNotFoundErrorMessage;

    @Getter(onMethod = @__(@NotNull))
    @Setter(value = AccessLevel.PROTECTED, onParam = @__(@NotNull))
    private String playerNotFoundErrorMessage;

    @Getter
    private boolean forceCommandOnlyPlayerErrorMessage;

    /**
     * the global error message displayed when the console performs a command that can only be executed by a player
     */
    @Getter(onMethod = @__(@NotNull))
    @Setter(value = AccessLevel.PROTECTED, onParam = @__(@NotNull))
    private String commandOnlyPlayerErrorMessage;

    @Getter
    private boolean forceUserTimeout;

    private long userTimeout;

    /**
     * the file of this plugins config
     */
    @Getter(onMethod = @__(@NotNull))
    private File configFile;

    /**
     * the config of this plugin
     */
    @Getter(onMethod = @__(@NotNull))
    private Configuration config;

    @Getter(onMethod = @__(@NotNull))
    private ListenerManager listenerManager;

    /**
     * the command manager responsible for storing and managing all commands of this plugin
     */
    @Getter(onMethod = @__(@NotNull))
    private CommandManager commandManager;

    /**
     * the user manager responsible for storing and managing all users of this plugin
     */
    private RUserManager userManager;

    /**
     * the maintenance manager responsible for storing all maintenance related information and managing them
     */
    private ProxyMaintenanceManager proxyMaintenanceManager;

    /**
     * the user input window manager responsible for storing and managing all user input windows of this plugin
     */
    @Getter(onMethod = @__(@NotNull))
    private UserInputWindowManager userInputWindowManager;

    /**
     * the hex code manager responsible for creating strings with color gradients of this plugin
     */
    @Getter(onMethod = @__(@NotNull))
    private HexCodeManager hexCodeManager;

    /**
     * the party manager responsible for creating and managing parties
     */
    @Getter(onMethod = @__(@NotNull))
    private PartyManager partyManager;

    @Getter(onMethod = @__(@NotNull))
    private LocalisationManager localisationManager;

    @Getter(onMethod = @__(@NotNull))
    private ConfigManager configManager;

    public final String PLAYER_IDENTIFIER = "%PLAYER%";

    /**
     * called when this plugin enables
     */
    public abstract void onPluginEnable() throws Exception;

    /**
     * called when this plugin disables
     */
    public abstract void onPluginDisable() throws Exception;

    @Override
    public final void onEnable() {
        try {
            getLogger().log(Level.INFO, "attempting to enable proxyapi...");
            RPlugin.instance = this;

            final PluginInfo info = getClass().getDeclaredAnnotation(PluginInfo.class);

            Class<? extends RUser> userClass = RUser.class;
            Class<? extends RUserManager> userManagerClass = RUserManager.class;
            String[] localisationConfigUrls = {};

            if(info != null) {
                userClass = info.userClass();
                userManagerClass = info.userManagerClass();
                localisationConfigUrls = info.localisationConfigUrls();
            }

            this.configFile = new File(getDataFolder(), "config.yml");
            createConfig();
            setupConfig();
            this.listenerManager = new ListenerManager();
            this.commandManager = new CommandManager();
            this.userManager = userManagerClass.getDeclaredConstructor(Class.class, long.class).newInstance(userClass, this.userTimeout);
            this.proxyMaintenanceManager = new ProxyMaintenanceManager(this.maintenanceEnabled, this.maintenanceMessage);
            this.userInputWindowManager = new UserInputWindowManager();
            this.hexCodeManager = new HexCodeManager();
            this.partyManager = new PartyManager();
            this.localisationManager = new LocalisationManager(localisationConfigUrls);
            this.configManager = new ConfigManager();
            this.listenerManager.registerAll("me.xra1ny.proxyapi.listeners");
            getLogger().log(Level.INFO, "proxyapi successfully enabled!");

            try {
                getLogger().log(Level.INFO, "attempting to enable external plugin...");
                onPluginEnable();
                saveConfig();

                for(RConfig _config : this.configManager.getConfigs()) {
                    _config.update();
                }

                getLogger().log(Level.INFO, "external plugin successfully enabled!");
            }catch(Exception ex) {
                getLogger().log(Level.SEVERE, "error while enabling external plugin!", ex);
            }
        }catch(Exception ex) {
            getLogger().log(Level.SEVERE, "error while enabling proxyapi!", ex);
        }
    }

    @Override
    public final void onDisable() {
        try {
            getLogger().log(Level.INFO, "attempting to disable proxyapi...");
            getLogger().log(Level.INFO, "proxyapi successfully disabled!");

            try {
                getLogger().log(Level.INFO, "attempting to disable external plugin...");
                onPluginDisable();
                saveConfig();
                getLogger().log(Level.INFO, "external plugin successfully disabled!");
            }catch(Exception ex) {
                getLogger().log(Level.SEVERE, "error while disabling external plugin!", ex);
            }
        } catch (Exception e) {
            getLogger().log(Level.INFO, "error while disabling proxyapi!", e);
        }
    }

    private void createConfig() {
        try {
            getLogger().log(Level.INFO, "attempting to create config...");

            if(!this.configFile.exists()) {
                getDataFolder().mkdirs();
                this.configFile.getParentFile().mkdirs();
                this.configFile.createNewFile();

                final FileOutputStream out = new FileOutputStream(this.configFile);
                final InputStream in = getResourceAsStream("config.yml");

                in.transferTo(out);
            }else {
                getLogger().log(Level.INFO, "config already exists!");
            }

            getLogger().log(Level.INFO, "loading config...");
            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.configFile);
            getLogger().log(Level.INFO, "config successfully loaded!");
        }catch (IOException ex) {
            getLogger().log(Level.INFO, "error while creating config!", ex);
        }
    }

    private void setupConfig() {
        getLogger().log(Level.INFO, "attempting to setup config...");

        getLogger().setLevel(Level.parse(getConfig().getString(ConfigKeys.LOGGING_LEVEL, "ALL")));
        getConfig().set(ConfigKeys.LOGGING_LEVEL, getLogger().getLevel().toString());

        this.maintenanceEnabled = getConfig().getBoolean(ConfigKeys.MAINTENANCE_ENABLED, false);
        getConfig().set(ConfigKeys.MAINTENANCE_ENABLED, this.maintenanceEnabled);

        this.maintenanceMessage = getConfig().getString(ConfigKeys.MAINTENANCE_MESSAGE, "§lMaintenance!");
        getConfig().set(ConfigKeys.MAINTENANCE_MESSAGE, this.maintenanceMessage);

        this.mysqlEnabled = getConfig().getBoolean(ConfigKeys.MYSQL_ENABLED, false);
        getConfig().set(ConfigKeys.MYSQL_ENABLED, this.mysqlEnabled);

        this.forcePrefix = getConfig().getBoolean(ConfigKeys.NON_MYSQL_PREFIX_FORCE, false);
        getConfig().set(ConfigKeys.NON_MYSQL_PREFIX_FORCE, this.forcePrefix);

        if(!this.mysqlEnabled || this.forcePrefix) {
            this.prefix = getConfig().getString(ConfigKeys.NON_MYSQL_PREFIX_VALUE, "§lMyPlugin  ");
            getConfig().set(ConfigKeys.NON_MYSQL_PREFIX_VALUE, this.prefix);
        }


        this.forceChatColor = getConfig().getBoolean(ConfigKeys.NON_MYSQL_CHAT_COLOR_FORCE, false);
        getConfig().set(ConfigKeys.NON_MYSQL_CHAT_COLOR_FORCE, this.forceChatColor);

        if(!this.mysqlEnabled || this.forceChatColor) {
            this.chatColor = ChatColor.valueOf(getConfig().getString(ConfigKeys.NON_MYSQL_CHAT_COLOR_VALUE, String.valueOf(ChatColor.GRAY)));
            getConfig().set(ConfigKeys.NON_MYSQL_CHAT_COLOR_VALUE, this.chatColor.name());
        }

        this.forcePlayerNoPermissionErrorMessage = getConfig().getBoolean(ConfigKeys.NON_MYSQL_PLAYER_NO_PERMISSION_ERROR_MESSAGE_FORCE, false);
        getConfig().set(ConfigKeys.NON_MYSQL_PLAYER_NO_PERMISSION_ERROR_MESSAGE_FORCE, this.forcePlayerNoPermissionErrorMessage);

        if(!this.mysqlEnabled || this.forcePlayerNoPermissionErrorMessage) {
            this.playerNoPermissionErrorMessage = getConfig().getString(ConfigKeys.NON_MYSQL_PLAYER_NO_PERMISSION_ERROR_MESSAGE_VALUE, "§l§cERROR! §r§cYou don't have permission to perform this action!");
            getConfig().set(ConfigKeys.NON_MYSQL_PLAYER_NO_PERMISSION_ERROR_MESSAGE_VALUE, this.playerNoPermissionErrorMessage);
        }

        this.forcePlayerNotFoundErrorMessage = getConfig().getBoolean(ConfigKeys.NON_MYSQL_PLAYER_NOT_FOUND_ERROR_MESSAGE_FORCE, false);
        getConfig().set(ConfigKeys.NON_MYSQL_PLAYER_NOT_FOUND_ERROR_MESSAGE_FORCE, this.forcePlayerNotFoundErrorMessage);

        if(!this.mysqlEnabled || this.forcePlayerNotFoundErrorMessage) {
            this.playerNotFoundErrorMessage = getConfig().getString(ConfigKeys.NON_MYSQL_PLAYER_NOT_FOUND_ERROR_MESSAGE_VALUE, "§l§cERROR! The specified player could not be found!");
            getConfig().set(ConfigKeys.NON_MYSQL_PLAYER_NOT_FOUND_ERROR_MESSAGE_VALUE, this.playerNotFoundErrorMessage);
        }

        this.forceCommandOnlyPlayerErrorMessage = getConfig().getBoolean(ConfigKeys.NON_MYSQL_COMMAND_ONLY_PLAYER_ERROR_MESSAGE_FORCE, false);
        getConfig().set(ConfigKeys.NON_MYSQL_COMMAND_ONLY_PLAYER_ERROR_MESSAGE_FORCE, this.forceCommandOnlyPlayerErrorMessage);

        if(!this.mysqlEnabled || this.forceCommandOnlyPlayerErrorMessage) {
            this.commandOnlyPlayerErrorMessage = getConfig().getString(ConfigKeys.NON_MYSQL_COMMAND_ONLY_PLAYER_ERROR_MESSAGE_VALUE, "§l§cERROR! §r§cThis command can only be executed by a player!");
            getConfig().set(ConfigKeys.NON_MYSQL_COMMAND_ONLY_PLAYER_ERROR_MESSAGE_VALUE, this.commandOnlyPlayerErrorMessage);
        }

        this.forceCommandErrorMessage = getConfig().getBoolean(ConfigKeys.NON_MYSQL_COMMAND_ERROR_MESSAGE_FORCE, false);
        getConfig().set(ConfigKeys.NON_MYSQL_COMMAND_ERROR_MESSAGE_FORCE, this.forceCommandErrorMessage);

        if(!this.mysqlEnabled || this.forceCommandErrorMessage) {
            this.commandErrorMessage = getConfig().getString(ConfigKeys.NON_MYSQL_COMMAND_ERROR_MESSAGE_VALUE, "§l§cERROR! §r§cError while executing command!");
            getConfig().set(ConfigKeys.NON_MYSQL_COMMAND_ERROR_MESSAGE_VALUE, this.commandErrorMessage);
        }

        this.forceCommandInvalidArgsErrorMessage = getConfig().getBoolean(ConfigKeys.NON_MYSQL_COMMAND_INVALID_ARGS_ERROR_MESSAGE_FORCE, false);
        getConfig().set(ConfigKeys.NON_MYSQL_COMMAND_INVALID_ARGS_ERROR_MESSAGE_FORCE, this.forceCommandInvalidArgsErrorMessage);

        if(!this.mysqlEnabled || this.forceCommandInvalidArgsErrorMessage) {
            this.commandInvalidArgsErrorMessage = getConfig().getString(ConfigKeys.NON_MYSQL_COMMAND_INVALID_ARGS_ERROR_MESSAGE_VALUE, "§l§cERROR! §r§cInvalid arguments!");
            getConfig().set(ConfigKeys.NON_MYSQL_COMMAND_INVALID_ARGS_ERROR_MESSAGE_VALUE, this.commandInvalidArgsErrorMessage);
        }

        this.forceCommandInternalErrorMessage = getConfig().getBoolean(ConfigKeys.NON_MYSQL_COMMAND_INTERNAL_ERROR_MESSAGE_FORCE, false);
        getConfig().set(ConfigKeys.NON_MYSQL_COMMAND_INTERNAL_ERROR_MESSAGE_FORCE, this.forceCommandInternalErrorMessage);

        if(!this.mysqlEnabled || this.forceCommandInternalErrorMessage) {
            this.commandInternalErrorMessage = getConfig().getString(ConfigKeys.NON_MYSQL_COMMAND_INTERNAL_ERROR_MESSAGE_VALUE, "§l§cERROR! §r§cInternal error while executing command!");
            getConfig().set(ConfigKeys.NON_MYSQL_COMMAND_INTERNAL_ERROR_MESSAGE_VALUE, this.commandInternalErrorMessage);
        }

        this.forceUserTimeout = getConfig().getBoolean(ConfigKeys.NON_MYSQL_USER_TIMEOUT_FORCE, false);
        getConfig().set(ConfigKeys.NON_MYSQL_USER_TIMEOUT_FORCE, this.forceUserTimeout);

        if(!this.mysqlEnabled || this.forceUserTimeout) {
            this.userTimeout = getConfig().getLong(ConfigKeys.NON_MYSQL_USER_TIMEOUT_VALUE, 5);
            getConfig().set(ConfigKeys.NON_MYSQL_USER_TIMEOUT_VALUE, this.userTimeout);
        }

        getLogger().log(Level.INFO, "config successfully setup!");
        getLogger().log(Level.INFO, "attempting to save config...");
        saveConfig();
        getLogger().log(Level.INFO, "config successfully saved!");
    }

    public final void saveConfig() {
        try {
            getLogger().log(Level.INFO, "attempting to save config...");
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.config, this.configFile);
            getLogger().log(Level.INFO, "config successfully saved!");
        } catch (IOException e) {
            getLogger().log(Level.INFO, "error while saving config!", e);
        }
    }

    public static void broadcastMessage(@NotNull String... message) {
        final StringBuilder messageBuilder = new StringBuilder();

        for(String msg : message) {
            messageBuilder.append(RPlugin.getInstance().getChatColor()).append(msg);
        }

        ProxyServer.getInstance().broadcast(
                TextComponent.fromLegacyText(
                        RPlugin.getInstance().getPrefix() + messageBuilder
                )
        );
    }

    public static void sendMessage(@NotNull CommandSender sender, @NotNull String... message) {
        final StringBuilder messageBuilder = new StringBuilder();

        for(String msg : message) {
            messageBuilder.append(RPlugin.getInstance().getChatColor()).append(msg);
        }

        sender.sendMessage(
                TextComponent.fromLegacyText(
                        RPlugin.getInstance().getPrefix() + messageBuilder
                )
        );
    }

    public <T extends RUserManager> T getUserManager() {
        return (T) userManager;
    }

    public <T extends ProxyMaintenanceManager> T getProxyMaintenanceManager() {
        return (T) proxyMaintenanceManager;
    }
}
