package cn.whiteg.moeitems;

import cn.whiteg.moeitems.Listener.BreakArmourStand;
import cn.whiteg.moeitems.Listener.PluginListener;
import cn.whiteg.moeitems.furniture.DeskClock;
import cn.whiteg.moeitems.furniture.FlowerVase;
import cn.whiteg.moeitems.furniture.GardenLamp;
import cn.whiteg.moeitems.furniture.Scarecrow;
import cn.whiteg.moeitems.hats.*;
import cn.whiteg.moeitems.items.*;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Logger;

import static cn.whiteg.moeitems.Setting.reload;


public class MoeItems extends JavaPlugin {
    public static Logger logger;
    public static MoeItems plugin;
    public CommandManage mainCommand;
    public Map<String, Listener> listenerMap = new HashMap<>();
    private List<CustItem> items = new ArrayList<>();


    public MoeItems() {
        plugin = this;
    }

    public void onLoad() {
        saveDefaultConfig();
        logger = getLogger();
        reload();
    }

    public void onEnable() {
        logger.info("开始加载插件");
        if (Setting.DEBUG) logger.info("§a调试模式已开启");
        mainCommand = new CommandManage();
        getCommand("moeitems").setExecutor(mainCommand);
        logger.info("全部加载完成");
        initItems();
        regEven(new PluginListener(this));
        regEven(new BreakArmourStand());
    }

    public void onDisable() {
        unregEven();
        //注销注册玩家加入服务器事件
        listenerMap.clear();
        clearItems();
        logger.info("插件已关闭");
    }

    public void onReload() {
        logger.info("--开始重载--");
        reload();
        logger.info("--重载完成--");
    }

    public void initItems() {
        regItem(DeskClock.get());
        regItem(FlowerVase.get());
        regItem(Scarecrow.get());
        regItem(ChristmasHat.get());
        regItem(StrawHat.get());
        regItem(CatEarWhite.get());
        regItem(CatEarDiamond.get());
        regItem(CatEarGolden.get());
        regItem(GardenLamp.get());
        regItem(Artillery.get());
        regItem(Broom.get());
        regItem(Creeper.get());
        regItem(FireBerry.get());
        regItem(CherryBomb.get());
        regItem(LightningRod.get());
        regItem(BigIvan.get());
//        regItem(PhamtomKiller.get());
    }

    public List<CustItem> getItems() {
        return items;
    }

    public void clearItems() {
        for (CustItem ca : items) {
            RPGArmour.plugin.getItemManager().unregItem(ca);
        }
        items.clear();
    }

    public void regItem(CustItem item) {
        items.add(item);
        RPGArmour.plugin.getItemManager().regItem(item);
    }


    public void regEven(Listener listener) {
        regEven(listener.getClass().getName(),listener);
    }

    public void regEven(String key,Listener listener) {
        listenerMap.put(key,listener);
        Bukkit.getPluginManager().registerEvents(listener,plugin);
    }

    public void unregEven() {
        Iterator<Map.Entry<String, Listener>> it = listenerMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Listener> set = it.next();
            Listener listener = set.getValue();
            if (listener != null){
                unregListener(listener);
            }
        }
    }

    /**
     * 卸载事件
     *
     * @param Key "卸载"
     */
    public void unregEven(String Key) {
        Listener listenr = listenerMap.remove(Key);
        if (listenr == null){
            return;
        }
        unregListener(listenr);
    }

    public void unregListener(Listener listener) {
        //注销事件
        Class listenerClass = listener.getClass();
        try{
            for (Method method : listenerClass.getMethods()) {
                if (method.isAnnotationPresent(EventHandler.class)){
                    Type[] tpyes = method.getGenericParameterTypes();
                    if (tpyes.length == 1){
                        Class<?> tc = Class.forName(tpyes[0].getTypeName());
                        Method tm = tc.getMethod("getHandlerList");
                        HandlerList handlerList = (HandlerList) tm.invoke(null);
                        handlerList.unregister(listener);
                    }
                }
            }
        }catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e){
            e.printStackTrace();
        }

        //调用类中的unreg()方法
        try{
            Method unreg = listenerClass.getDeclaredMethod("unreg");
            unreg.setAccessible(true);
            unreg.invoke(listener);
        }catch (Exception ignored){

        }
    }
}
