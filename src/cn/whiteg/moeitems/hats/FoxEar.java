package cn.whiteg.moeitems.hats;

import cn.whiteg.mmocore.util.CoolDownUtil;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.event.PlayerDeathPreprocessEvent;
import cn.whiteg.rpgArmour.listener.UndyingListener;
import cn.whiteg.rpgArmour.utils.RandomUtil;
import cn.whiteg.rpgArmour.utils.VectorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

public class FoxEar extends CustItem_CustModle implements Listener {
    private static final FoxEar a = new FoxEar();
    private static final String skull_key = "§7狐仙庇护";
    private final int cooldown = 256;


    private FoxEar() {
        super(Material.SHEARS,60,"§7狐耳头饰");
//        NamespacedKey key = new NamespacedKey(MoeItems.plugin,"fox_ear_hat");
//        ShapedRecipe r = new ShapedRecipe(key,createItem());
//        r.shape(
//                "ABA",
//                "BAB",
//                "CCC"
//        );
//        r.setIngredient('A',Material.WHITE_WOOL);
//        r.setIngredient('B',Material.PHANTOM_MEMBRANE);
//        r.setIngredient('C',Material.DIAMOND);
//        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
    }

    public static FoxEar get() {
        return a;
    }

    @EventHandler(ignoreCancelled = true)
    public void onSneak(PlayerToggleSneakEvent event) {
        var hat = event.getPlayer().getInventory().getHelmet();
        if (!is(hat)){
            return;
        }
        if (event.isSneaking()){
            toggleStatus(hat,false);
        } else if (!isDisability(event.getPlayer())){
            toggleStatus(hat,true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (event.getDamage() < 1) return;
        var e = event.getEntity();
        if (e instanceof LivingEntity){
            LivingEntity entity = (LivingEntity) e;
            var hat = getEntityHat(e);
            if (!is(hat)) return;
            if (isDisability(entity)){
                toggleStatus(hat,false);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onRegain(EntityRegainHealthEvent event) {
        var e = event.getEntity();
        if (e instanceof LivingEntity){
            LivingEntity entity = (LivingEntity) e;
            var hat = getEntityHat(e);
            if (!is(hat)) return;
            if (!isDisability(entity)){
                toggleStatus(hat,true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDie(PlayerDeathPreprocessEvent event) {
        Player player = event.getPlayer();
        var hat = getEntityHat(player);
        if (is(hat)){
            if (!CoolDownUtil.hasCd(player.getName(),skull_key)) return;
            CoolDownUtil.setCd(player.getName(),skull_key,cooldown * 1000);
            //undyingListener.useOffHand(inv,new ItemStack(Material.TOTEM_OF_UNDYING));
            UndyingListener.EntityResurrect(player,hat);
            event.setCancelled(true);
            Location loc1 = player.getLocation();

            //弹开附近实体
            for (Entity entity : player.getNearbyEntities(4D,4D,4D)) {
                final EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent(player,entity,EntityDamageEvent.DamageCause.ENTITY_ATTACK,0);
                Bukkit.getPluginManager().callEvent(ev);
                if (ev.isCancelled()) return;
                Location loc2 = entity.getLocation();
                loc2.setY(loc2.getY() + 0.2);
                float yaw = VectorUtils.getLocYaw(loc1,loc2);
                float pitch = VectorUtils.getLocPitch(loc1,loc2);
                final double distance = loc1.distance(loc2); //距离
                final float mult = (float) (distance / 4D); //距离衰减
                loc2.setYaw(yaw);
                loc2.setPitch(pitch);
                Vector v = VectorUtils.viewVector(loc2);
                v.setY(v.getY() * 0.5);
                v.multiply(5 - (3 * mult));
                entity.setVelocity(v);
            }
        }
    }

    //是否为残血
    public boolean isDisability(LivingEntity entity) {
        var attr = entity.getAttribute(Attribute.MAX_HEALTH);
        if (attr == null) return false;
        double maxHealth = attr.getValue();
        return entity.getHealth() <= (maxHealth / 2) || (entity instanceof Player && !CoolDownUtil.hasCd(entity.getName(),skull_key));
    }

    public void toggleStatus(ItemStack hat,boolean b) {
        int id;
        if (b){
            id = 60;
        } else {
            id = RandomUtil.getRandom().nextBoolean() ? 61 : 62;
        }
        var meta = hat.getItemMeta();
        if (id == meta.getCustomModelData()) return;
        meta.setCustomModelData(id);
        hat.setItemMeta(meta);
    }

    public @Nullable
    ItemStack getEntityHat(Entity entity) {
        if (entity instanceof LivingEntity){
            var e = ((LivingEntity) entity).getEquipment();
            if (e != null) return e.getHelmet();
        }
        return null;
    }

    @Override
    public boolean hasId(int id) {
        return id >= 60 && id <= 62;
    }

    //    @Override
//    public ItemStack createItem() {
//        ItemStack item = super.createItem();
//        ItemMeta im = item.getItemMeta();
//        im.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED,new AttributeModifier(UUID.randomUUID(),getDisplayName(),0.02,AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HEAD));
//        item.setItemMeta(im);
//        return item;
//    }
}

