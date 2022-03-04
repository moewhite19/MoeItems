package cn.whiteg.moeitems.furniture;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

public class Crucible extends CustItem_CustModle {
    private final static Crucible a = new Crucible();

    private Crucible() {
        super(Material.BOWL,65,"§7坩埚");
        NamespacedKey key = new NamespacedKey(MoeItems.plugin,"crucible");
        ShapedRecipe r = new ShapedRecipe(key,createItem());
        r.shape(
                "B",
                "A"
        );
        r.setIngredient('A',Material.CAULDRON);
        r.setIngredient('B',Material.LAVA_BUCKET);
        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
        RPGArmour.plugin.getCanBreakEntityItem().addCanPlaceItemFarm(this);
    }

    public static Crucible get() {
        return a;
    }
}

