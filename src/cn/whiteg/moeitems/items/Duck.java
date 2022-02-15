package cn.whiteg.moeitems.items;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Duck extends CustItem_CustModle implements Listener {
    NamespacedKey pitchKey = new NamespacedKey(MoeItems.plugin,"pitch");

    public Duck() {
        super(Material.BOWL,6,"§e小黄鸭");
    }

    @EventHandler
    public void onani(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK) return;
        final Player player = event.getPlayer();
        if (player.hasCooldown(getMaterial())) return;
        final PlayerInventory pi = player.getInventory();
        ItemStack item = pi.getItemInMainHand();
        if (is(item)){
            float pitch = item.getItemMeta().getPersistentDataContainer().getOrDefault(pitchKey,PersistentDataType.FLOAT,1f);
            player.getWorld().playSound(player.getLocation(),"rpgarmour:items.duck_0",1,pitch);
            player.setCooldown(getMaterial(),15);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSwap(PlayerSwapHandItemsEvent event) {
        ItemStack item = event.getOffHandItem();
        if (is(item)){
            event.setCancelled(true);
            //noinspection ConstantConditions
            ItemMeta itemMeta = item.getItemMeta();
            PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
            float pitch = dataContainer.getOrDefault(pitchKey,PersistentDataType.FLOAT,1f);
            pitch = new BigDecimal(pitch).add(new BigDecimal("0.1")).setScale(1,RoundingMode.HALF_UP).floatValue();
            if (pitch > 2) pitch = 0f;
            dataContainer.set(pitchKey,PersistentDataType.FLOAT,pitch);
            item.setItemMeta(itemMeta);
            Player player = event.getPlayer();
            player.sendActionBar("更新音符" + pitch);
            player.getInventory().setItemInMainHand(item);
        }
    }
}

