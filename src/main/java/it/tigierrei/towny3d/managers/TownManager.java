package it.tigierrei.towny3d.managers;

import it.tigierrei.configapi.ConfigFile;
import it.tigierrei.towny3d.Towny3D;
import it.tigierrei.towny3d.residents.Resident;
import it.tigierrei.towny3d.towns.Town;
import it.tigierrei.towny3d.utils.Formatter;
import it.tigierrei.towny3d.utils.Selection;
import it.tigierrei.towny3d.utils.Vector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

public class TownManager {

    private Towny3D pl;
    private HashMap<String,Town> townList = new HashMap<String, Town>();

    public TownManager(Towny3D pl){
        this.pl = pl;
        File townDir = new File(pl.getDataFolder().getPath()+"/towns");
        if(!townDir.exists()){
            townDir.mkdirs();
        }
        for(String fileName : townDir.list()){
            String townName = fileName.split(Pattern.quote("."))[0];
            townList.put(townName,getTown(townName));
        }
    }

    public HashMap<String, Town> getTownList() {
        return townList;
    }

    public void createTown(Player player, String townName){
        ConfigFile townConfig = new ConfigFile(Formatter.getTownPath(pl.getDataFolder().getPath(),townName),false);
        Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(player.getName());

        UUID townUUID = UUID.randomUUID();
        townConfig.set("name",townName);
        townConfig.set("uuid", townUUID.toString());
        townConfig.set("nation-name",null);
        townConfig.set("mayor",player.getName());
        townConfig.set("mayor-uuid",player.getUniqueId().toString());
        townConfig.set("assistants",new ArrayList<String>());
        townConfig.set("residents", Collections.singletonList(player.getName()));
        //Inizializzo la città con 2560$ perchè poi quando crea il primo plot in automatico glieli scala
        townConfig.set("balance",256*(Double)Towny3D.getDataManager().getPluginConfig().get("layer-cost"));
        townConfig.set("layers",256);
        townConfig.set("home-location", player.getLocation().getBlockX()+"."+player.getLocation().getBlockY()+"."+player.getLocation().getBlockZ());
        List<String> residents = new ArrayList<String>();
        residents.add(player.getName());

        townList.put(townName,new Town(townName,townUUID,null,player.getName(),player.getUniqueId(),new ArrayList<String>(),residents,256*(Double)Towny3D.getDataManager().getPluginConfig().get("layer-cost"),256,player.getLocation().getBlock().getLocation()));

        //Cambio la città di appertenenza del giocatore che ha creato la città
        Towny3D.getDataManager().getResidentManager().setResidentTown(resident,townName);
    }

    public void deleteTown(String townName,Player player){
        double townBalance = townList.get(townName).getBalance();
        Towny3D.economy.depositPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()),townBalance);
        for(String resident : townList.get(townName).getResidents()){
            //Cambio la città di appertenenza di ogni giocatore della città
            Towny3D.getDataManager().getResidentManager().setResidentTown(Towny3D.getDataManager().getResidentManager().getResidentList().get(resident),townName);
        }
        //Elimino il file
        new File(Formatter.getTownPath(pl.getDataFolder().getPath(),townName)).delete();
        //Rimuovo la città dalla lista
        townList.remove(townName);
    }



    public Town getTown(String name){
        if(name == null){
            return null;
        }
        if(!new File(Formatter.getTownPath(pl.getDataFolder().getPath(),name)).exists()){
            return null;
        }
        ConfigFile townConfig = new ConfigFile(Formatter.getTownPath(pl.getDataFolder().getPath(),name),false);
        String townName = (String)townConfig.get("name");
        String nationName = (String)townConfig.get("nation-name");
        String uuidString = (String)townConfig.get("uuid");
        UUID uuid = (uuidString == null) ? null : UUID.fromString(uuidString);
        String mayorName = (String)townConfig.get("mayor");
        UUID mayorUuid = UUID.fromString((String)townConfig.get("mayor-uuid"));
        List<String> assistantList = townConfig.getStringList("assistants");
        List<String> residentsList = townConfig.getStringList("residents");
        double balance = (Double)townConfig.get("balance");
        int layers = (Integer)townConfig.get("layers");
        String[] location = ((String)townConfig.get("home-location")).split(Pattern.quote("."));
        if(townName == null || mayorName == null || uuid == null || assistantList == null || residentsList == null || location == null){
            return null;
        }else{
            return new Town(townName,uuid,nationName,mayorName,mayorUuid,assistantList,residentsList,balance,layers,new Location(Bukkit.getWorlds().get(0),Integer.parseInt(location[0]),Integer.parseInt(location[1]),Integer.parseInt(location[2])));
        }

    }

    public void addTownResident(Town town, String playerName){
        town.getResidents().add(playerName);
        Town town2 = new Town(town.getName(),town.getUuid(),town.getNationName(),town.getMayorName(),town.getMayorUuid(),town.getAssistantsList(),town.getResidents(),town.getBalance(),town.getLayers(),town.getHome());
        Towny3D.getDataManager().getTownManager().getTownList().put(town.getName(),town2);
        ConfigFile townConfig = new ConfigFile(Formatter.getTownPath(pl.getDataFolder().getPath(),town.getName()),false);
        List<String> residents = townConfig.getStringList("residents");
        residents.add(playerName);
        townConfig.set("residents",residents);
        townConfig.save();
    }

    public void removeTownResident(Town town, String playerName){
        town.getResidents().remove(playerName);
        Town town2 = new Town(town.getName(),town.getUuid(),town.getNationName(),town.getMayorName(),town.getMayorUuid(),town.getAssistantsList(),town.getResidents(),town.getBalance(),town.getLayers(),town.getHome());
        Towny3D.getDataManager().getTownManager().getTownList().put(town.getName(),town2);
        ConfigFile townConfig = new ConfigFile(Formatter.getTownPath(pl.getDataFolder().getPath(),town.getName()),false);
        List<String> residents = townConfig.getStringList("residents");
        residents.remove(playerName);
        townConfig.set("residents",residents);
        townConfig.save();
    }

    public void addTownAssistant(Town town, String playerName){
        town.getAssistantsList().add(playerName);
        ConfigFile townConfig = new ConfigFile(Formatter.getTownPath(pl.getDataFolder().getPath(),town.getName()),false);
        List<String> assistants = townConfig.getStringList("residents");
        assistants.add(playerName);
        townConfig.set("assistants",assistants);
        townConfig.save();
    }

    public void removeTownAssistant(Town town, String playerName){
        town.getAssistantsList().remove(playerName);
        ConfigFile townConfig = new ConfigFile(Formatter.getTownPath(pl.getDataFolder().getPath(),town.getName()),false);
        List<String> assistants = townConfig.getStringList("residents");
        assistants.remove(playerName);
        townConfig.set("assistants",assistants);
        townConfig.save();
    }

    public void setMoney(Town town, double balance){
        Town town2 = new Town(town.getName(),town.getUuid(),town.getNationName(),town.getMayorName(),town.getMayorUuid(),town.getAssistantsList(),town.getResidents(),balance,town.getLayers(),town.getHome());
        Towny3D.getDataManager().getTownManager().getTownList().put(town.getName(),town2);
        ConfigFile townConfig = new ConfigFile(Formatter.getTownPath(pl.getDataFolder().getPath(),town.getName()),false);
        townConfig.set("balance",balance);
        townConfig.save();
    }

    public void setLayers(Town town, int layers){
        Town town2 = new Town(town.getName(),town.getUuid(),town.getNationName(),town.getMayorName(),town.getMayorUuid(),town.getAssistantsList(),town.getResidents(),town.getBalance(),layers,town.getHome());
        Towny3D.getDataManager().getTownManager().getTownList().put(town.getName(),town2);
        ConfigFile townConfig = new ConfigFile(Formatter.getTownPath(pl.getDataFolder().getPath(),town.getName()),false);
        townConfig.set("layers",layers);
        townConfig.save();
    }

}
