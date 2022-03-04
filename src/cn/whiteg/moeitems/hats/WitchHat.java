package cn.whiteg.moeitems.hats;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Phantom;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;

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

}

