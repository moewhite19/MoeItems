package cn.whiteg.moeitems.items;

import cn.whiteg.chanlang.LangUtils;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.entity.CraftSnowball;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class FairyBall extends CustItem_CustModle implements Listener {
    private final static FairyBall fairyBall = new FairyBall();
    private final static Random random = new Random();
    //private final String TAG = this.getClass().getSimpleName().toLowerCase();

    public FairyBall() {
        super(Material.SNOWBALL,5,"§6精灵球");
    }

    public static FairyBall get() {
        return fairyBall;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onShor(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Snowball)) return;
        CraftSnowball snowball = (CraftSnowball) event.getEntity();
        ItemStack item = EntityUtils.getSnowballItem(snowball);
        if (!is(item)) return;
        Player player = snowball.getShooter() instanceof Player ? (Player) snowball.getShooter() : null;
        if (player == null || !player.hasPermission("moeitems.fairball.use")){
            if (player != null) player.sendActionBar(" §7无法使用");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball)) return;
        CraftSnowball snowball = (CraftSnowball) event.getEntity();
        ItemStack item = EntityUtils.getSnowballItem(snowball);
        if (!is(item)) return;
        Player player = snowball.getShooter() instanceof Player ? (Player) snowball.getShooter() : null;
        Location loc = snowball.getLocation();
        FlagPermissions p = Residence.getInstance().getPermsByLoc(loc);
        if (player == null ? (!p.has(Flags.build,false)) : (!Residence.getInstance().isResAdminOn(player) && !p.playerHasHints(player,Flags.build,false))){
            loc.getWorld().dropItem(loc,item);
            return;
        }
        String key = "Capture";
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        //获取物品NBT
        CompoundTag rootTag = nmsItem.getComponents();
        if (rootTag.contains(key)){
            CompoundTag capDir = rootTag.getCompound(key);
            String id = null;
            CompoundTag data = null;
            if (capDir.contains("id")){
                data = capDir;
                id = data.getString("id");
            } else {
                Iterator<Map.Entry<String, Tag>> it = capDir.tags.entrySet().iterator();
                if (it.hasNext()){
                    Map.Entry<String, Tag> base = it.next();
                    if (base.getValue() instanceof CompoundTag){
                        data = (CompoundTag) base.getValue();
                        id = base.getKey();
                    }
                }
            }

            if (data == null || id == null){
                if (player != null) player.sendMessage(" 无效数据");
                return;
            }
            ResourceLocation minecraftKey = ResourceLocation.parse(id);
            net.minecraft.world.entity.EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(minecraftKey);
            ServerPlayer humanEntity = player == null ? null : ((CraftPlayer) player).getHandle();
            //生成实体

            final ServerLevel level = ((CraftWorld) loc.getWorld()).getHandle();
            var entity = type.spawn(level,null,humanEntity,new BlockPos(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ()),MobSpawnType.SPAWN_EGG,true,true);
            //导入元数据
            if (entity != null && entity.isAlive()){
                removeUUID(data);
                setLoc(data,entity.getBlockX(),entity.getBlockY(),entity.getBlockZ());
                entity.load(data);
            } else if (player != null) player.sendMessage(" 无法生成实体");

        } else {
            org.bukkit.entity.Entity hit = event.getHitEntity();

            if (hit == null || hit instanceof Player || hit.isDead()) return;
            if ((hit instanceof Boss || hit instanceof Monster || hit instanceof Ghast || hit instanceof Slime || hit instanceof ArmorStand) && (player == null || !player.hasPermission("moeitems.fairball.monster"))){
                if (player != null) player.sendActionBar(" §7无法扑捉");
                return;
            }

            if (hit instanceof LivingEntity){
                //获取并计算几率
                float rate;
                rate = rootTag.getFloat("Rate");
                if (rate == 0F) rate = 0.1F;
                if (random.nextFloat() > rate){
                    if (player != null){
                        player.sendActionBar(" §7扑捉失败");
                    }
                    return;
                }


                net.minecraft.world.entity.LivingEntity nmsHit = ((CraftLivingEntity) hit).getHandle();

                CompoundTag capDir = new CompoundTag();
                CompoundTag cap = new CompoundTag();
                nmsHit.ejectPassengers();   //清理乘客
                if (nmsHit.saveAsPassenger(cap)){
                    removeUUID(cap);
                    capDir.set(nmsHit.getMinecraftKey().toString(),cap);
                    rootTag.set(key,capDir);
                    rootTag.remove("Rate");
                    ItemStack nItem = nmsItem.getBukkitStack();
                    ItemMeta meta = nItem.getItemMeta();
                    meta.setLore(Arrays.asList("","§9扑捉到了: " + "§7" + (hit.getCustomName() == null || hit.getCustomName().isEmpty() ? LangUtils.getEntityTypeName(hit.getType()) : hit.getCustomName() + "§7(" + LangUtils.getEntityTypeName(hit.getType()) + ")")));
                    nItem.setItemMeta(meta);
                    hit.remove();
                    loc.getWorld().dropItem(loc,nItem);
                }
            } else {
                player.sendActionBar(" §7没有扑捉到任何东西");
            }
        }
    }

    @Override
    public ItemStack createItem(List<String> list) {
        ItemStack item = super.createItem(list);
        if (list.size() >= 2){
            try{
                float f = Float.parseFloat(list.get(1));
                var nms = CraftItemStack.asNMSCopy(item);
                CompoundTag tag = nms.getOrCreateTag();
                tag.set("Rate",NBTTagFloat.a(f));
                item = nms.asBukkitMirror();
                ItemMeta met = item.getItemMeta();
                met.setLore(Arrays.asList("","§3成功率: §7" + f * 100 + "%"));
                item.setItemMeta(met);
                return nms.asBukkitMirror();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return item;
    }

    public void removeUUID(CompoundTag nbtTagCompound) {
        nbtTagCompound.remove("UUID");
        nbtTagCompound.remove("Rotation");
        nbtTagCompound.remove("Motion");
        nbtTagCompound.remove("Pos");
        nbtTagCompound.remove("Dimension");
    }

    public void setLoc(CompoundTag nbt,double x,double y,double z) {
        NBTTagList list = new NBTTagList();
        list.add(NBTTagDouble.a(x));
        list.add(NBTTagDouble.a(y));
        list.add(NBTTagDouble.a(z));
        nbt.set("Pos",list);
    }
}

