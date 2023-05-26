package me.xra1ny.proxyapi.models.task;

import me.xra1ny.proxyapi.RPlugin;
import me.xra1ny.proxyapi.exceptions.ClassNotAnnotatedException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Slf4j
public abstract class RRepeatableTask {
    /**
     * the bukkit runnable of this repeatable task defining its logic
     */
    @Getter(onMethod = @__(@NotNull))
    private Runnable runnable;

    /**
     * the bukkit task of this repeatable task
     */
    @Getter(onMethod = @__(@Nullable))
    private ScheduledTask task;

    /**
     * the interval of this repeatable task
     */
    @Getter
    private final int interval;

    public RRepeatableTask() throws ClassNotAnnotatedException {
        final RepeatableTaskInfo info = getClass().getDeclaredAnnotation(RepeatableTaskInfo.class);

        if(info == null) {
            throw new ClassNotAnnotatedException(getClass(), RepeatableTaskInfo.class);
        }

        this.interval = info.interval();

        run();
    }

    public RRepeatableTask(int interval) {
        this.interval = interval;
        run();
    }

    private void run() {
        this.runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    tick();
                } catch (Exception ex) {
                    RPlugin.getInstance().getLogger().log(Level.INFO, "exception in repeatable task " + this);
                    ex.printStackTrace();
                }
            }
        };
    }

    /**
     * starts this repeatable task
     */
    public final void start() {
        stop();

        this.task = ProxyServer.getInstance().getScheduler().schedule(RPlugin.getInstance(), this.runnable, 0L, this.interval / 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * stops this repeatable task
     */
    public final void stop() {
        if(this.task == null) {
            return;
        }

        task.cancel();
        this.task = null;
    }

    public final boolean isRunning() {
        return this.task != null;
    }

    /**
     * called whenever the interval of this repeatable task expires
     */
    public abstract void tick() throws Exception;
}
