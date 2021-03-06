package cn.whiteg.moeitems.Event;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class BreakCustItemEntity extends EntityEvent implements Cancellable {
    private static final HandlerList handers = new HandlerList();
    private boolean cancelled = false;
    private Entity damager;

    public BreakCustItemEntity(Entity entity) {
        super(entity);
    }

    public BreakCustItemEntity(Entity entity,Entity damager) {
        super(entity);
        this.damager = damager;
    }

    public static HandlerList getHandlerList() {
        return handers;
    }

    @Override
    public HandlerList getHandlers() {
        return handers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    public Entity getDamager() {
        return damager;
    }
}
