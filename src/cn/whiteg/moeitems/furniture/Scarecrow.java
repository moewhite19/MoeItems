package cn.whiteg.moeitems.furniture;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.ItemTypeUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;

public class Scarecrow extends CustItem_CustModle {
    private final static Scarecrow a = new Scarecrow();

    private Scarecrow() {
        super(Material.BOWL,33,"§6稻草人");
        NamespacedKey key = new NamespacedKey(MoeItems.plugin,"scarecrow");
        ShapedRecipe r = new ShapedRecipe(key,createItem());
        r.shape(
                " B ",
                "BAB",
                "CCC"
        );
        r.setIngredient('A',Material.ARMOR_STAND);
        r.setIngredient('B',Material.HAY_BLOCK);
        r.setIngredient('C',new RecipeChoice.MaterialChoice(new ArrayList<>(ItemTypeUtils.getWools())));
        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
    }

    public static Scarecrow get() {
        return a;
    }
}

