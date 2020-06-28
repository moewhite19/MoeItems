package cn.whiteg.moeitems.foods;

import cn.whiteg.chanlang.LangUtils;
import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.moeitems.Setting;
import cn.whiteg.moepacketapi.MoePacketAPI;
import cn.whiteg.moepacketapi.api.event.PacketSendEvent;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import com.mojang.datafixers.util.Pair;
import io.netty.channel.Channel;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.lang.reflect.Field;
import java.util.*;

public class GuisePotion extends CustItem_CustModle implements Listener {
    private static final GuisePotion a;
    private static DataWatcherObject<Optional<IChatBaseComponent>> entityCustName;
    private static Field spawnEntityLivingPacketId;
    private static Field spawnEntityLivingPacketType;
    //玩家生成包对象Id
    private static Field spawnHumanId;
    private static Field spawnEntityPacketId;
    private static Field spawnEntityPacketType;
    private static Field entityMetadataList;
    private static Field entityMetadataId;
    //实体传送
    private static Field entityTeleportId;
    private static Field datawatcherItemVault;
    //实体是否显示名字
    private static DataWatcherObject<Boolean> entityCustomNameVisible;
    //坐着，狗和猫等动物标签
    private static DataWatcherObject<Byte> entitySitting;

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
            entityMetadataList = PacketPlayOutEntityMetadata.class.getDeclaredField("b");
            entityMetadataList.setAccessible(true);
            entityMetadataId = PacketPlayOutEntityMetadata.class.getDeclaredField("a");
            entityMetadataId.setAccessible(true);
            datawatcherItemVault = DataWatcher.Item.class.getDeclaredField("b");
            datawatcherItemVault.setAccessible(true);
            entityTeleportId = PacketPlayOutEntityTeleport.class.getDeclaredField("a");
            entityTeleportId.setAccessible(true);

            Field f = net.minecraft.server.v1_16_R1.Entity.class.getDeclaredField("az");
            f.setAccessible(true);
            entityCustName = (DataWatcherObject<Optional<IChatBaseComponent>>) f.get(null);

            f = Entity.class.getDeclaredField("aA");
            f.setAccessible(true);
            entityCustomNameVisible = (DataWatcherObject<Boolean>) f.get(null);

            f = EntityTameableAnimal.class.getDeclaredField("bw");
            f.setAccessible(true);
            entitySitting = (DataWatcherObject<Byte>) f.get(null);

        }catch (NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
        }
    }

    Map<Integer, GuisePlayer> map = Collections.synchronizedMap(new HashMap<>());


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
        for (org.bukkit.entity.Entity e : p.getNearbyEntities(128D,128D,128D)) {
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

    @EventHandler
    public void onPlayerToggle(PlayerToggleSneakEvent event) {
        if (map.isEmpty()) return;
        GuisePlayer state = map.get(event.getPlayer().getEntityId());
        if (state == null) return;
        state.setSitting(event.isSneaking());
        state.syncMetaPacket();
    }

    @EventHandler(ignoreCancelled = true)
    public void onSendPack(PacketSendEvent event) {
        if (map.isEmpty()) return;
        if (event.getPacket() instanceof PacketPlayOutNamedEntitySpawn){
            try{
                //当服务器给玩家发送玩家生成包时如果玩家有伪装实体则拦截并发送伪装的实体
                int id = (int) spawnHumanId.get(event.getPacket());
                GuisePlayer sta = map.get(id);
                if (sta != null){
                    if (event.getPlayer() != null && event.getPlayer().getEntityId() == id) return;
                    event.setCancelled(true);
                    Channel c = event.getChannel();
                    c.write(sta.getSpawnPacket());
                    c.write(sta.getMetaPacket());
                    c.write(sta.getEquipmentPacket());
                    c.flush();
                }
            }catch (IllegalAccessException e){
                e.printStackTrace();
            }

        }
    }

    public boolean setGuise(Player player,org.bukkit.entity.Entity tager) {
        EntityPlayer np = ((CraftPlayer) player).getHandle();
        try{

            GuisePlayer staus = map.get(np.getId());
            if (staus != null){
                staus.stop();
            }
            staus = new GuisePlayer(player,tager);
            Object spawnPacket = staus.getSpawnPacket();
            PacketPlayOutEntityDestroy destroyEntity = new PacketPlayOutEntityDestroy(np.getId());
            Object metaPacket = staus.getMetaPacket();
            Object equipmentPacket = staus.getEquipmentPacket();
            for (org.bukkit.entity.Entity entity : player.getWorld().getEntities()) {
                if (entity instanceof Player){
                    Player p = (Player) entity;
                    if (p.equals(player)) continue;
                    if (!p.canSee(player)) continue;
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

    public class GuisePlayer {
        private final EntityPlayer player;
        private final PacketPlayOutEntityMetadata metaPacket;
        private final Class<? extends Entity> tagerClass;
        private final EntityTypes<?> entityType;

        public GuisePlayer(Player player,org.bukkit.entity.Entity entity) {
            this.player = ((CraftPlayer) player).getHandle();
            player.setCustomName(player.getName());
            Entity tager = ((CraftEntity) entity).getHandle();
            tagerClass = tager.getClass();
            entityType = tager.getEntityType();
            metaPacket = new PacketPlayOutEntityMetadata(this.player.getId(),tager.getDataWatcher(),true);
            updateMetaDataWatcher();
/*          有bug暂时禁用
            lateLoc = tager.getBukkitEntity().getLocation();
            fakeId = CommonUtils.getNextEntityId();
            fakeMetaPacket = new PacketPlayOutEntityMetadata();
            try{
                entityMetadataId.set(fakeMetaPacket,fakeId);
                entityMetadataList.set(fakeMetaPacket,entityMetadataList.get(metaPacket));
            }catch (IllegalAccessException e){
                e.printStackTrace();
            }
            spawnFakeEntityPacket();
            task = new BukkitRunnable() {
                @Override
                public void run() {
                    Location loc = player.getLocation();
                    if (!lateLoc.equals(loc)){
                        if (lateLoc.getWorld() != loc.getWorld()){
                            spawnFakeEntityPacket();
                        }
                        lateLoc = loc;
                        syncFakeEntityPacket();
                    }
                }
            }.runTaskTimer(MoeItems.plugin,1,1);*/

        }

        public EntityPlayer getPlayer() {
            return player;
        }

        public Object getSpawnPacket() throws IllegalAccessException {
            if (EntityLiving.class.isAssignableFrom(tagerClass)){
                PacketPlayOutSpawnEntityLiving spawnLivinEntity = new PacketPlayOutSpawnEntityLiving(player);
                spawnEntityLivingPacketType.set(spawnLivinEntity,IRegistry.ENTITY_TYPE.a(entityType));
                return spawnLivinEntity;
            } else {
                PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(player.getId(),player.getUniqueID(),player.locX(),player.locY(),player.locZ(),0F,0F,entityType,0,new Vec3D(0,0,0));
                spawnEntityPacketId.set(packet,player.getId());
                return packet;
            }
        }

/*
        有bug暂时禁用
        //向伪装者自己发送一个伪装的实体
        public void spawnFakeEntityPacket() {
            try{
                Object p = getSpawnPacket();
                if (p instanceof PacketPlayOutSpawnEntityLiving){
                    spawnEntityLivingPacketId.set(p,fakeId);
                } else {
                    spawnEntityPacketId.set(p,fakeId);
                }
                this.player.playerConnection.sendPacket((Packet<?>) p);
                syncFakeEntityPacket();
            }catch (IllegalAccessException e){
                e.printStackTrace();
            }

        }
        //向伪装者自己发送同步伪装的实体
        public void syncFakeEntityPacket() {
            player.playerConnection.sendPacket(fakeMetaPacket);
//            Vec3D vec3d = player.getPositionVector().d(PacketPlayOutEntity.a(player.tar,this.yLoc,this.zLoc));
//            long k = PacketPlayOutEntity.a(vec3d.x);
//            long l = PacketPlayOutEntity.a(vec3d.y);
//            long i1 = PacketPlayOutEntity.a(vec3d.z);
//            int i = MathHelper.d(player.yaw * 256.0F / 360.0F);
//            int j = MathHelper.d(player.pitch * 256.0F / 360.0F);
//            PacketPlayOutEntity.PacketPlayOutRelEntityMove movePack = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(fakeId,(short) ((int) k),(short) ((int) l),(short) ((int) i1),player.onGround);
//                PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook movePack = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(fakeId,(short) ((int) k),(short) ((int) l),(short) ((int) i1),(byte) i,(byte) j,player.onGround);
            PacketDataSerializer data = new PacketDataSerializer(Unpooled.buffer());
            data.writeInt(fakeId);
            data.writeDouble(lateLoc.getX());
            data.writeDouble(lateLoc.getY());
            data.writeDouble(lateLoc.getZ());
            data.writeByte((byte) ((int) (player.yaw * 256.0F / 360.0F)));
            data.writeByte((byte) ((int) (player.pitch * 256.0F / 360.0F)));
            data.writeBoolean(player.onGround);
            try{
                if (movePacket != null){
                    player.playerConnection.sendPacket(movePacket);
                } else {
                    movePacket = new PacketPlayOutEntityTeleport();
                }
                movePacket.a(data);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
*/

        public Object getMetaPacket() {
            return metaPacket;
        }

        public Object getEquipmentPacket() {
            List<Pair<EnumItemSlot, ItemStack>> list = new ArrayList<>(8);
            for (EnumItemSlot value : EnumItemSlot.values()) {
                ItemStack item = player.getEquipment(value);
                if (item == null || item.isEmpty()) continue;
                list.add(new Pair<>(value,item));
            }
            return new PacketPlayOutEntityEquipment(player.getId(),list);
        }

        @SuppressWarnings("unchecked")
        public void setSitting(boolean b) {
            if (EntityTameableAnimal.class.isAssignableFrom(tagerClass)){
                try{
                    List<DataWatcher.Item<? extends Object>> list = (List<DataWatcher.Item<? extends Object>>) entityMetadataList.get(metaPacket);
                    for (DataWatcher.Item<? extends Object> item : list) {
                        if (item.a().equals(entitySitting)){
                            byte var1 = (byte) datawatcherItemVault.get(item);
                            if (b){
                                datawatcherItemVault.set(item,(byte) (var1 | 1));
                            } else {
                                datawatcherItemVault.set(item,(byte) (var1 & -2));
                            }
                        }
                    }
                }catch (IllegalAccessException e){
                    e.printStackTrace();
                }
            }
        }

        public void updateMetaDataWatcher() {
            try{
                List<DataWatcher.Item<? extends Object>> list = (List<DataWatcher.Item<? extends Object>>) entityMetadataList.get(metaPacket);
                boolean isTamseable = EntityTameableAnimal.class.isAssignableFrom(tagerClass);
                for (DataWatcher.Item<? extends Object> item : list) {
                    if (Setting.DEBUG){
                        MoeItems plugin = MoeItems.plugin;
                        plugin.getLogger().info("dataWatcherType: " + item.a().toString());
                    }
                    if (item.a().equals(entityCustName)){
                        datawatcherItemVault.set(item,Optional.ofNullable(this.player.getCustomName()));
                    } else if (item.a().equals(entityCustomNameVisible)){
                        datawatcherItemVault.set(item,true);
                    } else if (isTamseable && item.a().equals(entitySitting)){
                        byte var1 = (byte) datawatcherItemVault.get(item);
                        if (player.isSneaking()){
                            datawatcherItemVault.set(item,(byte) (var1 | 1));
                        } else {
                            datawatcherItemVault.set(item,(byte) (var1 & -2));
                        }
                    }
                }
            }catch (IllegalAccessException e){
                e.printStackTrace();
            }
        }

        public void syncMetaPacket() {
            for (org.bukkit.entity.Entity e : player.getBukkitEntity().getWorld().getEntities()) {
                if (e instanceof Player){
                    Player ep = (Player) e;
                    if (ep == player.getBukkitEntity()) continue;
                    if (ep.canSee(player.getBukkitEntity())){
                        MoePacketAPI.getInstance().getPlayerPacketManage().sendPacket(ep,metaPacket);
                        if (Setting.DEBUG){
                            ep.sendMessage(player.getName() + "刷新了");
                        }
                    }
                }
            }
        }

        public void stop() {
//            task.cancel();
        }
    }

}
