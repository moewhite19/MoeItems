package cn.whiteg.moeitems.utils;

import cn.whiteg.rpgArmour.api.CustItem;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import net.minecraft.server.v1_16_R1.Entity;
import net.minecraft.server.v1_16_R1.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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
