package cn.whiteg.moeitems.items;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.moetp.utils.EntityTpUtils;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

public class ShulkerBull extends CustItem_CustModle implements Listener {
    private final static ShulkerBull o = new ShulkerBull();
    //private final String TAG = this.getClass().getSimpleName().toLowerCase();

    public ShulkerBull() {
        super(Material.SNOWBALL,6,"§6潜影球");
    }

    public static ShulkerBull get() {
        return o;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onShor(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Snowball snowball)) return;
        ItemStack item = EntityUtils.getSnowballItem(snowball);
        if (!is(item)) return;
        var loc = snowball.getLocation();
        var source = snowball.getShooter();
        if (source instanceof Entity shooter){
            var shulkerBullet = loc.getWorld().spawn(loc,ShulkerBullet.class);
            shooter.addPassenger(shulkerBullet);
            Bukkit.getScheduler().runTask(MoeItems.plugin,snowball::remove);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamage() < 1D) return;
        var entity = event.getEntity();
        var passengers = entity.getPassengers();
        if (passengers.isEmpty()) return;
        for (Entity passenger : passengers)
            if (passenger instanceof ShulkerBullet){
                var shulkerBullet = (ShulkerBullet) passenger;
                shulkerBullet.setTarget(getDmager(event.getDamager()));
                EntityTpUtils.forgeStopRide(shulkerBullet);
            }
    }

    Entity getDmager(Entity damager) {
        if (damager instanceof Projectile){
            var s = ((Projectile) damager).getShooter();
            if (s instanceof Entity){
                return (Entity) s;
            }
        }
        return damager;
    }
}

