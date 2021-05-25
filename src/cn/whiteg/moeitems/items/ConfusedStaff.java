package cn.whiteg.moeitems.items;

import cn.whiteg.moeitems.utils.CommonUtils;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sittable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ConfusedStaff extends CustItem_CustModle implements Listener {
    private static final ConfusedStaff a;

    static {
        a = new ConfusedStaff();
    }

    private ConfusedStaff() {
        super(Material.SHEARS,30,"§d迷惑权杖");
    }

    public static ConfusedStaff get() {
        return a;
    }

    @EventHandler()
    public void onInteraction(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (!is(item)) return;
        Entity entity = event.getRightClicked();

        if (entity instanceof Sittable){
            event.setCancelled(true);
            if (!new EntityDamageByEntityEvent(event.getPlayer(),entity,EntityDamageEvent.DamageCause.ENTITY_ATTACK,0D).callEvent()){
                return;
            }
            var e = (Sittable) entity;
            e.setSitting(!e.isSitting());
            CommonUtils.damageItem(event.getPlayer().getInventory(),EquipmentSlot.HAND,item,null);
        }
    }
}
