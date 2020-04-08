package cn.whiteg.moeitems.items;

import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.moeitems.Setting;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.utils.ItemToolUtil;
import cn.whiteg.rpgArmour.utils.RandomUtil;
import cn.whiteg.rpgArmour.utils.VectorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

public class GravityStaff extends CustItem_CustModle implements Listener {
    final static private GravityStaff a = new GravityStaff();
    private Map<UUID, Staus> map = new HashMap<>(16);
    private Set<EntityType> entityList = new HashSet<>();
    private Set<Material> blockList = new HashSet<>();
    private double damageRisk = 0.03;

    public GravityStaff() {
        super(Material.SHEARS,32,"§6重力法杖");
        ConfigurationSection c = Setting.getCustItemConfig(this);
        if (c != null){
            List<String> l = c.getStringList("BlockList");
            boolean chan = false;
            Iterator<String> it = l.iterator();
            while (it.hasNext()) {
                String str = it.next();
                try{
                    blockList.add(Material.valueOf(str.toUpperCase()));
                }catch (IllegalArgumentException e){
                    RPGArmour.logger.warning("无效的方块ID: " + str);
//                    chan = true;
//                    it.remove();
                }
            }
//            if (chan){
//                c.set("BlockList",l);
//                try{
//                    Setting.getConfig().save(new File(RPGArmour.plugin.getDataFolder(),"config.yml"));
//                }catch (IOException e){
//                    e.printStackTrace();
//                }
//            }
            l = c.getStringList("EntityList");
            for (String str : l) {
                try{
                    entityList.add(EntityType.valueOf(str.toUpperCase()));
                }catch (IllegalArgumentException e){
                    MoeItems.logger.warning("无效的实体ID: " + str);
                }
            }

            damageRisk = (float) c.getDouble("DamageRisk",damageRisk);
        }
//        else {
//            FileConfiguration cf = Setting.getConfig();
//            c = cf.createSection("CustItemSetting." + getClass().getSimpleName());
//            c.set("BlockList",Arrays.asList("GRASS_BLOK","STONE"));
//            c.set("EntityList",Arrays.asList("PIG","SLEEP"));
//            try{
//                cf.save(new File(RPGArmour.plugin.getDataFolder(),"config.yml"));
//            }catch (IOException e){
//                e.printStackTrace();
//            }
//
//        }
    }

    public static GravityStaff get() {
        return a;
    }
//
//    @EventHandler
//    public void onFallinprotection(EntityChangeBlockEvent event) {
//        Entity ent = event.getEntity();
//        if(plugin.isDisabledWorldListener(ent.getWorld()))return;
//        if (ent instanceof FallingBlock){
//            FallingBlock fallingBlock = (FallingBlock) ent;
//            Material bt = event.getBlock().getType();
//            if (fallingBlock.getBlockData().getMaterial() == bt) return;
//            FlagPermissions per = plugin.getPermsByLoc(ent.getLocation());
//            if (per.has(Flags.fallinprotection,false)){
//                event.setCancelled(true);
//                ent.remove();
//            }
//        }
//    }
//

    @EventHandler(ignoreCancelled = true)
    public void onRClickEntity(PlayerInteractEntityEvent event) {
        EquipmentSlot hand = event.getHand();
        Player p = event.getPlayer();
        PlayerInventory inv = p.getInventory();
        ItemStack item;
        if (hand == EquipmentSlot.HAND){
            item = inv.getItemInMainHand();
        } else {
            item = inv.getItemInOffHand();
        }
        if (!is(item)) return;
        Entity entity = event.getRightClicked();
        if ((!entityList.contains(entity.getType())) && !event.getPlayer().hasPermission("rpgarmour.gravitystaff.use." + entity.getType().name().toLowerCase())){
            return;
        }
        Player player = event.getPlayer();
        Staus staus = map.get(player.getUniqueId());
        if (staus == null){
            EntityDamageByEntityEvent e = new EntityDamageByEntityEvent(player,entity,EntityDamageEvent.DamageCause.ENTITY_ATTACK,0D);
            Bukkit.getPluginManager().callEvent(e);
            if (e.isCancelled()) return;
            new Staus(player,entity,event.getHand());
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onRClickBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking()) return;
        Action action = event.getAction();
        switch (action) {
            case RIGHT_CLICK_BLOCK:
                EquipmentSlot hand = event.getHand();
                if (hand == null) return;
                PlayerInventory inv = player.getInventory();

                ItemStack item;
                if (hand == EquipmentSlot.HAND){
                    item = inv.getItemInMainHand();
                } else {
                    item = inv.getItemInOffHand();
                }
                if (!is(item)) return;
                Block block = event.getClickedBlock();
                if (block == null) return;
                if (!blockList.contains(block.getType()) && !(player.hasPermission("rpgarmour.gravitystaff.use." + block.getType().name().toLowerCase())))
                    return;
                Staus staus = map.get(player.getUniqueId());
                if (staus != null){
                    if (staus.distance > 1){
                        staus.addDistance(-0.5);
                        event.setCancelled(true);
                    }
                    return;
                }
                Location loc = block.getLocation();
                BlockBreakEvent e = new BlockBreakEvent(block,player);
                Bukkit.getPluginManager().callEvent(e);
                if (e.isCancelled()) return;
                try{
                    if (e.isDropItems()){
                        FallingBlock entity = loc.getWorld().spawnFallingBlock(loc,block.getBlockData());
//                        EntityUtils.setBoundingBox(entity,BoundingBox.of(loc,0.3D,0.3D,0.3D));
                        new Staus(player,entity,hand);
                    }
                    block.setType(Material.AIR);
                    if (ItemToolUtil.damage(item,5)){
                        if (hand == EquipmentSlot.HAND)
                            inv.setItemInMainHand(null);
                        else inv.setItemInOffHand(null);
//                        inv.setItem(hand,null);
                    }
                    event.setCancelled(true);
                }catch (IllegalArgumentException ex){
                    ex.printStackTrace();
                }
                break;
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK: {
                Staus sta = map.get(player.getUniqueId());
                if (sta != null && sta.distance < 8){
                    sta.addDistance(0.5);
                    event.setCancelled(true);
                }
                break;
            }
            case RIGHT_CLICK_AIR: {
                Staus sta = map.get(player.getUniqueId());
                if (sta != null && sta.distance > 1){
                    sta.addDistance(-0.5);
                    event.setCancelled(true);
                }
                break;
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (map.isEmpty()) return;
        if (event.getDamager() instanceof Player){
            Player player = (Player) event.getDamager();
            Staus sta = map.get(player.getUniqueId());
            if (sta == null) return;
            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK && event.getEntity() == sta.entity){
                event.setCancelled(true);
            }
        }
    }

    public void unreg() {
        if (!map.isEmpty()){
            Iterator<Map.Entry<UUID, Staus>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, Staus> e = it.next();
                e.getValue().stop();
            }
        }
    }

    class Staus {
        final Player player;
        final Entity entity;
        final BukkitTask task;
        final PlayerInventory inv;
        double distance;
        private EquipmentSlot hand;

        Staus(Player player,Entity entity,EquipmentSlot hand) {
            this.hand = hand;
            player.sendActionBar("控制");
            this.player = player;
            this.entity = entity;
            this.distance = player.getLocation().distance(entity.getLocation());
            inv = player.getInventory();
            task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline() || player.isDead() || entity.isDead() || !check()){
                        stop();
                        return;
                    }
                    try{
                        Location ploc = player.getEyeLocation();
                        Location eloc = entity instanceof LivingEntity ? ((LivingEntity) entity).getEyeLocation() : entity.getLocation();
                        Vector v = VectorUtils.viewVector(ploc).multiply(distance);
                        Location vloc = ploc.clone().add(v);
                        if (eloc.distance(vloc) > 5){
                            stop();
                            return;
                        }
                        Vector vv = vloc.subtract(eloc).toVector();
                        entity.setVelocity(vv);
                        entity.setFallDistance(0F);
                    }catch (Exception e){
//                        e.printStackTrace();
                        stop();
                        return;
                    }


                }
            }.runTaskTimer(RPGArmour.plugin,1,1);
            map.put(player.getUniqueId(),this);
        }

        boolean check() {
            ItemStack item;
            if (hand == EquipmentSlot.HAND) item = inv.getItemInMainHand();
            else item = inv.getItemInOffHand();
            if (!is(item)) return false;
            if (RandomUtil.getRandom().nextDouble() < damageRisk && ItemToolUtil.damage(item,1)){
                if (hand == EquipmentSlot.HAND) inv.setItemInMainHand(null);
                else inv.setItemInOffHand(null);
//                inv.setItem(hand,null);
                return false;
            }
            return true;
        }

        void stop() {
            player.sendActionBar("取消");
            task.cancel();
            map.remove(player.getUniqueId());
        }

        void addDistance(double v) {
            distance += v;
        }
    }
}

