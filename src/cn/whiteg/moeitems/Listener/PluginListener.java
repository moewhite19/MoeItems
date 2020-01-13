package cn.whiteg.moeitems.Listener;

import cn.whiteg.moeitems.MoeItems;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class PluginListener implements Listener {
    final private MoeItems plugin;

    public PluginListener(MoeItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        if (event.getPlugin().getName().equals("RPGArmour")){
            plugin.initItems();
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().getName().equals("RPGArmour")){
            plugin.getItems().clear();
        }
    }
}
