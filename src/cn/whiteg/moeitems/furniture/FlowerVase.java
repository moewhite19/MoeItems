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
        super(Material.BOWL,4,"§3花瓶");
        NamespacedKey key = new NamespacedKey(MoeItems.plugin,"flower_vase");
        ShapedRecipe r = new ShapedRecipe(key,createItem());
        r.shape(
                "ABA",
                "AAA"
        );
        r.setIngredient('A',Material.GLASS_PANE);
        r.setIngredient('B',Material.ALLIUM);
        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
    }

    public static FlowerVase get() {
        return a;
    }
}

