package cn.whiteg.moeitems.items;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustEntityID;
import cn.whiteg.rpgArmour.api.CustItem;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import cn.whiteg.rpgArmour.utils.VectorUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

public class FireBerry extends CustItem_CustModle implements Listener {
    private static final FireBerry FIRE_BERRY;

    static {
        FIRE_BERRY = new FireBerry();
    }

    final BootFire bootFire;

    private FireBerry() {
        super(Material.PUMPKIN_PIE,6,"§c火焰浆果");
        bootFire = new BootFire();
    }

    public static FireBerry get() {
        return FIRE_BERRY;
    }

    public BootFire getBootFire() {
        return bootFire;
    }


    @EventHandler(ignoreCancelled = true)
    public void onEat(PlayerItemConsumeEvent event) {
        if (!is(event.getItem())) return;
        Player p = event.getPlayer();
        Location loc = p.getLocation();
        ArmorStand v = (ArmorStand) bootFire.summon(loc);
        v.addPassenger(p);
        new BukkitRunnable() {
            int tick = 200;
            int rffFlag = 0;

            @Override
            public void run() {
                Entity e = p.getVehicle();
                if (e == null || e.getUniqueId() != v.getUniqueId()){
                    if (!v.isDead()) v.remove();
                    cancel();
                    return;
                }
                tick--;
                if (tick <= 0){
                    v.remove();
                    cancel();
                    return;
                }
                if (rffFlag >= 3){
                    Location l = v.getLocation();
                    Particle[] particles = new Particle[]{Particle.CAMPFIRE_COSY_SMOKE,Particle.CAMPFIRE_SIGNAL_SMOKE};
                    int type = tick % particles.length;
                    l.getWorld().spawnParticle(particles[type],l,3,0.1,0.2,0.1,0.22);
                } else {
                    rffFlag++;
                }
                Location ploc = p.getLocation();
                Vector vec = v.getVelocity();
                float ws = EntityUtils.getWS(p);
                float ad = EntityUtils.getAD(p);
//                boolean jump = EntityUtils.getJumping(p);
//                boolean down = p.isSneaking();
//                    p.sendActionBar("左右 " + ad + "  前后 " + ws + "  " + (jump ? "正在上升" : (down ? "正在下降" : "")));
                if (ws != 0F){
                    Vector locv = VectorUtils.viewVector(ploc);
                    if (Math.abs(vec.getX()) + Math.abs(vec.getZ()) < 0.5){
                        vec.add(locv.multiply(0.035F * ws));
                    }
                }
                if (ad != 0F){
                    ploc.setYaw(Location.normalizeYaw(ploc.getYaw() - 90));
                    Vector locv = VectorUtils.viewVector(ploc);
                    if (Math.abs(vec.getX()) + Math.abs(vec.getZ()) < 0.7){
                        vec.add(locv.multiply(0.05F * ad));
                    }
                }
                if (vec.getY() < 2.9)
                    vec.setY(vec.getY() + 0.15);
                v.setVelocity(vec);
                v.setHealth(((double) tick) * 0.1D);
            }
        }.runTaskTimer(RPGArmour.plugin,1,1);
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onLeave(EntityDismountEvent event) {
        Entity v = event.getDismounted();
        Entity p = event.getEntity();
        if (p.isDead() || v.isDead()) return;
        if (bootFire.is(v)){
            event.setCancelled(true);
        }
    }

    public class BootFire extends CustEntityID implements Listener {
        public BootFire() {
            super("boot_fire",ArmorStand.class);
            RPGArmour.plugin.getEntityManager().regEntity(this);
        }


        @Override
        public boolean is(Entity entity) {
            if (entity instanceof ArmorStand){
                return super.is(entity);
            }
            return false;
        }

        @Override
        public boolean init(Entity entity) {
            if (entity instanceof ArmorStand && super.init(entity)){
                ArmorStand e = (ArmorStand) entity;
                e.setFireTicks(Integer.MAX_VALUE);
                e.addScoreboardTag("dontsave");
                e.setVisible(false);
                EntityUtils.setBoundingBox(e,BoundingBox.of(e.getLocation(),0.43,1.42,0.43));
                return true;
            }
            return false;
        }
    }

}
