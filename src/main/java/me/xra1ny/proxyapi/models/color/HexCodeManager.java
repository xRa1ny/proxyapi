package me.xra1ny.proxyapi.models.color;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;

public class HexCodeManager {
    public String createGradientFromString(String message, String[] colours) {
        final int count = message.length();

        if (Math.min(count, colours.length) < 2) {
            return message;
        }

        final ArrayList<String> cols = createGradient(count, colours);
        final StringBuilder colourCodes = new StringBuilder();

        for(int i = 0; i < cols.size(); i++) {
            colourCodes.append(ChatColor.of(cols.get(i))).append(message.charAt(i));
        }

        return colourCodes.toString();
    }

    public ArrayList<String> createGradient(int count, String[] colours) {
        final Rainbow rainbow = new Rainbow();

        try {
            rainbow.setNumberRange(1, count);
            rainbow.setSpectrum(colours);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final ArrayList<String> hexCodes = new ArrayList<String>();

        for(int i = 1; i <= count; i++) {
            hexCodes.add("#" + rainbow.colourAt(i));
        }

        return hexCodes;
    }
}
