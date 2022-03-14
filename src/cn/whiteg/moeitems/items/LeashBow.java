package cn.whiteg.moeitems.items;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustEntityChunkEvent;
import cn.whiteg.rpgArmour.api.CustEntityID;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.manager.CustEntityManager;
import cn.whiteg.rpgArmour.utils.NMSUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
                event.setCancelled(true);
                var vector = arrow.getVelocity().multiply(0.8F);
                entity.setVelocity(entity.getVelocity().add(vector));
                if (entity.setLeashHolder(arrow)){
//                    leashArrow.setLife(arrow,Integer.MIN_VALUE); //让弓箭不会消失
                    leashArrow.putLeashed(arrow,entity); //记录已牵着的实体
                }
            }
        }
    }

    //不掉落栓绳
    @EventHandler
    public void onUnLeash(EntityUnleashEvent event) {
        if (event.getEntity() instanceof LivingEntity entity && entity.isLeashed()){
            var leashed = entity.getLeashHolder();
            if (leashed instanceof Arrow arrow && leashArrow.is(arrow)){
                event.setDropLeash(false);
                leashArrow.removeLeashed(arrow,entity);
            }
        }
    }

    public static class LeashArrow extends CustEntityID implements CustEntityChunkEvent {
        static NamespacedKey LEASH_PATH = new NamespacedKey(MoeItems.plugin,"Leashed");
        static NamespacedKey UUID_MOST_SIGNIFICANT = new NamespacedKey(MoeItems.plugin,"Most");
        static NamespacedKey UUID_LEAST_SIGNIFICANT = new NamespacedKey(MoeItems.plugin,"Least");
        static Field LeashNBTTagCompound; //实体储存的栓绳对象

        static {
            try{
                LeashNBTTagCompound = NMSUtils.getFieldFormType(EntityInsentient.class,NBTTagCompound.class);
                LeashNBTTagCompound.setAccessible(true);
            }catch (NoSuchFieldException e){
                e.printStackTrace();
            }
        }

        public LeashArrow() {
            super("LeashArrow",Arrow.class);
            RPGArmour.plugin.getEntityManager().regEntity(this);
        }

        public void putLeashed(Arrow arrow,LivingEntity entity) {
            var root = CustEntityManager.getPersistentDataContainer(arrow,false);
            if (root == null) return;
            var array = root.get(LEASH_PATH,PersistentDataType.TAG_CONTAINER_ARRAY);
            var con = root.getAdapterContext().newPersistentDataContainer();
            serializationUuid(entity.getUniqueId(),con);
            if (array == null){
                array = new PersistentDataContainer[]{con};
            } else {
                array = Arrays.copyOf(array,array.length + 1);
                array[array.length - 1] = con;
            }
            root.set(LEASH_PATH,PersistentDataType.TAG_CONTAINER_ARRAY,array);
//            var nms = ((CraftLivingEntity) entity).getHandle();
//            if (nms instanceof EntityInsentient insentient){
//                NBTTagCompound compound = new NBTTagCompound();
//                compound.a("UUID",arrow.getUniqueId());
//                try{
//                    LeashNBTTagCompound.set(insentient,compound);
//                }catch (IllegalAccessException e){
//                    e.printStackTrace();
//                }
//            }
            CustEntityManager.setPersistentDataContainer(arrow,root); //保存
        }

        public void removeLeashed(Arrow arrow,LivingEntity entity) {
            var root = CustEntityManager.getPersistentDataContainer(arrow,false);
            if (root == null) return;
            var array = root.get(LEASH_PATH,PersistentDataType.TAG_CONTAINER_ARRAY);
            if (array == null) return;
            var uuid = entity.getUniqueId();

            var list = new ArrayList<PersistentDataContainer>(array.length - 1);
            for (PersistentDataContainer dataContainer : array) {
                var u = deserializeUuid(dataContainer);
                if (u == null || u.equals(uuid)) continue;
                list.add(dataContainer);
            }
            if (list.isEmpty()){
                root.remove(LEASH_PATH);
//                setLife(arrow,0);
            } else {
                root.set(LEASH_PATH,PersistentDataType.TAG_CONTAINER_ARRAY,list.toArray(new PersistentDataContainer[0]));
                CustEntityManager.setPersistentDataContainer(arrow,root); //保存
            }
        }

        public List<LivingEntity> getLeashed(Arrow arrow) {
            var root = CustEntityManager.getPersistentDataContainer(arrow,false);
            if (root == null) return null;
            var array = root.get(LEASH_PATH,PersistentDataType.TAG_CONTAINER_ARRAY);
            if (array == null) return null;
            var list = new ArrayList<LivingEntity>(array.length);
            for (PersistentDataContainer container : array) {
                var uuid = deserializeUuid(container);
                if (uuid == null) continue;
                var entity = arrow.getWorld().getEntity(uuid);
                if (entity instanceof LivingEntity livingEntity){
                    list.add(livingEntity);
                }
            }
            return list.isEmpty() ? null : list;
        }

        public UUID deserializeUuid(PersistentDataContainer container) {
            var m = container.get(UUID_MOST_SIGNIFICANT,PersistentDataType.LONG);
            var l = container.get(UUID_LEAST_SIGNIFICANT,PersistentDataType.LONG);
            if (l == null || m == null) return null;
            return new UUID(m,l);
        }


        public void serializationUuid(UUID uuid,PersistentDataContainer container) {
            container.set(UUID_MOST_SIGNIFICANT,PersistentDataType.LONG,uuid.getMostSignificantBits());
            container.set(UUID_LEAST_SIGNIFICANT,PersistentDataType.LONG,uuid.getLeastSignificantBits());
        }


        @Override
        public void load(Entity entity) {
            if (entity instanceof Arrow arrow){
                var entities = getLeashed(arrow);
                if (entities == null){
                    //如果没拴着实体则清理掉弓箭
                    arrow.remove();
                } else {
//                    setLife(arrow,Integer.MIN_VALUE);
                    for (LivingEntity livingEntity : entities) {
                        livingEntity.setLeashHolder(arrow);
                    }
                }
            }
        }

        @Override
        public void unload(Entity entity) {
            if (entity instanceof Arrow arrow){
                //如果没拴着实体则清理掉弓箭
                var entities = getLeashed(arrow);
                if (entities == null){
                    //如果没拴着实体则清理掉弓箭
                    arrow.remove();
                    return;
                }
                for (LivingEntity livingEntity : entities) {
                    livingEntity.setLeashHolder(null); //解除栓绳
                }
            }
        }
    }
}
