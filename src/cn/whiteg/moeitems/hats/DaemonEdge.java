package cn.whiteg.moeitems.hats;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.RandomUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;

public class DaemonEdge extends CustItem_CustModle implements Listener {
    private static final DaemonEdge a = new DaemonEdge();
    EquipmentSlot[] armourSorts = new EquipmentSlot[]{EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET};

    private DaemonEdge() {
        super(Material.TOTEM_OF_UNDYING,3,"§7守护灵");
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

    public static DaemonEdge get() {
        return a;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamage() < 1) return;
        if (event.getEntity() instanceof LivingEntity entity && hasItem(entity)){

            if (event.getDamager() instanceof Mob damager){
                if (damager instanceof Player player){
                    final Location location = player.getLocation();
                    //在玩家周围召唤一圈幻魔者獠牙EvokerFangs.class
                    for (int i = 0; i < 10; i++) {
                        Location spawnLoc = location.clone().add(RandomUtil.getRandom().nextDouble(2),0,RandomUtil.getRandom().nextDouble(2));
                        final EvokerFangs fangs = location.getWorld().spawn(spawnLoc,EvokerFangs.class);
                        fangs.setOwner(entity);
                    }
                } else if (damager instanceof Monster){
                    //遍历玩家周围的其他怪物，把攻击者的仇恨转移到其他怪物上
                    for (Entity findEntity : damager.getNearbyEntities(10,10,10)) {
                        if (findEntity instanceof Monster monster && monster != damager){
                            damager.setTarget(monster);
                        }
                    }
                }
            }
        }
    }

    public boolean hasItem(LivingEntity entity) {
        final EntityEquipment equipment = entity.getEquipment();
        if (equipment != null) for (EquipmentSlot slot : armourSorts) {
            if (is(equipment.getItem(slot))){
                return true;
            }
        }
        return false;
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

