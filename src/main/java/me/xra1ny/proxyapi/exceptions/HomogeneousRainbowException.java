package me.xra1ny.proxyapi.exceptions;

import me.xra1ny.proxyapi.models.exception.RainbowException;

public class HomogeneousRainbowException extends RainbowException {
    public HomogeneousRainbowException() {
        super("rainbow must have two or more colors");
    }
}
