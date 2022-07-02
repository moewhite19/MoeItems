package cn.whiteg.moeitems.items;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.moeitems.Setting;
import cn.whiteg.rpgArmour.api.CustEntityChunkEvent;
import cn.whiteg.rpgArmour.api.CustEntityID;
import cn.whiteg.rpgArmour.api.CustItem_MultiModel;
import cn.whiteg.rpgArmour.listener.CanBreakEntityItem;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import cn.whiteg.rpgArmour.utils.NMSUtils;
import cn.whiteg.rpgArmour.utils.VectorUtils;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.*;

public class Broom extends CustItem_MultiModel implements Listener {
    final static Broom o;

    static {
        o = new Broom();
    }

    private final float wheelSpeed = 8F; //转向速度
    private final float moveSpeed =0.7F; //飞行速度
    private final BoomEntity boomEntity = new BoomEntity();
    Map<UUID, BroomRun> map = new HashMap<>();

    private Broom() {
        super(Material.SHEARS,45,"§e魔法扫帚",45,4,5,6);
    }

    public static Broom get() {
        return o;
    }

    public BoomEntity getBoomEntity() {
        return boomEntity;
    }

    public boolean join(ArmorStand e,Player p) {
        if (getBoomEntity().is(e) && e.getPassengers().isEmpty()){
            new BroomRun(e,p);
            return true;
        }
        /*e.addPassenger(p);
        CraftPlayer cp = (CraftPlayer) p;
        EntityPlayer np = cp.getHandle();
        net.minecraft.server.v1_16_R3.BoomEntity ne = ((CraftEntity) e).getHandle();
        np.fauxSleeping = true;*/
//        p.sendActionBar("看向地面并按下潜行键离开");
/*        new BukkitRunnable() {
            byte effnum = 0;

            @Override
            public void run() {
                org.bukkit.entity.BoomEntity ve = p.getVehicle();
                if (ve == null || !e.getUniqueId().equals(ve.getUniqueId()) || p.isDead() || ve.isDead()){
                    cancel();
                    return;
                }
                float ad = EntityUtils.getAD(p);
                float ws = EntityUtils.getWS(p);
                boolean jump = EntityUtils.getJumping(p);
                boolean down = p.isSneaking();
//                    p.sendActionBar("左右 " + ad + "  前后 " + ws + "  " + (jump ? "正在上升" : (down ? "正在下降" : "")));

                float ycz = VectorUtils.getDifferenceAngle(np.yaw,ne.yaw);
                if (ycz > 0.1F || ycz < -0.1F){
                    float ys = speedLimiter(ycz,wheelSpeed);
                    ne.yaw += ys;
//                    p.sendActionBar("视角差: " + ys + "玩家视角" + np.yaw + " 实体视角" + ne.yaw);
                }
                Vector vec = ve.getVelocity();
                Location loc = ve.getLocation();
                Vector locv = VectorUtils.viewVector(loc);
                if (ws != 0F){
                    if (Math.abs(vec.getX()) + Math.abs(vec.getZ()) < moveSpeed){
                        vec.add(locv.multiply(0.065F * ws));
                    }
                }
                if (ad != 0F){
                    loc.setYaw(Location.normalizeYaw(loc.getYaw() - 90));
                    locv = VectorUtils.viewVector(loc);
                    if (Math.abs(vec.getX()) + Math.abs(vec.getZ()) < moveSpeed){
                        vec.add(locv.multiply(0.065F * ad));
                    }
                }
                if (jump){
                    vec.setY(0.22F);
                } else if (!down){
                    vec.setY(0F);
                    effnum++;
                    if (effnum > 4){
                        loc.setY(loc.getY() + 1.65D);
                        loc.getWorld().spawnParticle(Particle.TOTEM,loc,3,0.2,0.1,0.2,0.25);
                        effnum = 0;
*//*                        int exp = p.getTotalExperience();
                        p.sendActionBar("剩余经验值" + exp);
                        if (exp <= 1){
                            cancel();
                            return;
                        }
                        p.setTotalExperience(exp - 1);*//*
                    }
                }
                ve.setVelocity(vec);

            }
        }.runTaskTimer(RPGArmour.plugin,1,1);*/
        return false;
    }

    public float speedLimiter(float v,float max) {
        float max2 = -max;
        if (v > max) return max;
        if (v < max2) return max2;
        return v;
    }

    public void drop(ArmorStand e,Location loc) {
        ItemStack helmet = e.getHelmet();
        e.remove();
        if (is(helmet)){
            loc.getWorld().dropItem(loc,helmet);
        }
    }

    public void unreg() {
        if (!map.isEmpty()){
            Iterator<Map.Entry<UUID, BroomRun>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, BroomRun> s = it.next();
                s.getValue().stop();
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity entity && event.getCause() == EntityDamageEvent.DamageCause.FALL){
            if (this.boomEntity.is(entity.getVehicle())){
                event.setCancelled(true);
            }
        }
    }

    //进入扫帚
    @EventHandler(ignoreCancelled = true)
    public void onRClickEntity(PlayerInteractAtEntityEvent event) {
        org.bukkit.entity.Entity e = event.getRightClicked();
        Player p = event.getPlayer();
        if (e instanceof ArmorStand && boomEntity.is(e) && e.getPassengers().isEmpty()){
            event.setCancelled(true);
            Location loc = e.getLocation();
            Residence res = Residence.getInstance();
            if (!res.isResAdminOn(p)){
                FlagPermissions flag = Residence.getInstance().getPermsByLocForPlayer(loc,p);
                if (!flag.playerHasHints(p,Flags.riding,true)){
                    return;
                }
            }
            join((ArmorStand) e,p);
        }
    }

    @EventHandler(ignoreCancelled = true) //1.16后失效, 1.18后又恢复
    public void onPlayerLeaveV(EntityDismountEvent event) {
        org.bukkit.entity.Entity dismounted = event.getDismounted();
        if (dismounted.isDead()) return;
        org.bukkit.entity.Entity player = event.getEntity();
        if (player instanceof Player && ((Player) player).isSneaking() && dismounted instanceof ArmorStand armorStand && getBoomEntity().is(dismounted)){
            if (!player.isDead() && !armorStand.isOnGround()){
                event.setCancelled(true);
            }
        }
    }

//    @EventHandler(priority = EventPriority.LOW)
//    public void onLClickEntity(EntityDamageByEntityEvent event) {
//        //需要领地权限检查
//        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
//        org.bukkit.entity.Entity e = event.getEntity();
//        if (e.isDead()) return;
//        org.bukkit.entity.Entity damager = event.getDamager();
//        if (damager instanceof Player && e instanceof ArmorStand && entity.is(e)){
//            Set<String> s = e.getScoreboardTags();
//            s.add("candestroy");
//        }
//    }

//    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
//    public void onTp(PlayerTeleportEvent event) {
//        Player p = event.getPlayer();
//        org.bukkit.entity.Entity v = p.getVehicle();
//        if (getEntity().is(v)){
//            if (r != null){
//                r.stop();
//            }
//        }
//}

    @EventHandler(ignoreCancelled = true)
    public void onDroupItem(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        if (!p.isSneaking()) return;
        if (p.getVehicle() != null) return;
        ItemStack item = event.getItemDrop().getItemStack();
        if (!is(item)) return;
        if (item.getAmount() != 1) return;
        event.getItemDrop().remove();
        Location loc = p.getLocation();
        ArmorStand armorStand = (ArmorStand) boomEntity.summon(loc,item);
        EntityUtils.setSlotsDisabled(armorStand,true);

//        armorStand.setHeadPose(new EulerAngle(pitch / 45,0,0));//设置盔甲架仰角
//        EntityArmorStand nmsEntity = ((CraftArmorStand) armorStand).getHandle();
//        nmsEntity.setYRot(loc.getYaw());
        EntityUtils.setEntityRotYaw(armorStand,loc.getYaw());
        join(armorStand,p);
    }

    //放置
    @EventHandler(ignoreCancelled = true)
    public void onRClickBlock(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getBlockFace() != BlockFace.UP) return;
        Player p = event.getPlayer();
        EquipmentSlot hand = event.getHand();
        ItemStack item;
        PlayerInventory pi = p.getInventory();
        if (hand == EquipmentSlot.HAND){
            item = pi.getItemInMainHand();
        } else if (hand == EquipmentSlot.OFF_HAND){
            item = pi.getItemInOffHand();
        } else return;
        if (!is(item)) return;

        Block block = event.getClickedBlock();
        if (block == null) return;
        Location loc = block.getLocation();
        loc.setX(loc.getBlockX() + 0.5D);
        loc.setY(loc.getBlockY() + 0.5D);
        loc.setZ(loc.getBlockZ() + 0.5D);

        loc.setY(loc.getY() + 1);
        if (loc.getBlock().getType() != Material.AIR) return;
        Residence res = Residence.getInstance();
        if (!res.isResAdminOn(p)){
            FlagPermissions flag = Residence.getInstance().getPermsByLocForPlayer(loc,p);
            if (!flag.playerHasHints(p,Flags.place,true)){
                return;
            }
        }
        event.setCancelled(true);
        if (item.getAmount() > 1){
            item.setAmount(item.getAmount() - 1);
        } else if (hand == EquipmentSlot.HAND){
            pi.setItemInMainHand(null);
        } else {
            pi.setItemInOffHand(null);
        }
        item = item.clone();
        item.setAmount(1);
        loc.setY(loc.getY() + 1);
        ArmorStand armorStand = (ArmorStand) boomEntity.summon(loc,item);
        EntityUtils.setSlotsDisabled(armorStand,true);

        float yaw = EntityUtils.getEntityRotYaw(p);
//        loc.setYaw(yaw);
//        EntityArmorStand nmsEntity = ((CraftArmorStand) armorStand).getHandle();
//        nmsEntity.setYRot(yaw);
        EntityUtils.setEntityRotYaw(armorStand,yaw);
        event.setCancelled(true);

    }

    public class BoomEntity extends CustEntityID implements CustEntityChunkEvent {
//        Map<UUID, BroomStaus> map = new HashMap<>();

        public BoomEntity() {
            super("broom",ArmorStand.class);
        }

        @Override
        public void load(final org.bukkit.entity.Entity entity) {
            if (entity.isDead()) return;
            Location loc = entity.getLocation();
            EntityUtils.setBoundingBox(entity,BoundingBox.of(loc,0.4,0.55D,0.4));
            if (Setting.DEBUG) MoeItems.logger.info("加载扫把" + loc);
        }

        @Override
        public void unload(org.bukkit.entity.Entity entity) {
//            entity.remove();
        }

        @Override
        public boolean init(org.bukkit.entity.Entity entity) {
            return init(entity,createItem());
        }

        public boolean init(org.bukkit.entity.Entity entity,ItemStack item) {
            if (super.init(entity)){
                Set<String> s = entity.getScoreboardTags();
                s.add("dontedit");
                s.add(CanBreakEntityItem.TAG);
                load(entity);
                if (entity instanceof ArmorStand armorStand){
                    armorStand.setVisible(false);
                    //paper方法
                    EntityUtils.setSlotsDisabled(armorStand,true);
                    armorStand.setHelmet(item);
//                    armorStand.setMarker(true);
                }
                return true;
            }
            return false;
        }

        public Entity summon(Location location,ItemStack item) {
            final ArmorStand armorStand = location.getWorld().spawn(location,ArmorStand.class);
            init(armorStand,item);
            return armorStand;
        }

        @Override
        public boolean is(org.bukkit.entity.Entity entity) {
            return entity instanceof ArmorStand && super.is(entity);
        }
        //        public Map<UUID, BroomStaus> getMap() {
//            return map;
//        }
//        public class BroomStaus {
//            final LivingEntity e;
//            final BukkitTask task;
//            final List<LivingEntity> passengers = new ArrayList<>(2);
//
//            BroomStaus(LivingEntity entity) {
//                e = entity;
//                task = new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        if (e.isDead()){
//                            cancel();
//                            map.remove(e.getUniqueId());
//                            return;
//                        }
//                        if (passengers.isEmpty()) return;
//
//                    }
//                }.runTaskTimer(RPGArmour.plugin,1,1);
//            }
//
//            public void remove(LivingEntity e) {
//                map.remove(e.getUniqueId());
//                task.cancel();
//                for (LivingEntity passenger : passengers) {
//                    passenger.remove();
//                }
//            }
//        }

    }

    public static float getDifferenceAngle(float v1, float v2) {
        v1 += 180.0F;
        v2 += 180.0F;
        float r = v1 - v2;
        if (r > 180.0F) {
            r -= 360.0F;
        } else if (r < -180.0F) {
            r += 360.0F;
        }
        return r;
    }

    public class BroomRun extends BukkitRunnable {
        ArmorStand broom;
        EntityArmorStand ne;
        byte effnum = 0;

        public BroomRun(ArmorStand armor,Player p) {
            ne = (EntityArmorStand) NMSUtils.getNmsEntity(armor);
            broom = armor;
            broom.addPassenger(p);
//            e.setMarker(true);
            runTaskTimer(MoeItems.plugin,1,1);
            map.put(broom.getUniqueId(),this);
        }

        void stop() {
            cancel();
            //EntityTpUtils.forgeStopRide(p);
//            e.setMarker(false);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void run() {
            if (broom.getPassenger() instanceof LivingEntity livingEntity && !broom.isDead() && !livingEntity.isDead() && livingEntity.getVehicle() != null){
                float ad = EntityUtils.getInputX(livingEntity);
                float ws = EntityUtils.getInputZ(livingEntity);
                boolean jump;
                jump = EntityUtils.getJumping(livingEntity);
                boolean down;
                if(livingEntity instanceof Player player){
                    down = player.isSneaking();
                }else {
                    final float inputY = EntityUtils.getInputY(livingEntity);
                    down = inputY < -0.1f;
                }

//                        p.sendActionBar("左右 " + ad + "  前后 " + ws + "  " + (jump ? "正在上升" : (down ? "正在下降" : "")));
                /*//同步扫帚和玩家的方向
//                final float entityRotYaw = EntityUtils.getEntityRotYaw(entity);
//                float ycz = getDifferenceAngle(EntityUtils.getEntityRotYaw(livingEntity),entityRotYaw);
//                if (ycz > 0.1F || ycz < -0.1F){
//                    float ys = speedLimiter(ycz,wheelSpeed);
//                    EntityUtils.setEntityRotYaw(entity,entityRotYaw + ys);
//                    //                    p.sendActionBar("视角差: " + ys + "玩家视角" + np.yaw + " 实体视角" + ne.yaw);
//                }*/
                //直接同步玩家方向
                EntityUtils.setEntityRotYaw(broom,EntityUtils.getEntityRotYaw(livingEntity));
                Vector vec = broom.getVelocity();
                Location loc = broom.getLocation();
                Vector locv = VectorUtils.viewVector(loc);
                if (ws != 0F){
                    if (Math.abs(vec.getX()) + Math.abs(vec.getZ()) < moveSpeed){
                        vec.add(locv.multiply(0.035F * ws));
                    }
                }
                if (ad != 0F){
                    loc.setYaw(Location.normalizeYaw(loc.getYaw() - 90));
                    locv = VectorUtils.viewVector(loc);
                    if (Math.abs(vec.getX()) + Math.abs(vec.getZ()) < moveSpeed){
                        vec.add(locv.multiply(0.025F * ad));
                    }
                }
                if (jump){
                    vec.setY(0.22F);
                } else if (down){
                    broom.setVelocity(vec);
                    livingEntity.setFallDistance(0F); //防止玩家摔死
                    return;
                } else {
                    vec.setY(0F);
                }
                effnum++;
                if (effnum > 4){
                    loc.setY(loc.getY() + 1.8D);
                    loc.getWorld().spawnParticle(Particle.TOTEM,loc,3,0.2,0.1,0.2,0.25);
                    effnum = 0;
                }
                broom.setVelocity(vec);
            } else {
                stop();
            }
        }
    }
}


