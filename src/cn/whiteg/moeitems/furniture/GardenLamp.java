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

public class GardenLamp extends CustItem_CustModle {
    private final static GardenLamp a = new GardenLamp();

    private GardenLamp() {
        super(Material.BOWL,23,"§6庭院灯");
        NamespacedKey key = new NamespacedKey(MoeItems.plugin,"garden_lmap");
        ShapedRecipe r = new ShapedRecipe(key,createItem());
        r.shape(
                " C ",
                "BAB",
                "CBC"
        );
        r.setIngredient('A',Material.GLOWSTONE);
        r.setIngredient('B',Material.STICK);
        r.setIngredient('C',new RecipeChoice.MaterialChoice(new ArrayList<>(ItemTypeUtils.getLogs())));
        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
        RPGArmour.plugin.getCanBreakEntityItem().addCanPlaceItemFarm(this);
    }

    public static GardenLamp get() {
        return a;
    }
}

