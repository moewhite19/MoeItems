package cn.whiteg.moeitems.Listener;

import cn.whiteg.moeitems.Event.BreakCustItemEntity;
import cn.whiteg.rpgArmour.api.CustItem;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
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
    public static String TAG = "candestroy";

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

    public static boolean addCanPlaceItemFarm(CustItem custItem) {
        return canPlaceItemFarm.add(custItem);
    }

    public static boolean removeCanPlaceItemFarm(CustItem custItem) {
        return canPlaceItemFarm.remove(custItem);
    }


    @EventHandler
    public void onLClickEntity(EntityDamageByEntityEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
        if (event.getEntity() instanceof ArmorStand){
            Player player = null;
            Entity damage = event.getDamager();
            if (damage instanceof Projectile){
                Projectile projectile = (Projectile) damage;
                if (projectile.getShooter() instanceof Player) player = (Player) ((Projectile) damage).getShooter();
            } else if (damage instanceof Player) player = (Player) damage;
            if (player == null) return;
            ArmorStand as = (ArmorStand) event.getEntity();
            if (as.isVisible()) return;
            Set<String> s = as.getScoreboardTags();
            if (!s.contains(TAG)) return;
            Location loc = as.getLocation();
            //检查领地权限
            Residence res = Residence.getInstance();
            if (!res.isResAdminOn(player)){
                FlagPermissions flag = res.getPermsByLocForPlayer(loc,player);
                if (!flag.playerHasHints(player,Flags.destroy,true)){
                    return;
                }
            }
            BreakCustItemEntity ev = new BreakCustItemEntity(as,player);
            Bukkit.getPluginManager().callEvent(ev);
            if (ev.isCancelled()) return;
            drop(as,loc);
        }
    }

    //锁住的展示框不会触发Damage事件，所以只好用交互事件右键了
    @EventHandler
    public void onRClick(PlayerInteractEntityEvent event) {
        if (event.getPlayer().isSneaking() && event.getRightClicked() instanceof ItemFrame){
            ItemFrame itemFrame = (ItemFrame) event.getRightClicked();
            if (itemFrame.isFixed() && itemFrame.getScoreboardTags().contains(TAG)){
                Location loc = itemFrame.getLocation();
                //检查领地权限
                Residence res = Residence.getInstance();
                Player player = event.getPlayer();
                if (!res.isResAdminOn(player)){
                    FlagPermissions flag = res.getPermsByLocForPlayer(loc,player);
                    if (!flag.playerHasHints(player,Flags.destroy,true)){
                        return;
                    }
                }
                BreakCustItemEntity ev = new BreakCustItemEntity(itemFrame,player);
                Bukkit.getPluginManager().callEvent(ev);
                if (ev.isCancelled()) return;
                drop(itemFrame,loc);
                event.setCancelled(true);
            }
        }
    }

    //未完成的家具放置
    @EventHandler(ignoreCancelled = true)
    public void placeItemFarm(PlayerInteractEvent event) {
        if (canPlaceItemFarm.isEmpty() || event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getBlockFace() != BlockFace.UP)
            return;
        Player player = event.getPlayer();
        EquipmentSlot hand = event.getHand();
        ItemStack item;
        PlayerInventory pi = player.getInventory();
        if (hand == EquipmentSlot.HAND){
            item = pi.getItemInMainHand();
        } else if (hand == EquipmentSlot.OFF_HAND){
            item = pi.getItemInOffHand();
        } else return;

        for (CustItem custItem : canPlaceItemFarm) {
            if (custItem.is(item)){
                Block block = event.getClickedBlock();
                if (block == null || !block.getType().isSolid()) return;
                event.setCancelled(true);
                Location loc = block.getLocation();
                loc.setY(loc.getY() + 1);
                if (loc.getBlock().getType().isSolid()) return;

                //检查领地权限
                Residence res = Residence.getInstance();
                if (!res.isResAdminOn(player)){
                    FlagPermissions flag = Residence.getInstance().getPermsByLocForPlayer(loc,player);
                    if (!flag.playerHasHints(player,Flags.place,true)){
                        return;
                    }
                }

                //生成物品展示框
                ItemFrame itemFrame;
                try{
                    itemFrame = loc.getWorld().spawn(loc,ItemFrame.class);
                }catch (IllegalArgumentException e){
                    return;
                }
                if (itemFrame.isDead()) return;

                if (item.getAmount() > 1){
                    item.setAmount(item.getAmount() - 1);
                } else if (hand == EquipmentSlot.HAND){
                    pi.setItemInMainHand(null);
                } else {
                    pi.setItemInOffHand(null);
                }


                itemFrame.setFacingDirection(BlockFace.UP);

                float yaw = Math.abs(EntityUtils.getEntityRotYaw(player) % 360);
                itemFrame.setRotation(getRotation(yaw));
                //player.sendMessage("方向: " + getRotation(yaw) + " : " + yaw);
                itemFrame.setFixed(true);
                itemFrame.setVisible(false);
                itemFrame.addScoreboardTag(TAG);
                itemFrame.addScoreboardTag("dontedit");
                item = item.clone();
                item.setAmount(1);
                itemFrame.setItem(item);
                return;
            }
        }
    }

    public Rotation getRotation(float yaw) {
        if (yaw > 337.5) return Rotation.FLIPPED;
        if (yaw > 292.5) return Rotation.CLOCKWISE_135;
        if (yaw > 247.5) return Rotation.CLOCKWISE;
        if (yaw > 202.5) return Rotation.CLOCKWISE_45;
        if (yaw > 157.5) return Rotation.NONE;
        if (yaw > 112.5) return Rotation.COUNTER_CLOCKWISE_45;
        if (yaw > 67.5) return Rotation.COUNTER_CLOCKWISE;
        if (yaw > 22.5) return Rotation.FLIPPED_45;
        return Rotation.FLIPPED;
    }
}
