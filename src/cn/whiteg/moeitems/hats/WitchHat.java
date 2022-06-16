package cn.whiteg.moeitems.hats;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.ItemToolUtil;
import cn.whiteg.rpgArmour.utils.RandomUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class WitchHat extends CustItem_CustModle implements Listener {
    private static final WitchHat a = new WitchHat();

    private WitchHat() {
        super(Material.SHEARS,96,"§c女巫帽");
//        NamespacedKey key = new NamespacedKey(MoeItems.plugin,"witch_hat");
//        ShapedRecipe r = new ShapedRecipe(key,createItem());
//        r.shape(
//                "A A"
//        );
//        r.setIngredient('A',Material.RED_CANDLE);
//        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
    }

    public static WitchHat get() {
        return a;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.MAGIC){
            if (event.getEntity() instanceof LivingEntity livingEntity){
                final EntityEquipment equipment = livingEntity.getEquipment();
                if (equipment != null){
                    final ItemStack helmet = equipment.getHelmet();
                    if (is(helmet)){
                        final double immune = event.getDamage() * 0.75;
                        event.setDamage(EntityDamageEvent.DamageModifier.ARMOR,-immune);//修改盔甲值
                        //扣耐久值
                        if (ItemToolUtil.damage(helmet,RandomUtil.getRandom().nextInt(2)/*(int) Math.ceil(immune)*/)){
                            equipment.setHelmet(null);
//                            livingEntity.getWorld().playSound(Sound.DAMA); //物品损坏播放声音
                        }
                        //                    event.setDamage(event.getDamage() * 0.25); //直接设置总值
                    }
                }
            }
        }
    }

}

