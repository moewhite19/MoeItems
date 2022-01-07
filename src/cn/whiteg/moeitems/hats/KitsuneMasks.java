package cn.whiteg.moeitems.hats;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitsuneMasks extends CustItem_CustModle implements Listener {
    final static KitsuneMasks obj = new KitsuneMasks();
    int idMin = 84;
    int idMax = 89;

    public KitsuneMasks() {
        super(Material.SHEARS,85,"§6稻荷神面具");
    }

    public static KitsuneMasks get() {
        return obj;
    }

    @Override
    public boolean is(ItemStack itemStack) {
        if (itemStack != null && itemStack.getType() == this.getMaterial() && itemStack.hasItemMeta()){
            ItemMeta var2 = itemStack.getItemMeta();
            if (!var2.hasCustomModelData()){
                return false;
            } else {
                int data = var2.getCustomModelData();
                return data >= idMin && data <= idMax;
            }
        } else {
            return false;
        }
    }

    //获取下一个ID
    public int nextId(int id) {
        return (id >= idMax) ? idMin : id + 1;
    }

    //切换面具下一个Id
    public void onSwitch(ItemStack itemStack) {
        if (is(itemStack)){
            ItemMeta meta = itemStack.getItemMeta();
            meta.setCustomModelData(nextId(meta.getCustomModelData()));
            itemStack.setItemMeta(meta);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (event.getClick() != ClickType.RIGHT || event.getSlotType() != InventoryType.SlotType.ARMOR) return;
        ItemStack currentItem = event.getCurrentItem();
        if (!is(currentItem)) return;
        onSwitch(currentItem);
        event.setCancelled(true);
    }
}
