package me.xra1ny.proxyapi.models.color;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;

public class HexCodeManager {

    public String createGradientFromString(String message, String[] colours) {

        int count = message.length();
        if (Math.min(count, colours.length) < 2) {
            return message;
        }

        ArrayList<String> cols = createGradient(count, colours);

        String colourCodes = "";
        for (int i = 0; i < cols.size(); i++) {
            colourCodes += ChatColor.of(cols.get(i)) + "" + message.charAt(i);
        }
        return colourCodes;
    }

    public ArrayList<String> createGradient(int count, String[] colours) {
        Rainbow rainbow = new Rainbow();

        try {
            rainbow.setNumberRange(1, count);
            rainbow.setSpectrum(colours);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<String> hexCodes = new ArrayList<String>();
        for (int i = 1; i <= count; i++) {
            hexCodes.add("#" + rainbow.colourAt(i));
        }
        return hexCodes;
    }
}
