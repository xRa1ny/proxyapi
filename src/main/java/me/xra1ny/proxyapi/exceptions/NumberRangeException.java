package me.xra1ny.proxyapi.exceptions;

import me.xra1ny.proxyapi.models.exception.RainbowException;

public class NumberRangeException extends RainbowException {
    public NumberRangeException(double minNumber, double maxNumber) {
        super("maxNumber (" + maxNumber + ") is not greater than minNumber (" + minNumber + ")");
    }
}
