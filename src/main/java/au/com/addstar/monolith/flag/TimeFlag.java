/*
 * Copyright (c) 2020. AddstarMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 *  and associated documentation files (the "Software"), to deal in the Software without restriction,
 *  including without limitation the rights to use, copy, modify, merge, publish, distribute,
 *  sublicense, and/or copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package au.com.addstar.monolith.flag;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import au.com.addstar.monolith.command.BadArgumentException;

public class TimeFlag extends Flag<Integer> {
    private static final Pattern mTimePattern = Pattern.compile("([\\d]+)ticks|(\\d{2}:\\d{2})|(\\d{1,2}(?::\\d{1,2})?)(am|pm)");

    public static int parseTime(String[] args) throws IllegalArgumentException, BadArgumentException {
        if (args.length != 1)
            throw new IllegalArgumentException("<4000ticks|3pm|13:00|4:20am>");

        Matcher match = mTimePattern.matcher(args[0]);

        if (!match.matches()) {
            BadArgumentException ex = new BadArgumentException(0, "Unknown time.");
            ex.addInfo(ChatColor.GOLD + "Expected time formats: " + ChatColor.GRAY + "1000ticks 10:30 3:20pm 4am");
            throw ex;
        }

        if (match.group(1) != null)
            return Integer.parseInt(match.group(1));
        else if (match.group(2) != null) {
            String[] parts = match.group(2).split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);

            int ticks = hours * 1000 + (minutes * 1000 / 60);
            ticks -= 6000;
            if (ticks < 0)
                ticks += 24000;
            while (ticks >= 24000)
                ticks -= 24000;
            return ticks;
        } else {
            boolean am = match.group(4).equalsIgnoreCase("am");
            String[] parts = match.group(3).split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = (parts.length == 2 ? Integer.parseInt(parts[1]) : 0);

            if (hours == 12)
                hours = 0;

            while (hours > 12) {
                hours -= 12;
                am = !am;
            }

            if (!am)
                hours += 12;

            int ticks = hours * 1000 + (minutes * 1000 / 60);
            ticks -= 6000;
            if (ticks < 0)
                ticks += 24000;
            while (ticks >= 24000)
                ticks -= 24000;
            return ticks;
        }
    }

    public static String timeToString(int time) {
        int ticks = time + 6000;
        if (ticks >= 24000)
            ticks -= 24000;

        int hours = ticks / 1000;
        int minutes = (ticks - (hours * 1000)) * 60 / 1000;

        return String.format("%02d:%02d", hours, minutes);
    }

    @Override
    public Integer parse(Player sender, String[] args) throws IllegalArgumentException, BadArgumentException {
        return parseTime(args);
    }

    @Override
    public List<String> tabComplete(Player sender, String[] args) {
        return null;
    }

    @Override
    public void save(ConfigurationSection section) {
        section.set("value", value);
    }

    @Override
    public void read(ConfigurationSection section) throws InvalidConfigurationException {
        value = section.getInt("value");
    }

    @Override
    public String getValueString() {
        return timeToString(value);
    }

}
