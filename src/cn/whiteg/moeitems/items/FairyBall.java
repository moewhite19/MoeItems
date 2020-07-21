package cn.whiteg.moeitems.items;

import cn.whiteg.chanlang.LangUtils;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftSnowball;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
        net.minecraft.server.v1_16_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        //获取物品NBT
        NBTTagCompound rootTag = nmsItem.getOrCreateTag();
        if (rootTag.hasKey(key)){
            NBTTagCompound cap = rootTag.getCompound(key);

            for (Map.Entry<String, NBTBase> entry : cap.map.entrySet()) {
                String[] args = entry.getKey().split(":");
                if (args.length != 2 && !(entry.getValue() instanceof NBTTagCompound)){
                    if (player != null){
                        player.sendMessage("无效实体ID " + entry.getKey());
                    }
                    continue;
                }
                MinecraftKey minecraftKey = new MinecraftKey(args[0],args[1]);
                EntityTypes<?> type = IRegistry.ENTITY_TYPE.get(minecraftKey);
                NBTTagCompound data = (NBTTagCompound) entry.getValue();
                String custName = data.getString("CustomName");
                EntityHuman humanEntity = player == null ? null : ((CraftHumanEntity) player).getHandle();
                //生成实体
                net.minecraft.server.v1_16_R1.Entity entity = type.spawnCreature(((CraftWorld) loc.getWorld()).getHandle(),data,IChatBaseComponent.ChatSerializer.jsonToComponent(custName),humanEntity,new BlockPosition(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ()),EnumMobSpawn.SPAWN_EGG,true,true,CreatureSpawnEvent.SpawnReason.EGG);
                //导入元数据
                if (entity != null && !entity.dead){
                    entity.load(data);
                    if (entity instanceof EntityLiving){
                        ((EntityLiving) entity).loadData(data);
                    }
                }
            }

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


                EntityLiving nmsHit = ((CraftLivingEntity) hit).getHandle();

                NBTTagCompound cap = new NBTTagCompound();
                nmsHit.save(cap);
                nmsHit.saveData(cap);

                NBTTagCompound compound = new NBTTagCompound();
                compound.set(nmsHit.getMinecraftKey().toString(),cap);
                rootTag.set(key,compound);
                rootTag.remove("Rate");

                ItemStack nItem = nmsItem.getBukkitStack();
                ItemMeta meta = nItem.getItemMeta();
                meta.setLore(Arrays.asList("","§9扑捉到了: " + "§7" + (hit.getCustomName() == null || hit.getCustomName().isEmpty() ? LangUtils.getEntityTypeName(hit.getType()) : hit.getCustomName() + "§7(" + LangUtils.getEntityTypeName(hit.getType()) + ")")));
                nItem.setItemMeta(meta);
                hit.remove();
                loc.getWorld().dropItem(loc,nItem);
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
                net.minecraft.server.v1_16_R1.ItemStack nms = CraftItemStack.asNMSCopy(item);
                NBTTagCompound tag = nms.getOrCreateTag();
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
}

