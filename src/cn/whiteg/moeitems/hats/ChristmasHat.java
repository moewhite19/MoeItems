package cn.whiteg.moeitems.hats;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class ChristmasHat extends CustItem_CustModle {
    private final static ChristmasHat a = new ChristmasHat();

    private ChristmasHat() {
        super(Material.SHEARS,14,"§4圣诞帽");
        NamespacedKey key = new NamespacedKey(MoeItems.plugin,"christmas_hat");
        ShapedRecipe r = new ShapedRecipe(key,createItem());
        r.shape(
                "BBB",
                "A A"
        );
        r.setIngredient('A',Material.WHITE_WOOL);
        r.setIngredient('B',Material.RED_WOOL);
        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
    }

    public static ChristmasHat get() {
        return a;
    }

    @Override
    public ItemStack createItem() {
        ItemStack item = super.createItem();
        ItemMeta im = item.getItemMeta();
        im.addAttributeModifier(Attribute.ARMOR,new AttributeModifier(new NamespacedKey("moeitem","armor"),2,AttributeModifier.Operation.ADD_NUMBER,EquipmentSlotGroup.HEAD));
        im.addAttributeModifier(Attribute.MAX_HEALTH,new AttributeModifier(new NamespacedKey("moeitem","health"),4,AttributeModifier.Operation.ADD_NUMBER,EquipmentSlotGroup.HEAD));
        item.setItemMeta(im);
        return item;
    }
}

