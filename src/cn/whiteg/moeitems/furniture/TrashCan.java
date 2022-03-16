package cn.whiteg.moeitems.furniture;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import com.google.common.collect.MapMaker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TrashCan extends CustItem_CustModle implements Listener {
    private final static TrashCan a = new TrashCan();
    Map<UUID, Inventory> inventoryMap = new MapMaker().weakValues().makeMap();

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

    @EventHandler(ignoreCancelled = true)
    public void onBreak(HangingBreakEvent event) {
        if (!inventoryMap.isEmpty() && event.getEntity() instanceof ItemFrame itemFrame){
            if (is(itemFrame.getItem())){
                final Inventory inv = breakInv(itemFrame);
                if (inv != null){
                    inv.clear();
                    final List<HumanEntity> viewers = inv.getViewers();
                    if (!viewers.isEmpty()){
                        for (HumanEntity viewer : viewers) {
                            viewer.closeInventory();
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(PlayerInteractEntityEvent event) {
        final Player player = event.getPlayer();
        if (!player.isSneaking() && event.getRightClicked() instanceof ItemFrame itemFrame){
            if (is(itemFrame.getItem())){
                final Inventory inv = getOrCreateInv(itemFrame);
                player.openInventory(inv);
                event.setCancelled(true);
            }
        }

    }

    public Inventory getInv(Entity entity) {
        return inventoryMap.get(entity.getUniqueId());
    }

    public Inventory breakInv(Entity entity) {
        return inventoryMap.remove(entity.getUniqueId());
    }

    public Inventory getOrCreateInv(Entity entity) {
        final UUID uniqueId = entity.getUniqueId();
        var inv = inventoryMap.get(uniqueId);
        if (inv == null){
            inv = Bukkit.createInventory(null,InventoryType.CHEST,getDisplayName());
//            inventoryMap.put(new UUID(uniqueId.getMostSignificantBits(),uniqueId.getLeastSignificantBits()),inv);
            inventoryMap.put(uniqueId,inv);
        }
        return inv;
    }
}

