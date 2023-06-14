package me.xra1ny.proxyapi.models.task;

import lombok.Getter;
import lombok.Setter;
import me.xra1ny.proxyapi.exceptions.ClassNotAnnotatedException;

public abstract class RTimer extends RRepeatableTask {
    @Getter
    private final int initialCountdown;

    @Getter
    @Setter
    private int countdown;

    public RTimer() {
        final TimerInfo info = getClass().getDeclaredAnnotation(TimerInfo.class);

        if(info == null) {
            throw new ClassNotAnnotatedException(getClass(), TimerInfo.class);
        }

        this.initialCountdown = info.countdown();
        this.countdown = this.initialCountdown;
    }

    public RTimer(int countdown) {
        this.initialCountdown = countdown;
        this.countdown = this.initialCountdown;
    }

    @Override
    public final void onTick() throws Exception {
        if(this.countdown <= 0) {
            stop();

            onExpire();

            return;
        }

        onTimerTick();

        this.countdown--;
    }

    /**
     * called whenever the interval of this timer expires
     */
    public abstract void onTimerTick() throws Exception;

    /**
     * called when the countdown of this timer expires (after this task has been stopped)
     */
    public abstract void onExpire() throws Exception;
}
