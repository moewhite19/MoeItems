package cn.whiteg.moeitems.items;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class VirtueAxe extends CustItem_CustModle implements Listener {
    final static VirtueAxe a;

    static {
        a = new VirtueAxe();
    }

    public VirtueAxe() {
        super(Material.NETHERITE_AXE,2,"§9以德服人");
    }

    public static VirtueAxe get() {
        return a;
    }

    @EventHandler
    public void killEntity(PlayerDeathEvent event){
        final LivingEntity entity = event.getEntity();
        final Player killer = entity.getKiller();
        if(killer != null && is(killer.getInventory().getItemInMainHand())){
            entity.getWorld().playSound(entity.getLocation() ,"minecraft:mabaoguo.bujiangwude" , 1, 1);
        }
    }
}

