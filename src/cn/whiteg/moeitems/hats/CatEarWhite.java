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

public class CatEarWhite extends CustItem_CustModle {
    private static final CatEarWhite a = new CatEarWhite();

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

