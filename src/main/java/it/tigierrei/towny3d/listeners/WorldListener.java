package it.tigierrei.towny3d.listeners;

import it.tigierrei.towny3d.Towny3D;
import it.tigierrei.towny3d.regions.Region;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;

public class WorldListener implements Listener {

    @EventHandler
    public void onFireSpread(BlockIgniteEvent event){
        Region region = Towny3D.getDataManager().getRegionManager().getRegion(event.getBlock().getLocation());
        //Impedisco che il fuoco si diffonda nel wild
        if(region == null){
            event.setCancelled(true);
            return;
        }
        //Se il fuoco e' disabilitato nella region annullo l'evento e termino la funzione
        if(!region.isFire()){
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onExplosion(BlockExplodeEvent event){
        Region region = Towny3D.getDataManager().getRegionManager().getRegion(event.getBlock().getLocation());
        //TODO Config per l'esplosione in wild
        //Se l'esplosione è in wild o se è disabilitata nella region termino la funzione ed annullo l'evento
        if(region == null || !region.isExplosion()){
            event.setCancelled(true);
        }
    }
}
