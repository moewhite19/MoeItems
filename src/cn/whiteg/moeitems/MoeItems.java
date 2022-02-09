package cn.whiteg.moeitems;

import cn.whiteg.mmocore.common.CommandManage;
import cn.whiteg.moeitems.Listener.BreakEntityItem;
import cn.whiteg.moeitems.Listener.DebugTickListener;
import cn.whiteg.moeitems.Listener.PluginListener;
import cn.whiteg.moeitems.foods.FireBerry;
import cn.whiteg.moeitems.foods.GSoup;
import cn.whiteg.moeitems.foods.LetheWater;
import cn.whiteg.moeitems.foods.SaltSodaWater;
import cn.whiteg.moeitems.furniture.DeskClock;
import cn.whiteg.moeitems.furniture.FlowerVase;
import cn.whiteg.moeitems.furniture.GardenLamp;
import cn.whiteg.moeitems.furniture.Scarecrow;
import cn.whiteg.moeitems.hats.*;
import cn.whiteg.moeitems.items.*;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem;
import com.bekvon.bukkit.residence.Residence;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static cn.whiteg.moeitems.Setting.reload;


public class MoeItems extends PluginBase {
    public static Logger logger;
    public static MoeItems plugin;
    private final List<CustItem> items = new ArrayList<>();
    public CommandManage mainCommand;
    private Residence residence = null;


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
        mainCommand = new CommandManage(this);
        mainCommand.setExecutor();
        initItems();
        regListener(new PluginListener(this));
        regListener(new BreakEntityItem());
        Plugin pl = Bukkit.getPluginManager().getPlugin("Residence");
        if (pl != null){
            residence = (Residence) pl;
        }
        logger.info("全部加载完成");
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
        RPGArmour.plugin.getRecipeManage().onSync();
        logger.info("--重载完成--");
    }

    public void initItems() {
        regItem(DeskClock.get());
        regItem(FlowerVase.get());
        regItem(Scarecrow.get()); //稻草人
        regItem(ChristmasHat.get());
        regItem(StrawHat.get());
        regItem(CatEarWhite.get());
        regItem(CatEarDiamond.get());
        regItem(CatEarGolden.get());
        regItem(GardenLamp.get());
        regItem(Artillery.get());
//        regItem(Broom.get());
        regItem(Creeper.get());
        regItem(FireBerry.get());
        regItem(CannonBall.get());
//        regItem(LightningRod.get());
        regItem(BigIvan.get());
        regItem(SaltSodaWater.get());
        regItem(WaterGun.get());
        regItem(GravityStaff.get());
        regItem(Wrench.get());
        regItem(BurstPickaxe.get());
        regItem(Ushanka.get());
        regItem(QuickFiringCrossbow.get());
        regItem(LetheWater.get());
        regItem(ConfusedStaff.get());
        regItem(BunnyEar.get());
        regItem(ShulkerBull.get());
        regItem(FoxEar.get());
        regItem(SuperLeash.get());
        regItem(LeashBow.get());
        regItem(KitsuneMasks.get());
        regItem(BlindBox.get());
        regItem(new Duck()); //小黄鸭
        regListener(new GSoup()); //寄汤
//        regItem(GuisePotion.get());
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

    public Residence getResidence() {
        return residence;
    }
}
