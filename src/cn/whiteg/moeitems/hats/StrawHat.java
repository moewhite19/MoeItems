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

public class StrawHat extends CustItem_CustModle {
    private static final StrawHat a = new StrawHat();

    private StrawHat() {
        super(Material.SHEARS,11,"§6草帽");
        NamespacedKey key = new NamespacedKey(MoeItems.plugin,"straw_hat");
        ShapedRecipe r = new ShapedRecipe(key,createItem());
        r.shape(
                "BBB",
                "AFA",
                "DEC"
        );
        r.setIngredient('A',Material.LEATHER);
        r.setIngredient('B',Material.HAY_BLOCK);
        r.setIngredient('C',Material.DANDELION);
        r.setIngredient('D',Material.POPPY);
        r.setIngredient('E',Material.BLUE_ORCHID);
        r.setIngredient('F',Material.LILY_OF_THE_VALLEY);

        RPGArmour.plugin.getRecipeManage().addRecipe(key,r);
    }

    public static StrawHat get() {
        return a;
    }

    @Override
    public ItemStack createItem() {
        ItemStack item = super.createItem();
        ItemMeta im = item.getItemMeta();
        im.addAttributeModifier(Attribute.GENERIC_ARMOR,new AttributeModifier(UUID.randomUUID(),"",2,AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HEAD));
        im.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE,new AttributeModifier(UUID.randomUUID(),"",0.25,AttributeModifier.Operation.ADD_NUMBER,EquipmentSlot.HEAD));
        item.setItemMeta(im);
        return item;
    }
}

