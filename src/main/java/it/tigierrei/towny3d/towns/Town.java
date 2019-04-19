package it.tigierrei.towny3d.towns;

import it.tigierrei.towny3d.Towny3D;
import it.tigierrei.towny3d.residents.Resident;
import it.tigierrei.towny3d.utils.Selection;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

public class Town {

    private String name;
    private String nationName;
    private String mayorName;
    private UUID uuid;
    private UUID mayorUuid;
    private List<String> assistantsList;
    private List<String> residents;
    private double balance;
    private int maxLayers;
    private int layers;
    private Location home;

    public Town(String name,UUID uuid,String nationName,String mayorName,UUID mayorUUID,List<String> assistantList,List<String> residents,double balance,int layers, Location home){
        this.name = name;
        this.nationName = nationName;
        this.mayorName = mayorName;
        this.uuid = uuid;
        this.mayorUuid = mayorUUID;
        this.assistantsList = assistantList;
        this.residents = residents;
        this.balance = balance;

        Towny3D.getDataManager().getPluginConfig();
        Towny3D.getDataManager().getPluginConfig().get("layers-per-player");
        Integer.parseInt(Towny3D.getDataManager().getPluginConfig().get("layers-per-player").toString());
        this.maxLayers = 256 + residents.size() * (Integer)Towny3D.getDataManager().getPluginConfig().get("layers-per-player");
        this.layers = layers;
        this.home = home;
    }

    public String getName() {
        return name;
    }

    public String getNationName() {
        return nationName;
    }

    public String getMayorName() {
        return mayorName;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Resident getMayor(){
        return Towny3D.getDataManager().getResidentManager().getResident(mayorUuid);
    }

    public UUID getMayorUuid() {
        return mayorUuid;
    }

    public List<String> getAssistantsList() {
        return assistantsList;
    }

    public List<String> getResidents() {
        return residents;
    }

    public double getBalance() {
        return balance;
    }

    public int getMaxLayers() {
        return maxLayers;
    }

    public int getLayers() {
        return layers;
    }

    public Location getHome(){
        return home;
    }
}
