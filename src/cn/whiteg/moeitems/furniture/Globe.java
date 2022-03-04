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

public class Globe extends CustItem_CustModle {
    private final static Globe a = new Globe();

    private Globe() {
        super(Material.BOWL,66,"§a地球仪");
        NamespacedKey key = new NamespacedKey(MoeItems.plugin,"globe");
        ShapedRecipe r = new ShapedRecipe(key,createItem());
        r.shape(
                "A",
                "B",
                "C"
        );
        r.setIngredient('A',new RecipeChoice.MaterialChoice(new ArrayList<>(ItemTypeUtils.getPlanks())));
        r.setIngredient('B',Material.STICK);
        r.setIngredient('C',new RecipeChoice.MaterialChoice(new ArrayList<>(ItemTypeUtils.getSlabs())));
        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
        RPGArmour.plugin.getCanBreakEntityItem().addCanPlaceItemFarm(this);
    }

    public static Globe get() {
        return a;
    }
}

