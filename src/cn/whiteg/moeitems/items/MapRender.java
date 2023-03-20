package cn.whiteg.moeitems.items;

import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.dynmap.DynmapCore;
import org.dynmap.DynmapLocation;
import org.dynmap.DynmapWorld;
import org.dynmap.MapManager;
import org.dynmap.bukkit.DynmapPlugin;
import org.dynmap.common.DynmapCommandSender;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class MapRender extends CustItem_CustModle implements Listener {
    private static MapRender o;
    private static DynmapCore core;

    private static Method worldRadius;

    private static DynmapPlugin plugin;

    private static final Object lock;

    static {
        Object lockObj = null;
        try{
            if (Bukkit.getPluginManager().getPlugin("dynmap") != null){
                plugin = DynmapPlugin.plugin;
                final Field field = DynmapPlugin.class.getDeclaredField("core");
                field.setAccessible(true);
                core = (DynmapCore) field.get(plugin);
                Objects.requireNonNull(core);

                worldRadius = MapManager.class.getDeclaredMethod("renderWorldRadius",DynmapLocation.class,DynmapCommandSender.class,String.class,int.class);
                worldRadius.setAccessible(true);

                lockObj = Objects.requireNonNull(core.mapManager.lock);

                o = new MapRender();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        lock = lockObj;
    }

    public MapRender() {
        super(Material.FIREWORK_ROCKET,1,"§6卫星地图刷新弹");
    }

    @Nullable
    public static MapRender get() {
        return o;
    }

    //烟花爆炸
    @EventHandler(ignoreCancelled = true,priority = EventPriority.LOWEST)
    public void onFire(FireworkExplodeEvent event) {
        final Firework firework = event.getEntity();
        final ItemStack item = firework.getItem();
        if (!is(item)) return;
        final Location location = firework.getLocation();
        final DynmapWorld world = core.getWorld(firework.getWorld().getName());
        if (world != null){
            final MapManager manager = core.mapManager;
//            synchronized(lock) {
//                var rndr = manager.active_renders.get(wname);
//                if (rndr == null){
//                    rndr = new MapManager.FullWorldRenderState(world,l,sender,mapname,radius);
//                    this.active_renders.put(wname,rndr);
//                    MapManager.scheduleDelayedJob(rndr,0L);
//                }
//            }


            final RenderSender sender = new RenderSender();

            final DynmapLocation dynmapLocation = new DynmapLocation(location.getWorld().getName(),location.getX(),location.getY(),location.getZ());
            try{
                worldRadius.invoke(manager,dynmapLocation,sender,null,24);
            }catch (IllegalAccessException | InvocationTargetException e){
                e.printStackTrace();
            }
            if (sender.getStatus().startsWith("Render of ")){
                return;
            }
        }

        //如果正确开始渲染了就抛出,否则视为未渲染,返回物品
        if (!firework.isDead()) firework.getWorld().dropItem(location,item.asOne());
        firework.remove();
        event.setCancelled(true);
    }

    public static class RenderSender implements DynmapCommandSender {
        String status = "";

        @Override
        public boolean hasPrivilege(String s) {
            return true;
        }

        @Override
        public void sendMessage(String s) {
            status = s;
            Bukkit.getConsoleSender().sendMessage(s);
        }

        @Override
        public boolean isConnected() {
            return true;
        }

        @Override
        public boolean isOp() {
            return true;
        }

        @Override
        public boolean hasPermissionNode(String s) {
            return true;
        }

        public String getStatus() {
            return status;
        }
    }
}

