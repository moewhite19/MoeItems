package cn.whiteg.moeitems.furniture;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

public class TrashCan extends CustItem_CustModle {
    private final static TrashCan a = new TrashCan();

    private TrashCan() {
        super(Material.BOWL,67,"§7垃圾桶");
        NamespacedKey key = new NamespacedKey(MoeItems.plugin,"trash_can");
        ShapedRecipe r = new ShapedRecipe(key,createItem());
        r.shape(
                "A",
                "B"
        );
        r.setIngredient('A',Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        r.setIngredient('B',Material.BUCKET);
        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
        RPGArmour.plugin.getCanBreakEntityItem().addCanPlaceItemFarm(this);
    }

    public static TrashCan get() {
        return a;
    }
}

