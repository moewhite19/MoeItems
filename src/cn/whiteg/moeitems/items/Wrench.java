package cn.whiteg.moeitems.items;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.moeitems.Setting;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Piston;
import org.bukkit.block.data.type.PistonHead;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Wrench extends CustItem_CustModle implements Listener {
    final static Wrench a;
    final static String editTag = "dontedit";

    static {
        a = new Wrench();
    }

    public Wrench() {
        super(Material.SHEARS,48,"§5扳手");
    }

    public static Wrench get() {
        return a;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onRClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        Player player = event.getPlayer();
        if (player.isSneaking()) return;
        ItemStack item = event.getItem();
        if (!is(item)) return;
        if (!Residence.getInstance().isResAdminOn(player)){
            FlagPermissions perm = Residence.getInstance().getPermsByLocForPlayer(block.getLocation(),player);
            if (!perm.playerHasHints(player,Flags.build,true)) return;
        }
        BlockData data = block.getBlockData();
        if (data instanceof Piston){
            Piston piston = (Piston) data;
            if (piston.isExtended()) return;
            chanBlock(block,piston);
        } else if (data instanceof Chest){
            Chest chest = (Chest) data;
            if (chest.getType() != Chest.Type.SINGLE) return;
            chanBlock(block,(Directional) data);
        } else if (data instanceof Bed || data instanceof PistonHead){
            return;
        } else if (data instanceof Directional){
            chanBlock(block,(Directional) data);
        } else if (data instanceof Rotatable){
            rotableBlock(block,(Rotatable) data);
        } else return;
        player.sendActionBar("已修改方块");
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onClickItemFram(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof ItemFrame){
            if (event.getHand() != EquipmentSlot.HAND)
                return;
            Player player = event.getPlayer();
            if (player.isSneaking()) return;
            ItemStack item = player.getInventory().getItemInMainHand();
            if (!is(item)) return;
            ItemFrame itemFrame = (ItemFrame) event.getRightClicked();
            if (itemFrame.getScoreboardTags().contains(editTag)) return;
            if (Setting.DEBUG) player.sendMessage("当前展示框标签: " + itemFrame.getScoreboardTags());
            if (!Residence.getInstance().isResAdminOn(player)){
                FlagPermissions perm = Residence.getInstance().getPermsByLocForPlayer(itemFrame.getLocation(),player);
                if (!perm.playerHasHints(player,Flags.build,true)) return;
            }
            itemFrame.setFixed(!itemFrame.isFixed());
            player.sendActionBar("已修改展示框为: " + (itemFrame.isFixed() ? "锁定" : "解锁"));
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamageItemFram(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof ItemFrame && event.getDamager() instanceof Player){
            Player player = (Player) event.getDamager();
            ItemStack item = player.getInventory().getItemInMainHand();
            ItemFrame itemFrame = (ItemFrame) event.getEntity();
            if (itemFrame.getScoreboardTags().contains(editTag)) return;
            if (!is(item)){
                if (!itemFrame.isVisible()){
                    item = itemFrame.getItem();
                    if (item.getType() != Material.AIR){
                        Location loc = itemFrame.getLocation();
                        itemFrame.remove();
                        loc.getWorld().dropItem(loc,new ItemStack(Material.ITEM_FRAME));
                        loc.getWorld().dropItem(loc,item);
                    }
                }
                return;
            }
            if (player.isSneaking()) return;
            if (itemFrame.getItem().getType() == Material.AIR) return;
            if (!Residence.getInstance().isResAdminOn(player)){
                FlagPermissions perm = Residence.getInstance().getPermsByLocForPlayer(itemFrame.getLocation(),player);
                if (!perm.playerHasHints(player,Flags.build,true)) return;
            }
            itemFrame.setVisible(!itemFrame.isVisible());
            player.sendActionBar("已修改展示框为: " + (itemFrame.isVisible() ? "可视" : "不可视"));
            event.setCancelled(true);
        }
    }


    public void chanBlock(Block block,Directional data) {
        List<BlockFace> faces = new ArrayList<>(data.getFaces());
        int size = faces.size();
        int i = 0;
        while (i < size) {
            if (data.getFacing() == (faces.get(i++))){
                if (i < size) data.setFacing(faces.get(i));
                else data.setFacing(faces.get(0));
                block.setBlockData(data);
                return;
            }
        }
        MoeItems.logger.warning("没有找到方块方向");
    }

    public void rotableBlock(Block block,Rotatable data) {
        BlockFace[] fs = BlockFace.values();
        List<BlockFace> faces = new ArrayList<>(fs.length - 3);
        for (BlockFace v : fs) {
            if (v == BlockFace.UP || v == BlockFace.DOWN || v == BlockFace.SELF) continue;
            faces.add(v);
        }
        BlockFace f = data.getRotation();
        int size = faces.size();
        int i = 0;
        while (i < size) {
            if (f == (faces.get(i++))){
                if (i < size) data.setRotation(faces.get(i));
                else data.setRotation(faces.get(0));
                block.setBlockData(data);
                return;
            }
        }

    }
}

