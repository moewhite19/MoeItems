package cn.whiteg.moeitems.items;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.ItemToolUtil;
import cn.whiteg.rpgArmour.utils.RandomUtil;
import cn.whiteg.rpgArmour.utils.VectorUtils;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class WaterGun extends CustItem_CustModle implements Listener {
    final static WaterGun a;
    private static String tag = WaterGun.class.getSimpleName();

    static {
        a = new WaterGun();
    }

    private Entity damager = null;

    public WaterGun() {
        super(Material.SHEARS,46,"§b水元素");
    }

    public static WaterGun get() {
        return a;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.getDamage() < 0.5 || !(damager instanceof LivingEntity) || this.damager == damager)
            return;
        final ItemStack item = ((LivingEntity) damager).getEquipment().getItemInMainHand();
        if (is(item)){
            event.setCancelled(true);
            onLaunch((LivingEntity) damager,item);
        }
    }

    @EventHandler()
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        final Player player = event.getPlayer();
        final ItemStack item = player.getEquipment().getItemInMainHand();
        if (is(item)){
            onLaunch(player,item);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof LlamaSpit) || !event.getEntity().getScoreboardTags().contains(tag)) return;
        Projectile entity = event.getEntity();
        //检查领地权限
        FlagPermissions perms = Residence.getInstance().getPermsByLoc(entity.getLocation());
        if (entity.getShooter() instanceof Player){
            Player shooter = (Player) entity.getShooter();
            if (!Residence.getInstance().isResAdminOn(shooter) && !perms.playerHasHints(shooter,Flags.build,false)){
                return;
            }
        } else {
            if (!perms.has(Flags.build,false)){
                return;
            }
        }

        Entity hitEntity = event.getHitEntity();
        //熄灭实体火
        if (hitEntity != null){
            hitEntity.setFireTicks(0);
        }

        Block block = event.getHitBlock();
        if (block != null){
            //熄灭火焰
            if (block.getType() == Material.FIRE) block.setBlockData(Bukkit.createBlockData(Material.AIR));
            else {
                BlockData data = block.getBlockData();
                //熄灭火焰
                if (data instanceof Lightable){
                    ((Lightable) data).setLit(false);
                }
            }
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakBlock(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (is(p.getInventory().getItemInMainHand())){
            event.setCancelled(true);
        }
    }

    public void onLaunch(LivingEntity user,ItemStack item) {
        if (user instanceof Player){
            Player player = (Player) user;
            if (player.hasCooldown(getMaterial())){
                return;
            }
            player.setCooldown(getMaterial(),5);
        }
        damager = user;
        Location loc = user.getLocation();
        Vector v = VectorUtils.viewVector(loc);
        v.multiply(3.4F);
        loc.getWorld().playSound(loc,"minecraft:entity.llama.spit",SoundCategory.AMBIENT,1F,1F);
        LlamaSpit spit = user.launchProjectile(LlamaSpit.class,v);
        if (spit.isDead()) return;

        spit.addScoreboardTag(tag);
        damager = null;
        if (!item.getItemMeta().isUnbreakable() && RandomUtil.getRandom().nextDouble() < 0.5 && ItemToolUtil.damage(item,1)){
            user.getEquipment().setItemInMainHand(null);
        }
    }


}

