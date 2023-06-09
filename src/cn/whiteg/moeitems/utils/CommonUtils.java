package cn.whiteg.moeitems.utils;

import cn.whiteg.mmocore.reflection.FieldAccessor;
import cn.whiteg.mmocore.reflection.ReflectUtil;
import cn.whiteg.mmocore.util.NMSUtils;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class CommonUtils {
    //todo 计划移动到RPGArmour
    private static final FieldAccessor<Integer> playerAttackCooldownnField;
    static {
        try{
            playerAttackCooldownnField = new FieldAccessor<>(ReflectUtil.getFieldFormStructure(EntityLiving.class,
                    float.class,
                    int.class,
                    float.class,
                    float.class,
                    int.class,
                    float.class,
                    float.class,
                    float.class,
                    int.class)[4]);
        }catch (NoSuchFieldException e){
            throw new RuntimeException(e);
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
        if (item.hasItemMeta()){
            var meta = item.getItemMeta();
            meta = item.getItemMeta();
            if (meta instanceof Damageable damageable){
                return damageable.getDamage();
            }
        }
        return 0;
    }

    @Deprecated
    //获取攻击cd
    public static float getPlayerAttackCooldown(org.bukkit.entity.Entity player) {
        if (player instanceof LivingEntity) return playerAttackCooldownnField.get(NMSUtils.getNmsEntity(player));
        return 0f;
    }

    @Deprecated
    //设置攻击cd
    public static void setPlayerAttackCooldown(org.bukkit.entity.Entity player,int cooldown) {
        if (player instanceof LivingEntity) playerAttackCooldownnField.set(NMSUtils.getNmsEntity(player),cooldown);
    }
}
