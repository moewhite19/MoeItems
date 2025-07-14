package cn.whiteg.moeitems.hats;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.ItemTypeUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.UUID;

public class Ushanka extends CustItem_CustModle {
    private static final Ushanka a = new Ushanka();

    private Ushanka() {
        super(Material.SHEARS,20,"§4翻毛帽");
        NamespacedKey key = new NamespacedKey(MoeItems.plugin,"ushanka");
        ShapedRecipe r = new ShapedRecipe(key,createItem());
        r.shape(
                "BAB",
                "B B"
        );
        r.setIngredient('A',Material.WHITE_WOOL);
        r.setIngredient('B',new RecipeChoice.MaterialChoice(new ArrayList<>(ItemTypeUtils.getWools())));
        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
    }

    public static Ushanka get() {
        return a;
    }

    @Override
    public ItemStack createItem() {
        ItemStack item = super.createItem();
        ItemMeta im = item.getItemMeta();
        im.addAttributeModifier(Attribute.ARMOR,new AttributeModifier(new NamespacedKey("moeitems","armor"),2,AttributeModifier.Operation.ADD_NUMBER,EquipmentSlotGroup.HEAD.HEAD));
        im.addAttributeModifier(Attribute.KNOCKBACK_RESISTANCE,new AttributeModifier(new NamespacedKey("moeitems","knockback"),0.15,AttributeModifier.Operation.ADD_NUMBER,EquipmentSlotGroup.HEAD));
        item.setItemMeta(im);
        return item;
    }
}

