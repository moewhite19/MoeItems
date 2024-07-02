package cn.whiteg.moeitems.items;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.moeitems.utils.CommonUtils;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.EntityUtils;
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

public abstract class EpeeAbs extends CustItem_CustModle implements Listener {
    private final float DAMAGE;
    UUID DelayDamage;


    public EpeeAbs(int id,String displayName,float damage) {
        super(Material.NETHERITE_SWORD,id,displayName);
        this.DAMAGE = damage;
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent event) {
        final double damage = event.getDamage();
        if (damage < 3 || event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
        if (event.getDamager() instanceof LivingEntity damager){
            final Entity entity = event.getEntity();
            if (entity.getUniqueId().equals(DelayDamage)){
                DelayDamage = null;
                event.setDamage(EntityDamageEvent.DamageModifier.BASE,damage + DAMAGE);
                return; //忽略使用延迟攻击触发的事件
            }
            final EntityEquipment equipment = damager.getEquipment();
            if (equipment == null) return;
            final ItemStack hand = equipment.getItemInMainHand();
            //如果手持不是巨剑跳出
            if (!is(hand)) return;
            //cd内无法攻击
            if (damager instanceof Player player && player.hasCooldown(getMaterial())){
                if (EntityUtils.getPlayerPrepTime(damager) < 15){
                    event.setCancelled(true);
                }
                return;
            }
            damager.getWorld().playSound(damager.getLocation(),Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK,SoundCategory.PLAYERS,1f,0.2f); //播放挥砍音效
            //延迟后攻击
            if (damager instanceof Player player) player.setCooldown(getMaterial(),15);
            Bukkit.getScheduler().runTaskLater(MoeItems.plugin,() -> {
                if (damager instanceof Player player) player.setCooldown(getMaterial(),15);
                if (damager.isDead() || !is(equipment.getItemInMainHand()) || entity.isDead() || !damager.getWorld().equals(entity.getWorld()))
                    return;//如果挥砍结束条件不符合
                var loc = damager.getEyeLocation();
                loc.getWorld().spawnParticle(Particle.SWEEP_ATTACK,loc.clone().add(VectorUtils.viewVector(loc).multiply(1.1F)),3); //在前方播放粒子
                damager.getWorld().playSound(loc,Sound.ENTITY_PLAYER_ATTACK_NODAMAGE,SoundCategory.PLAYERS,1f,0.4f); //播放挥砍音效
                DelayDamage = entity.getUniqueId();
                EntityUtils.setPlayerPrepTime(damager,20);
                damager.attack(entity);
                onDamage(entity,damager,hand);
                //挥砍
                for (Entity nearbyEntity : entity.getNearbyEntities(1D,1D,1D)) {
                    if (nearbyEntity.equals(damager)) continue; //这是要砍到自己啊哈哈哈哈
                    DelayDamage = nearbyEntity.getUniqueId();
                    EntityUtils.setPlayerPrepTime(damager,20);
                    damager.attack(nearbyEntity);
                    onDamage(nearbyEntity,damager,hand);
                }
            },12L);
            event.setCancelled(true);
        }
    }

    public void onDamage(Entity entity,Entity damager,ItemStack item) {

    }

}
