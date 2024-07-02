package cn.whiteg.moeitems.hats;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BunnyEar extends CustItem_CustModle implements Listener {
    private static final BunnyEar a = new BunnyEar();

    private BunnyEar() {
        super(Material.SHEARS,50,"§f兔耳头饰");
//        EffectHat.regHat(this,() -> new PotionEffect(PotionEffectType.JUMP,30,2));
//        NamespacedKey key = new NamespacedKey(MoeItems.plugin,"bunny_ears_hat");
//        ShapedRecipe r = new ShapedRecipe(key,createItem());
//        r.shape(
//                "ABA",
//                "BAB",
//                "CCC"
//        );
//        r.setIngredient('A',Material.WHITE_WOOL);
//        r.setIngredient('B',Material.PHANTOM_MEMBRANE);
//        r.setIngredient('C',Material.GOLD_NUGGET);
//        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
    }

    public static BunnyEar get() {
        return a;
    }

    @EventHandler(ignoreCancelled = true)
    public void onSneak(PlayerToggleSneakEvent event) {
        if (!isHat(event.getPlayer())) return;
        if (event.isSneaking()){
            var effType = PotionEffectType.JUMP_BOOST;
            if (event.getPlayer().hasPotionEffect(effType)) return;
            event.getPlayer().addPotionEffect(new PotionEffect(effType,10,2,false,false));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player) || event.getCause() != EntityDamageEvent.DamageCause.FALL || !isHat((Player) event.getEntity()))
            return;
        double damage = event.getDamage() - 2;
        if (damage <= 0) event.setCancelled(true);
    }

    boolean isHat(Player player) {
        return is(player.getInventory().getHelmet());
    }

}

