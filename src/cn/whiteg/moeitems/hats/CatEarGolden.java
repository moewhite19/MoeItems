package cn.whiteg.moeitems.hats;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class CatEarGolden extends CustItem_CustModle {
    private static final CatEarGolden a = new CatEarGolden();

    private CatEarGolden() {
        super(Material.SHEARS,38,"§6黄金猫耳头饰");
        NamespacedKey key = new NamespacedKey(MoeItems.plugin,"golden_catear_hat");
        ShapedRecipe r = new ShapedRecipe(key,createItem());
        r.shape(
                "ABA",
                "BAB",
                "CCC"
        );
        r.setIngredient('A',Material.WHITE_WOOL);
        r.setIngredient('B',Material.PHANTOM_MEMBRANE);
        r.setIngredient('C',Material.GOLD_NUGGET);
        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
    }

    public static CatEarGolden get() {
        return a;
    }

    @Override
    public ItemStack createItem() {
        ItemStack item = super.createItem();
        ItemMeta im = item.getItemMeta();
        im.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED,new AttributeModifier(UUID.randomUUID(),getDisplayName(),0.01,AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HEAD));
        item.setItemMeta(im);
        return item;
    }
}

