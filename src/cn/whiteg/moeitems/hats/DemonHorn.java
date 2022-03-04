package cn.whiteg.moeitems.hats;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Phantom;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class DemonHorn extends CustItem_CustModle {
    private static final DemonHorn a = new DemonHorn();

    private DemonHorn() {
        super(Material.SHEARS,97,"§c恶魔角");
        NamespacedKey key = new NamespacedKey(MoeItems.plugin,"demon_horn");
        ShapedRecipe r = new ShapedRecipe(key,createItem());
        r.shape(
                "A A"
        );
        r.setIngredient('A',Material.RED_CANDLE);
        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
    }

    public static DemonHorn get() {
        return a;
    }


    //    @EventHandler(ignoreCancelled = true)
    //todo 来自地狱生物的肯定，不被地狱生物敌对(未完成，考虑中
    public void onTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof LivingEntity entity && (event.getEntity() instanceof Creeper || event.getEntity() instanceof Phantom)){
            ItemStack helmet;
            var equipment = entity.getEquipment();
            if (equipment == null) return;
            helmet = equipment.getHelmet();
            if (helmet == null) return;

            if (is(helmet)){
                event.setCancelled(true);
            }
        }
    }

}

