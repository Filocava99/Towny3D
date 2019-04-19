package it.tigierrei.towny3d.managers;

import it.tigierrei.configapi.ConfigFile;
import it.tigierrei.towny3d.Towny3D;
import it.tigierrei.towny3d.regions.Region;
import it.tigierrei.towny3d.towns.Town;
import it.tigierrei.towny3d.utils.Formatter;
import it.tigierrei.towny3d.utils.Selection;
import it.tigierrei.towny3d.utils.Vector;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

public class RegionManager {

    private Towny3D pl;

    private HashMap<Player,Selection> playerSelectionHashMap = new HashMap<Player, Selection>();

    private HashMap<String,ConfigurationSection> worldsRegions = new HashMap<String, ConfigurationSection>();

    public RegionManager(Towny3D pl){
        this.pl = pl;
        File worldsDir =  new File(pl.getDataFolder().getPath()+"/regions");
        //Se non esiste la cartella contenente tutte le region nei vari mondi la creo
        if(!worldsDir.exists()){
            worldsDir.mkdirs();
        }
        //Per ogni file nella cartella...
        for(String name : worldsDir.list()){
            name = name.split(Pattern.quote("."))[0];
            ConfigFile worldConfig = new ConfigFile(Formatter.getRegionPath(pl.getDataFolder().getPath(),name),false);
            //Se esiste il file ed esiste pure la sezione regions allora la prendo e la aggiungo alla lista delle worldsRegions
            if(worldConfig.getSection("regions") != null){
                worldsRegions.put(name,worldConfig.getSection("regions"));
            }
        }
    }

    public HashMap<Player, Selection> getPlayerSelectionHashMap() {
        return playerSelectionHashMap;
    }

    public Region getRegion(Location location){
        //Se non esistono region in quel mondo allora ritorno null
        if(!worldsRegions.containsKey(location.getWorld().getName())){
            return null;
        }else{
            //Prendo le coordinate del chunk per trovare la sua posizione nel config
            int chunkX =  location.getChunk().getX()*16;
            int chunkZ =  location.getChunk().getZ()*16;
            ConfigurationSection chunk = worldsRegions.get(location.getWorld().getName()).getConfigurationSection("X"+chunkX+"Z"+chunkZ);
            //Se il chunk non esiste vuol dire che non esistono regioni
            if(chunk == null){
                return null;
            }
            //Per ogni claim nel chunk effettuo un controllo
            Iterator iterator = chunk.getKeys(false).iterator();
            while(iterator.hasNext()){
                String regionName = (String) iterator.next();
                ConfigurationSection regionSection = chunk.getConfigurationSection(regionName);
                //Se la selection e' dentro uno dei claim gia' esistenti ritorno true, altrimenti terminato il ciclo ritornero' false
                if(isYInsideRegion(location.getBlockY(),regionSection.getInt("y1"),regionSection.getInt("y2"))){
                    return new Region(regionSection.getInt("y1"),
                            regionSection.getInt("y2"),
                            regionSection.getString("owner"),
                            regionSection.getString("town"),
                            regionSection.getBoolean("for_sale"),
                            regionSection.getDouble("value"),
                            regionSection.getString("type"),
                            regionSection.getString("name"),
                            regionSection.getBoolean("fire"),
                            regionSection.getBoolean("explosion"),

                            regionSection.getBoolean("res_pvp"),
                            regionSection.getBoolean("res_inventory"),
                            regionSection.getBoolean("res_interact"),
                            regionSection.getBoolean("res_build"),
                            regionSection.getBoolean("res_destroy"),
                            regionSection.getBoolean("res_drop"),
                            regionSection.getBoolean("res_pick"),
                            regionSection.getBoolean("res_move"),

                            regionSection.getBoolean("ally_pvp"),
                            regionSection.getBoolean("ally_inventory"),
                            regionSection.getBoolean("ally_interact"),
                            regionSection.getBoolean("ally_build"),
                            regionSection.getBoolean("ally_destroy"),
                            regionSection.getBoolean("ally_drop"),
                            regionSection.getBoolean("ally_pick"),
                            regionSection.getBoolean("ally_move"),

                            regionSection.getBoolean("gen_pvp"),
                            regionSection.getBoolean("gen_inventory"),
                            regionSection.getBoolean("gen_interact"),
                            regionSection.getBoolean("gen_build"),
                            regionSection.getBoolean("gen_destroy"),
                            regionSection.getBoolean("gen_drop"),
                            regionSection.getBoolean("gen_pick"),
                            regionSection.getBoolean("gen_move"),

                            regionSection.getBoolean("friendly_mobs_spawn"),
                            regionSection.getBoolean("hostile_mobs_spawn"));
                }
            }
            return null;
        }
    }

    public void createNewRegion(Location location,Selection selection, String townName){
        //List regioni in un mondo
        ConfigurationSection regions;
        //File contente le regioni in un mondo
        ConfigFile worldConfig = new ConfigFile(Formatter.getRegionPath(pl.getDataFolder().getPath(),location.getWorld().getName()),false);
        //Se il file non esiste lo creo ed inizializzo il contenuto - Aggiungo poi tale contenuto alla hashMap worldsRegions
        if(!worldsRegions.containsKey(location.getWorld().getName())){
            worldConfig.createSection("regions");
            regions = worldConfig.getSection("regions");
            worldsRegions.put(location.getWorld().getName(),regions);
        //Se il file esiste gia' carico il contenuto
        }else{
            regions = worldsRegions.get(location.getWorld().getName());
        }
        //Nome del chunk da creare
        String chunkName = ("X"+location.getChunk().getX()*16)+"Z"+(location.getChunk().getZ()*16);
        //Carico la sezione del chunk
        ConfigurationSection chunk = regions.getConfigurationSection(chunkName);
        //Se non esiste la creo nuova
        if(chunk == null){
            chunk = regions.createSection(chunkName);
        }
        //Creo la sezione della region
        ConfigurationSection region = chunk.createSection("Y1" + selection.getVector1().getY() + "Y2" + selection.getVector2().getY());
        //Imposto i valori della region nel chunk
        region.set("y1",selection.getVector1().getY());
        region.set("y2",selection.getVector2().getY());
        region.set("owner",null);
        region.set("town",townName);
        region.set("for_sale",false);
        region.set("value",(double)0);
        region.set("type","normal");
        region.set("name",null);
        region.set("fire",false);
        region.set("explosion",false);
        region.set("friendly_mobs_spawn",true);
        region.set("hostile_mobs_spawn",false);

        region.set("res_pvp",false);
        region.set("res_inventory",true);
        region.set("res_interact",true);
        region.set("res_build",true);
        region.set("res_destroy",true);
        region.set("res_drop",true);
        region.set("res_pick",true);
        region.set("res_move",true);

        region.set("ally_pvp",false);
        region.set("ally_inventory",false);
        region.set("ally_interact",true);
        region.set("ally_build",false);
        region.set("ally_destroy",false);
        region.set("ally_drop",true);
        region.set("ally_pick",true);
        region.set("ally_move",true);

        region.set("gen_pvp",true);
        region.set("gen_inventory",false);
        region.set("gen_interact",false);
        region.set("gen_build",false);
        region.set("gen_destroy",false);
        region.set("gen_drop",true);
        region.set("gen_pick",true);
        region.set("gen_move",true);

        Town town = Towny3D.getDataManager().getTownManager().getTownList().get(townName);
        int layersNumber = selection.getVector2().getY() - selection.getVector1().getY();
        //Aggiorno il numero di layers della città
        Towny3D.getDataManager().getTownManager().setLayers(town,town.getLayers() + layersNumber);
        //Scalo i soldi del costo dei layer
        Towny3D.getDataManager().getTownManager().setMoney(town,town.getBalance()-((Double)Towny3D.getDataManager().getPluginConfig().get("layer-cost")*layersNumber));
        //Scrivo tutto su file
        worldConfig.set("regions",regions);
        worldConfig.save();
    }

    public boolean deleteRegion(Location location){
        //Controllo che la region esista (ridondante essendo già controllato in TownCommand ma per scrupolo e per eventuali API conviene
        if(getRegion(location) != null){
            //List regioni in un mondo
            ConfigurationSection regions = worldsRegions.get(location.getWorld().getName());;
            //File contente le regioni in un mondo
            ConfigFile worldConfig = new ConfigFile(Formatter.getRegionPath(pl.getDataFolder().getPath(),location.getWorld().getName()),false);
            //Nome del chunk da cercare
            String chunkName = ("X"+location.getChunk().getX()*16)+"Z"+(location.getChunk().getZ()*16);
            //Carico la sezione del chunk
            ConfigurationSection chunk = regions.getConfigurationSection(chunkName);
            Iterator<String> iterator = chunk.getKeys(false).iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                ConfigurationSection region = chunk.getConfigurationSection(key);
                if(isYInsideRegion((int)location.getY(),region.getInt("y1"),region.getInt("y2"))){
                    chunk.set(key,null);
                    worldConfig.set("regions",regions);
                    worldConfig.save();
                    return true;
                }
            }
            return false;
        }else{
            return false;
        }
    }

    public boolean setRegionProperty(Location location, String propertyName, Object value){

        //Se il file non esiste ritorno false (non posso cambiare i flag di una region che non esiste)
        if(!worldsRegions.containsKey(location.getWorld().getName())){
            return false;
        }

        //File contente le regioni in un mondo
        ConfigFile worldConfig = new ConfigFile(Formatter.getRegionPath(pl.getDataFolder().getPath(),location.getWorld().getName()),false);

        //List regioni in un mondo
        ConfigurationSection regions = worldsRegions.get(location.getWorld().getName());

        //Prendo le coordinate del chunk per trovare la sua posizione nel config
        int chunkX =  location.getChunk().getX()*16;
        int chunkZ =  location.getChunk().getZ()*16;
        String chunkName = "X"+chunkX+"Z"+chunkZ;
        ConfigurationSection chunk = regions.getConfigurationSection(chunkName);
        //Se il chunk non esiste vuol dire che non esistono regioni
        if(chunk == null){
            return false;
        }
        //Per ogni claim nel chunk effettuo un controllo
        Iterator iterator = chunk.getKeys(false).iterator();
        while(iterator.hasNext()) {
            String regionName = (String) iterator.next();
            ConfigurationSection regionSection = chunk.getConfigurationSection(regionName);
            //Se la selection e' dentro uno dei claim gia' esistenti ritorno true, altrimenti terminato il ciclo ritornero' false
            if (isYInsideRegion(location.getBlockY(), regionSection.getInt("y1"), regionSection.getInt("y2"))) {
                //Controllo che il flag passato alla funzione sia valido
                if(regionSection.contains(propertyName)){
                    regionSection.set(propertyName,value);
                    chunk.set(regionName,regionSection);
                    regions.set(chunkName,chunk);
                    worldConfig.set("regions",regions);
                    worldConfig.save();
                    return true;
                }else{
                    return false;
                }
            }
        }
        return false;
    }

    public boolean setRegionOwner(Location location, String playerName) {

        //Se il file non esiste ritorno false (non posso cambiare i flag di una region che non esiste)
        if (!worldsRegions.containsKey(location.getWorld().getName())) {
            return false;
        }

        //File contente le regioni in un mondo
        ConfigFile worldConfig = new ConfigFile(Formatter.getRegionPath(pl.getDataFolder().getPath(), location.getWorld().getName()), false);

        //List regioni in un mondo
        ConfigurationSection regions = worldsRegions.get(location.getWorld().getName());

        //Prendo le coordinate del chunk per trovare la sua posizione nel config
        int chunkX = location.getChunk().getX() * 16;
        int chunkZ = location.getChunk().getZ() * 16;
        String chunkName = "X" + chunkX + "Z" + chunkZ;
        ConfigurationSection chunk = regions.getConfigurationSection(chunkName);
        //Se il chunk non esiste vuol dire che non esistono regioni
        if (chunk == null) {
            return false;
        }
        //Per ogni claim nel chunk effettuo un controllo
        Iterator iterator = chunk.getKeys(false).iterator();
        while (iterator.hasNext()) {
            String regionName = (String) iterator.next();
            ConfigurationSection regionSection = chunk.getConfigurationSection(regionName);
            //Se la selection e' dentro uno dei claim gia' esistenti ritorno true, altrimenti terminato il ciclo ritornero' false
            if (isYInsideRegion(location.getBlockY(), regionSection.getInt("y1"), regionSection.getInt("y2"))) {
                regionSection.set("owner", playerName);
                chunk.set(regionName, regionSection);
                regions.set(chunkName, chunk);
                worldConfig.set("regions", regions);
                worldConfig.save();
                return true;
            }
        }
        return false;
    }

    public boolean setRegionName(Location location, String name) {

        //Se il file non esiste ritorno false (non posso cambiare i flag di una region che non esiste)
        if (!worldsRegions.containsKey(location.getWorld().getName())) {
            return false;
        }

        //File contente le regioni in un mondo
        ConfigFile worldConfig = new ConfigFile(Formatter.getRegionPath(pl.getDataFolder().getPath(), location.getWorld().getName()), false);

        //List regioni in un mondo
        ConfigurationSection regions = worldsRegions.get(location.getWorld().getName());

        //Prendo le coordinate del chunk per trovare la sua posizione nel config
        int chunkX = location.getChunk().getX() * 16;
        int chunkZ = location.getChunk().getZ() * 16;
        String chunkName = "X" + chunkX + "Z" + chunkZ;
        ConfigurationSection chunk = regions.getConfigurationSection(chunkName);
        //Se il chunk non esiste vuol dire che non esistono regioni
        if (chunk == null) {
            return false;
        }
        //Per ogni claim nel chunk effettuo un controllo
        Iterator iterator = chunk.getKeys(false).iterator();
        while (iterator.hasNext()) {
            String regionName = (String) iterator.next();
            ConfigurationSection regionSection = chunk.getConfigurationSection(regionName);
            //Se la selection e' dentro uno dei claim gia' esistenti ritorno true, altrimenti terminato il ciclo ritornero' false
            if (isYInsideRegion(location.getBlockY(), regionSection.getInt("y1"), regionSection.getInt("y2"))) {
                regionSection.set("name", name);
                chunk.set(regionName, regionSection);
                regions.set(chunkName, chunk);
                worldConfig.set("regions", regions);
                worldConfig.save();
                return true;
            }
        }
        return false;
    }

    public boolean isSelectionOverlapping(Location location, Selection selection){
        //Se non esiste il mondo ritorno false
        if(!worldsRegions.containsKey(location.getWorld().getName())){
            return false;
        }else{
            //Prendo le coordinate del chunk per trovare la sua posizione nel config
            int chunkX =  location.getChunk().getX()*16;
            int chunkZ =  location.getChunk().getZ()*16;
            ConfigurationSection chunk = worldsRegions.get(location.getWorld().getName()).getConfigurationSection("X"+chunkX+"Z"+chunkZ);
            //Se non esiste il chunk non esiste region quindi la zona è sicuramente libera
            if(chunk == null){
                return false;
            }
            //Per ogni claim nel chunk effettuo un controllo
            Iterator iterator = chunk.getKeys(false).iterator();
            while(iterator.hasNext()){
                String regionName = (String) iterator.next();
                ConfigurationSection region = chunk.getConfigurationSection(regionName);
                Vector vector1 = selection.getVector1();
                Vector vector2 = selection.getVector2();
                //Se la selection e' dentro uno dei claim gia' esistenti ritorno true, altrimenti terminato il ciclo ritornero' false
                if(isSelectionInsideRegion(vector1.getY(),vector2.getY(),region)){
                    return true;
                }
            }

            return false;
        }
    }

    private boolean isYInsideRegion(int playerY,int y1, int y2){
        if(playerY >= y1 && playerY <= y2){
            return true;
        }else{
            return false;
        }
    }

    //Controllo
    private boolean isSelectionInsideRegion(int y1, int y2, ConfigurationSection region){
        if((y1 >= region.getInt("y1") &&
                y1 <= region.getInt("y2")) ||
                (y2 >= region.getInt("y1") &&
                        y2 <= region.getInt("y2"))
        ){
            return true;
        }else{
            return false;
        }
    }

}
