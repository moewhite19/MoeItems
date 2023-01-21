package cn.whiteg.moeitems.items;

import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class LightningRod extends CustItem_CustModle implements Listener {
    private static final LightningRod a;

    static {
        a = new LightningRod();
    }

    private LightningRod() {
        super(Material.BOWL,46,"§9聚雷阵");
        RPGArmour.plugin.getCanBreakEntityItem().addCanPlaceItemFarm(this);
//        NamespacedKey key = new NamespacedKey(MoeItems.plugin,"lightningrod");
//        ShapedRecipe r = new ShapedRecipe(key,createItem());
//        r.shape(
//                " A ",
//                "ABA"
//        );
//        r.setIngredient('A',Material.IRON_BARS);
//        r.setIngredient('B',Material.IRON_BLOCK);
//        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
    }

    public static LightningRod get() {
        return a;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntitySpawn(LightningStrikeEvent event) {
        LightningStrike e = event.getLightning();
        if (event.getCause() == LightningStrikeEvent.Cause.UNKNOWN || e.isEffect()) return;
        Location rod = null;
        Location loc = e.getLocation();
        double d = 0;
        for (Entity entity : e.getNearbyEntities(64,512,64)) {
            if (entity instanceof LivingEntity){
                LivingEntity le = (LivingEntity) entity;
                EntityEquipment ee = le.getEquipment();
                if (ee != null){
                    ItemStack h = ee.getHelmet();
                    if (is(h)){
                        if (rod == null){
                            rod = entity.getLocation();
                            d = rod.distanceSquared(loc);
                        } else {
                            Location r2 = entity.getLocation();
                            double d2 = loc.distanceSquared(r2);
                            if (d2 < d){
                                rod = r2;
                                d = d2;
                            }
                        }
                    }
                }

            } else if (entity instanceof ItemFrame){
                ItemFrame frame = (ItemFrame) entity;
                ItemStack item = frame.getItem();
                if (is(item)){
                    if (rod == null){
                        rod = entity.getLocation();
                        d = rod.distanceSquared(loc);
                    } else {
                        Location r2 = entity.getLocation();
                        double d2 = loc.distanceSquared(r2);
                        if (d2 < d){
                            rod = r2;
                            d = d2;
                        }
                    }
                }
            }
        }
        if (rod != null){
//            EntityLightning ne = ((CraftLightningStrike) e).getHandle();
//            ne.setPosition(rod.getX(),rod.getY(),rod.getZ());
//            event.setCancelled(true);
//            rod.getWorld().strikeLightning(rod,LightningStrikeEvent.Cause.UNKNOWN);
//            CraftWorld
            e.teleport(rod);
//            e.remove();
//            event.setCancelled(true);
//            rod.getWorld().strikeLightningEffect(rod);
//            MoeItems.logger.info("闪电");

        }
    }
//
//    public void bkockIgnite(BlockIgniteEvent event) {
//        if (event.getCause() != BlockIgniteEvent.IgniteCause.LIGHTNING) return;
//        MoeItems.logger.info("点燃");
//    }

    //@EventHandler(ignoreCancelled = true)

}

