package cn.whiteg.moeitems.items;

import cn.whiteg.rpgArmour.api.CustEntityID;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class LeashBow extends CustItem_CustModle implements Listener {
    public static LeashBow THIS = new LeashBow();
    public static LeashArrow leashArrow = new LeashArrow();

    public LeashBow() {
        super(Material.BOW,10,"栓绳弓");
    }

    public static LeashBow get() {
        return THIS;
    }

    @EventHandler(ignoreCancelled = true)
    public void shoot(EntityShootBowEvent event) {
        if (is(event.getBow()) && event.getProjectile() instanceof Arrow arrow){
            leashArrow.init(arrow);
            event.setConsumeItem(true); //无视无限附魔属性，永远都会消耗弓箭
            var shooter = event.getEntity();
            for (Entity nearbyEntity : shooter.getNearbyEntities(10,10,10)) {
                if (nearbyEntity instanceof LivingEntity livingEntity){
                    if (livingEntity.isLeashed() && livingEntity.getLeashHolder().equals(shooter)){
                        livingEntity.setLeashHolder(event.getProjectile());
                        livingEntity.setVelocity(arrow.getVelocity());
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(ProjectileHitEvent event) {
        if (event.getHitEntity() instanceof LivingEntity entity){
            if (event.getEntity() instanceof Arrow arrow && leashArrow.is(arrow)){
                entity.setLeashHolder(arrow);
                var vector = arrow.getVelocity().multiply(0.8F);
                entity.setVelocity(entity.getVelocity().add(vector));
                event.setCancelled(true);
            }
        }
    }

    //不掉落栓绳
    @EventHandler
    public void onUnLeash(EntityUnleashEvent event) {
        if (event.getEntity() instanceof LivingEntity entity && entity.isLeashed()){
            var leash = entity.getLeashHolder();
            if (leash instanceof Arrow){
                event.setDropLeash(false);
            }
        }
    }


    public static class LeashArrow extends CustEntityID {
        public LeashArrow() {
            super("LeashArrow",Arrow.class);
        }
    }

}
