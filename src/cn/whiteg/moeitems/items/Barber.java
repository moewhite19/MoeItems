package cn.whiteg.moeitems.items;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityDismountEvent;

public class Barber extends CustItem_CustModle implements Listener {

    public Barber() {
        super(Material.BOWL,160,"理发店");
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteraction(PlayerInteractAtEntityEvent event) {
        final Player player = event.getPlayer();
        final ItemStack hand = player.getItemInHand();
        if (is(hand )){
            final ItemStack one = hand.asOne();
            hand.subtract(); //物品减少一个
            Entity clicked = event.getRightClicked();
            if(!clicked.getPassengers().isEmpty())return; //头上有实体时无视
            final Item item = clicked.getWorld().dropItem(clicked.getLocation(),one);
            if (item.isDead()){
                player.sendActionBar("无法生成掉落物");
                return;
            }
            item.setCanMobPickup(false);
            item.setCanPlayerPickup(false);
            clicked.addPassenger(item);
        }
    }

    @EventHandler
    public void onEntityLeave(EntityDismountEvent event){
        if(event.getEntity() instanceof Item item && is(item.getItemStack())){
            item.setCanPlayerPickup(true);
            item.setCanMobPickup(true);
        }
    }
}
