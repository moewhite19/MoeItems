package cn.whiteg.moeitems.food;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.api.CustItem_Lore;
import cn.whiteg.rpgArmour.utils.VectorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class SaltSodaWater extends CustItem_Lore implements Listener {
    private static final SaltSodaWater a;

    static {
        a = new SaltSodaWater();
    }

    private SaltSodaWater() {
        super(Material.POTION,"§6盐汽水",Arrays.asList("","§b喝完胸大"),1);
    }

    public static SaltSodaWater get() {
        return a;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEat(PlayerItemConsumeEvent event) {
        if (!is(event.getItem())) return;
        Player p = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(MoeItems.plugin,() -> {
            if (p.isDead()) return;
            Location loc = p.getLocation();
            Vector v = VectorUtils.viewVector(loc);
            v.multiply(1.5F);
            loc.getWorld().playSound(loc,"minecraft:entity.llama.spit",SoundCategory.AMBIENT,1F,1F);
            p.launchProjectile(LlamaSpit.class,v);
        },20L);
    }

}
