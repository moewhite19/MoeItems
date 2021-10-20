package cn.whiteg.moeitems.items;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.NMSUtils;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import net.minecraft.server.level.PlayerInteractManager;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.IBlockDataHolder;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.Random;

import static org.bukkit.enchantments.Enchantment.DURABILITY;

public class BurstPickaxe extends CustItem_CustModle implements Listener {
    private static final BurstPickaxe pickaxe = new BurstPickaxe();
    private static final Random random = new Random();
    static Field getBlock;

    static {
        try{
            getBlock = IBlockDataHolder.class.getDeclaredField("c");
            getBlock.setAccessible(true);
        }catch (NoSuchFieldException e){
            e.printStackTrace();
        }
    }

    final int size = 1;
    boolean looping = false;

    public BurstPickaxe() {
        super(Material.STONE_PICKAXE,2,"§4爆裂镐");
    }

    public static BurstPickaxe get() {
        return pickaxe;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (looping){
            if (is(item)){
                ItemMeta meta = item.getItemMeta();
                boolean durability = player.getGameMode() == GameMode.CREATIVE;
                meta.removeEnchant(DURABILITY);
                //确保物品没有耐久附魔的情况下 百分之80的几率恢复耐久度
                if (!durability && random.nextFloat() < 0.95F){
                    if (meta instanceof Damageable){
                        Damageable damageable = (Damageable) meta;
                        if (damageable.hasDamage() && damageable.getDamage() > 0){
                            damageable.setDamage(damageable.getDamage() - 1);
                        }
                        item.setItemMeta(meta);
                    }
                }
            } else {
                looping = false;
            }
        } else if (is(item)){
            try{
                Block block = event.getBlock();
                IBlockData iblockdata = ((CraftBlock) block).getNMS();
                World world = block.getWorld();
                if (getBreakSpeed(item,iblockdata) < 2F) return;
                event.setCancelled(true);
                int x = block.getX();
                int y = block.getY();
                int z = block.getZ();
                int sx = x - size;
                int sy = y - size;
                int sz = z - size;

                int ex = x + size;
                int ey = y + size;
                int ez = z + size;
                float durability = iblockdata.getBlock().getDurability();
                looping = true;
                loop:
                for (x = sx; x <= ex; x++) {
                    for (y = sy; y <= ey; y++) {
                        for (z = sz; z <= ez; z++) {
                            Block b = world.getBlockAt(x,y,z);
                            IBlockData ib = ((CraftBlock) b).getNMS();
                            if (getBreakSpeed(item,ib) > 2F && durability == ib.getBlock().getDurability() /*&& ((CraftBlock) b).getNMS().getBlock().isDestroyable()*/){
                                if (is(item)){
                                    PlayerBreakBlock(player,b);
                                } else {
                                    break loop;
                                }
                            }
                        }
                    }
                }
            }catch (Throwable e){
                e.printStackTrace();
            }
            looping = false;
        }
    }

    public void PlayerBreakBlock(Player player,Block block) {
        PlayerInteractManager playerInv = ((CraftPlayer) player).getHandle().d;
        playerInv.a(((CraftBlock) block).getPosition(),PacketPlayInBlockDig.EnumPlayerDigType.a,"destroyed");
//        playerInv.a(((CraftBlock) block).getPosition(),PacketPlayInBlockDig.EnumPlayerDigType.STOP_DESTROY_BLOCK,"destroyed");
    }

    public float getBreakSpeed(ItemStack item,IBlockData block) {
        var nmsItem = CraftItemStack.asNMSCopy(item);
        return nmsItem.getItem().getDestroySpeed(nmsItem,block);
    }
}
