package me.xra1ny.proxyapi.models.user;

import lombok.Getter;
import lombok.Setter;
import me.xra1ny.proxyapi.RPlugin;
import me.xra1ny.proxyapi.models.task.RRepeatableTask;
import me.xra1ny.proxyapi.models.task.RepeatableTaskInfo;

@RepeatableTaskInfo(interval = 1000)
public class UserTimeoutHandler extends RRepeatableTask {
    @Getter
    @Setter
    private long userTimeout;

    public UserTimeoutHandler(long userTimeout) {
        this.userTimeout = userTimeout;
    }

    @Override
    public void onTick() throws Exception {
        for(RUser user : RPlugin.getInstance().getUserManager().getUsers()) {
            if(user.getPlayer() != null && user.getPlayer().isConnected()) {
                continue;
            }

            if(user.getTimeout() <= 0) {
                RPlugin.getInstance().getUserManager().unregister(user);

                return;
            }

            user.setTimeout(user.getTimeout()-1);
        }
    }
}
