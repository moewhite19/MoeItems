package cn.whiteg.moeitems.furniture;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

public class DeskClock extends CustItem_CustModle {
    private final static DeskClock a = new DeskClock();

    private DeskClock() {
        super(Material.BOWL,28,"§6座钟");
        NamespacedKey key = new NamespacedKey(MoeItems.plugin,"desk_clock");
        ShapedRecipe r = new ShapedRecipe(key,createItem());
        r.shape(
                "AAA",
                "ABA",
                "AAA"
        );
        r.setIngredient('A',Material.OAK_WOOD);
        r.setIngredient('B',Material.CLOCK);
        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
        RPGArmour.plugin.getCanBreakEntityItem().addCanPlaceItemFarm(this);
    }

    public static DeskClock get() {
        return a;
    }
}

