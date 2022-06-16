package cn.whiteg.moeitems.hats;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;

public class WitchHat extends CustItem_CustModle {
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

    @EventHandler(ignoreCancelled = true,priority = EventPriority.LOW)
    public void onDamage(EntityDamageEvent event){
        if (event.getCause() == EntityDamageEvent.DamageCause.MAGIC){
            if(event.getEntity() instanceof LivingEntity livingEntity){
                final EntityEquipment equipment = livingEntity.getEquipment();
                if(equipment != null && is(equipment.getHelmet())){
//            event.setDamage(EntityDamageEvent.DamageModifier.ARMOR,-(event.getDamage() * 0.85));//修改盔甲值
                    event.setDamage(event.getDamage() * 0.25); //直接设置总值
                }
            }
        }
    }

}

