package cn.whiteg.moeitems.items;

import cn.whiteg.mmocore.reflection.FieldAccessor;
import cn.whiteg.mmocore.reflection.ReflectUtil;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.RandomUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BurstPickaxe extends CustItem_CustModle implements Listener {
    private static final BurstPickaxe pickaxe = new BurstPickaxe();
    static FieldAccessor<Float> blockDurabilityField;
    static Method getItem;
    static Method getDestroySpeed;//  public float getDestroySpeed(BlockState state)
    static Sound sound = Sound.BLOCK_STONE_BREAK;

    static {

        for (Method method : net.minecraft.world.item.ItemStack.class.getMethods()) {
            if (method.getReturnType().equals(Item.class)){
                getItem = method;
                break;
            }
        }

        for (Method method : net.minecraft.world.item.ItemStack.class.getMethods()) {
            if (method.getReturnType().equals(float.class)){
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1 && parameterTypes[0].equals(BlockState.class)){
                    getDestroySpeed = method;
                    break;
                }
            }
        }

        try{
            blockDurabilityField = new FieldAccessor<>(ReflectUtil.getFieldFormStructure(BlockBehaviour.class,boolean.class,float.class,boolean.class)[1]);
        }catch (NoSuchFieldException e){
            e.printStackTrace();
        }


//        try{
//            getBlock = BlockStateHolder.class.getDeclaredField("c");
//            getBlock.setAccessible(true);
//        }catch (NoSuchFieldException e){
//            e.printStackTrace();
//        }
    }

    final int size = 1;
    private final int maxDamage;
    boolean looping = false;

    public BurstPickaxe() {
        super(Material.STONE_PICKAXE,2,"§4爆裂镐");
        maxDamage = getMaterial().getMaxDurability() - 2;
    }

    public static BurstPickaxe get() {
        return pickaxe;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (looping){
            if (!is(item) || !(item.getItemMeta() instanceof Damageable damageable) || damageable.getDamage() >= maxDamage){
                looping = false;
            }
        } else if (is(item)){
            try{
                Block block = event.getBlock();
                BlockState iblockdata = ((CraftBlock) block).getNMS();
                World world = block.getWorld();
                if (getBreakSpeed(item,iblockdata) < 2F) return;
                world.playSound(block.getLocation(),sound,1f,0.5f); //播放音效
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
                net.minecraft.world.level.block.Block nBlock = iblockdata.getBlock();
                float durability = getBlockDurability(nBlock);
                looping = true;
                int startDamage = ((Damageable) item.getItemMeta()).getDamage();
                int destroy = 0;
                loop:
                for (x = sx; x <= ex; x++) {
                    for (y = sy; y <= ey; y++) {
                        for (z = sz; z <= ez; z++) {
                            Block b = world.getBlockAt(x,y,z);
                            BlockState ibd = ((CraftBlock) b).getNMS();
                            net.minecraft.world.level.block.Block nb = ibd.getBlock();
                            if (getBreakSpeed(item,ibd) > 2F && durability == getBlockDurability(nb) /*&& ((CraftBlock) b).getNMS().getBlock().isDestroyable()*/){
                                if (is(item)){
                                    PlayerBreakBlock(player,b);
                                    destroy++;
                                } else {
                                    break loop;
                                }
                            }
                        }
                    }
                }

                //恢复耐久
                final ItemMeta itemMeta1 = item.getItemMeta();
                if (!(itemMeta1 instanceof Damageable damageable)){
                    return;
                }
                int nowDamage = damageable.getDamage();
//                player.sendMessage(startDamage + " -> " + nowDamage);
                if (startDamage < nowDamage){
                    //计算扣耐久的几率(受耐久附魔影响
                    float ratio = (nowDamage - startDamage) / ((float) destroy);
//                    player.sendMessage(String.valueOf(ratio));
                    if (RandomUtil.getRandom().nextFloat() <= ratio){
                        //总共扣一点耐久
                        damageable.setDamage(startDamage + 1);
                    } else {
                        damageable.setDamage(startDamage);
                    }
                    item.setItemMeta(damageable);
                }
            }catch (Throwable e){
                e.printStackTrace();
            }
            looping = false;
        }
    }

    public void PlayerBreakBlock(Player player,Block block) {
        player.breakBlock(block);
//        PlayerInteractManager playerInv = ((EntityPlayer) NMSUtils.getNmsEntity(player)).d;
//        playerInv.a(((CraftBlock) block).getPosition(),PacketPlayInBlockDig.EnumPlayerDigType.a,"destroyed");
//        playerInv.a(((CraftBlock) block).getPosition(),PacketPlayInBlockDig.EnumPlayerDigType.STOP_DESTROY_BLOCK,"destroyed");
    }

    public float getBreakSpeed(ItemStack itemStack,BlockState block) {
        try{
            var nmsItem = CraftItemStack.asNMSCopy(itemStack);
            return (float) getDestroySpeed.invoke(nmsItem,block);
        }catch (IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
        return 0f;
    }

    public float getBlockDurability(net.minecraft.world.level.block.Block block) {
        try{
            return (float) blockDurabilityField.get(block);
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0f;
    }

    public Item getItem(net.minecraft.world.item.ItemStack itemStack) {
        try{
            return (Item) getItem.invoke(itemStack);
        }catch (IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
        return null;
    }
}
