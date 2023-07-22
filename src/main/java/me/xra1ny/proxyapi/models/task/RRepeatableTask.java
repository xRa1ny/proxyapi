package me.xra1ny.proxyapi.models.task;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.xra1ny.proxyapi.RPlugin;
import me.xra1ny.proxyapi.exceptions.ClassNotAnnotatedException;
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
    }

    public RRepeatableTask(int interval) {
        this.interval = interval;
    }

    /**
     * starts this repeatable task
     */
    public final void start() {
        if(isRunning()) {
            return;
        }

        onStart();

        this.runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    onTick();
                } catch (Exception ex) {
                    RPlugin.getInstance().getLogger().log(Level.INFO, "exception in repeatable task " + this, ex);
                }
            }
        };
        this.task = ProxyServer.getInstance().getScheduler().schedule(RPlugin.getInstance(), this.runnable, 1L, this.interval, TimeUnit.MILLISECONDS);
    }

    /**
     * called when this repeatable task starts
     */
    public abstract void onStart();

    /**
     * stops this repeatable task
     */
    public final void stop() {
        if(!isRunning()) {
            return;
        }

        onStop();

        this.task.cancel();
        this.task = null;
        this.runnable = null;
    }

    /**
     * called when this repeatable task stops
     */
    public abstract void onStop();

    public final boolean isRunning() {
        return this.task != null && this.runnable != null;
    }

    /**
     * called whenever the interval of this repeatable task expires
     */
    public abstract void onTick() throws Exception;
}
