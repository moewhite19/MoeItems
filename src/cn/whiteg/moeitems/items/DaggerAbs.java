package cn.whiteg.moeitems.items;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.VectorUtils;
import org.bukkit.*;
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

import java.util.UUID;

public abstract class DaggerAbs extends CustItem_CustModle implements Listener {
    private final float DAMAGE;

    public DaggerAbs(int id,String displayName,float damage) {
        super(Material.IRON_SWORD,id,displayName);
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
            //匕首不支持挥砍
            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK || (damager instanceof Player player && player.hasCooldown(getMaterial()))){
                event.setCancelled(true);
                return;
            }

            var loc = damager.getEyeLocation();
            damager.getWorld().playSound(loc,Sound.ENTITY_PLAYER_ATTACK_NODAMAGE,SoundCategory.PLAYERS,1f,0.4f); //播放挥砍音效
            if (damager instanceof Player player){
                player.setCooldown(getMaterial(),5);
            }
            if (event.getEntity() instanceof LivingEntity entity){
                entity.setNoDamageTicks(0);
            }
            event.setDamage(EntityDamageEvent.DamageModifier.BASE,DAMAGE);
            onDamage(event.getEntity(),damager,hand);
        }
    }

    public void onDamage(Entity entity,Entity damager,ItemStack item) {
    }
}
