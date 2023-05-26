package me.xra1ny.proxyapi.models.user;

import me.xra1ny.proxyapi.exceptions.ClassNotAnnotatedException;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to catch Chatinput from an RUSer
 */
public abstract class UserInputWindow {
    /**
     * the timer of this user input window
     */
    @Getter
    private final int timer;

    /**
     * the user input window handler of this user input window
     */
    @Getter(onMethod = @__(@NotNull))
    private final UserInputWindowHandler inputWindowHandler;

    /**
     * the map of all users this user input window is currently open for
     */
    @Getter(onMethod = @__(@NotNull))
    private final Map<RUser, Integer> users = new HashMap<>();

    public UserInputWindow(int millis) throws ClassNotAnnotatedException {
        this.timer = millis;
        this.inputWindowHandler = new UserInputWindowHandler(this);
    }

    /**
     * opens this user input window for the user specified
     * @param user the user
     */
    public void open(@NotNull RUser user) {
        if(this.users.containsKey(user)) {
            return;
        }

        this.users.put(user, this.timer);
    }

    /**
     * called when this user input window is opened for the user specified
     * @param user the user
     */
    public abstract void onOpen(@NotNull RUser user);

    /**
     * closes this user input windows for the user specified
     * @param user the user
     * @param input the input
     */
    public void close(@NotNull RUser user, @Nullable String input) {
        if(!this.users.containsKey(user)) {
            return;
        }

        this.users.remove(user);

        onClose(user, input);
    }

    /**
     * called when this user input window is closed for the specified user
     * @param user the user
     * @param input the input upon closing
     */
    public abstract void onClose(@NotNull RUser user, @Nullable String input);
}
