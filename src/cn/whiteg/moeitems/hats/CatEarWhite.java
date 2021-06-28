package cn.whiteg.moeitems.hats;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Phantom;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class CatEarWhite extends CustItem_CustModle implements Listener {
    private static final CatEarWhite WHITE = new CatEarWhite();
    private static final CustItem[] hats;

    static {
        hats = new CustItem[]{WHITE,CatEarGolden.get(),CatEarDiamond.get()};
    }

    private CatEarWhite() {
        super(Material.SHEARS,36,"§f白色猫耳头饰");
        NamespacedKey key = new NamespacedKey(MoeItems.plugin,"white_catear_hat");
        ShapedRecipe r = new ShapedRecipe(key,createItem());
        r.shape(
                "ABA",
                "BAB"
        );
        r.setIngredient('A',Material.WHITE_WOOL);
        r.setIngredient('B',Material.PHANTOM_MEMBRANE);
        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
    }

    public static CatEarWhite get() {
        return WHITE;
    }

    @Override
    public ItemStack createItem() {
        ItemStack item = super.createItem();
        ItemMeta im = item.getItemMeta();
        im.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED,new AttributeModifier(UUID.randomUUID(),getDisplayName(),0.01,AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HEAD));
        item.setItemMeta(im);
        return item;
    }

    @EventHandler(ignoreCancelled = true)
    public void onTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof LivingEntity entity && (event.getEntity() instanceof Creeper || event.getEntity() instanceof Phantom)){
            ItemStack helmet;
            var equipment = entity.getEquipment();
            if (equipment == null) return;
            helmet = equipment.getHelmet();
            if (helmet == null) return;

            for (CustItem hat : hats) {
                if (hat.is(helmet)){
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }
}

