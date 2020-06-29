package cn.whiteg.moeitems.furniture;

import cn.whiteg.moeitems.Listener.BreakEntityItem;
import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.ItemTypeUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class Scarecrow extends CustItem_CustModle implements Listener {
    private final static Scarecrow a = new Scarecrow();
    private WeakReference<Location> cacheLoc = new WeakReference<>(null);

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
        BreakEntityItem.addCanPlaceItemFarm(this);
    }

    public static Scarecrow get() {
        return a;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity e = event.getEntity();
        Location cl = cacheLoc.get();
        if (cl != null){
            Location el = e.getLocation();
            if (el.getWorld() == cl.getWorld() && el.distance(cl) < 64){
                event.setCancelled(true);
                return;
            }
        }
        if (e.getType() != EntityType.PHANTOM) return;
        for (Entity entity : e.getNearbyEntities(64,512D,64)) {
            if (entity instanceof LivingEntity){
                LivingEntity le = (LivingEntity) entity;
                EntityEquipment ee = le.getEquipment();
                if (ee != null){
                    ItemStack h = ee.getHelmet();
                    if (is(h)){
                        event.setCancelled(true);
                        cacheLoc = new WeakReference<>(e.getLocation());
                        return;
                    }
                }

            } else if (entity instanceof ItemFrame){
                ItemFrame frame = (ItemFrame) entity;
                ItemStack item = frame.getItem();
                if (is(item)){
                    event.setCancelled(true);
                    cacheLoc = new WeakReference<>(e.getLocation());
                    return;
                }
            }
        }
    }
}

