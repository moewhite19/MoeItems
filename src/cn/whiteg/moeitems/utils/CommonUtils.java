package cn.whiteg.moeitems.utils;

import cn.whiteg.rpgArmour.utils.NMSUtils;
import net.minecraft.world.entity.Entity;
import org.bukkit.Location;
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
            Field count_f = NMSUtils.getFieldFormType(Entity.class,AtomicInteger.class);

            count_f.setAccessible(true);
            entityCount = (AtomicInteger) count_f.get(null);
        }catch (NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
        }
    }

    public static int getNextEntityId() {
        return entityCount.incrementAndGet();
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
        if (item.hasItemMeta()){
            var meta = item.getItemMeta();
            meta = item.getItemMeta();
            if (meta instanceof Damageable damageable){
                return damageable.getDamage();
            }
        }
        return 0;
    }

}
