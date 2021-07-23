package cn.whiteg.moeitems.items;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class SuperLeash extends CustItem_CustModle implements Listener {
    public static SuperLeash THIS = new SuperLeash();

    public SuperLeash() {
        super(Material.LEAD,1,"超级栓绳");
    }

    public static SuperLeash get() {
        return THIS;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamage() <= 0) return;
        if (event.getDamager() instanceof LivingEntity damager && event.getEntity() instanceof LivingEntity entity){
            var equipment = damager.getEquipment();
            if (equipment == null) return;
            var slot = damager.getHandRaised();
            var item = equipment.getItem(slot);
            if (!is(item)) return;

            //扣减物品
            if (item.getAmount() > 1) item.setAmount(item.getAmount() - 1);
            else equipment.setItem(slot,null);

            event.setCancelled(true);
            entity.setLeashHolder(damager);
        }
    }
}
