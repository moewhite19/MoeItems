package cn.whiteg.moeitems.Listener;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class DebugTickListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerRClickEntity(PlayerInteractEntityEvent event) {
        Player p = event.getPlayer();
        if (!p.hasPermission("whiteg.test") || !hasItem(event.getPlayer().getInventory().getItemInMainHand())) return;
        Entity e = event.getRightClicked();
        p.sendMessage("点击实体: " + e);
        p.sendMessage("实体类" + e.getClass());
        p.sendMessage("实体标签" + e.getScoreboardTags());
        p.sendMessage("实体生成原因" + e.getEntitySpawnReason());
        if (e.fromMobSpawner()) p.sendMessage("实体来自刷怪笼");
        if (e instanceof Minecart){
            p.sendMessage("矿车最大速度" + ((Minecart) e).getMaxSpeed());
            p.sendMessage("矿车重力" + e.hasGravity());
        }
    }

//    @EventHandler
//    public void onDamage(VehicleDamageEvent event) {
//        Vehicle v = event.getVehicle();
//        if (v instanceof Minecart){
//            Entity p = event.getAttacker();
//            if (p instanceof Player){
//                if (!p.hasPermission("whiteg.test") || !hasItem(((Player) p).getInventory().getItemInMainHand())) {
//                    return;
//                }
////                p.sendMessage("恢复矿车");
////                MinecartMemberStore.convert((Minecart) v);
//            }
//
//        }
//    }

    boolean hasItem(ItemStack item) {
        if (item == null) return false;
        return item.getType() == Material.DEBUG_STICK;
    }
}
