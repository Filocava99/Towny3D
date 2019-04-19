package it.tigierrei.towny3d.managers;

import it.tigierrei.towny3d.Towny3D;
import it.tigierrei.towny3d.towns.Town;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class RequestManager {
    private Towny3D pl;
    private HashMap<String,Town> inviteRequestHashMap = new HashMap<String, Town>();

    public RequestManager(Towny3D pl) {
        this.pl = pl;
    }

    public HashMap<String, Town> getInviteRequestHashMap() {
        return inviteRequestHashMap;
    }

    public void addInviteRequest(Town town, final Player player){
        inviteRequestHashMap.put(player.getName(),town);

        //Dopo 60 secondi rimuovo la richiesta dalla lista (thread asincrono)
        Bukkit.getScheduler().runTaskLaterAsynchronously(pl, new Runnable() {
            @Override
            public void run() {
                inviteRequestHashMap.remove(player.getName());
            }
        },20*60);
    }

    public boolean existInviteRequest(String playerName){
        if(inviteRequestHashMap.containsKey(playerName))
            return true;
        else
            return false;
    }

    public void inviteRequestResponse(Player player,boolean response){
        //Controllo se esiste una richiesta in attesa
        if(Towny3D.getDataManager().getRequestManager().existInviteRequest(player.getName())){
            Town town = inviteRequestHashMap.remove(player.getName());
            if(response){
                Towny3D.getDataManager().getTownManager().addTownResident(town,player.getName());
                Towny3D.getDataManager().getResidentManager().setResidentTown(Towny3D.getDataManager().getResidentManager().getResidentList().get(player.getName()),town.getName());
                player.sendMessage(ChatColor.GREEN + "Hai accettato l'invito della città " + town.getName());
            }else{
                player.sendMessage(ChatColor.YELLOW + "Hai rifiutato l'invito della città " + town.getName());
            }
        }else{
            player.sendMessage(ChatColor.RED + "Non hai alcuna richiesta in attesa!");
        }
    }
}
