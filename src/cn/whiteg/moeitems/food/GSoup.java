package cn.whiteg.moeitems.food;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GSoup implements Listener {
    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item.hasItemMeta() && item.getItemMeta().getDisplayName().equals("§6寄汤")){
            Player player = event.getPlayer();
//            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,600,2),false);
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON,600,6),false);
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER,600,6),false);
        }
    }
}
