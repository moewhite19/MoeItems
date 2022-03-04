package cn.whiteg.moeitems.food;

import cn.whiteg.moeitems.utils.CommonUtils;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class LetheWater extends CustItem_CustModle implements Listener {
    private static final LetheWater a;

    static {
        a = new LetheWater();
    }

    private LetheWater() {
        super(Material.BEETROOT_SOUP,2,"§6孟婆汤");
    }

    public static LetheWater get() {
        return a;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEat(PlayerItemConsumeEvent event) {
        if (!is(event.getItem())) return;
        //暂时无功能
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteraction(PlayerInteractEntityEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (!is(item)) return;
        Entity entity = event.getRightClicked();
        if (entity instanceof Tameable){
            if (!new EntityDamageByEntityEvent(event.getPlayer(),entity,EntityDamageEvent.DamageCause.ENTITY_ATTACK,0D).callEvent())
                return;
            Tameable e = (Tameable) entity;
            e.setOwner(null);
            entity.getWorld().playSound(entity.getLocation(),Sound.ENTITY_FOX_EAT,1f,0.5F);
            CommonUtils.useItem(event.getPlayer().getInventory(),EquipmentSlot.HAND,item);
        }
    }
}
