package cn.whiteg.moeitems.items;

import cn.whiteg.moeitems.utils.CommonUtils;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class NormalSwordAbs extends CustItem_CustModle implements Listener {

    private final float DAMAGE;

    public NormalSwordAbs(int id,String displayName,float damage) {
        super(Material.DIAMOND_SWORD,id,displayName);
        this.DAMAGE = damage;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
        if (event.getDamager() instanceof LivingEntity damager){
            final EntityEquipment equipment = damager.getEquipment();
            if (equipment == null) return;
            final ItemStack hand = equipment.getItemInMainHand();
            if (!is(hand)) return;
            if (damager instanceof Player && CommonUtils.getPlayerAttackCooldown(damager) < 15) return;//如果攻击cd没满
            event.setDamage(EntityDamageEvent.DamageModifier.BASE,DAMAGE + event.getDamage(EntityDamageEvent.DamageModifier.BASE));
        }
    }


    public void onDamage(Entity entity,Entity damager,ItemStack item) {

    }
}
