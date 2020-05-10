package cn.whiteg.moeitems;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class PluginBase extends JavaPlugin {
    private final Map<String, Listener> listenerMap = new HashMap<>();

    public void regListener(Listener listener) {
        regListener(listener.getClass().getName(),listener);

    }

    public void regListener(String key,Listener listener) {
//        getLogger().info("注册事件:" + key);
        listenerMap.put(key,listener);
        Bukkit.getPluginManager().registerEvents(listener,this);

    }

    public void unregListener() {
        for (Map.Entry<String, Listener> entry : listenerMap.entrySet()) {
            unregListener(entry.getValue());
        }
        listenerMap.clear();
    }


    /**
     * 卸载事件
     *
     * @param Key "卸载"
     */
    public boolean unregListener(String Key) {
        Listener listenr = listenerMap.remove(Key);
        if (listenr == null){
            return false;
        }
        unregListener(listenr);
        return true;
    }

    public void unregListener(Listener listener) {
        //注销事件
        HandlerList.unregisterAll(listener);
        //旧的事件
//        try{
//            for (Method method : listenerClass.getMethods()) {
//                if (method.isAnnotationPresent(EventHandler.class)){
//                    try{
//                        Type[] tpyes = method.getGenericParameterTypes();
//                        if (tpyes.length == 1){
//                            Class<?> tc = Class.forName(tpyes[0].getTypeName());
//                            Method tm = tc.getMethod("getHandlerList");
//                            HandlerList handlerList = (HandlerList) tm.invoke(null);
//                            handlerList.unregister(listener);
//                        }
//                    }catch (ClassNotFoundException | NoClassDefFoundError e){
//                    }
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        //调用类中的unreg()方法
        try{
            Class listenerClass = listener.getClass();
            Method unreg = listenerClass.getDeclaredMethod("unreg");
            unreg.setAccessible(true);
            unreg.invoke(listener);
        }catch (Throwable ignored){
        }
    }

    public Map<String, Listener> getListenerMap() {
        return listenerMap;
    }
}
