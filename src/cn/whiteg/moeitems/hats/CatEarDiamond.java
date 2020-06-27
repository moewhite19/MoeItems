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

public class CatEarDiamond extends CustItem_CustModle {
    private static final CatEarDiamond a = new CatEarDiamond();

    private CatEarDiamond() {
        super(Material.SHEARS,37,"§b钻石猫耳头饰");
        NamespacedKey key = new NamespacedKey(MoeItems.plugin,"diamond_catear_hat");
        ShapedRecipe r = new ShapedRecipe(key,createItem());
        r.shape(
                "ABA",
                "BAB",
                "CCC"
        );
        r.setIngredient('A',Material.WHITE_WOOL);
        r.setIngredient('B',Material.PHANTOM_MEMBRANE);
        r.setIngredient('C',Material.DIAMOND);
        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
    }

    public static CatEarDiamond get() {
        return a;
    }

    @Override
    public ItemStack createItem() {
        ItemStack item = super.createItem();
        ItemMeta im = item.getItemMeta();
        im.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED,new AttributeModifier(UUID.randomUUID(),getDisplayName(),0.02,AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HEAD));
        item.setItemMeta(im);
        return item;
    }
}

