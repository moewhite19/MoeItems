package cn.whiteg.moeitems.items;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Reclamation extends CustItem_CustModle implements Listener {
    static final BlockData BLOCK_DATA_AIR = Bukkit.createBlockData(Material.AIR);

    public Reclamation() {
        super(Material.GOLDEN_SWORD,26,"§0开拓者");
    }

    @EventHandler(ignoreCancelled = true)
    public void onUse(PlayerAnimationEvent event) {
        final Player player = event.getPlayer();
        if (is(player.getItemInHand())){
            player.sendActionBar("§0你好 精卫");
            final Location location = player.getLocation();
            final World world = location.getWorld();
            int size = 15;
            final int endX = location.getBlockX() + size;
            for (int x = location.getBlockX() - size; x < endX; x++) {
                final int endY = location.getBlockY() + size;
                for (int y = location.getBlockY() - size; y < endY; y++) {
                    final int endZ = location.getBlockZ() + size;
                    for (int z = location.getBlockZ() - size; z < endZ; z++) {
                        final Block blockAt = world.getBlockAt(x,y,z);
                        if (blockAt.getType() == Material.WATER)
                            world.setBlockData(x,y,z,BLOCK_DATA_AIR);
                        else if(blockAt.getBlockData() instanceof Waterlogged waterlogged){
                            waterlogged.setWaterlogged(false);
                            blockAt.setBlockData(waterlogged);
                        }
                    }
                }
            }
        }
    }
}
