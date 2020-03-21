package cn.whiteg.moeitems;

import cn.whiteg.rpgArmour.api.CustItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Setting {
    public static boolean DEBUG;
    public static FileConfiguration config;
    private static ConfigurationSection custItemConfit;

    public static void reload() {
        File file = new File(MoeItems.plugin.getDataFolder(),"config.yml");
        config = YamlConfiguration.loadConfiguration(file);
        DEBUG = config.getBoolean("debug");
        custItemConfit = config.getConfigurationSection("CustItemSetting");
    }

    public static ConfigurationSection getCustItemConfig(CustItem custItem) {
        ConfigurationSection var1 = getCustItemConfit();
        return var1 != null ? var1.getConfigurationSection(custItem.getClass().getSimpleName()) : null;
    }

    public static ConfigurationSection getCustItemConfit() {
        return custItemConfit;
    }
}
