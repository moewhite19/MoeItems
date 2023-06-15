package cn.whiteg.moeitems.items;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CorrodeEpee extends EpeeAbs {

    private final PotionEffect EFFECT = new PotionEffect(PotionEffectType.POISON,100,2,false,false,false);

    public CorrodeEpee() {
        super(4,"§2§l腐蚀巨刃",3);
    }

    @Override
    public void onDamage(Entity entity,Entity damager,ItemStack item) {
        if (entity instanceof LivingEntity living){
            living.addPotionEffect(EFFECT);
        }
    }
}
