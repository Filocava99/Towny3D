package it.tigierrei.towny3d.listeners;

import com.connorlinfoot.titleapi.TitleAPI;
import it.tigierrei.towny3d.Towny3D;
import it.tigierrei.towny3d.regions.Region;
import it.tigierrei.towny3d.residents.Resident;
import it.tigierrei.towny3d.towns.Town;
import it.tigierrei.towny3d.utils.Selection;
import it.tigierrei.towny3d.utils.Utils;
import it.tigierrei.towny3d.utils.Vector;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;


public class PlayerListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Region region = Towny3D.getDataManager().getRegionManager().getRegion(event.getBlock().getLocation());
        //Se il blocco rotto e' in wild termino la funzione
        if(region == null){
            return;
        }

        Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(event.getPlayer().getName());
        //Controllo se l'utente è il Owner del plot
        if(region.getOwner() != null && region.getOwner().equalsIgnoreCase(event.getPlayer().getName())){
            return;
        }
        //Controllo se il giocatore non ha una città
        if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()) == null){
            if(!region.isGen_destroy()){
                event.getPlayer().sendMessage(ChatColor.RED + "You can't break blocks in this zone!");
                event.setCancelled(true);
                return;
            }
            //Controllo se il giocatore è un cittadino della città in cui si trova
        }else if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getName().equalsIgnoreCase(region.getTown())){
            if(!region.isRes_destroy()){
                event.getPlayer().sendMessage(ChatColor.RED + "You can't break blocks in this zone!");
                event.setCancelled(true);
                return;
            }
        //Controllo se è un cittadino della nazione della città
        }else if(Towny3D.getDataManager().getTownManager().getTownList().get(region.getTown()).getNationName().equalsIgnoreCase(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getNationName())){
            if(!region.isAlly_destroy()){
                event.getPlayer().sendMessage(ChatColor.RED + "You can't break blocks in this zone!");
                event.setCancelled(true);
                return;
            }
        //Altrimenti controllo i permessi generici
        }else{
            if(!region.isGen_destroy()){
                event.getPlayer().sendMessage(ChatColor.RED + "You can't break blocks in this zone!");
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent event){
        Region region = Towny3D.getDataManager().getRegionManager().getRegion(event.getBlock().getLocation());
        //Se il blocco piazzato e' in wild termino la funzione
        if(region == null){
            return;
        }

        Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(event.getPlayer().getName());
        //Controllo se l'utente è il Owner del plot
        if(region.getOwner() != null && region.getOwner().equalsIgnoreCase(event.getPlayer().getName())){
            return;
        }
        //Controllo se il giocatore non ha una città
        if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()) == null){
            if (!region.isGen_build()) {
                event.getPlayer().sendMessage(ChatColor.RED + "You can't place blocks in this zone!");
                event.setCancelled(true);
                return;
            }
            //Controllo se il giocatore è un cittadino della città in cui si trova
        } else if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getName().equalsIgnoreCase(region.getTown())){
            if(!region.isRes_build()){
                event.getPlayer().sendMessage(ChatColor.RED + "You can't place blocks in this zone!");
                event.setCancelled(true);
                return;
            }
        //Controllo se è un cittadino della nazione della città
        }else if(Towny3D.getDataManager().getTownManager().getTownList().get(region.getTown()).getNationName().equalsIgnoreCase(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getNationName())){
            if(!region.isAlly_build()){
                event.getPlayer().sendMessage(ChatColor.RED + "You can't place blocks in this zone!");
                event.setCancelled(true);
                return;
            }
        //Altrimenti controllo i permessi generici
        }else {
            if (!region.isGen_build()) {
                event.getPlayer().sendMessage(ChatColor.RED + "You can't place blocks in this zone!");
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        //Carico il config del giocatore e nel caso non esista lo creo. Vedi ResidentManager#getResident()
        Towny3D.getDataManager().getResidentManager().getResidentList().put(event.getPlayer().getName(),Towny3D.getDataManager().getResidentManager().getResident(event.getPlayer()));
        //Mando al player le info sulla region in cui si trova
        Region region = Towny3D.getDataManager().getRegionManager().getRegion(event.getPlayer().getLocation());
        if(region == null){
            TitleAPI.sendTitle(event.getPlayer(),20,40,20,ChatColor.GOLD + "Wildlands",ChatColor.YELLOW + "Be careful!");
        }else {
            TitleAPI.sendTitle(event.getPlayer(), 20, 40, 20, ChatColor.GOLD + region.getTown(), ChatColor.YELLOW + "Nation name - WIP");
            event.getPlayer().sendMessage(ChatColor.GREEN + "[*]Owner: " + ChatColor.AQUA + ((region.getOwner() != null) ? region.getOwner() : "Noone") + ((region.isFor_sale()) ? ChatColor.RESET + " | " + ChatColor.DARK_PURPLE + "For sale: " + ChatColor.LIGHT_PURPLE + region.getValue() + "$" : ""));
        }
    }



    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        //Rimuovo il giocatore dalla lista dei Resident online
        Towny3D.getDataManager().getResidentManager().getResidentList().remove(event.getPlayer().getName());
    }

    @EventHandler
    public void onItemPickedUp(EntityPickupItemEvent event){
        Region region = Towny3D.getDataManager().getRegionManager().getRegion(event.getEntity().getLocation());
        //Se l'oggetto raccolta era in wild termino la funzione
        if(region == null){
            return;
        }

        //Nel caso un'entity provi a raccogliere un oggetto (tipo uno zombie) ma non ha i permessi generali per farlo, allora annullo l'evento, altrimenti
        //sempre nel caso in cui l'entity non sia un player termino la funzione
        if(event.getEntity().getType() != EntityType.PLAYER){
            if(!region.isGen_pick()) {
                event.setCancelled(true);
            }
            return;
        }
        Player player = Bukkit.getPlayer(event.getEntity().getUniqueId());
        Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(player.getName());
        //Controllo se l'utente è il Owner del plot
        if(region.getOwner() != null && region.getOwner().equalsIgnoreCase(player.getName())){
            return;
        }
        //Controllo se il giocatore non ha una città
        if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()) == null){
            if(!region.isGen_pick()){
                player.sendMessage(ChatColor.RED + "You can't pick up items in this zone!");
                event.setCancelled(true);
                return;
            }
            //Controllo se il giocatore è un cittadino della città in cui si trova
        }else if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()) != null && Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getName().equalsIgnoreCase(region.getTown())){
            if(!region.isRes_pick()){
               player.sendMessage(ChatColor.RED + "You can't pick up items in this zone!");
                event.setCancelled(true);
                return;
            }
        //Controllo se è un cittadino della nazione della città
        }else if(Towny3D.getDataManager().getTownManager().getTownList().get(region.getTown()).getNationName().equalsIgnoreCase(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getNationName())){
            if(!region.isAlly_pick()){
                player.sendMessage(ChatColor.RED + "You can't pick up items in this zone!");
                event.setCancelled(true);
                return;
            }
        //Altrimenti controllo i permessi generici
        }else{
            if(!region.isGen_pick()){
                player.sendMessage(ChatColor.RED + "You can't pick up items in this zone!");
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onItemDropped(PlayerDropItemEvent event){
        Region region = Towny3D.getDataManager().getRegionManager().getRegion(event.getPlayer().getLocation());
        //Se l'oggetto raccolta era in wild termino la funzione
        if(region == null){
            return;
        }

        Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(event.getPlayer().getName());
        //Controllo se l'utente è il Owner del plot
        if(region.getOwner() != null && region.getOwner().equalsIgnoreCase(event.getPlayer().getName())){
            return;
        }
        //Controllo se il giocatore non ha una città
        if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()) == null){
            if(!region.isGen_drop()){
                event.getPlayer().sendMessage(ChatColor.RED + "You can't drop items in this zone!");
                event.setCancelled(true);
                return;
            }
            //Controllo se il giocatore è un cittadino della città in cui si trova
        }else if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()) != null && Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getName().equalsIgnoreCase(region.getTown())){
            if(!region.isRes_drop()){
                event.getPlayer().sendMessage(ChatColor.RED + "You can't drop items in this zone!");
                event.setCancelled(true);
                return;
            }
            //Controllo se è un cittadino della nazione della città
        }else if(Towny3D.getDataManager().getTownManager().getTownList().get(region.getTown()).getNationName().equalsIgnoreCase(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getNationName())){
            if(!region.isAlly_drop()){
                event.getPlayer().sendMessage(ChatColor.RED + "You can't drop items in this zone!");
                event.setCancelled(true);
                return;
            }
            //Altrimenti controllo i permessi generici
        }else{
            if(!region.isGen_drop()){
                event.getPlayer().sendMessage(ChatColor.RED + "You can't drop items in this zone!");
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerInteraction(PlayerInteractEvent event){
        //Se l'utente non ha clickato nulla
        if(event.getClickedBlock() == null){
            return;
        }
        Region region = Towny3D.getDataManager().getRegionManager().getRegion(event.getClickedBlock().getLocation());
        if(region != null) {
            Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(event.getPlayer().getName());
            //Se l'utente sta cercando di aprire un inventario
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getState() instanceof InventoryHolder){
                //Controllo se l'utente è il Owner del plot
                if(region.getOwner() != null && region.getOwner().equalsIgnoreCase(event.getPlayer().getName())){
                    return;
                }
                //Controllo se il giocatore non ha una città
                if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()) == null){
                    if(!region.isGen_inventory()){
                        event.getPlayer().sendMessage(ChatColor.RED + "You can't open this inventory!");
                        event.setCancelled(true);
                        return;
                    }
                    //Controllo se il giocatore è un cittadino della città in cui si trova
                }else if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()) != null && Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getName().equalsIgnoreCase(region.getTown())){
                    if(!region.isRes_inventory()){
                        event.getPlayer().sendMessage(ChatColor.RED + "You can't open this inventory!");
                        event.setCancelled(true);
                        return;
                    }
                //Controllo se è un cittadino della nazione della città
                }else if(Towny3D.getDataManager().getTownManager().getTownList().get(region.getTown()).getNationName().equalsIgnoreCase(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getNationName())){
                    if(!region.isAlly_inventory()){
                        event.getPlayer().sendMessage(ChatColor.RED + "You can't open this inventory!");
                        event.setCancelled(true);
                        return;
                    }
                //Altrimenti controllo i permessi generici
                }else{
                    if(!region.isGen_inventory()){
                        event.getPlayer().sendMessage(ChatColor.RED + "You can't open this inventory!");
                        event.setCancelled(true);
                        return;
                    }
                }
            //Controllo che stia interagento con un blocco interagibile
            }else if(event.getAction() == Action.RIGHT_CLICK_BLOCK && Utils.getInteractables().contains(event.getClickedBlock().getType())){
                //Controllo se l'utente è il Owner del plot
                if(region.getOwner() != null && region.getOwner().equalsIgnoreCase(event.getPlayer().getName())){
                    return;
                }
                //Controllo se il giocatore non ha una città
                if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()) == null){
                    if(!region.isGen_interact()){
                        event.getPlayer().sendMessage(ChatColor.RED + "You can't interact with this block!");
                        event.setCancelled(true);
                        return;
                    }
                    //Controllo se il giocatore è un cittadino della città in cui si trova
                }else if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getName().equalsIgnoreCase(region.getTown())){
                    if(!region.isRes_interact()){
                        event.getPlayer().sendMessage(ChatColor.RED + "You can't interact with this block!");
                        event.setCancelled(true);
                        return;
                    }
                //Controllo se è un cittadino della nazione della città
                }else if(Towny3D.getDataManager().getTownManager().getTownList().get(region.getTown()).getNationName().equalsIgnoreCase(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getNationName())){
                    if(!region.isAlly_interact()){
                        event.getPlayer().sendMessage(ChatColor.RED + "You can't interact with this block!");
                        event.setCancelled(true);
                        return;
                    }
                //Altrimenti controllo i permessi generici
                }else{
                    if(!region.isGen_interact()){
                        event.getPlayer().sendMessage(ChatColor.RED + "You can't interact with this block!");
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
        //Se arrivo qua' verifico che abbia un oggetto in mano per poi verificare se si tratti del Claimer Stick
        if(event.getItem() == null){
            return;
        }
        //Se l'oggetto non ha un ItemMeta o non ha un displayName termino la funzione
        if(event.getItem().getItemMeta() == null || event.getItem().getItemMeta().getDisplayName() == null){
            return;
        }
        //Claim stick control
        if(event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.DARK_PURPLE + "Claimer Stick")){
            //Left click on block
            if(event.getAction() == Action.LEFT_CLICK_BLOCK){
                HashMap<Player, Selection> playerSelectionHashMap =  Towny3D.getDataManager().getRegionManager().getPlayerSelectionHashMap();
                Selection selection;
                if(!playerSelectionHashMap.containsKey(event.getPlayer())){
                    playerSelectionHashMap.put(event.getPlayer(),new Selection());
                }
                selection = playerSelectionHashMap.get(event.getPlayer());
                selection.setVector1(new Vector(event.getClickedBlock().getX(),event.getClickedBlock().getY(),event.getClickedBlock().getZ()));
                event.getPlayer().sendMessage(ChatColor.YELLOW + "First position set!");
                event.setCancelled(true);
            //Right click on block
            }else if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
                HashMap<Player, Selection> playerSelectionHashMap =  Towny3D.getDataManager().getRegionManager().getPlayerSelectionHashMap();
                Selection selection;
                if(!playerSelectionHashMap.containsKey(event.getPlayer())){
                    playerSelectionHashMap.put(event.getPlayer(),new Selection());
                }
                selection = playerSelectionHashMap.get(event.getPlayer());
                selection.setVector2(new Vector(event.getClickedBlock().getX(),event.getClickedBlock().getY(),event.getClickedBlock().getZ()));
                event.getPlayer().sendMessage(ChatColor.YELLOW + "Second position set!");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){

        if(event.getFrom().equals(event.getTo())){
            return;
        }

        Region region = Towny3D.getDataManager().getRegionManager().getRegion(event.getTo());
        Region prevRegion = Towny3D.getDataManager().getRegionManager().getRegion(event.getFrom());

        //Se il giocatore si sta spostando nel wild termino la funzione
        if(prevRegion == null && region == null) {
            return;
        }
        //Il giocatore sta entrando nel wild
        if(prevRegion != null && region == null){
            TitleAPI.sendTitle(event.getPlayer(),20,40,20,ChatColor.GOLD + "Wildlands",ChatColor.YELLOW + "Be careful!");
            return;
        }

        Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(event.getPlayer().getName());

        //Controllo se l'utente è il Owner del plot
        if(region.getOwner() == null || !region.getOwner().equalsIgnoreCase(event.getPlayer().getName())){
            //Controllo se il giocatore non ha una città
            if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()) == null){
                if(!region.isGen_move()){
                    event.getPlayer().sendMessage(ChatColor.RED + "You can't access this zone!");
                    event.setCancelled(true);
                }
                //Controllo se il giocatore è un cittadino della città in cui si trova
            }else if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getName().equalsIgnoreCase(region.getTown())){
                if(!region.isRes_move()){
                    event.getPlayer().sendMessage(ChatColor.RED + "You can't access this zone!");
                    event.setCancelled(true);
                    return;
                }
                //Controllo se è un cittadino della nazione della città
            }else if(Towny3D.getDataManager().getTownManager().getTownList().get(region.getTown()).getNationName() != null && Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getNationName() != null && Towny3D.getDataManager().getTownManager().getTownList().get(region.getTown()).getNationName().equalsIgnoreCase(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getNationName())){
                if(!region.isAlly_move()){
                    event.getPlayer().sendMessage(ChatColor.RED + "You can't access this zone!");
                    event.setCancelled(true);
                    return;
                }
                //Altrimenti controllo i permessi generici
            }else{
                if(!region.isGen_move()){
                    event.getPlayer().sendMessage(ChatColor.RED + "You can't access this zone!");
                    event.setCancelled(true);
                    return;
                }
            }
        }

        //Se il giocatore si sposta dal wild o da una città in un altra gli invio il nome della città
        if(prevRegion == null && region != null){
            TitleAPI.sendTitle(event.getPlayer(),20,40,20,ChatColor.GOLD + region.getTown(),ChatColor.YELLOW + "Nation name - WIP");
            event.getPlayer().sendMessage(ChatColor.GREEN + "[*]Owner: " + ChatColor.AQUA + ((region.getOwner() != null) ? region.getOwner() : "Noone") + ((region.isFor_sale()) ? ChatColor.RESET + " | " + ChatColor.DARK_PURPLE + "For sale: " + ChatColor.LIGHT_PURPLE + region.getValue() + "$" : ""));
            return;
        }
        if(!(prevRegion.getTown().equalsIgnoreCase(region.getTown()))){
            TitleAPI.sendTitle(event.getPlayer(),20,40,20,ChatColor.GOLD + region.getTown(),ChatColor.YELLOW + "Nation name - WIP");
            event.getPlayer().sendMessage(ChatColor.GREEN + "[*]Owner: " + ChatColor.AQUA + ((region.getOwner() != null) ? region.getOwner() : "Noone") + ((region.isFor_sale()) ? ChatColor.RESET + " | " + ChatColor. DARK_PURPLE +"For sale: " + ChatColor.LIGHT_PURPLE + region.getValue() + "$" : ""));
            return;
        }
        //Se siamo quà vuol dire che il giocatore si sta spostando all'interno della stessa città
        int fromChunkX = event.getFrom().getChunk().getX()*16;
        int fromChunkZ = event.getFrom().getChunk().getZ()*16;
        int toChunkX = event.getTo().getChunk().getX()*16;
        int toChunkZ = event.getTo().getChunk().getZ()*16;
        //Se arriviamo quì è perchè il giocatore si sposta all'interno dello stesso plot nella stessa città e termino la funzione
        if(fromChunkX == toChunkX && fromChunkZ == toChunkZ && prevRegion.getY1() == region.getY1() && prevRegion.getY2() == region.getY2()){
            return;
        //Se il giocatore si sposta in un plot diverso all'interno della città lo avviso
        }else{
            event.getPlayer().sendMessage(ChatColor.GREEN + "[*]Owner: " + ChatColor.AQUA + ((region.getOwner() != null) ? region.getOwner() : "Noone") + ((region.isFor_sale()) ? ChatColor.RESET + " | " + ChatColor. DARK_PURPLE +"For sale: " + ChatColor.LIGHT_PURPLE + region.getValue() + "$" : ""));
        }
    }

    @EventHandler
    public void onPlayerDamagedByPlayer(EntityDamageByEntityEvent event){
        Region region = Towny3D.getDataManager().getRegionManager().getRegion(event.getEntity().getLocation());
        if(region == null){
            return;
        }
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
            Resident damager = Towny3D.getDataManager().getResidentManager().getResidentList().get(((Player)event.getDamager()).getName());
            Resident damaged = Towny3D.getDataManager().getResidentManager().getResidentList().get(((Player)event.getEntity()).getName());
            //Controllo se il giocatore non ha una città
            if(damaged.getTown() == null || damager.getTown() == null){
                if(!region.isGen_pvp()){
                    event.setCancelled(true);
                }
                return;
            }
            //Controllo se sono concittadini
            if(Towny3D.getDataManager().getTownManager().getTownList().get(damaged.getTown()).getName().equalsIgnoreCase(Towny3D.getDataManager().getTownManager().getTownList().get(damager.getTown()).getName())){
                if(!region.isRes_pvp()){
                    event.setCancelled(true);
                }
            //Controllo se sono connazionali
            }else if(Towny3D.getDataManager().getTownManager().getTownList().get(damaged.getTown()).getNationName().equalsIgnoreCase(Towny3D.getDataManager().getTownManager().getTownList().get(damager.getTown()).getNationName())){
                if(!region.isAlly_pvp()){
                    event.setCancelled(true);
                }
            //Altrimenti applico i permessi generali
            }else{
                if(!region.isGen_pvp()){
                    event.setCancelled(true);
                }
            }
        }
    }


    //Teleports player to his town home if he does not have a bed spawn location set
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();
        if(player.getBedSpawnLocation() == null){
            Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(player.getName());
            if(resident.getTown() != null){
                Town town = Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown());
                player.teleport(town.getHome());
            }
        }
    }
}
