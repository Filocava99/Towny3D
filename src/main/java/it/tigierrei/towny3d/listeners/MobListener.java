package it.tigierrei.towny3d.listeners;

import it.tigierrei.towny3d.Towny3D;
import it.tigierrei.towny3d.regions.Region;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class MobListener implements Listener {

    @EventHandler
    public void onMobSpawn(EntitySpawnEvent event){
        Region region = Towny3D.getDataManager().getRegionManager().getRegion(event.getLocation());
        //Se la region è in wild allora termino al funzione
        if(region == null){
            return;
        }
        //Controllo se è un mob ostile
        if(event.getEntity() instanceof Monster){
            if(!region.isHostile_mobs_spawn()){
                event.setCancelled(true);
            }
        //oppure se è un mob pacifico
        }else{
            if(!region.isFriendly_mobs_spawn()){
                event.setCancelled(true);
            }
        }
    }


    //TODO Config per l'esplosione in wild

    @EventHandler
    public void onEntityDecisionToExplode(ExplosionPrimeEvent event){
        Region region = Towny3D.getDataManager().getRegionManager().getRegion(event.getEntity().getLocation());
        if(region == null || !region.isExplosion()){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event){
        Region region = Towny3D.getDataManager().getRegionManager().getRegion(event.getLocation());
        if(region == null || !region.isExplosion()){
            event.setCancelled(true);
        }
    }
}
