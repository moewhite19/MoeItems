package cn.whiteg.moeitems;

import cn.whiteg.mmocore.common.CommandManage;
import cn.whiteg.moeitems.Listener.DebugTickListener;
import cn.whiteg.moeitems.Listener.PluginListener;
import cn.whiteg.moeitems.food.FireBerry;
import cn.whiteg.moeitems.food.GSoup;
import cn.whiteg.moeitems.food.LetheWater;
import cn.whiteg.moeitems.food.SaltSodaWater;
import cn.whiteg.moeitems.furniture.*;
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
        regItem(Broom.get());
        regItem(Creeper.get());
        regItem(FireBerry.get());
        regItem(CannonBall.get());
//        regItem(LightningRod.get()); //暂时不开放,1.17已有避雷针
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
        regItem(BoltHat.get());
        regItem(DemonHorn.get());
        regItem(WitchHat.get()); //暂时不知道写什么x
        regItem(Crucible.get());
        regItem(Globe.get());
        regItem(TrashCan.get());
        regItem(Chair.get());
        regItem(new Duck()); //小黄鸭
        regItem(BurstAxe.get());
        regListener(new GSoup()); //寄汤
        regItem(VirtueAxe.get());
        regItem(Panzer.get()); //坦克
        regItem(new Barber());
        regItem(MapRender.get());
        //光凌武器
        regItem(new LingEpee());
        regItem(new LingDagger());
        regItem(new LingSword());
        //腐蚀武器
        regItem(new CorrodeEpee());
        regItem(new CorrodeDagger());
        regItem(new CorrodeSword());
        regItem(new Reclamation()); //手持海绵x
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
        if(item == null) return;
        items.add(item);
        RPGArmour.plugin.getItemManager().regItem(item);
    }

    public Residence getResidence() {
        return residence;
    }
}
