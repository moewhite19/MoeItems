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
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftSnowball;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
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
    private final Map<UUID, BukkitTask> movein = new HashMap<>();
    private final ArtilleryEntity artilleryEntity;

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
        if (e instanceof ArmorStand armorStand && artilleryEntity.is(e)){
            event.setCancelled(true);
            if (p.isSneaking()){
                BukkitTask task = movein.get(e.getUniqueId());
                if (task != null){
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent("§c这个火炮已经在移动了"));
                    //p.sendActionBar("§c这个火炮已经在移动了");
                    return;
                }
                Location loc = p.getLocation();
                FlagPermissions flag = Residence.getInstance().getPermsByLocForPlayer(loc,p);
                if (!flag.playerHasHints(p,Flags.use,true)){
                    return;
                }
                task = new BukkitRunnable() {
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
                        var nmsEntity = ((CraftEntity) e).getHandle();
                        float mYaw = VectorUtils.getDifferenceAngle(yaw,nmsEntity.getYRot());
                        float mPitch = pitch - nmsEntity.getXRot();
                        float speed2 = -speed;
                        if (mYaw > speed) mYaw = speed;
                        if (mYaw < speed2) mYaw = speed2;
                        if (mPitch > speed) mPitch = speed;
                        if (mPitch < speed2) mPitch = speed2;
                        nmsEntity.setXRot(nmsEntity.getXRot() + mPitch);
                        nmsEntity.setYRot(nmsEntity.getYRot() + mYaw);
                        armorStand.setHeadPose(new EulerAngle(nmsEntity.getXRot() / 45,0,0));//设置盔甲架仰角
                    }

                    void stop() {
                        if (p.isOnline()){
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent("§b停止移动火炮"));
                        }
                        movein.remove(e.getUniqueId());
                        cancel();
                    }
                }.runTaskTimer(RPGArmour.plugin,2L,2L);
                movein.put(e.getUniqueId(),task);
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent("§b开始移动火炮"));
            } else {
                if (p.hasCooldown(getMaterial())) return;
                PlayerInventory inv = p.getInventory();
                EntityEquipment ainv = armorStand.getEquipment();
                ItemStack aitem = ainv.getChestplate();
                ItemStack hand = inv.getItemInMainHand();
                if (CannonBall.get().is(hand)){
                    ainv.setChestplate(hand);
                    inv.setItemInMainHand(aitem);
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent("§6更换弹匣"));
                    return;
                } else if (CannonBall.get().is(aitem)){
                    //扣除物品
                    int ammo = aitem.getAmount() - 1;
                    if (ammo <= 0){
                        ainv.setChestplate(null);
                    } else {
                        aitem.setAmount(ammo);
                        ainv.setChestplate(aitem);
                    }


                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent("§7剩余弹药: §f" + ammo));

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
                } else {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent("§4没有弹药"));
                }

            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(ProjectileHitEvent event) {
        Projectile p = event.getEntity();
        if (p instanceof Snowball){
            CraftSnowball snowball = (CraftSnowball) p;
            if (bullet.is(EntityUtils.getSnowballItem(snowball))){

                snowball.getWorld().createExplosion(snowball,2.6F,true,true); //Paper方法
//                snowball.getWorld().createExplosion(snowball.getLocation(),2.6F,true,true);

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
        Location loc = block.getLocation().toCenterLocation(); //Paper方法
//        Location loc = block.getLocation();
//        loc.setX(loc.getBlockX() + 0.5D);
//        loc.setY(loc.getBlockY() + 0.5D);
//        loc.setZ(loc.getBlockZ() + 0.5D);

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
        armorStand.setDisabledSlots(EquipmentSlot.HEAD); //锁住盔甲架 Paper方法
        armorStand.setHeadPose(new EulerAngle(pitch / 45,0,0));//设置盔甲架仰角
        EntityArmorStand nmsEntity = ((CraftArmorStand) armorStand).getHandle();
        nmsEntity.setXRot(pitch);
        nmsEntity.setYRot(yaw);

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
            super(Material.SNOWBALL,9,"炮弹");
        }
    }
}

