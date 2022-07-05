package cn.whiteg.moeitems.items;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Barber extends CustItem_CustModle implements Listener {
    public static final String TAG = Barber.class.getSimpleName();

    public Barber() {
        super(Material.BOWL,160,"理发店");
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteraction(PlayerInteractAtEntityEvent event) {
        final Player player = event.getPlayer();
        final ItemStack hand = player.getItemInHand();
        if (is(hand) && event.getRightClicked() instanceof Turtle turtle){
            final ItemStack one = hand.asOne();
            hand.subtract(); //物品减少一个
            final Item item = turtle.getWorld().dropItem(turtle.getLocation(),one);
            if (item.isDead()){
                player.sendActionBar("无法生成掉落物");
                return;
            }
            item.setCanMobPickup(false);
            item.setCanPlayerPickup(false);
            turtle.addPassenger(item);
        }
    }
}
