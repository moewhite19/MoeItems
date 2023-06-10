package cn.whiteg.moeitems.furniture;

import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Chair extends CustItem_CustModle implements Listener {
    private final static Chair a = new Chair();

    private Chair() {
        super(Material.BOWL,90,"§f椅子");
//        NamespacedKey key = new NamespacedKey(MoeItems.plugin,"chair");
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

    //    @EventHandler(ignoreCancelled = true ,priority = EventPriority.LOW)
    @EventHandler
    public void onClick(PlayerInteractEntityEvent event) {
        if (!event.getPlayer().isSneaking() && event.getRightClicked() instanceof ItemFrame frame){
            final ItemStack item = frame.getItem();
            if (is(item)){
                frame.addPassenger(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @Override
    public boolean hasId(int id) {
        return (id >= 90 && id <= 106) || (id >= 164 && id <= 176);
    }
}

