package cn.whiteg.moeitems.items;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BurstPickaxe extends CustItem_CustModle implements Listener {
    private static final BurstPickaxe pickaxe = new BurstPickaxe();

    public BurstPickaxe() {
        super(Material.GOLDEN_PICKAXE,2,"§4爆裂稿");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (is(item)){
            //player.block
        }
    }
}
