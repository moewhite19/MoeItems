package cn.whiteg.moeitems.items;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LingSword extends NormalSwordAbs {

    private final PotionEffect EFFECT = new PotionEffect(PotionEffectType.GLOWING,120,1,false,false,false);

    public LingSword() {
        super(1,"§b§l光棱",1);
    }
    @Override
    public void onDamage(Entity entity,Entity damager,ItemStack item) {
        if (entity instanceof LivingEntity living){
            living.addPotionEffect(EFFECT);
        }
    }
}
