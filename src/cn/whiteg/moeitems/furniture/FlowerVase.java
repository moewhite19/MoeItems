package cn.whiteg.moeitems.furniture;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

public class FlowerVase extends CustItem_CustModle {
    private final static FlowerVase a = new FlowerVase();

    private FlowerVase() {
        super(Material.BOWL,4,"§3绒球葱花瓶");
        NamespacedKey key = new NamespacedKey(MoeItems.plugin,"flower_vase");
        ShapedRecipe r = new ShapedRecipe(key,createItem());
        r.shape(
                "AAA",
                " B "
        );
        r.setIngredient('A',Material.ALLIUM);
        r.setIngredient('B',Material.GLASS_BOTTLE);
        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
        RPGArmour.plugin.getCanBreakEntityItem().addCanPlaceItemFarm(this);
    }

    public static FlowerVase get() {
        return a;
    }
}

