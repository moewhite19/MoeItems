package cn.whiteg.moeitems.items;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustEntityID;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import cn.whiteg.rpgArmour.utils.VectorUtils;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import net.minecraft.server.v1_15_R1.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftSnowball;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Artillery extends CustItem_CustModle implements Listener {
    private static final Artillery a;

    static {
        a = new Artillery();
    }

    private final float speed = 1.5F;
    BulletItem bullet = new BulletItem();
    private Map<UUID, BukkitTask> movein = new HashMap<>();
    private ArtilleryEntity artilleryEntity;

    private Artillery() {
        super(Material.BOWL,32,"§e火炮");
        artilleryEntity = new ArtilleryEntity();
        NamespacedKey key = new NamespacedKey(MoeItems.plugin,"artillery");
        ShapedRecipe r = new ShapedRecipe(key,createItem());
        r.shape(
                " B ",
                "ABA",
                "CCC"
        );
        r.setIngredient('A',Material.SHIELD);
        r.setIngredient('B',Material.CAULDRON);
        r.setIngredient('C',Material.ARMOR_STAND);
        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
    }

    public static Artillery get() {
        return a;
    }

    public float fixPitch(float pitch) {
        if (pitch < -22) return -22;
        else if (pitch > 15) return 15;
        return pitch;
    }

    @EventHandler(ignoreCancelled = true)
    public void onRClickEntity(PlayerInteractAtEntityEvent event) {
        Entity e = event.getRightClicked();
        Player p = event.getPlayer();
        if (e instanceof ArmorStand && artilleryEntity.is(e)){
            ArmorStand armorStand = (ArmorStand) e;
            event.setCancelled(true);
            if (p.isSneaking()){
                BukkitTask task = movein.get(e.getUniqueId());
                if (task != null){
                    p.sendActionBar("§c这个火炮已经在移动了");
                    return;
                }
                Location loc = p.getLocation();
                FlagPermissions flag = Residence.getInstance().getPermsByLocForPlayer(loc,p);
                if (!flag.playerHasHints(p,Flags.use,true)){
                    return;
                }
                task = new BukkitRunnable() {

                    /**
                     * When an object implementing interface <code>Runnable</code> is used
                     * to create o thread, starting the thread causes the object's
                     * <code>run</code> method to be called in that separately executing
                     * thread.
                     * <p>
                     * The general contract of the method <code>run</code> is that it may
                     * take any action whatsoever.
                     *
                     * @see Thread#run()
                     */
                    @Override
                    public void run() {
                        if (!p.isOnline() || p.isDead() || e.isDead() || !p.isSneaking()){
                            stop();
                            return;
                        }
                        Location ploc = p.getLocation();
                        if (loc.distance(ploc) > 2D){
                            stop();
                            return;
                        }
                        float yaw = ploc.getYaw();
                        float pitch = fixPitch(ploc.getPitch());
                        net.minecraft.server.v1_15_R1.Entity nmsEntity = ((CraftEntity) e).getHandle();
                        float mYaw = VectorUtils.getDifferenceAngle(yaw,nmsEntity.yaw);
//                p.sendActionBar("视角差 " + mYaw);
                        float mPitch = pitch - nmsEntity.pitch;
                        float speed2 = -speed;
                        if (mYaw > speed) mYaw = speed;
                        if (mYaw < speed2) mYaw = speed2;
                        if (mPitch > speed) mPitch = speed;
                        if (mPitch < speed2) mPitch = speed2;
                        nmsEntity.pitch += mPitch;
                        nmsEntity.yaw += mYaw;
                        armorStand.setHeadPose(new EulerAngle(nmsEntity.pitch / 45,0,0));//设置盔甲架仰角
                    }

                    void stop() {
                        if (p.isOnline()){
                            p.sendActionBar("§b停止移动火炮");
                        }
                        movein.remove(e.getUniqueId());
                        cancel();
                    }
                }.runTaskTimer(RPGArmour.plugin,2L,2L);
                movein.put(e.getUniqueId(),task);

                p.sendActionBar("§b开始移动火炮");
            } else {
                if (p.hasCooldown(getMaterial())) return;
                Location loc = e.getLocation();
                FlagPermissions flag = Residence.getInstance().getPermsByLocForPlayer(loc,p);
                if (!flag.playerHasHints(p,Flags.use,true)){
                    return;
                }
                Location spawnLoc = loc.clone();
                spawnLoc.setY(spawnLoc.getY() + 1.45D);
                Vector v = VectorUtils.viewVector(loc);
                spawnLoc.add(v.clone().multiply(2));
                v.multiply(3.5F);
                spawnLoc.getWorld().playSound(spawnLoc,"entity.firework_rocket.blast",1,0.15F);
                spawnLoc.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL,spawnLoc,4);
                ItemStack b = bullet.createItem();
                Snowball snowball = loc.getWorld().spawn(spawnLoc,Snowball.class);
                ((CraftSnowball) snowball).getHandle().setItem(CraftItemStack.asNMSCopy(b));
                snowball.setVelocity(v);
                snowball.setCustomName(getDisplayName());
                p.setCooldown(getMaterial(),30);
                snowball.setShooter(p);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(ProjectileHitEvent event) {
        Projectile p = event.getEntity();
        if (p instanceof Snowball){
            CraftSnowball snowball = (CraftSnowball) p;
            if (bullet.is(EntityUtils.getSnowballItem(snowball))){
                Location loc = snowball.getLocation();
                loc.getWorld().createExplosion(snowball,2.6F,true,true);
            }
        }
    }

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
        Location loc = block.getLocation().toCenterLocation();
        loc.setY(loc.getY() + 1);
        if (loc.getBlock().getType() != Material.AIR) return;
        Residence res = Residence.getInstance();
        if (!res.isResAdminOn(p)){
            FlagPermissions flag = Residence.getInstance().getPermsByLocForPlayer(loc,p);
            if (!flag.playerHasHints(p,Flags.place,true)){
                return;
            }
        }
        if (item.getAmount() > 1){
            item.setAmount(item.getAmount() - 1);
        } else if (hand == EquipmentSlot.HAND){
            pi.setItemInMainHand(null);
        } else {
            pi.setItemInOffHand(null);
        }
        event.setCancelled(true);
        ArmorStand armorStand = (ArmorStand) artilleryEntity.summon(loc);
        Location ploc = p.getLocation();
        float pitch = -2F;
        float yaw = ploc.getYaw();
        armorStand.setDisabledSlots(EquipmentSlot.HEAD);
        armorStand.setHeadPose(new EulerAngle(pitch / 45,0,0));//设置盔甲架仰角
        EntityArmorStand nmsEntity = ((CraftArmorStand) armorStand).getHandle();
        nmsEntity.pitch = pitch;
        nmsEntity.yaw = yaw;

    }

    public class ArtilleryEntity extends CustEntityID {
        ArtilleryEntity() {
            super("artillery",ArmorStand.class);
//            RPGArmour.plugin.getEntityManager().regEntity(this);
        }

        @Override
        public boolean init(Entity entity) {
            if (entity instanceof ArmorStand){
                Set<String> s = entity.getScoreboardTags();
                s.add("dontedit");
                s.add("candestroy");

                ArmorStand armorStand = (ArmorStand) entity;
                armorStand.setHelmet(Artillery.get().createItem());
                armorStand.setVisible(false);
            }
            return super.init(entity);
        }
    }


    public class BulletItem extends CustItem_CustModle {
        public BulletItem() {
            super(Material.SNOWBALL,6,"炮弹");
        }
    }
}

