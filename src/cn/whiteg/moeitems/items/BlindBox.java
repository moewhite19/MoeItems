package cn.whiteg.moeitems.items;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.RandomUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Container;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlindBox extends CustItem_CustModle implements Listener {
    final static BlindBox a;
    final static NamespacedKey itemsKey = new NamespacedKey(MoeItems.plugin,"items");

    static {
        a = new BlindBox();
    }

    public BlindBox() {
        super(Material.BOWL,18,"§b盲盒");
    }

    public static BlindBox get() {
        return a;
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (event.getClick() != ClickType.RIGHT) return;
        var item = event.getCurrentItem();
        if (is(item)){
            event.setCancelled(true);
            ItemStack cursor = event.getCursor();
            if (hasContainer(cursor)){
                //noinspection ConstantConditions
                Join(item,cursor);
            } else {
                //noinspection ConstantConditions
                var items = getItems(item);
                if (items == null) return;
                ItemStack randomItem = items.get(RandomUtil.getRandom().nextInt(items.size()));//从数组里随机抽取一个物品


                var player = event.getWhoClicked();
                var inv = player.getInventory();
                player.sendMessage(Component.text(" §b抽到了§r").append(randomItem.displayName().hoverEvent(randomItem.asHoverEvent())));
                player.playSound(Sound.sound(Key.key("entity.puffer_fish.blow_up"),Sound.Source.PLAYER,1f,1f));
                HashMap<Integer, ItemStack> callBack = inv.addItem(randomItem);
                if (!callBack.isEmpty()){
                    player.getWorld().dropItem(player.getLocation(),randomItem);
                    player.sendMessage(" 背包已经满了");
                }
                item.subtract(1);

            }
        }
    }

    public boolean hasContainer(ItemStack itemStack) {
        return itemStack != null && itemStack.getItemMeta() instanceof BlockStateMeta bm && bm.getBlockState() instanceof Container;
    }

    public void Join(ItemStack box,ItemStack container) {
        if (container.getItemMeta() instanceof BlockStateMeta bm && bm.getBlockState() instanceof Container c){
            Inventory inventory = c.getInventory();
            setItems(box,inventory.getStorageContents());
//            清理容器
//            inventory.clear();
//            container.setItemMeta(bm);
        }
    }

    public void setItems(ItemStack box,ItemStack... itemStack) {
        if (itemStack.length > 0){
            var meta = box.getItemMeta();
            PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
            var config = new YamlConfiguration();
            for (int i = 0; i < itemStack.length; i++) {
                ItemStack value = itemStack[i];
                if (itemIsAir(value)) continue; //屏蔽空气
                config.set(String.valueOf(i),value);
            }
            dataContainer.set(itemsKey,PersistentDataType.STRING,config.saveToString());
            box.setItemMeta(meta);
        }
    }


    public List<ItemStack> getItems(ItemStack box) {
        var meta = box.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        var str = dataContainer.get(itemsKey,PersistentDataType.STRING);
        if (str != null){
            var config = new YamlConfiguration();
            try{
                config.loadFromString(str);
                var keys = config.getKeys(false);
                var itemList = new ArrayList<ItemStack>(keys.size());
                for (String key : keys) {
                    ItemStack itemStack = config.getItemStack(key);
                    if (itemStack != null) itemList.add(itemStack);
                }
                if (!itemList.isEmpty()) return itemList;
            }catch (InvalidConfigurationException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean hasItems(ItemStack box) {
        var meta = box.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        return dataContainer.has(itemsKey,PersistentDataType.STRING);
    }

    public boolean itemIsAir(ItemStack item) {
        return item == null || item.getType().isAir();
    }
}

