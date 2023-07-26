package me.xra1ny.proxyapi.exceptions.color;

import me.xra1ny.proxyapi.models.exception.RainbowException;

public class InvalidColorException extends RainbowException {
    public InvalidColorException(String nonColor) {
        super(nonColor + " is not a valid color");
    }
}
