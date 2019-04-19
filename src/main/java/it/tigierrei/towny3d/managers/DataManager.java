package it.tigierrei.towny3d.managers;

import it.tigierrei.configapi.ConfigFile;
import it.tigierrei.towny3d.Towny3D;

public class DataManager {

    private Towny3D pl;
    private ConfigFile pluginConfig;
    private TownManager townManager;
    private ResidentManager residentManager;
    private RegionManager regionManager;
    private RequestManager requestManager;

    public DataManager(Towny3D pl){
        this.pl = pl;

        //Inizializzo il config del plugin
        pluginConfig = new ConfigFile(pl.getDataFolder() + "/config.yml",true);
        if(pluginConfig.get("town-cost") == null){
            pluginConfig.set("town-cost",1000.0);
        }

        //1 layer = 16 blocks x 16 blocks = 256 blocks
        //1 chunk = 256 layers = 65536 blocks
        if(pluginConfig.get("layers-per-player") == null){
            pluginConfig.set("layers-per-player",10);
        }

        if(pluginConfig.get("layer-cost") == null){
            pluginConfig.set("layer-cost",10.0);
        }
    }

    public void initializeManagers(){
        residentManager = new ResidentManager(pl);
        //TOWNMANAGER SEMPRE DOPO RESIDENTMANAGER
        townManager = new TownManager(pl);
        regionManager = new RegionManager(pl);
        requestManager = new RequestManager(pl);
    }

    public TownManager getTownManager(){
        return townManager;
    }

    public ResidentManager getResidentManager(){
        return residentManager;
    }

    public RegionManager getRegionManager() {
        return regionManager;
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    public ConfigFile getPluginConfig(){
        return pluginConfig;
    }

}
