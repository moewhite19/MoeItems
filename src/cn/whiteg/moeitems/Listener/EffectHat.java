package cn.whiteg.moeitems.Listener;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.moeitems.utils.CallBack;
import cn.whiteg.rpgArmour.api.CustItem;
import cn.whiteg.rpgArmour.event.ArmourChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EffectHat implements Listener {
    static Map<CustItem, CallBack<PotionEffect>> hats = new HashMap<>();

    static {
        MoeItems.plugin.regListener(new EffectHat());
    }

    BukkitRunnable runnable = null;
    Set<Player> list = new HashSet<>(Bukkit.getMaxPlayers());

    public static void regHat(CustItem item,CallBack<PotionEffect> type) {
        hats.put(item,type);
    }

    @EventHandler(ignoreCancelled = true)
    public void onChan(ArmourChangeEvent event) {
        has(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        has(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onRespawn(PlayerRespawnEvent event) {
        has(event.getPlayer());
    }

    public void has(Player player) {
        var eff = getEffectType(player);
        if (eff != null){
            list.add(player);
            setTime();
        }
    }

    public void setTime() {
        if (runnable == null){
            runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    var it = list.iterator();
                    while (it.hasNext()) {
                        var p = it.next();
                        var eff = getEffectType(p);
                        if (eff != null){
                            p.addPotionEffect(eff);
                        } else {
                            it.remove();
                        }
                    }

                    if (list.isEmpty()){
                        runnable = null;
                        cancel();
                    }
                }
            };
            runnable.runTaskTimer(MoeItems.plugin,20,20);
        }
    }

    public PotionEffect getEffectType(Player p) {
        var h = p.getInventory().getHelmet();
        if (p.isDead() || h == null){
            return null;
        }
        for (var entry : hats.entrySet()) {
            if (entry.getKey().is(h)){
                return entry.getValue().call();
            }
        }
        return null;
    }
}
