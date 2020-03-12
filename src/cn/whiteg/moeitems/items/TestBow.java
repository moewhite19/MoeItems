package cn.whiteg.moeitems.items;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import com.destroystokyo.paper.event.player.PlayerReadyArrowEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TestBow extends CustItem_CustModle implements Listener {

    public TestBow() {
        super(Material.BOW,11,"§9测试弓");
    }

    @EventHandler
    public void bowRead(PlayerReadyArrowEvent event) {
        Player p = event.getPlayer();
        p.sendMessage("准备箭: " + event.getArrow() + " 弓箭: " + event.getBow());
    }
}
