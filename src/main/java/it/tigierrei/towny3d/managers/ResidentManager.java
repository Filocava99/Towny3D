package it.tigierrei.towny3d.managers;

import it.tigierrei.configapi.ConfigFile;
import it.tigierrei.towny3d.Towny3D;
import it.tigierrei.towny3d.residents.Resident;
import it.tigierrei.towny3d.utils.Formatter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ResidentManager {

    private Towny3D pl;

    public ResidentManager(Towny3D pl){
        this.pl = pl;
    }
    private HashMap<String,Resident> residentList = new HashMap<String, Resident>();

    public Resident getResident(Player player){
        ConfigFile residentConfig = new ConfigFile(Formatter.getResidentPath(pl.getDataFolder().getPath(),player.getUniqueId().toString()),false);
        String uuidString = (String)residentConfig.get("uuid");
        String name = (String)residentConfig.get("name");

        UUID uuid;
        String town;
        if(uuidString == null || name == null){
            uuid = player.getUniqueId();
            residentConfig.set("uuid",uuid.toString());
            name = player.getName();
            residentConfig.set("name",name);
            residentConfig.set("town",null);
        }else{
            uuid = UUID.fromString(uuidString);

        }
        town = (String)residentConfig.get("town");
        return new Resident(name,uuid,town);
    }

    public HashMap<String, Resident> getResidentList() {
        return residentList;
    }

    public Resident getResident(UUID uuid){
        ConfigFile residentConfig = new ConfigFile(Formatter.getResidentPath(pl.getDataFolder().getPath(),uuid.toString()),false);
        String name = (String)residentConfig.get("name");
        String town = (String)residentConfig.get("town");
        if(name == null || town == null){
            return null;
        }else{
            return new Resident(name,uuid,town);
        }
    }

    public void setResidentTown(Resident resident, String townName){
        ConfigFile residentConfig = new ConfigFile(Formatter.getResidentPath(pl.getDataFolder().getPath(),resident.getUuid().toString()),false);
        residentConfig.set("town",townName);
        residentList.put(resident.getName(),new Resident(resident.getName(),resident.getUuid(),townName));
    }

}
