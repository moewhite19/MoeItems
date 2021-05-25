package cn.whiteg.moeitems.utils;

import net.minecraft.server.v1_16_R3.Entity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

public class CommonUtils {
    static AtomicInteger entityCount;

    static {
        try{
            Field count_f = Entity.class.getDeclaredField("entityCount");
            count_f.setAccessible(true);
            entityCount = (AtomicInteger) count_f.get(null);
        }catch (NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
        }
    }

    public static int getNextEntityId() {
        return entityCount.incrementAndGet();
    }

    //获取位位置中心点
    public static Location locationToCenter(Location loc) {
        loc.setX(loc.getBlockX() + 0.5D);
        loc.setY(loc.getBlockY() + 0.5D);
        loc.setZ(loc.getBlockZ() + 0.5D);
        return loc;
    }

    public static Object getNmsEntity(org.bukkit.entity.Entity entity) {
        try{
            return entity.getClass().getMethod("getHandle").invoke(entity);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 消耗物品耐久度
     *
     * @param inv  玩家背包
     * @param slot 使用栏位
     * @param item 物品
     */
    public static void damageItem(PlayerInventory inv,EquipmentSlot slot,ItemStack item,ItemMeta meta) {
        if (meta == null) meta = item.getItemMeta();
        if (meta instanceof Damageable){
            Damageable damageable = (Damageable) meta;
            if (damageable.getDamage() < getItemMaxDamage(item)){
                damageable.setDamage(damageable.getDamage() + 1);
                item.setItemMeta(meta);
            } else {
                useItem(inv,slot,item);
            }
        }
    }

    /**
     * 消耗物品
     *
     * @param inv  玩家背包
     * @param slot 使用栏位
     * @param item 物品
     */
    public static void useItem(PlayerInventory inv,EquipmentSlot slot,ItemStack item) {
        if (item.getAmount() > 1){
            item.setAmount(item.getAmount() - 1);
        } else inv.setItem(slot,null);
    }

    public static int getItemMaxDamage(ItemStack item) {
        var ni = ((CraftItemStack) item).getHandle();
        if (ni == null) ni = CraftItemStack.asNMSCopy(item);
        var tm = ni.getItem();
//        return ni.getItemUseMaxDuration();
        return tm.getMaxDurability();
    }

}
