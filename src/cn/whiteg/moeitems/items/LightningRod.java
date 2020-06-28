package cn.whiteg.moeitems.items;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class LightningRod extends CustItem_CustModle implements Listener {
    private static final LightningRod a;

    static {
        a = new LightningRod();
    }

    private LightningRod() {
        super(Material.BOWL,46,"§9聚雷阵");
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

    @EventHandler(ignoreCancelled = true)
    public void onRClickBlock(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getBlockFace() != BlockFace.UP) return;
        Player p = event.getPlayer();
        EquipmentSlot hand = event.getHand();
        ItemStack item;
        PlayerInventory pi = p.getInventory();
        if (hand == EquipmentSlot.HAND){
            item = pi.getItemInMainHand();
        } else if (hand == EquipmentSlot.OFF_HAND){
            item = pi.getItemInOffHand();
        } else return;
        if (!is(item)) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        Location loc = block.getLocation().toCenterLocation();
        loc.setY(loc.getY() + 1);
        if (loc.getBlock().getType() != Material.AIR) return;
        Residence res = Residence.getInstance();
        if (!res.isResAdminOn(p)){
            FlagPermissions flag = Residence.getInstance().getPermsByLocForPlayer(loc,p);
            if (!flag.playerHasHints(p,Flags.place,true)){
                return;
            }
        }
        if (item.getAmount() > 1){
            item.setAmount(item.getAmount() - 1);
        } else if (hand == EquipmentSlot.HAND){
            pi.setItemInMainHand(null);
        } else {
            pi.setItemInOffHand(null);
        }
        event.setCancelled(true);
        ItemStack i = item.clone();
        i.setAmount(1);
        ItemFrame itemFrame = loc.getWorld().spawn(loc,ItemFrame.class);
        itemFrame.setFixed(true);
        itemFrame.setVisible(false);
        itemFrame.setItem(item);
    }


}

