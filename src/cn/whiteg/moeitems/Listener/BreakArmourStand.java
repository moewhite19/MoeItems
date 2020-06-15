package cn.whiteg.moeitems.Listener;

import cn.whiteg.moeitems.Event.BreakCustArmourStand;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class BreakArmourStand implements Listener {
    public static Item drop(ArmorStand e,Location loc) {
        ItemStack helmet = e.getHelmet();
        e.remove();
        if (helmet.getType() != Material.AIR){
            return loc.getWorld().dropItem(loc,helmet);
        }
        return null;

    }

    @EventHandler
    public void onLClickEntity(EntityDamageByEntityEvent event) {
        //需要领地权限检查
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
        org.bukkit.entity.Entity e = event.getEntity();
        if (e.isDead()) return;
        if (e instanceof ArmorStand){
            ArmorStand as = (ArmorStand) e;
            if (as.isVisible()) return;
            Set<String> s = as.getScoreboardTags();
            if (!s.contains("candestroy")) return;
            Location loc = e.getLocation();
            org.bukkit.entity.Entity damager = event.getDamager();
            if (damager instanceof Player){
                Player p = (Player) damager;
                Residence res = Residence.getInstance();
                if (!res.isResAdminOn(p)){
                    FlagPermissions flag = res.getPermsByLocForPlayer(loc,p);
                    if (!flag.playerHasHints(p,Flags.destroy,true)){
                        return;
                    }
                }
            }
            BreakCustArmourStand ev = new BreakCustArmourStand(e,damager);
            ev.callEvent();
            if (ev.isCancelled()) return;
            drop((ArmorStand) e,loc);
        }
    }

}
