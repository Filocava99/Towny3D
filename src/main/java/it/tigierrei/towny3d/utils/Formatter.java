package it.tigierrei.towny3d.utils;

import org.bukkit.Location;

public class Formatter {

    public static String getResidentPath(String pluginPath, String playerUUID){
        return pluginPath + "/residents/" + playerUUID + ".yml";
    }

    public static String getTownPath(String pluginPath, String townName){
        return pluginPath + "/towns/" + townName + ".yml";
    }

    public static String getRegionPath(String pluginPath, String worldName){
        return pluginPath + "/regions/" + worldName + ".yml";
    }
}
