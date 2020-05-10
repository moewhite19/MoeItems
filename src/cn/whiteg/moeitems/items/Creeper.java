package cn.whiteg.moeitems.items;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.Setting;
import cn.whiteg.rpgArmour.api.CustEntityChunkEvent;
import cn.whiteg.rpgArmour.api.CustEntityID;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Creeper extends CustItem_CustModle implements Listener {
    final static Creeper a;
    private final static int id2 = 25;

    static {
        a = new Creeper();
    }

    private final Figurine creeperEntity = new Figurine();
    private int fuze = 50;

    private Creeper() {
        super(Material.BOWL,5,"§2苦力怕");
        ConfigurationSection c = Setting.getCustItemConfig(this);
        if (c != null){
            fuze = c.getInt("fuze",fuze);
        } else {
            RPGArmour.logger.warning("找不到" + getDisplayName() + "的配置文件");
        }
    }

    public static Creeper get() {
        return a;
    }

    public Figurine getCreeperEntity() {
        return creeperEntity;
    }

    @EventHandler(ignoreCancelled = true)
    public void onFurnChan(FurnaceBurnEvent event) {
        if (is(event.getFuel())){
//            MoeItems.logger.info("燃烧时间" + event.getBurnTime());
            event.setBurnTime(2400);
//            Block block = event.getBlock();
//            Location loc = block.getLocation();
//            Residence res = MoeItems.plugin.getResidence();
//            if (res != null){
//                FlagPermissions flag = res.getPermsByLoc(loc);
//                if (!flag.has(Flags.explode,true)){
//                    return;
//                }
//            }
//            loc.getWorld().createExplosion(loc,4.5F,true,true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().isSneaking()) return;
        ItemStack item = event.getItemDrop().getItemStack();
        if (is(item)){
            if (item.getAmount() > 1) return;
            item = item.clone();
            final ItemMeta meta = item.getItemMeta();
            event.getItemDrop().remove();
            meta.setCustomModelData(id2);
            item.setItemMeta(meta);
            final Location loc = event.getItemDrop().getLocation();
            loc.getWorld().playSound(loc,"minecraft:entity.creeper.primed",SoundCategory.MASTER,1.5F,1.5F);
            final Vector vector = event.getItemDrop().getVelocity();
            vector.multiply(2.5);
            Entity entity = creeperEntity.summon(loc);
            if (entity.isDead()) return;
            entity.setVelocity(vector);
            if (meta.isUnbreakable()){
                event.setCancelled(true);
            }
        }
    }

    @Override
    public boolean is(ItemStack item) {
        if (item == null) return false;
        if (item.getType() == getMaterial() && item.hasItemMeta()){
            final ItemMeta meta = item.getItemMeta();
            return meta.hasCustomModelData() && (meta.getCustomModelData() == getId() || meta.getCustomModelData() == id2);
        }
        return false;
    }

    public class Figurine extends CustEntityID implements CustEntityChunkEvent {
        Map<UUID, BukkitTask> taskMap = new HashMap<>();

        public Figurine() {
            super("Creeper",org.bukkit.entity.ArmorStand.class);
        }

        @Override
        public void load(final Entity entity) {
            if (entity.isDead()) return;
            EntityUtils.setBoundingBox(entity,BoundingBox.of(entity.getLocation(),0.1D,0.15D,0.1D));
//            EntityUtils.setEntitySize(entity,0.1F,0.1F);
            Bukkit.getScheduler().runTaskLater(RPGArmour.plugin,() -> {
                if (entity.isDead()) return;
                final Location loc = entity.getLocation();
                entity.remove();
                Residence res = MoeItems.plugin.getResidence();
                if (res != null){
                    FlagPermissions flag = res.getPermsByLoc(loc);
                    if (!flag.has(Flags.explode,true)){
                        return;
                    }
                }
                loc.getWorld().createExplosion(entity,3.2F,true,true);
            },fuze);
        }

        @Override
        public void unload(Entity entity) {
            entity.remove();
        }

        @Override
        public boolean init(Entity entity) {
            if (super.init(entity)){
                entity.getScoreboardTags().add("dontedit");
                entity.getScoreboardTags().add("dontsave");
                entity.setCustomName(getDisplayName());
                load(entity);
                if (entity instanceof ArmorStand){
                    ArmorStand armorStand = (ArmorStand) entity;
                    ItemStack item = createItem();
                    armorStand.setVisible(false);
                    //paper方法
                    armorStand.setDisabledSlots(EquipmentSlot.HEAD);
//                    Main.nms.setSlotsDisabled(armorStand,true);
                    ItemMeta meta = item.getItemMeta();
                    meta.setCustomModelData(id2);
                    item.setItemMeta(meta);
                    armorStand.setHelmet(item);
                }
                return true;
            }
            return false;
        }
    }
}


