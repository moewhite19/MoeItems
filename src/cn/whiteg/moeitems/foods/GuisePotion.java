package cn.whiteg.moeitems.foods;

import cn.whiteg.chanlang.LangUtils;
import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.moeitems.Setting;
import cn.whiteg.moeitems.reflection.FieldAccessor;
import cn.whiteg.moepacketapi.MoePacketAPI;
import cn.whiteg.moepacketapi.api.event.PacketSendEvent;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.NMSUtils;
import com.mojang.datafixers.util.Pair;
import io.netty.channel.Channel;
import net.minecraft.core.IRegistry;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.lang.reflect.Field;
import java.util.*;

//旧版备份
public class GuisePotion extends CustItem_CustModle implements Listener {
    private static final GuisePotion a;
    private static DataWatcherObject<Optional<IChatBaseComponent>> entityCustName;
    private static FieldAccessor<Integer> spawnEntityLivingPacketId;
    private static FieldAccessor<Integer> spawnEntityLivingPacketType;
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
            spawnEntityLivingPacketId = new FieldAccessor<>(PacketPlayOutSpawnEntityLiving.class.getDeclaredField("a"));
            spawnEntityLivingPacketType = new FieldAccessor<>(PacketPlayOutSpawnEntityLiving.class.getDeclaredField("c"));
            spawnEntityPacketId = PacketPlayOutSpawnEntity.class.getDeclaredField("c");
            spawnEntityPacketId.setAccessible(true);
            spawnEntityPacketType = PacketPlayOutSpawnEntity.class.getDeclaredField("m");
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

            Field f;
            f = NMSUtils.getFieldFormType(Entity.class,DataWatcherObject.class.getName() + "<" + Optional.class.getName() + "<" + IChatBaseComponent.class.getName() + ">>");
            f.setAccessible(true);
            entityCustName = (DataWatcherObject<Optional<IChatBaseComponent>>) f.get(null);

            //获取entityCustomNameVisible
            var fields = Entity.class.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                var ff = fields[i];
                if (ff.getAnnotatedType().getType().getTypeName().contains("IChatBaseComponent")){
                    f = fields[i + 1];
                    break;
                }
            }
            f.setAccessible(true);
            entityCustomNameVisible = (DataWatcherObject<Boolean>) f.get(null);

            f = NMSUtils.getFieldFormType(EntityTameableAnimal.class,DataWatcherObject.class.getName() + "<" + Byte.class.getName() + ">");
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

    //获取装备包
    public static Packet<PacketListenerPlayOut> getEquipmentPacket(EntityPlayer player) {
        List<Pair<EnumItemSlot, ItemStack>> list = new ArrayList<>(8);
        for (EnumItemSlot value : EnumItemSlot.values()) {
            ItemStack item = player.getEquipment(value);
            if (item == null || item.isEmpty()) continue;
            list.add(new Pair<>(value,item));
        }
        return new PacketPlayOutEntityEquipment(player.getId(),list);
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
                    c.write(getEquipmentPacket(sta.player));
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
            Object equipmentPacket = getEquipmentPacket(np);
            player.setCustomNameVisible(true);
            player.setCustomName(player.getName());

            var tracker = np.getWorldServer().getChunkProvider().a.G.get(np.getId());
            tracker.broadcast(destroyEntity);
            tracker.broadcast((Packet<?>) spawnPacket);
            tracker.broadcast((Packet<?>) metaPacket);
            tracker.broadcast((Packet<?>) equipmentPacket);

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
                try{
                    var type = NMSUtils.getEntityType((Class<? extends Entity>) tagerClass);
                    spawnEntityLivingPacketType.set(spawnLivinEntity,IRegistry.Y.getId(type));
                }catch (IllegalArgumentException e){
                    e.printStackTrace();
                }
                return spawnLivinEntity;
            } else {
                return new PacketPlayOutSpawnEntity(player.getId(),player.getUniqueID(),player.locX(),player.locY(),player.locZ(),0F,0F,entityType,0,new Vec3D(0,0,0));
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

        //坐下
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
                        plugin.getLogger().info("dataWatcherType: " + item.a());
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
                if (e instanceof Player ep){
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
            map.remove(player.getId());
//            task.cancel();
        }

        public void reset() {
            Object spawnPacket = new PacketPlayOutNamedEntitySpawn(player);
            PacketPlayOutEntityDestroy destroyEntity = new PacketPlayOutEntityDestroy(player.getId());
            Object metaPacket = getMetaPacket();
            Object equipmentPacket = getEquipmentPacket(player);

            var tracker = player.getWorldServer().getChunkProvider().a.G.get(player.getId());
            tracker.broadcast(destroyEntity);
            tracker.broadcast((Packet<?>) spawnPacket);
            tracker.broadcast((Packet<?>) metaPacket);
            tracker.broadcast((Packet<?>) equipmentPacket);
            stop();
        }
    }

}
