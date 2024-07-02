package cn.whiteg.moeitems.items;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

public class Testball extends CustItem_CustModle implements Listener {
    public static final String TAG = Testball.class.getSimpleName();

    public Testball() {
        super(Material.SNOWBALL,11,"§9测试雪球");
    }

    public void onShort(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Snowball snowball){
            final ItemStack snowballItem = EntityUtils.getSnowballItem(snowball);
            if (is(snowballItem)){
                if (snowball.getShooter() instanceof Player player){

                }
            }
        }
    }


    //开始伪装
    public static void start(Player player,Entity target) {


    }
}
