package it.tigierrei.towny3d.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TownCreationEvent extends Event implements Cancellable {

    HandlerList handlerList = new HandlerList();
    private boolean cancelled = false;

    private Player creator;
    private String name;

    public TownCreationEvent(Player creator, String name){
        this.creator = creator;
        this.name = name;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public Player getCreator(){
        return creator;
    }

    public String getTownName(){
        return name;
    }
}
