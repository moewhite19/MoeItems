package cn.whiteg.moeitems.furniture;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem_RangeOfModel;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class Chair extends CustItem_RangeOfModel implements Listener {
    private final static Chair a = new Chair();

    private Chair() {
        super(Material.BOWL,90,"§f椅子",90,105);
        NamespacedKey key = new NamespacedKey(MoeItems.plugin,"chair");
//        ShapedRecipe r = new ShapedRecipe(key,createItem());
//        r.shape(
//                "B",
//                "A"
//        );
//        r.setIngredient('A',Material.CAULDRON);
//        r.setIngredient('B',Material.LAVA_BUCKET);
//        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
        RPGArmour.plugin.getCanBreakEntityItem().addCanPlaceItemFarm(this);
    }

    public static Chair get() {
        return a;
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(PlayerInteractEntityEvent event) {
        if (!event.getPlayer().isSneaking() && event.getRightClicked() instanceof ItemFrame frame){
            final ItemStack item = frame.getItem();
            if (is(item)){
                frame.addPassenger(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }
}

