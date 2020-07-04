package cn.whiteg.moeitems.items;

import cn.whiteg.chanlang.LangUtils;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class FairyBall extends CustItem_CustModle implements Listener {
    private final static FairyBall fairyBall = new FairyBall();
    //private final String TAG = this.getClass().getSimpleName().toLowerCase();

    public FairyBall() {
        super(Material.SNOWBALL,5,"§6大师球");
    }

    public static FairyBall get() {
        return fairyBall;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball)) return;
        CraftSnowball snowball = (CraftSnowball) event.getEntity();
        ItemStack item = EntityUtils.getSnowballItem(snowball);
        if (!is(item)) return;
        CommandSender sender = snowball.getShooter() instanceof Player ? (CommandSender) snowball.getShooter() : Bukkit.getConsoleSender();


        Location loc = snowball.getLocation();
        String key = "Capture";
        net.minecraft.server.v1_16_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsItem.getTag();
        if (tag.hasKey(key)){
            NBTTagCompound cap = tag.getCompound(key);
            Iterator<Map.Entry<String, NBTBase>> it = cap.map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, NBTBase> entry = it.next();
                String[] args = entry.getKey().split(":");
                if (args.length != 2 && !(entry.getValue() instanceof NBTTagCompound)){
                    sender.sendMessage("无效实体ID " + entry.getKey());
                    continue;
                }
                MinecraftKey minecraftKey = new MinecraftKey(args[0],args[1]);
                EntityTypes<?> type = IRegistry.ENTITY_TYPE.get(minecraftKey);
                NBTTagCompound data = (NBTTagCompound) entry.getValue();
                String custName = data.getString("CustomName");
                //sender.sendMessage("放出:" + (custName.isEmpty() ? entry.getKey() : custName) + "\n" + data.toString());
                EntityHuman humanEntity;
                if (sender instanceof HumanEntity){
                    humanEntity = ((CraftHumanEntity) sender).getHandle();
                } else humanEntity = null;
                net.minecraft.server.v1_16_R1.Entity entity = type.spawnCreature(((CraftWorld) loc.getWorld()).getHandle(),data,IChatBaseComponent.ChatSerializer.jsonToComponent(custName),humanEntity,new BlockPosition(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ()),EnumMobSpawn.SPAWN_EGG,true,true,CreatureSpawnEvent.SpawnReason.EGG);
                if (entity instanceof EntityLiving){
                    ((EntityLiving) entity).loadData(data);
                }

            }
        } else {
            org.bukkit.entity.Entity hit = event.getHitEntity();
            if (hit instanceof Player || hit instanceof Boss) return;
            if (hit instanceof LivingEntity){
                EntityLiving entity = ((CraftLivingEntity) hit).getHandle();

                NBTTagCompound cap = new NBTTagCompound();
                entity.save(cap);
                entity.saveData(cap);

                NBTTagCompound map = new NBTTagCompound();
                map.set(entity.getMinecraftKey().toString(),cap);
                tag.set(key,map);

                ItemStack nItem = nmsItem.getBukkitStack();
                ItemMeta meta = nItem.getItemMeta();
                meta.setLore(Arrays.asList("","§9扑捉到了: " + "§7" + (hit.getCustomName() == null || hit.getCustomName().isEmpty() ? LangUtils.getEntityTypeName(hit.getType()) : hit.getCustomName() + "§7(" + LangUtils.getEntityTypeName(hit.getType()) + ")")));
                nItem.setItemMeta(meta);
                hit.remove();
                loc.getWorld().dropItem(loc,nItem);
            } else {
                sender.sendMessage("没有扑捉到任何东西");
            }
        }


    }


}

