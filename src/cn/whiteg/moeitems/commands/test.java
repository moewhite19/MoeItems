package cn.whiteg.moeitems.commands;

import cn.whiteg.mmocore.common.HasCommandInterface;
import cn.whiteg.rpgArmour.RPGArmour;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class test extends HasCommandInterface {

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1){
            try{
                if (sender instanceof Player){
                    Player player = (Player) sender;
                    float num = Float.parseFloat(args[0]);
                    Arrow arrow = player.getWorld().spawnArrow(player.getLocation(),new Vector(),0F,0F);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (arrow.isDead() || arrow.isInBlock()){
                                cancel();
                                return;
                            }
                            var av = arrow.getVelocity();
                            av.setY(av.getY() + num);
                            arrow.setVelocity(av);
                        }
                    }.runTaskTimer(RPGArmour.plugin,1L,1L);
                }

            }catch (NumberFormatException e){
                sender.sendMessage(e.getMessage());
            }
        }
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
