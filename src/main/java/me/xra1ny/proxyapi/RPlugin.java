package me.xra1ny.proxyapi;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.proxyapi.listeners.DefaultPluginConnectionListener;
import me.xra1ny.proxyapi.listeners.DefaultPluginListener;
import me.xra1ny.proxyapi.models.color.HexCodeManager;
import me.xra1ny.proxyapi.models.command.CommandManager;
import me.xra1ny.proxyapi.models.listener.ListenerManager;
import me.xra1ny.proxyapi.models.maintenance.RMaintenanceManager;
import me.xra1ny.proxyapi.models.party.PartyManager;
import me.xra1ny.proxyapi.models.user.RUser;
import me.xra1ny.proxyapi.models.user.RUserManager;
import me.xra1ny.proxyapi.models.user.UserInputWindowManager;
import me.xra1ny.proxyapi.utils.ConfigKeys;
import net.md_5.bungee.api.ChatColor;
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

    /**
     * the config setting whether to use mysql or not
     */
    @Getter
    private boolean mysqlEnabled;

    /**
     * the config setting whether to force non mysql config settings even when a mysql connection has been established
     */
    @Getter
    private boolean forceNonMysqlSettings;

    /**
     * the global prefix of this plugin
     */
    @Getter(onMethod = @__(@NotNull))
    private String prefix;

    /**
     * the global chat color of this plugin
     */
    @Getter(onMethod = @__(@NotNull))
    private ChatColor chatColor;

    /**
     * the global message to display when an error occurs while executing a command
     */
    @Getter(onMethod = @__(@NotNull))
    private String commandErrorMessage;

    /**
     * the global message to display when an internal error occurs while executing a command
     */
    @Getter(onMethod = @__(@NotNull))
    private String commandInternalErrorMessage;

    /**
     * the global message to display when a command has been passed invalid arguments
     */
    @Getter(onMethod = @__(@NotNull))
    private String commandInvalidArgsErrorMessage;

    /**
     * the global error message displayed when a player does not have the permissions to perform an action
     */
    @Getter(onMethod = @__(@NotNull))
    private String playerNoPermissionErrorMessage;

    /**
     * the global error message displayed when the console performs a command that can only be executed by a player
     */
    @Getter(onMethod = @__(@NotNull))
    private String onlyPlayerCommandErrorMessage;

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
    private RMaintenanceManager maintenanceManager;

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
            Class<? extends RMaintenanceManager> maintenanceManagerClass = RMaintenanceManager.class;

            if(info != null) {
                userClass = info.userClass();
                userManagerClass = info.userManagerClass();
                maintenanceManagerClass = info.maintenanceManagerClass();
            }

            this.configFile = new File(getDataFolder(), "config.yml");

            createConfig();
            setupConfig();

            this.listenerManager = new ListenerManager();
            this.commandManager = new CommandManager();
            this.userManager = userManagerClass.getDeclaredConstructor(Class.class, long.class).newInstance(userClass, this.userTimeout);
            this.maintenanceManager = maintenanceManagerClass.getDeclaredConstructor().newInstance();
            this.userInputWindowManager = new UserInputWindowManager();
            this.hexCodeManager = new HexCodeManager();
            this.partyManager = new PartyManager();

            // TODO: add database support...
            // if(this.mysqlEnabled) {
            // this.databaseApiManager = new DatabaseApiManager();
            // }

            this.listenerManager.register(new DefaultPluginConnectionListener());
            this.listenerManager.register(new DefaultPluginListener());

            getLogger().log(Level.INFO, "proxyapi successfully enabled!");

            try {
                getLogger().log(Level.INFO, "attempting to enable external plugin...");

                onPluginEnable();

                saveConfig();

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

            // TODO shutdown logic

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

        this.mysqlEnabled = getConfig().getBoolean(ConfigKeys.MYSQL_ENABLED, false);
        getConfig().set(ConfigKeys.MYSQL_ENABLED, this.mysqlEnabled);

        Configuration nonMysql = getConfig().getSection(ConfigKeys.NON_MYSQL);

        if(nonMysql == null) {
            getConfig().set(ConfigKeys.NON_MYSQL, "");
            nonMysql = getConfig().getSection(ConfigKeys.NON_MYSQL);
        }

        this.forceNonMysqlSettings = nonMysql.getBoolean(ConfigKeys.NON_MYSQL_FORCE, true);
        nonMysql.set(ConfigKeys.NON_MYSQL_FORCE, this.forceNonMysqlSettings);

        if(!this.mysqlEnabled || this.forceNonMysqlSettings) {
            this.prefix = nonMysql.getString(ConfigKeys.NON_MYSQL_PREFIX, ChatColor.BOLD + "MyPlugin  ");
            nonMysql.set(ConfigKeys.NON_MYSQL_PREFIX, this.prefix);

            this.chatColor = ChatColor.valueOf(nonMysql.getString(ConfigKeys.NON_MYSQL_CHAT_COLOR, String.valueOf(ChatColor.GRAY)));
            nonMysql.set(ConfigKeys.NON_MYSQL_CHAT_COLOR, this.chatColor.name());

            this.playerNoPermissionErrorMessage = nonMysql.getString(ConfigKeys.NON_MYSQL_PLAYER_NO_PERMISSION_ERROR_MESSAGE, "§l§cFEHLER! §r§cDafür hast du keine Rechte!");
            nonMysql.set(ConfigKeys.NON_MYSQL_PLAYER_NO_PERMISSION_ERROR_MESSAGE, this.playerNoPermissionErrorMessage);

            this.onlyPlayerCommandErrorMessage = nonMysql.getString(ConfigKeys.NON_MYSQL_COMMAND_ONLY_PLAYER_ERROR_MESSAGE, "§l§cFEHLER! §r§cDieser Command kann nur durch einen Spieler ausgeführt werden!");
            nonMysql.set(ConfigKeys.NON_MYSQL_COMMAND_ONLY_PLAYER_ERROR_MESSAGE, this.onlyPlayerCommandErrorMessage);

            this.commandErrorMessage = nonMysql.getString(ConfigKeys.NON_MYSQL_COMMAND_ERROR_MESSAGE, "§l§cFEHLER! §r§cCommand konnte nicht ausgeführt werden!");
            nonMysql.set(ConfigKeys.NON_MYSQL_COMMAND_ERROR_MESSAGE, this.commandErrorMessage);

            this.commandInvalidArgsErrorMessage = nonMysql.getString(ConfigKeys.NON_MYSQL_COMMAND_INVALID_ARGS_ERROR_MESSAGE, "§l§cFEHLER! §r§cUngültige Command Argumente!");
            nonMysql.set(ConfigKeys.NON_MYSQL_COMMAND_INVALID_ARGS_ERROR_MESSAGE, this.commandInvalidArgsErrorMessage);

            this.commandInternalErrorMessage = nonMysql.getString(ConfigKeys.NON_MYSQL_COMMAND_INTERNAL_ERROR_MESSAGE, "§l§cFEHLER! §r§cInterner Fehler beim Ausführen des Commands!");
            nonMysql.set(ConfigKeys.NON_MYSQL_COMMAND_INTERNAL_ERROR_MESSAGE, this.commandInternalErrorMessage);

            this.userTimeout = nonMysql.getLong(ConfigKeys.NON_MYSQL_USER_TIMEOUT, 20);
            nonMysql.set(ConfigKeys.NON_MYSQL_USER_TIMEOUT, this.userTimeout);
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

    public static void broadcastMessage(@NotNull String message) {
        ProxyServer.getInstance().broadcast(
                TextComponent.fromLegacyText(
                        RPlugin.getInstance().getPrefix() + RPlugin.getInstance().getChatColor() + message
                )
        );
    }

    public <T extends RUserManager> T getUserManager() {
        return (T) userManager;
    }

    public <T extends RMaintenanceManager> T getMaintenanceManager() {
        return (T) maintenanceManager;
    }
}
