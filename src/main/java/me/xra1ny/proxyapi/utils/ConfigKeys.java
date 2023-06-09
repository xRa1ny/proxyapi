package me.xra1ny.proxyapi.utils;

public interface ConfigKeys {
    String LOGGING_LEVEL = "logging-level";

    String MAINTENANCE = "maintenance";
    String MAINTENANCE_ENABLED = "enabled";
    String MAINTENANCE_MESSAGE = "message";
    String MAINTENANCE_IGNORED = "ignored";

    String MYSQL_ENABLED = "mysql-enabled";

    String NON_MYSQL = "non-mysql";
    String NON_MYSQL_FORCE = "force";
    String NON_MYSQL_PREFIX = "prefix";
    String NON_MYSQL_CHAT_COLOR = "chat-color";
    String NON_MYSQL_PLAYER_NO_PERMISSION_ERROR_MESSAGE = "player-no-permission-error-message";
    String NON_MYSQL_PLAYER_NOT_FOUND_ERROR_MESSAGE = "player-not-found-error-message";
    String NON_MYSQL_COMMAND_ONLY_PLAYER_ERROR_MESSAGE = "command-only-player-error-message";
    String NON_MYSQL_COMMAND_ERROR_MESSAGE = "command-error-message";
    String NON_MYSQL_COMMAND_INVALID_ARGS_ERROR_MESSAGE = "command-invalid-args-error-message";
    String NON_MYSQL_COMMAND_INTERNAL_ERROR_MESSAGE = "command-internal-error-message";
    String NON_MYSQL_USER_TIMEOUT = "user-timeout";
}
