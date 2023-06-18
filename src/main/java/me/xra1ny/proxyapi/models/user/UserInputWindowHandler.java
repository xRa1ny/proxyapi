package me.xra1ny.proxyapi.models.user;

import me.xra1ny.proxyapi.exceptions.ClassNotAnnotatedException;
import me.xra1ny.proxyapi.models.task.RRepeatableTask;
import me.xra1ny.proxyapi.models.task.RepeatableTaskInfo;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@RepeatableTaskInfo(interval = 50)
public class UserInputWindowHandler extends RRepeatableTask {
    /**
     * the user input window of this user input window handler
     */
    @Getter(onMethod = @__(@NotNull))
    private final UserInputWindow inputWindow;

    UserInputWindowHandler(@NotNull UserInputWindow inputWindow) throws ClassNotAnnotatedException {
        this.inputWindow = inputWindow;
        start();
    }

    @Override
    public void onTick() {
        for(Map.Entry<RUser, Integer> entry : this.inputWindow.getUsers().entrySet()) {
            for(int i = 0; i < 50; i++) {
                entry.setValue(entry.getValue()-1);

                if (entry.getValue() <= 0) {
                    stop();

                    close(entry.getKey(), null);
                }
            }
        }
    }

    private void close(@NotNull RUser user, @Nullable String input) {
        stop();
        this.inputWindow.close(user, input);
    }

    public void onUserSendChatMessage(@NotNull RUser user, @NotNull String message) {
        close(user, message);
    }
}
