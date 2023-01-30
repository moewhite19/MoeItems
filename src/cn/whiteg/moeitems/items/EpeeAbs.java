package cn.whiteg.moeitems.items;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.*;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class EpeeAbs extends CustItem_CustModle implements Listener {
    private final float DAMAGE;
    UUID IGNORE = new UUID(0,0);
    Map<UUID, DamageTick> DAMAGE_MAP = new HashMap<>();

    public EpeeAbs(int id,String displayName,float damage) {
        super(Material.NETHERITE_SWORD,id,displayName);
        this.DAMAGE = damage;
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof LivingEntity damager){
            if (event.getEntity().getUniqueId().equals(IGNORE)){
//                IGNORE = null; //感觉没必要这样做
                return; //忽略使用延迟攻击触发的事件
            }
            final EntityEquipment equipment = damager.getEquipment();
            if (equipment == null) return;
            final ItemStack hand = equipment.getItemInMainHand();
            if (!is(hand)) return;
            if (!multiAttacks(damager,event.getEntity(),event.getDamage() + DAMAGE)){
                final Location location = damager.getLocation();
                damager.getWorld().playSound(location,Sound.ENTITY_PLAYER_ATTACK_WEAK,SoundCategory.PLAYERS,1f,0.5f); //播放挥砍音效
                Bukkit.getScheduler().runTaskLater(MoeItems.plugin,() -> {
                    final DamageTick damageTick = attacksEnd(damager);
                    final Set<Entity> entities = damageTick.getEntitySet();
                    if (entities == null || damager.isDead() || !is(equipment.getItemInMainHand())) return;//如果挥砍结束条件不符合
                    for (Entity entity : entities) {
                        if (entity.isDead()) continue;
                        if (entity instanceof Damageable damageable){
                            IGNORE = entity.getUniqueId();
                            damageable.damage(damageTick.getDamage(),damager);
                        }

                    }
                },10L);
            }
            event.setCancelled(true);
        }
    }


    //记录攻击的目标
    public boolean multiAttacks(LivingEntity damager,Entity entity,double damage) {
        DamageTick list = DAMAGE_MAP.get(damager.getUniqueId());
        if (list == null){
            list = new DamageTick(damage);
            list.add(entity);
            DAMAGE_MAP.put(damager.getUniqueId(),list);
            return false;
        }
        list.add(entity);
        return true;
    }

    //横扫到的生物
    public DamageTick attacksEnd(LivingEntity damage) {
        final DamageTick remove = DAMAGE_MAP.remove(damage.getUniqueId());
        if (remove.getEntitySet().isEmpty()) return null;
        return remove;
    }

    public class DamageTick {
        final private Set<Entity> entitySet = new HashSet<>(8);
        final private double damage;

        DamageTick(double damage) {
            this.damage = damage;
        }

        public boolean add(Entity entity) {
            return entitySet.add(entity);
        }

        public Set<Entity> getEntitySet() {
            return entitySet;
        }

        public double getDamage() {
            return damage;
        }
    }
}
