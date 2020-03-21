package cn.whiteg.moeitems;

import cn.whiteg.moeitems.Listener.BreakArmourStand;
import cn.whiteg.moeitems.Listener.DebugTickListener;
import cn.whiteg.moeitems.Listener.PluginListener;
import cn.whiteg.moeitems.foods.FireBerry;
import cn.whiteg.moeitems.foods.SaltSodaWater;
import cn.whiteg.moeitems.furniture.DeskClock;
import cn.whiteg.moeitems.furniture.FlowerVase;
import cn.whiteg.moeitems.furniture.GardenLamp;
import cn.whiteg.moeitems.furniture.Scarecrow;
import cn.whiteg.moeitems.hats.*;
import cn.whiteg.moeitems.items.*;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static cn.whiteg.moeitems.Setting.reload;


public class MoeItems extends PluginBase {
    public static Logger logger;
    public static MoeItems plugin;
    public CommandManage mainCommand;
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
        regListener(new PluginListener(this));
        regListener(new BreakArmourStand());
        if (Setting.DEBUG) regListener(new DebugTickListener());
    }

    public void onDisable() {
        unregListener();
        //注销注册玩家加入服务器事件
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
        regItem(SaltSodaWater.get());
        regItem(WaterGun.get());
        regItem(GravityStaff.get());
//        regItem(new TestBow());
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
}
