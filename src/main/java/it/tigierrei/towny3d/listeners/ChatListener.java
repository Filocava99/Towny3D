package it.tigierrei.towny3d.listeners;

import it.tigierrei.towny3d.Towny3D;
import it.tigierrei.towny3d.residents.Resident;
import it.tigierrei.towny3d.towns.Town;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Iterator;

public class ChatListener implements Listener {

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event){
        //If a player is the direct cause of this event by an incoming packet, this event will be asynchronous. If a plugin triggers this event by compelling a player to chat, this event will be synchronous.
        Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(event.getPlayer().getName());
        Town town = Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown());

        if(resident.getChat().equalsIgnoreCase("GL") || town == null){
            return;
        }

        //TODO Titoli cittadini e nazionali
        if(resident.getChat().equalsIgnoreCase("tc")){
            event.setFormat("["+ ChatColor.AQUA+"TC"+ ChatColor.WHITE + "]" + (town.getMayorName().equalsIgnoreCase(resident.getName()) ? ChatColor.AQUA : "") + event.getPlayer().getDisplayName() + ChatColor.WHITE + ": " + ChatColor.AQUA + event.getMessage());
        }else{
            event.setFormat("["+ ChatColor.GOLD+"NC"+ ChatColor.WHITE + "]" + (town.getMayorName().equalsIgnoreCase(resident.getName()) ? ChatColor.GOLD : "") + event.getPlayer().getDisplayName() + ChatColor.WHITE + ": " + ChatColor.GOLD + event.getMessage());
        }
        Iterator iterator = event.getRecipients().iterator();
        while(iterator.hasNext()){
            Player p = (Player) iterator.next();
            Resident receiver = Towny3D.getDataManager().getResidentManager().getResidentList().get(p.getName());
            //Controllo
            if((resident.getChat().equalsIgnoreCase("tc") && (!resident.getTown().equalsIgnoreCase(receiver.getTown())) && !receiver.isSpyChat())){
                event.getRecipients().remove(p);
            }else if(town.getNationName() != null){
                Town receiverTown = Towny3D.getDataManager().getTownManager().getTownList().get(receiver.getTown());
                if(resident.getChat().equalsIgnoreCase("nc") && (!town.getNationName().equalsIgnoreCase((receiverTown.getNationName() == null) ? "" : receiverTown.getNationName()) && receiver.isSpyChat())){
                    event.getRecipients().remove(p);
                }
            }
        }
    }

}
