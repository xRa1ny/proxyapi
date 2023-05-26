package me.xra1ny.proxyapi;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.proxyapi.listeners.DefaultPluginConnectionListener;
import me.xra1ny.proxyapi.listeners.DefaultPluginListener;
import me.xra1ny.proxyapi.models.color.HexCodeManager;
import me.xra1ny.proxyapi.models.command.CommandManager;
import me.xra1ny.proxyapi.models.listener.ListenerManager;
import me.xra1ny.proxyapi.models.maintenance.MaintenanceManager;
import me.xra1ny.proxyapi.models.party.PartyManager;
import me.xra1ny.proxyapi.models.user.RUser;
import me.xra1ny.proxyapi.models.user.UserInputWindowManager;
import me.xra1ny.proxyapi.models.user.UserManager;
import net.md_5.bungee.api.ChatColor;
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
     * the config setting of the mysql url to connect to
     */
    @Getter(onMethod = @__(@NotNull))
    private String mysqlUrl;

    /**
     * the config setting of the mysql url port to connect to
     */
    @Getter
    private int mysqlPort;

    /**
     * the config setting of the mysql servers username to login as
     */
    @Getter(onMethod = @__(@NotNull))
    private String mysqlUsername;

    /**
     * the config setting of the mysql servers user password to login as
     */
    @Getter(onMethod = @__(@NotNull))
    private String mysqlPassword;

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
    @Getter(onMethod = @__(@NotNull))
    private UserManager userManager;

    /**
     * the maintenance manager responsible for storing all maintenance related information and managing them
     */
    @Getter(onMethod = @__(@NotNull))
    private MaintenanceManager maintenanceManager;

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

            Class<? extends RUser> userClass = RUser.class;

            final PluginInfo pluginInfo = getClass().getDeclaredAnnotation(PluginInfo.class);

            if(pluginInfo != null) {
                userClass = pluginInfo.userClass();
            }

            this.configFile = new File(getDataFolder(), "config.yml");

            createConfig();
            setupConfig();

            this.listenerManager = new ListenerManager();
            this.commandManager = new CommandManager();
            this.userManager = new UserManager(userClass, this.userTimeout);
            this.maintenanceManager = new MaintenanceManager();
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

        getLogger().setLevel(Level.parse(getConfig().getString("logging-level", "ALL")));
        getConfig().set("logging-level", getLogger().getLevel().toString());

        Configuration mysql = getConfig().getSection("mysql");

        if(mysql == null) {
            getConfig().set("mysql", null);
            mysql = getConfig().getSection("mysql");
        }

        this.mysqlEnabled = mysql.getBoolean("enabled", false);
        mysql.set("enabled", this.mysqlEnabled);

        if(this.mysqlEnabled) {
            this.mysqlUrl = mysql.getString("url", "127.0.0.1");
            mysql.set("url", this.mysqlUrl);

            this.mysqlPort = mysql.getInt("port", 3306);
            mysql.set("port", this.mysqlPort);

            this.mysqlUsername = mysql.getString("username", "root");
            mysql.set("username", this.mysqlUsername);

            this.mysqlPassword = mysql.getString("password", "");
            mysql.set("password", this.mysqlPassword);
        }

        Configuration nonMysql = getConfig().getSection("non-mysql");

        if(nonMysql == null) {
            getConfig().set("non-mysql", null);
            nonMysql = getConfig().getSection("non-mysql");
        }

        this.forceNonMysqlSettings = nonMysql.getBoolean("force", true);
        nonMysql.set("force", this.forceNonMysqlSettings);

        if(!this.mysqlEnabled || this.forceNonMysqlSettings) {
            this.prefix = nonMysql.getString("prefix", ChatColor.BOLD + "MyAwesomePlugin  ");
            nonMysql.set("prefix", this.prefix);

            this.chatColor = ChatColor.valueOf(nonMysql.getString("chat-color", String.valueOf(ChatColor.GRAY)));
            nonMysql.set("chat-color", this.chatColor.name());

            this.playerNoPermissionErrorMessage = nonMysql.getString("player-no-permission-error-message", "§l§cFEHLER! §r§cDafür hast du keine Rechte!");
            nonMysql.set("player-no-permission-error-message", this.playerNoPermissionErrorMessage);

            this.onlyPlayerCommandErrorMessage = nonMysql.getString("only-player-command-error-message", "§l§cFEHLER! §r§cDieser Command kann nur durch einen Spieler ausgeführt werden!");
            nonMysql.set("only-player-command-error-message", this.onlyPlayerCommandErrorMessage);

            this.commandErrorMessage = nonMysql.getString("command-error-message", "§l§cFEHLER! §r§cCommand konnte nicht ausgeführt werden!");
            nonMysql.set("command-error-message", this.commandErrorMessage);

            this.commandInvalidArgsErrorMessage = nonMysql.getString("command-invalid-args-error-message", "§l§cFEHLER! §r§cUngültige Command Argumente!");
            nonMysql.set("command-invalid-args-error-message", this.commandInvalidArgsErrorMessage);

            this.commandInternalErrorMessage = nonMysql.getString("command-internal-error-message", "§l§cFEHLER! §r§cInterner Fehler beim Ausführen des Commands!");
            nonMysql.set("command-internal-error-message", this.commandInternalErrorMessage);

            this.userTimeout = nonMysql.getLong("user-timeout", 20);
            nonMysql.set("user-timeout", this.userTimeout);
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
}
