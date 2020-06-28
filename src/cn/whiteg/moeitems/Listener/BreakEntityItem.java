package cn.whiteg.moeitems.Listener;

import cn.whiteg.moeitems.Event.BreakCustArmourStand;
import cn.whiteg.rpgArmour.api.CustItem;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class BreakEntityItem implements Listener {
    public final static List<CustItem> canPlaceItemFarm = new LinkedList<>();
    public static String tag = "candestroy";

    public static Item drop(ArmorStand e,Location loc) {
        ItemStack helmet = e.getEquipment().getHelmet();
        e.remove();
        if (helmet.getType() != Material.AIR){
            return loc.getWorld().dropItem(loc,helmet);
        }
        return null;
    }

    public static Item drop(ItemFrame e,Location loc) {
        ItemStack helmet = e.getItem();
        e.remove();
        if (helmet.getType() != Material.AIR){
            return loc.getWorld().dropItem(loc,helmet);
        }
        return null;
    }

    @EventHandler
    public void onLClickEntity(EntityDamageByEntityEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
        Entity entity = event.getDamager();
        Player player = null;
        if (entity instanceof Projectile){
            Projectile projectile = (Projectile) entity;
            if (projectile.getShooter() instanceof Player) player = (Player) ((Projectile) entity).getShooter();
        } else if (entity instanceof Player) player = (Player) entity;
        if (player == null) return;
        entity = event.getEntity();
        if (entity instanceof ArmorStand){
            ArmorStand as = (ArmorStand) entity;
            if (as.isVisible()) return;
            Set<String> s = as.getScoreboardTags();
            if (!s.contains(tag)) return;
            Location loc = entity.getLocation();
            //检查领地权限
            Residence res = Residence.getInstance();
            if (!res.isResAdminOn(player)){
                FlagPermissions flag = res.getPermsByLocForPlayer(loc,player);
                if (!flag.playerHasHints(player,Flags.destroy,true)){
                    return;
                }
            }
            BreakCustArmourStand ev = new BreakCustArmourStand(entity,player);
            Bukkit.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) return;
            drop((ArmorStand) entity,loc);
        } else if (entity instanceof ItemFrame){
            ItemFrame itemFrame = (ItemFrame) entity;
            if (itemFrame.isFixed() && itemFrame.getScoreboardTags().contains(tag)){
                Location loc = itemFrame.getLocation();
                //检查领地权限
                Residence res = Residence.getInstance();
                if (!res.isResAdminOn(player)){
                    FlagPermissions flag = res.getPermsByLocForPlayer(loc,player);
                    if (!flag.playerHasHints(player,Flags.destroy,true)){
                        return;
                    }
                }
                drop(itemFrame,loc);
                event.setCancelled(true);
            }
        }
    }

    //未完成的家具放置
    //@EventHandler(ignoreCancelled = true)
    public void placeItemFarm(PlayerInteractEvent event) {
        if (canPlaceItemFarm.isEmpty() || event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getBlockFace() != BlockFace.UP)
            return;
        Player p = event.getPlayer();
        EquipmentSlot hand = event.getHand();
        ItemStack item;
        PlayerInventory pi = p.getInventory();
        if (hand == EquipmentSlot.HAND){
            item = pi.getItemInMainHand();
        } else if (hand == EquipmentSlot.OFF_HAND){
            item = pi.getItemInOffHand();
        } else return;

        for (CustItem custItem : canPlaceItemFarm) {
            if (custItem.is(item)){
                Block block = event.getClickedBlock();
                if (block == null) return;
                Location loc = block.getLocation();
                loc.setX(loc.getBlockX() + 0.5D);
                loc.setY(loc.getBlockY() + 0.5D);
                loc.setZ(loc.getBlockZ() + 0.5D);
                loc.setY(loc.getY() + 1);
                if (loc.getBlock().getType() != Material.AIR) return;
                Residence res = Residence.getInstance();
                if (!res.isResAdminOn(p)){
                    FlagPermissions flag = Residence.getInstance().getPermsByLocForPlayer(loc,p);
                    if (!flag.playerHasHints(p,Flags.place,true)){
                        return;
                    }
                }
                event.setCancelled(true);
                loc.setY(loc.getY() + 1);
                ItemFrame itemFrame = loc.getWorld().spawn(loc,ItemFrame.class);
                if (itemFrame.isDead()) return;

                if (item.getAmount() > 1){
                    item.setAmount(item.getAmount() - 1);
                } else if (hand == EquipmentSlot.HAND){
                    pi.setItemInMainHand(null);
                } else {
                    pi.setItemInOffHand(null);
                }
                itemFrame.setFixed(true);
                itemFrame.setVisible(false);
                itemFrame.setFacingDirection(BlockFace.UP);
                itemFrame.addScoreboardTag(tag);
                itemFrame.addScoreboardTag("dontedit");
                itemFrame.setItem(item);
                return;
            }
        }
    }

    //@EventHandler
    public void onClick(PlayerInteractEntityEvent event) {
        if (event.isCancelled() && event.getRightClicked() instanceof ItemFrame){
            ItemFrame itemFrame = (ItemFrame) event.getRightClicked();

        }

    }


}
