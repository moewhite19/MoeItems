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

public class BoltHat extends CustItem_CustModle {
    private static final BoltHat a = new BoltHat();

    private BoltHat() {
        super(Material.SHEARS,95,"§7螺栓头");
        NamespacedKey key = new NamespacedKey(MoeItems.plugin,"bolt_hat");
        ShapedRecipe r = new ShapedRecipe(key,createItem());
        r.shape(
                "A A"
        );
        r.setIngredient('A',Material.GRAY_CANDLE);
        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
    }

    public static BoltHat get() {
        return a;
    }

}

