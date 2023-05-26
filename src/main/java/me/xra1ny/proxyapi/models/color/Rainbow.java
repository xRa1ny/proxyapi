package me.xra1ny.proxyapi.models.color;

import me.xra1ny.proxyapi.exceptions.HomogeneousRainbowException;
import me.xra1ny.proxyapi.exceptions.InvalidColorException;
import me.xra1ny.proxyapi.exceptions.NumberRangeException;

import java.util.ArrayList;

public class Rainbow {

    private double minNum;
    private double maxNum;
    private String[] colours;
    private ArrayList<ColorGradient> colorGradients;

    public Rainbow() {
        try {
            minNum = 0;
            maxNum = 100;
            colours = new String[]{"red", "yellow", "lime", "blue"};
            setSpectrum(colours);
        } catch (InvalidColorException | HomogeneousRainbowException | NumberRangeException e) {
            throw new AssertionError(e);
        }

    }

    public String colourAt(double number) {
        if (colorGradients.size() == 1) {
            return colorGradients.get(0).colourAt(number);
        } else {
            double segment = (maxNum - minNum) / (colorGradients.size());
            int index = (int) Math.min(Math.floor((Math.max(number, minNum) - minNum) / segment), colorGradients.size() - 1);
            return colorGradients.get(index).colourAt(number);
        }
    }


    public void setSpectrum(String... spectrum) throws HomogeneousRainbowException, InvalidColorException, NumberRangeException {
        if (spectrum.length < 2) {
            throw new HomogeneousRainbowException();
        } else {
            double increment = (maxNum - minNum) / (spectrum.length - 1);
            ColorGradient firstGradient = new ColorGradient();
            firstGradient.setGradient(spectrum[0], spectrum[1]);
            firstGradient.setNumberRange(minNum, minNum + increment);

            colorGradients = new ArrayList<ColorGradient>();
            colorGradients.add(firstGradient);

            for (int i = 1; i < spectrum.length - 1; i++) {
                ColorGradient colorGradient = new ColorGradient();
                colorGradient.setGradient(spectrum[i], spectrum[i + 1]);
                colorGradient.setNumberRange(minNum + increment * i, minNum + increment * (i + 1));
                colorGradients.add(colorGradient);
            }

            colours = spectrum;
        }
    }


    public void setNumberRange(double minNumber, double maxNumber) throws NumberRangeException {
        try {
            if (maxNumber > minNumber) {
                minNum = minNumber;
                maxNum = maxNumber;
                setSpectrum(colours);
            } else {
                throw new NumberRangeException(minNumber, maxNumber);
            }
        } catch (HomogeneousRainbowException | InvalidColorException e) {
            throw new RuntimeException(e);
        }
    }

    public String colorAt(double number) {
        return colourAt(number);
    }
}

