package cn.whiteg.moeitems.foods;

import cn.whiteg.chanlang.LangUtils;
import cn.whiteg.moepacketapi.api.event.PacketSendEvent;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GuisePotion extends CustItem_CustModle implements Listener {
    private static final GuisePotion a;
    //    private static DataWatcherObject<Optional<IChatBaseComponent>> entityCustName;
    private static Field spawnEntityLivingPacketId;
    private static Field spawnEntityLivingPacketType;
    private static Field spawnHumanId;
    private static Field spawnEntityPacketId;
    private static Field spawnEntityPacketType;
    private static Field entityMetaataList;

    static {
        a = new GuisePotion();
        try{
            spawnEntityLivingPacketId = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("a");
            spawnEntityLivingPacketId.setAccessible(true);
            spawnEntityLivingPacketType = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("c");
            spawnEntityLivingPacketType.setAccessible(true);
            spawnEntityPacketId = PacketPlayOutSpawnEntity.class.getDeclaredField("a");
            spawnEntityPacketId.setAccessible(true);
            spawnEntityPacketType = PacketPlayOutSpawnEntity.class.getDeclaredField("k");
            spawnEntityPacketType.setAccessible(true);
            spawnHumanId = PacketPlayOutNamedEntitySpawn.class.getDeclaredField("a");
            spawnHumanId.setAccessible(true);
            entityMetaataList = PacketPlayOutEntityMetadata.class.getDeclaredField("b");
            entityMetaataList.setAccessible(true);
            Field entityCustNameF = net.minecraft.server.v1_15_R1.Entity.class.getDeclaredField("az");
            entityCustNameF.setAccessible(true);

            //未完成
//            entityCustName = (DataWatcherObject<Optional<IChatBaseComponent>>) entityCustNameF.get(null);
        }catch (NoSuchFieldException e){
            e.printStackTrace();
        }
    }

    Map<Integer, Staus> map = Collections.synchronizedMap(new HashMap<>());


    private GuisePotion() {
        super(Material.POTION,2,"§d变形剂");
    }

    public static GuisePotion get() {
        return a;
    }


    @EventHandler(ignoreCancelled = true)
    public void onEat(PlayerItemConsumeEvent event) {
        if (!is(event.getItem())) return;
        Player p = event.getPlayer();
        Location ploc = p.getLocation();
        org.bukkit.entity.Entity entity = null;
        double d = 0D;
        for (org.bukkit.entity.Entity e : p.getNearbyEntities(30D,30D,30D)) {
            if (e instanceof Mob){
                if (e instanceof Player) continue;
                double ds = ploc.distance(e.getLocation());
                if (d == 0 || ds < d){
                    entity = e;
                    d = ds;
                }
            }
        }
        if (entity == null) return;
        setGuise(p,entity);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (map.isEmpty()) return;
        map.remove(event.getPlayer().getEntityId());
    }

    @EventHandler(ignoreCancelled = true)
    public void onSendPack(PacketSendEvent event) {
        if (map.isEmpty()) return;
        if (event.getPacket() instanceof PacketPlayOutNamedEntitySpawn){
            try{
                int id = (int) spawnHumanId.get(event.getPacket());
                Staus sta = map.get(id);
                if (sta != null){
                    if (event.getPlayer() != null && event.getPlayer().getEntityId() == id) return;
                    event.setPacket(sta.getSpawnPacket());
                    event.getChannel().pipeline().writeAndFlush(sta.getMetaPacket());
                    event.getChannel().pipeline().writeAndFlush(sta.getEquipmentPacket());
                }
            }catch (IllegalAccessException e){
                e.printStackTrace();
            }

        }
    }

    public boolean setGuise(Player player,org.bukkit.entity.Entity tager) {
        EntityPlayer np = ((CraftPlayer) player).getHandle();
        try{
            Staus staus = new Staus(player,tager);
            Object spawnPacket = staus.getSpawnPacket();
            PacketPlayOutEntityDestroy destroyEntity = new PacketPlayOutEntityDestroy(np.getId());
            Object metaPacket = staus.getMetaPacket();
            Object equipmentPacket = staus.getEquipmentPacket();
            for (org.bukkit.entity.Entity entity : player.getWorld().getEntities()) {
                if (entity instanceof Player){
                    Player p = (Player) entity;
                    if (!p.canSee(player)) continue;
                    if (p.equals(player)) continue;
                    PlayerConnection con = ((CraftPlayer) p).getHandle().playerConnection;
                    con.sendPacket(destroyEntity);
                    con.sendPacket((Packet<?>) spawnPacket);
                    con.sendPacket((Packet<?>) metaPacket);
                    con.sendPacket((Packet<?>) equipmentPacket);
                }
            }
            player.setCustomNameVisible(true);
            player.setCustomName(player.getName());
            player.sendMessage("已伪装成实体 " + LangUtils.getEntityTypeName(tager.getType()));
            map.put(np.getId(),staus);
            return true;
        }catch (IllegalAccessException ignored){
        }
        return false;
    }

    public class Staus {
        private final EntityPlayer player;
        private final PacketPlayOutEntityMetadata metaPacket;
        private net.minecraft.server.v1_15_R1.Entity tager;

        public Staus(Player player,org.bukkit.entity.Entity entity) {
            this.player = ((CraftPlayer) player).getHandle();
            player.setCustomName(player.getName());
            tager = ((CraftEntity) entity).getHandle();

            String v1 = entity.getCustomName();
            boolean v2 = entity.isCustomNameVisible();

            entity.setCustomName(player.getName());
            entity.setCustomNameVisible(true);
            metaPacket = new PacketPlayOutEntityMetadata(this.player.getId(),tager.getDataWatcher(),false);

            entity.setCustomName(v1);
            entity.setCustomNameVisible(v2);
//            try{
//                List<DataWatcher.Item<? extends Object>> list = (List<DataWatcher.Item<? extends Object>>) entityMetaataList.get(metaPacket);
//                for (DataWatcher.Item<? extends Object> item : list) {
//                    if (item.a().equals(entityCustName)){
//                        item.a(((Object) this.getPlayer().getCustomName()));
//                    }
//                }
//            }catch (IllegalAccessException e){
//                e.printStackTrace();
//            }

        }

        public EntityPlayer getPlayer() {
            return player;
        }

        public Object getSpawnPacket() throws IllegalAccessException {
            if (tager instanceof EntityLiving){
                PacketPlayOutSpawnEntityLiving spawnLivinEntity = new PacketPlayOutSpawnEntityLiving((EntityLiving) tager);
                spawnEntityLivingPacketId.set(spawnLivinEntity,player.getId());
                return spawnLivinEntity;
            } else {
                PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(tager);
                spawnEntityPacketId.set(packet,player.getId());
                return packet;
            }
        }

        public Object getMetaPacket() {
            return metaPacket;
        }

        public Object getEquipmentPacket() {
            ItemStack hat = player.getEquipment(EnumItemSlot.HEAD);
            return new PacketPlayOutEntityEquipment(player.getId(),EnumItemSlot.HEAD,hat);
        }

    }
}
