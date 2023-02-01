package cn.whiteg.moeitems.items;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CorrodeDagger extends DaggerAbs {

    private final PotionEffect EFFECT = new PotionEffect(PotionEffectType.POISON,50,4,false,false,false);

    public CorrodeDagger() {
        super(1,"§2§l腐蚀匕首",3);
    }

    @Override
    public void onDamage(Entity entity,Entity damager,ItemStack item) {
        if (entity instanceof LivingEntity living){
            living.addPotionEffect(EFFECT);
        }
    }
}
