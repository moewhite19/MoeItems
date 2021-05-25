package cn.whiteg.moeitems.items;

import cn.whiteg.chanlang.LangUtils;
import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.Setting;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.ItemToolUtil;
import cn.whiteg.rpgArmour.utils.RandomUtil;
import cn.whiteg.rpgArmour.utils.VectorUtils;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.*;

public class QuickFiringCrossbow extends CustItem_CustModle implements Listener {
    public static final String arrowTag = "seeker";
    public static QuickFiringCrossbow a = new QuickFiringCrossbow();
    public static Map<String, LivingEntity> map = new HashMap<>();
    static DecimalFormat decimalFormat = new DecimalFormat("#.#"); //数字格式化
    static boolean saveTarget = true;
    private static int duration = 60;
    private static float turningPower = 0.2F; //转向能力
    /*    @EventHandler
        public void onUse(PlayerInteractEvent event) {
            if (event.getAction() == Action.LEFT_CLICK_AIR){
                final ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
                if (is(item)){
                    final ItemMeta im = item.getItemMeta();
                    if (im instanceof CrossbowMeta){
                        List<ItemStack> items = new ArrayList<>(3);
                        items.add(new ItemStack(Material.ARROW));
                        items.add(new ItemStack(Material.FIREWORK_ROCKET));
                        items.add(new ItemStack(Material.ARROW));
                        ((CrossbowMeta) im).setChargedProjectiles(items);
                        item.setItemMeta(im);
                        event.getPlayer().sendActionBar("填装完成");
                    }

                }
            }
        }*/
    private static int delay = 4;
    private float itemDropChance = 0.05f;
    private float spawnChance = 0.075f;

    public QuickFiringCrossbow() {
        super(Material.CROSSBOW,3,"§9连弩");
        ConfigurationSection c = Setting.getCustItemConfit(getClass().getSimpleName());
        if (c != null){
            spawnChance = (float) c.getDouble("spawnChance",spawnChance);
            itemDropChance = (float) c.getDouble("itemDropChance",itemDropChance);
        }
    }

    public static QuickFiringCrossbow get() {
        return a;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onShor(final EntityShootBowEvent event) {
        if (!is(event.getBow())) return;
        final ItemStack bow = event.getBow();
        @SuppressWarnings("ConstantConditions") final ItemMeta im = bow.getItemMeta();
        if (im instanceof CrossbowMeta){
            CrossbowMeta cb = (CrossbowMeta) im;
            final List<ItemStack> items = cb.getChargedProjectiles();
            Bukkit.getScheduler().runTask(RPGArmour.plugin,() -> {
                if (!is(bow)) return;
                CrossbowMeta ncb = (CrossbowMeta) bow.getItemMeta();
                ncb.setChargedProjectiles(items);
                bow.setItemMeta(ncb);
            });
        }
        final Projectile projectile = (Projectile) event.getProjectile();
        if (!(projectile.getShooter() instanceof LivingEntity)) return;
        final LivingEntity shooter = (LivingEntity) projectile.getShooter();
        LivingEntity target = map.get(shooter.getName());
        if (target == null || target.isDead()) return;
        new SeekerArrow(projectile,target,1F).start();
    }

    @EventHandler(ignoreCancelled = true)
    public void onLock(PlayerSwapHandItemsEvent event) {
        @Nullable ItemStack item = event.getOffHandItem();
        if (!is(item)) return;
        var player = event.getPlayer();
        LivingEntity target = null;
        var pLoc = player.getLocation();
        double viewDistance = 16 * 6;
        double f = 0;
        for (Entity e : player.getNearbyEntities(viewDistance,viewDistance,viewDistance)) {
            LivingEntity le;
            if (e instanceof Mob || e instanceof Boss){
                le = (LivingEntity) e;
            } else if (e instanceof Player){
                Player tPlayer = (Player) e;
                //不追踪隐身以及生存模式以外的玩家
                if (player.canSee(tPlayer) && (tPlayer.getGameMode() == GameMode.SURVIVAL || tPlayer.getGameMode() == GameMode.ADVENTURE)){
                    le = tPlayer;
                } else continue;
            } else {
                continue;
            }
            if (le.hasPotionEffect(PotionEffectType.INVISIBILITY)) continue; //不追踪隐身目标
            var eLoc = e.getLocation();
            eLoc.setY(eLoc.getY() + (e.getHeight() / 2));
            var v = VectorUtils.checkViewCone(pLoc,eLoc,25F) - (pLoc.distance(eLoc) / viewDistance);
            if (v > f){
                target = le;
                f = v;
            }
        }
        if (target != null) map.put(player.getName(),target);
        else map.remove(player.getName());
        event.setCancelled(true);
        player.sendMessage("已选择目标" + target);
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow){
            Arrow arrow = (Arrow) event.getEntity();
            if (!arrow.getScoreboardTags().isEmpty()) arrow.getScoreboardTags().remove(arrowTag); //当箭矢命中目标后删除自身跟踪箭tag
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        map.remove(event.getPlayer());
    }

    @SuppressWarnings("ConstantConditions")
//    @EventHandler(ignoreCancelled = true)
    public void onSwap(PlayerSwapHandItemsEvent event) {
        ItemStack main = event.getOffHandItem();
        if (is(main)){
            var off = event.getMainHandItem();
            if (off == null) return;
            ItemMeta im = main.getItemMeta();
            if (im instanceof CrossbowMeta){
                CrossbowMeta crossbowMeta = (CrossbowMeta) im;
                List<ItemStack> list = new ArrayList<>(crossbowMeta.getChargedProjectiles());
                list.add(off);
                crossbowMeta.setChargedProjectiles(list);
                main.setItemMeta(crossbowMeta);
                event.setCancelled(true);
                event.getPlayer().sendMessage("已填装" + LangUtils.getItemDisplayName(off));
            }

        }
    }

    public void loadArrow() {

    }

    //    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntitySpwawn(final EntitySpawnEvent event) {
        if (spawnChance <= 0) return;
        if (!(event.getEntity() instanceof Pillager)) return;
        final Pillager entity = (Pillager) event.getEntity();
        final Random random = RandomUtil.getRandom();
        if (random.nextDouble() < spawnChance){
            final EntityEquipment ej = entity.getEquipment();
            if (ej != null){
                final ItemStack item = ItemToolUtil.lootDamageItem(createItem(),0.15F);
                ItemToolUtil.copyEnchat(ej.getItemInMainHand(),item);
                ej.setItemInMainHand(item);
                ej.setItemInMainHandDropChance(itemDropChance);
            }
        }
    }


    //跟踪箭矢
    public static class SeekerArrow extends BukkitRunnable {
        final Projectile projectile;
        final LivingEntity target;
        private final float force;
        Vector lastLocation;
        int i = duration;
        Vector prejudge = new Vector(); //向量预判
        int distance;

        SeekerArrow(Projectile projectile,LivingEntity target,float force) {
            this.projectile = projectile;
            this.target = target;
            this.force = force;
            lastLocation = target.getLocation().toVector();
            distance = ((int) projectile.getLocation().distance(target.getLocation()));
            i += distance >> 2;
        }

        @Override
        public void run() {
            if (i <= 0 || (projectile instanceof AbstractArrow && ((AbstractArrow) projectile).isInBlock()) || projectile.isDead() || target.isDead() || !projectile.getScoreboardTags().contains(arrowTag) || target.getWorld() != projectile.getWorld()){
                cancel();
                return;
            }
            i--;
            var aLoc = projectile.getLocation(); //弓箭位置
            Location tLoc = target.getLocation(); //目标位置
            tLoc.setY(tLoc.getY() + (target.getHeight() * 0.7)); //将位置提升半个多升高
            var av = projectile.getVelocity(); //弓箭原始向量
            var avc = VectorUtils.checkViewCone(aLoc,av,tLoc,15); //弓箭向量与目标位置的差值
            aLoc.getWorld().spawnParticle(Particle.END_ROD,aLoc,0,0,0,0,1);
            prejudge = tLoc.clone().subtract(lastLocation).toVector(); //预判
            prejudge.multiply(Math.min(200D,tLoc.distance(aLoc) * (av.length()) / 2));
            lastLocation = tLoc.toVector();
            tLoc.add(prejudge);
            var vec = VectorUtils.viewVector(aLoc,tLoc);//转向向量
            av.setY(av.getY() + 0.045); //弓箭的重力补偿(烂办法x
            float m = turningPower * //基础转向能力
                    (0.5f + (((float) Math.min(i,duration) / duration) / 2)) * //根据发射出去后的时间削弱
                    (avc > 0F ? 1 + (avc * 4) : 1) * //当箭头指向目标时加速
                    force; //拉弓程度
            vec.multiply(m); //放大转向
            av.multiply(1 - (m / 1.5)).add(vec); //消减原始速度并入转向向量
            projectile.setVelocity(av);
            if (Setting.DEBUG){
                CommandSender sender;
                if (projectile.getShooter() instanceof Player){
                    sender = (Player) projectile.getShooter();
                } else if (target instanceof Player){
                    sender = target;
                } else {
                    return;
                }
                sender.sendMessage("向量差" + decimalFormat.format(avc) + " 加速倍率" + m);
                sender.sendMessage("预判" + decimalFormat.format(prejudge.getX()) + ", " + decimalFormat.format(prejudge.getY()) + ", " + decimalFormat.format(prejudge.getZ()));
                sender.sendMessage("-----------");
            }
        }

        public void start() {
            runTaskTimer(MoeItems.plugin,delay + (distance >> 4),1L);
            projectile.addScoreboardTag(arrowTag); //为箭矢添加tag ，当命中后（包括命中盾牌，被插件阻止等）删除tag以此取消跟踪箭状态
        }
    }

}
