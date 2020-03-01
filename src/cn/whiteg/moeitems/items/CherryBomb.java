package cn.whiteg.moeitems.items;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class CherryBomb extends CustItem_CustModle implements Listener {
    private static final CherryBomb o = new CherryBomb();
    private final String TAG = this.getClass().getSimpleName().toLowerCase();

    public CherryBomb() {
        super(Material.SNOWBALL,8,"§6摔炮");
        NamespacedKey key = new NamespacedKey(MoeItems.plugin,TAG);
        ItemStack item = createItem();
        item.setAmount(8);
        ShapedRecipe r = new ShapedRecipe(key,item);
        r.shape(
                "A",
                "B",
                "C"
        );
        r.setIngredient('A',Material.FLINT);
        r.setIngredient('B',Material.GUNPOWDER);
        r.setIngredient('C',Material.PAPER);
        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
    }

    public static CherryBomb get() {
        return o;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball)) return;
        Snowball snowball = (Snowball) event.getEntity();
        if (is(EntityUtils.getSnowballItem(snowball))){
            Location loc = snowball.getLocation();
            FlagPermissions flag = Residence.getInstance().getPermsByLoc(loc);
            if (!flag.has(Flags.explode,true)){
                return;
            }
            loc.getWorld().createExplosion(snowball,0.2F,false,false);
        }
    }
}

