package cn.whiteg.moeitems.items;

import cn.whiteg.rpgArmour.RPGArmour;
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
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class BigIvan extends CustItem_CustModle implements Listener {
    final static BigIvan a;
    private final static int id2 = 19; //放出来后的动态模型

    static {
        a = new BigIvan();
    }

    private final Figurine creeperEntity = new Figurine();
    private final int fuze = 80;

    private BigIvan() {
        super(Material.BOWL,19,"§b大伊万");
//        ConfigurationSection c = Setting.getCustItemConfig(this);
//        if (c != null){
//            fuze = c.getInt("fuze",fuze);
//        } else {
//            MoeItems.logger.warning("找不到" + getDisplayName() + "的配置文件");
//        }
    }

    public static BigIvan get() {
        return a;
    }

    public Figurine getCreeperEntity() {
        return creeperEntity;
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
            loc.getWorld().playSound(loc,"minecraft:entity.creeper.primed",SoundCategory.MASTER,0.5F,0.5F);
            final Vector vector = event.getItemDrop().getVelocity();
            vector.multiply(5);
            Entity entity = creeperEntity.summon(loc);
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

        public Figurine() {
            super("BigIvan",ArmorStand.class);
        }

        @Override
        public void load(final Entity entity) {
            if (entity.isDead()) return;
            EntityUtils.setBoundingBox(entity,BoundingBox.of(entity.getLocation(),0.4D,0.4D,0.4D));
//            EntityUtils.setEntitySize(entity,0.1F,0.1F);
            Bukkit.getScheduler().runTaskLater(RPGArmour.plugin,() -> {
                if (entity.isDead()) return;
                final Location loc = entity.getLocation();
                entity.remove();
                FlagPermissions flag = Residence.getInstance().getPermsByLoc(loc);
                if (!flag.has(Flags.explode,true)){
                    return;
                }
                loc.getWorld().createExplosion(entity,32F,true,true);  //Paper方法
//                loc.getWorld().createExplosion(entity,32F,true,true);

//                WorldSetting ws = MoeAntiBuild.plugin.getWorldSetting(loc.getWorld().getName());
//                if (ws != null && ws.SafeTnT){
//                    loc.getWorld().createExplosion(entity,3.2F,false,false);
//                } else {
//                    loc.getWorld().createExplosion(entity,3.2F,true,true);
//                }
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
                    EntityUtils.setSlotsDisabled(armorStand,true);
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


