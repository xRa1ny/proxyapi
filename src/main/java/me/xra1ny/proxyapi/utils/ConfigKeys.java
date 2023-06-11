package me.xra1ny.proxyapi.utils;

public interface ConfigKeys {
    String LOGGING_LEVEL = "logging-level";

    String MAINTENANCE_SECTION = "maintenance";
    String MAINTENANCE_ENABLED = MAINTENANCE_SECTION + ".enabled";
    String MAINTENANCE_MESSAGE = MAINTENANCE_SECTION + ".message";
    String MAINTENANCE_IGNORED = MAINTENANCE_SECTION + ".ignored";

    String MYSQL_ENABLED = "mysql-enabled";

    String NON_MYSQL_SECTION = "non-mysql";
    String NON_MYSQL_PREFIX_SECTION = NON_MYSQL_SECTION + ".prefix";
    String NON_MYSQL_PREFIX_FORCE = NON_MYSQL_PREFIX_SECTION + ".force";
    String NON_MYSQL_PREFIX_VALUE = NON_MYSQL_PREFIX_SECTION + ".value";

    String NON_MYSQL_CHAT_COLOR_SECTION = NON_MYSQL_SECTION + ".chat-color";
    String NON_MYSQL_CHAT_COLOR_FORCE = NON_MYSQL_CHAT_COLOR_SECTION + ".force";
    String NON_MYSQL_CHAT_COLOR_VALUE = NON_MYSQL_CHAT_COLOR_SECTION + ".value";

    String NON_MYSQL_PLAYER_NO_PERMISSION_ERROR_MESSAGE_SECTION = NON_MYSQL_SECTION + ".player-no-permission-error-message";
    String NON_MYSQL_PLAYER_NO_PERMISSION_ERROR_MESSAGE_FORCE = NON_MYSQL_PLAYER_NO_PERMISSION_ERROR_MESSAGE_SECTION + ".force";
    String NON_MYSQL_PLAYER_NO_PERMISSION_ERROR_MESSAGE_VALUE = NON_MYSQL_PLAYER_NO_PERMISSION_ERROR_MESSAGE_SECTION + ".value";

    String NON_MYSQL_PLAYER_NOT_FOUND_ERROR_MESSAGE_SECTION = NON_MYSQL_SECTION + ".player-not-found-error-message";
    String NON_MYSQL_PLAYER_NOT_FOUND_ERROR_MESSAGE_FORCE = NON_MYSQL_PLAYER_NOT_FOUND_ERROR_MESSAGE_SECTION + ".force";
    String NON_MYSQL_PLAYER_NOT_FOUND_ERROR_MESSAGE_VALUE = NON_MYSQL_PLAYER_NOT_FOUND_ERROR_MESSAGE_SECTION + ".value";

    String NON_MYSQL_COMMAND_ONLY_PLAYER_ERROR_MESSAGE_SECTION = NON_MYSQL_SECTION + ".command-only-player-error-message";
    String NON_MYSQL_COMMAND_ONLY_PLAYER_ERROR_MESSAGE_FORCE = NON_MYSQL_COMMAND_ONLY_PLAYER_ERROR_MESSAGE_SECTION + ".force";
    String NON_MYSQL_COMMAND_ONLY_PLAYER_ERROR_MESSAGE_VALUE = NON_MYSQL_COMMAND_ONLY_PLAYER_ERROR_MESSAGE_SECTION + ".value";

    String NON_MYSQL_COMMAND_ERROR_MESSAGE_SECTION = NON_MYSQL_SECTION + ".command-error-message";
    String NON_MYSQL_COMMAND_ERROR_MESSAGE_FORCE = NON_MYSQL_COMMAND_ERROR_MESSAGE_SECTION + ".force";
    String NON_MYSQL_COMMAND_ERROR_MESSAGE_VALUE = NON_MYSQL_COMMAND_ERROR_MESSAGE_SECTION + ".value";

    String NON_MYSQL_COMMAND_INVALID_ARGS_ERROR_MESSAGE_SECTION = NON_MYSQL_SECTION + ".command-invalid-args-error-message";
    String NON_MYSQL_COMMAND_INVALID_ARGS_ERROR_MESSAGE_FORCE = NON_MYSQL_COMMAND_INVALID_ARGS_ERROR_MESSAGE_SECTION + ".force";
    String NON_MYSQL_COMMAND_INVALID_ARGS_ERROR_MESSAGE_VALUE = NON_MYSQL_COMMAND_INVALID_ARGS_ERROR_MESSAGE_SECTION + ".value";

    String NON_MYSQL_COMMAND_INTERNAL_ERROR_MESSAGE_SECTION = NON_MYSQL_SECTION + ".command-internal-error-message";
    String NON_MYSQL_COMMAND_INTERNAL_ERROR_MESSAGE_FORCE = NON_MYSQL_COMMAND_INTERNAL_ERROR_MESSAGE_SECTION + ".force";
    String NON_MYSQL_COMMAND_INTERNAL_ERROR_MESSAGE_VALUE = NON_MYSQL_COMMAND_INTERNAL_ERROR_MESSAGE_SECTION + ".value";

    String NON_MYSQL_USER_TIMEOUT_SECTION = NON_MYSQL_SECTION + ".user-timeout";
    String NON_MYSQL_USER_TIMEOUT_FORCE = NON_MYSQL_USER_TIMEOUT_SECTION + ".force";
    String NON_MYSQL_USER_TIMEOUT_VALUE = NON_MYSQL_USER_TIMEOUT_SECTION + ".value";
}
