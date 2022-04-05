package cn.whiteg.moeitems.items;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class BurstAxe extends CustItem_CustModle implements Listener {
    final static BurstAxe a;
    public static Tag<Material> LOGS;
    public static byte[][] FACES;
    static Sound sound = Sound.BLOCK_STONE_BREAK;

    static {
        a = new BurstAxe();
        LOGS = Tag.LOGS; //树干标签
//        FACES = new BlockFace[]{BlockFace.UP,BlockFace.WEST,BlockFace.SOUTH,BlockFace.EAST,BlockFace.NORTH};
        List<byte[]> list = new ArrayList<>();
        for (byte x = -1; x <= 1; x++) {
            for (byte y = 0; y <= 1; y++) {
                for (byte z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue; //忽略自己
                    list.add(new byte[]{x,y,z});
                }
            }
        }
        FACES = list.toArray(new byte[0][]);
    }

    public BurstAxe() {
        super(Material.STONE_AXE,3,"§4爆裂斧");
    }

    public static BurstAxe get() {
        return a;
    }


    @EventHandler(ignoreCancelled = true)
    public void onCutTree(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        if (player.hasCooldown(getMaterial())) return; //物品在CD时返回
        final Block block = event.getBlock();
        final Material material = block.getBlockData().getMaterial();
        if (!LOGS.isTagged(material)) return; //如果不是砍树木返回
        final ItemStack item = player.getInventory().getItemInMainHand();
        if (is(item)){
            final HashSet<Block> logs = new HashSet<>();
            final AtomicInteger leaves = new AtomicInteger();
            deepScan(logs,leaves,block,48);
//            player.sendMessage(leaves.get() + " , " + logs.size());
            if (logs.isEmpty() || leaves.get() < logs.size()) return; //没有原木，或者树叶较少时跳出
            logs.remove(block); //剔除当前采集的方块
            player.setCooldown(getMaterial(),Math.max(logs.size() / 2,20)); //设置cd，随着采集的木头增加而增加，最少20tick
            block.getWorld().playSound(block.getLocation(),sound,1f,0.5f); //播放音效
            for (Block log : logs) {
                final BlockData blockData = log.getBlockData();
                final Material mat = blockData.getMaterial();
                if (!mat.isAir()){
                    log.breakNaturally(item,true);  //不掉耐久度，但是不会触发事件
//                    player.breakBlock(log); //虽然可以检查玩家权限之类的，但是会掉耐久度
                }
            }
        }
    }


    public void deepScan(Set<Block> logs,AtomicInteger leaves,Block block,int life) {
        if (--life <= 0) return; //超出生命周期跳出
        final Material material = block.getBlockData().getMaterial();
        if (LOGS.isTagged(material)){
            logs.add(block);
        } else {
            if (block.getBlockData() instanceof Leaves leaveData && !leaveData.isPersistent()){ //是树叶并且非持续时添加选项
                leaves.addAndGet(1);
            }
            return;
        }


        for (byte[] face : FACES) {
            final Block relative = block.getRelative(face[0],face[1],face[2]);
            if (logs.contains(relative)) continue; //跳出已存在方块
            deepScan(logs,leaves,relative,life);
        }
    }

}

