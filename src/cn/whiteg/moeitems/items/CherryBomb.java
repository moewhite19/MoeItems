package cn.whiteg.moeitems.items;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import net.minecraft.server.v1_15_R1.EntityProjectileThrowable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftSnowball;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.lang.reflect.Method;

public class CherryBomb extends CustItem_CustModle implements Listener {
    private static final CherryBomb o = new CherryBomb();
    private final String TAG = this.getClass().getSimpleName().toLowerCase();
    private final Method getItemMethod;

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
        r.setIngredient('A',Material.GUNPOWDER);
        r.setIngredient('B',Material.FLINT);
        r.setIngredient('C',Material.PAPER);
        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
        Method m;
        try{
            m = EntityProjectileThrowable.class.getDeclaredMethod("getItem");
            m.setAccessible(true);
        }catch (NoSuchMethodException e){
            e.printStackTrace();
            m = null;
        }
        getItemMethod = m;
    }

    public static CherryBomb get() {
        return o;
    }

    @EventHandler(ignoreCancelled = true)
    public void onShor(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Snowball){
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball) || !event.getEntity().getScoreboardTags().contains(TAG)) return;
        CraftSnowball snowball = (CraftSnowball) event.getEntity();
        try{
            ItemStack item = CraftItemStack.asBukkitCopy((net.minecraft.server.v1_15_R1.ItemStack) getItemMethod.invoke(snowball.getHandle()));
            if (is(item)){
                Location loc = snowball.getLocation();
                loc.getWorld().createExplosion(snowball,0.2F,false,false);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

