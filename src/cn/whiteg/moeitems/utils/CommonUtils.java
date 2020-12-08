package cn.whiteg.moeitems.utils;

import net.minecraft.server.v1_16_R3.Entity;
import org.bukkit.Location;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

public class CommonUtils {
    static AtomicInteger entityCount;

    static {
        try{
            Field count_f = Entity.class.getDeclaredField("entityCount");
            count_f.setAccessible(true);
            entityCount = (AtomicInteger) count_f.get(null);
        }catch (NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
        }
    }

    public static int getNextEntityId() {
        return entityCount.incrementAndGet();
    }

    //获取位位置中心点
    public static Location locationToCenter(Location loc) {
        loc.setX(loc.getBlockX() + 0.5D);
        loc.setY(loc.getBlockY() + 0.5D);
        loc.setZ(loc.getBlockZ() + 0.5D);
        return loc;
    }

    public static Object getNmsEntity(org.bukkit.entity.Entity entity) {
        try{
            return entity.getClass().getMethod("getHandle").invoke(entity);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
