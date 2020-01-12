package cn.whiteg.moeitems;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Setting {
    public static boolean DEBUG;
    public static FileConfiguration config;

    public static void reload() {
        File file = new File(MoeItems.plugin.getDataFolder(),"config.yml");
        config = YamlConfiguration.loadConfiguration(file);
        DEBUG = config.getBoolean("debug");
    }

}
