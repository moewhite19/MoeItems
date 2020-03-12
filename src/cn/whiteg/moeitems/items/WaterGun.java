package cn.whiteg.moeitems.items;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.ItemToolUtil;
import cn.whiteg.rpgArmour.utils.RandomUtil;
import cn.whiteg.rpgArmour.utils.VectorUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class WaterGun extends CustItem_CustModle implements Listener {
    final static WaterGun a;

    static {
        a = new WaterGun();
    }

    private Entity damager = null;

    public WaterGun() {
        super(Material.SHEARS,46,"§b水弹枪");
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
            onUse((LivingEntity) damager,item);
        }
    }

    @EventHandler()
    public void onani(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        final Player player = event.getPlayer();
        final ItemStack item = player.getEquipment().getItemInMainHand();
        if (is(item)){
            onUse(player,item);
        }
    }

    public void onUse(LivingEntity user,ItemStack item) {
        if (user instanceof Player){
            Player player = (Player) user;
            if (player.hasCooldown(getMaterial())){
//                player.setCooldown(getMaterial(),5);
                return;
            }
            player.setCooldown(getMaterial(),5);
        }
        damager = user;
        Location loc = user.getLocation();
//        loc.setY(loc.getY() - (user instanceof Player ? (((Player) user).isSneaking() ? 0.4D : 0.2D) : 0.3D));
        Vector v = VectorUtils.viewVector(loc);
        v.multiply(2.4F);
        loc.getWorld().playSound(loc,"minecraft:entity.llama.spit",SoundCategory.AMBIENT,1F,1F);
        user.launchProjectile(LlamaSpit.class,v);

        damager = null;
        if (!item.getItemMeta().isUnbreakable() && RandomUtil.getRandom().nextDouble() < 0.5 && ItemToolUtil.damage(item,1)){
            user.getEquipment().setItemInMainHand(null);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakBlock(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (is(p.getInventory().getItemInMainHand())){
            event.setCancelled(true);
        }
    }
}

