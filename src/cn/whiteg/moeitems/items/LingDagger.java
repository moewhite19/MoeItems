package cn.whiteg.moeitems.items;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LingDagger extends DaggerAbs {
    private final static PotionEffect EFFECT = new PotionEffect(PotionEffectType.GLOWING,100,1,false,false,false);

    public LingDagger() {
        super(1,"§b§l光棱匕首",6);
    }

    @Override
    public void onDamage(Entity entity,Entity damager,ItemStack item) {
        if (entity instanceof LivingEntity living){
            living.addPotionEffect(EFFECT);
        }
    }
}
