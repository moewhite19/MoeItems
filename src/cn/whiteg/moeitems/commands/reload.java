package cn.whiteg.moeitems.commands;

import cn.whiteg.mmocore.common.HasCommandInterface;
import cn.whiteg.moeitems.MoeItems;
import cn.whiteg.rpgArmour.RPGArmour;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class reload extends HasCommandInterface {

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        MoeItems.plugin.onReload();
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }
}
