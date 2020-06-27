package cn.whiteg.moeitems.utils;

import net.minecraft.server.v1_16_R1.Entity;
import net.minecraft.server.v1_16_R1.EntityArmorStand;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;

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



    private Object getNmsEntity(org.bukkit.entity.Entity entity) {
        try{
            return entity.getClass().getMethod("getHandle").invoke(entity);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
