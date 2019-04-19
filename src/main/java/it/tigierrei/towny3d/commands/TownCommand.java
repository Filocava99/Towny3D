package it.tigierrei.towny3d.commands;

import it.tigierrei.towny3d.Towny3D;
import it.tigierrei.towny3d.events.TownCreationEvent;
import it.tigierrei.towny3d.regions.Region;
import it.tigierrei.towny3d.residents.Resident;
import it.tigierrei.towny3d.towns.Town;
import it.tigierrei.towny3d.utils.Selection;
import it.tigierrei.towny3d.utils.Vector;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TownCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        Player player = (Player) commandSender;

        //SENZA PARAMETRI == HELP OPPURE INFO
        if (args.length == 0 || args[0].equalsIgnoreCase("info")) {
            Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(commandSender.getName());
            if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()) != null){
                printTownInfo(resident.getTown(),commandSender);
            }else{
                commandSender.sendMessage(ChatColor.RED + "You do not belong to any town!");
            }
            return true;
        }

        //HELP
        if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
            if (commandSender.hasPermission("town.help")) {
                printTownHelp(commandSender);
                return true;
            } else {
                noPermissionWarning(commandSender);
                return true;
            }
        }

        //LIST
        if(args[0].equalsIgnoreCase("list")){
            commandSender.sendMessage(ChatColor.YELLOW + "-----[ " + ChatColor.GOLD + "Town list" + ChatColor.YELLOW + " ]-----");
            Iterator<Map.Entry<String,Town>> iterator = Towny3D.getDataManager().getTownManager().getTownList().entrySet().iterator();

            while (iterator.hasNext()){
                Map.Entry<String,Town> entry = iterator.next();
                commandSender.sendMessage(ChatColor.YELLOW + entry.getKey() + (iterator.hasNext() ? "," : ""));
            }
            commandSender.sendMessage(ChatColor.YELLOW + "-----------------------");
        }

        //CREATE
        if (args[0].equalsIgnoreCase("create")) {
            //Controllo i permessi
            if (!commandSender.hasPermission("town.create")) {
                noPermissionWarning(commandSender);
                return true;
            }
            //Controllo il numero di parametri
            if (args.length < 2) {
                commandSender.sendMessage(ChatColor.RED + "You have to use /town create <townname>");
                return true;
            }

            //Controllo che non esista già una città con lo stesso nome
            if(Towny3D.getDataManager().getTownManager().getTownList().get(args[1]) != null){
                commandSender.sendMessage(ChatColor.RED + "The town's name is already in use!");
                return true;
            }

            //Controllo che la citta' non sia sopra il territorio di un'altra gia' esistente
            Selection selection = new Selection();
            selection.setVector1(new Vector(player.getLocation().getBlockX(),0,player.getLocation().getBlockZ()));
            selection.setVector2(new Vector(player.getLocation().getBlockX()+1,256,player.getLocation().getBlockZ()+1));
            if(Towny3D.getDataManager().getRegionManager().isSelectionOverlapping(player.getLocation(),selection)){
                player.sendMessage(ChatColor.RED + "You are inside a town borders! Go in wilderness to create a new one!");
                return true;
            }

            //Controllo che abbia i soldi
            double balance = Towny3D.economy.getBalance(Bukkit.getOfflinePlayer(player.getUniqueId()));
            double townCost = (Double) Towny3D.getDataManager().getPluginConfig().get("town-cost");
            //Se non ha abbastanza soldi termino tutto e lo avviso
            if (balance < townCost) {
                commandSender.sendMessage(ChatColor.RED + "You don't have enough moneys to create a new town!");
                return true;
            }

            //Prelievo i soldi all'utente
            Towny3D.economy.withdrawPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()),townCost);
            //Triggero l'evento
            TownCreationEvent event = new TownCreationEvent(player,args[1]);

            //Se l'evento non viene cancellato creo il config della città
            Bukkit.getServer().getPluginManager().callEvent(event);
            if(!event.isCancelled()){
                //Creo la città
                Towny3D.getDataManager().getTownManager().createTown(player,args[1]);
                //Creo il primo plot
                Towny3D.getDataManager().getRegionManager().createNewRegion(player.getLocation(),selection,args[1]);
                Towny3D.getDataManager().getRegionManager().setRegionProperty(player.getLocation(),"type","home");
                player.sendMessage(ChatColor.GREEN + "Town successfully created!");
                return true;
            }else{
                commandSender.sendMessage("Event cancelled - The town will not be created :P");
                return true;
            }

        }

        //MAYOR COMMANDS
        if(args[0].equalsIgnoreCase("mayor")){
            //Permissions check
            if(!commandSender.hasPermission("town.mayor")){
                noPermissionWarning(commandSender);
                return true;
            }
            Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(player.getName());
            //Check if he is a resident of a town
            if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()) == null){
                commandSender.sendMessage(ChatColor.RED + "You are not a citizen of any town!");
                return true;
            }
            //Check if he is the mayor of his town
            if(!Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getMayorName().equalsIgnoreCase(resident.getName())){
                commandSender.sendMessage(ChatColor.RED + "You are not the mayor of your city!");
                return true;
            }
            //Parametri insufficienti -> Print help
            if(args.length < 2){
                printMayorHelp(commandSender);
                return true;
            }
            //HELP
            if(args[1].equalsIgnoreCase("help") || args[1].equalsIgnoreCase("?")){
                printMayorHelp(commandSender);
                return true;
            }
            //GET CLAIMER STICKER
            if(args[1].equalsIgnoreCase("getClaimerStick") || args[1].equalsIgnoreCase("gcs")){
                //Item creation
                ItemStack stick = new ItemStack(Material.STICK,1);
                //Retrieving ItemMeta from previous item
                ItemMeta itemMeta = stick.getItemMeta();
                //Display name
                itemMeta.setDisplayName(ChatColor.DARK_PURPLE + "Claimer Stick");
                //Item lore
                itemMeta.setLore(Arrays.asList(ChatColor.LIGHT_PURPLE + "Click sx -> Select first corner",ChatColor.LIGHT_PURPLE + "Click dx -> Select second corner"));
                //Item enchant - Aesthetic purpose
                itemMeta.addEnchant(Enchantment.DURABILITY,3,false);
                //ItemMeta applying
                stick.setItemMeta(itemMeta);
                //Item gave
                player.getInventory().addItem(stick);
                //Player notified
                commandSender.sendMessage(ChatColor.GREEN + "Check your inventory!");
                return true;
            }
            //ADD ASSISTANT
            if(args[1].equalsIgnoreCase("addAssistant")){
                //Controllo se i parametri sono sufficienti
                if(args.length < 3){
                    commandSender.sendMessage(ChatColor.RED + "You have to specify the name of the citizen to be promoted!");
                    return true;
                }

                if(Bukkit.getPlayer(args[2]) == null){
                    commandSender.sendMessage(ChatColor.RED +"The provided player doesn't exists or is offline!");
                }
                Resident res = Towny3D.getDataManager().getResidentManager().getResidentList().get(args[2]);
                //Controllo che entrambi i giocatori siano nella stessa citta'
                if(resident.getTown().equalsIgnoreCase(res.getTown())){
                    Town town = Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown());
                    //Controllo se il giocatore specificato e' gia' un assistente
                    if(town.getAssistantsList().contains(args[2])){
                        commandSender.sendMessage(ChatColor.RED + "The provided player is already an assistant!");
                        return true;
                    }
                    //Aggiungo il giocatore come assistente
                    Towny3D.getDataManager().getTownManager().addTownAssistant(town,args[2]);
                    commandSender.sendMessage(ChatColor.GREEN + args[2] + " was added to the assistant list");
                    return true;
                }else{
                    commandSender.sendMessage(ChatColor.RED + args[2] + " is not in your same town!");
                    return true;
                }
            }

            //REMOVE ASSISTANT
            if(args[1].equalsIgnoreCase("removeAssistant")){
                //Controllo se i parametri sono sufficienti
                if(args.length < 3){
                    commandSender.sendMessage(ChatColor.RED + "You have to specify the name of the citizen to remove from the assistants list!");
                    return true;
                }

                Resident res = Towny3D.getDataManager().getResidentManager().getResidentList().get(args[2]);
                //Controllo che entrambi i giocatori siano nella stessa citta'
                if(resident.getTown().equalsIgnoreCase(res.getTown())){
                    Town town = Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown());
                    //Controllo se il giocatore specificato e' gia' un assistente
                    if(town.getAssistantsList().contains(args[2])){
                        //Rimuovo il giocatore dagli assistenti della città
                        Towny3D.getDataManager().getTownManager().removeTownAssistant(town,args[2]);
                        //Se il giocatore da kickare è online lo avviso
                        if(Bukkit.getPlayer(resident.getUuid()) != null){
                            Bukkit.getPlayer(resident.getUuid()).sendMessage(ChatColor.RED + "You have been demoted from assistant!");
                        }
                        commandSender.sendMessage(ChatColor.GREEN + args[2] + " has been removed from the assistants list");
                        return true;
                    }else{
                        commandSender.sendMessage(ChatColor.RED + "The provided player is not an assistant!");
                        return true;
                    }
                }else{
                    commandSender.sendMessage(ChatColor.RED + args[2] + " is not in your same town!");
                    return true;
                }
            }
        }

        //CLAIM
        if(args[0].equalsIgnoreCase("claim")){
            //Permissions check
            if(!commandSender.hasPermission("town.claim")){
                noPermissionWarning(commandSender);
                return true;
            }
            Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(player.getName());
            //Check if he is a resident of a town
            if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()) == null){
                commandSender.sendMessage(ChatColor.RED + "You are not a citizen of any town!");
                return true;
            }
            //Check if he is the mayor of his town or an assistant
            if(!(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getMayorName().equalsIgnoreCase(resident.getName())) || Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getAssistantsList().contains(resident.getName())){
                commandSender.sendMessage(ChatColor.RED + "You are not an assistant neither the mayor of that city!");
                return true;
            }
            Selection selection;
            //CLAIM CHUNK - Creo una selection che va da 0 a 256
            if(args.length > 1 && args[1].equalsIgnoreCase("chunk")){
                selection = new Selection();
                selection.setVector1(new Vector(0,0,0));
                selection.setVector2(new Vector(0,256,0));
            }else{
                //Altrimenti uso quella fatta dall'utente
                selection = Towny3D.getDataManager().getRegionManager().getPlayerSelectionHashMap().get(player);
            }

            //If selection's corners exists I claim the region
            if(selection != null && selection.getVector1() != null && selection.getVector2() != null){

                Location location = player.getLocation();
                Town town = Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown());
                if(Towny3D.getDataManager().getRegionManager().isSelectionOverlapping(location,selection)){
                    commandSender.sendMessage(ChatColor.RED+"The selected region is overlapping another region!");
                    return true;
                }else if(town.getBalance() >= (Double)Towny3D.getDataManager().getPluginConfig().get("layer-cost") && town.getLayers()+(selection.getVector2().getY()-selection.getVector1().getY()) <= town.getMaxLayers()){
                    commandSender.sendMessage(ChatColor.GREEN + "Region successfully created!");
                    Towny3D.getDataManager().getRegionManager().createNewRegion(location,selection,town.getName());
                    return true;
                }else{
                    commandSender.sendMessage(ChatColor.RED + "The town has not enough moneys or has reached the maximum number of claimed blocks!");
                    return true;
                }
            //Otherwise I notify the player
            }else{
                commandSender.sendMessage(ChatColor.RED + "You have not selected any region!");
                return true;
            }
        }

        //UNCLAIM
        if(args[0].equalsIgnoreCase("unclaim")){
            //Permissions check
            if(!commandSender.hasPermission("town.claim")){
                noPermissionWarning(commandSender);
                return true;
            }
            Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(player.getName());
            //Check if he is a resident of a town
            if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()) == null){
                commandSender.sendMessage(ChatColor.RED + "You are not a citizen of any town!");
                return true;
            }
            //Check if he is the mayor of his town or an assistant
            if(!(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getMayorName().equalsIgnoreCase(resident.getName())) || Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getAssistantsList().contains(resident.getName())){
                commandSender.sendMessage(ChatColor.RED + "You are not an assistant neither the mayor of that city!");
                return true;
            }

            Town town = Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown());
            Region region = Towny3D.getDataManager().getRegionManager().getRegion(player.getLocation());
            //Controllo che sia in città
            if(region == null){
                commandSender.sendMessage(ChatColor.RED + "You must be inside your town's borders to use that command!");
                return true;
            }
            //Controllo che la region non sia la home
            if(region.getType().equalsIgnoreCase("home")){
                commandSender.sendMessage(ChatColor.RED + "You can't delete the home region of your town!");
                return true;
            }
            //Controllo che il plot sia della città del giocatore che esegue il comando
            if(region.getTown().equalsIgnoreCase(town.getName())){
                //Aggiorno il numero di layers della città
                Towny3D.getDataManager().getTownManager().setLayers(town,town.getLayers()-region.getY2()-region.getY2());
                //Cancello il plot
                Towny3D.getDataManager().getRegionManager().deleteRegion(player.getLocation());
                commandSender.sendMessage(ChatColor.GREEN + "Region successfully deleted!");
                return true;
            }else{
                commandSender.sendMessage(ChatColor.RED + "This region does not belong to your town!");
                return true;
            }
        }

        //INVITE / ADD
        if((args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("add")) && args.length > 1){
            if(commandSender.hasPermission("town.invite")){
                Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(commandSender.getName());
                //Se il giocatore non fa parte di nessuna citta' termino la funzione e lo avviso
                if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()) == null){
                    commandSender.sendMessage(ChatColor.RED + "You are not a citizen of any town!");
                    return true;
                }
                Town town = Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown());
                //Controllo che sia il sindaco o un assistente
                if(town.getAssistantsList().contains(commandSender.getName()) || town.getMayorName().equalsIgnoreCase(commandSender.getName())){
                    Player p = Bukkit.getPlayer(args[1]);
                    //Se il giocatore da invitare non esiste o non e' online avviso il giocatore e termino al funzione
                    if(p == null){
                        player.sendMessage(ChatColor.RED + "The provided player doesn't exists or is offline!");
                        return true;
                    }
                    Towny3D.getDataManager().getRequestManager().addInviteRequest(town,p);
                    p.sendMessage(ChatColor.YELLOW + "You have been invited to join " + town.getName());
                    //TODO Json message
                    player.sendMessage(ChatColor.GREEN + "You have invited " + p.getName() + " to join your town");
                    return true;
                //Altrimenti avviso il giocatore e termino al funzione
                }else{
                    player.sendMessage(ChatColor.RED + "You are not an assistant neither the mayor of that city!");
                    return true;
                }
            }
        }

        //DEPOSIT
        if(args[0].equalsIgnoreCase("deposit")){
            //Permission check
            if(commandSender.hasPermission("town.deposit")){
                //Controllo che abbia inserito i soldi da depositare
                if(args.length > 1){
                    Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(commandSender.getName());
                    //Controllo che il giocatore abbia una città
                    if(resident.getTown() == null){
                        commandSender.sendMessage(ChatColor.RED + "You are not a citizen of any town!");
                        return true;
                    }
                    double amount = Double.parseDouble(args[1]);
                    //Controllo che abbia i soldi
                    if(Towny3D.economy.getBalance(Bukkit.getOfflinePlayer(((Player) commandSender).getUniqueId())) >= amount){
                        Towny3D.economy.withdrawPlayer(Bukkit.getOfflinePlayer(((Player) commandSender).getUniqueId()),amount);
                        Town town = Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown());
                        Towny3D.getDataManager().getTownManager().setMoney(town,town.getBalance()+amount);
                        commandSender.sendMessage(ChatColor.GREEN + "Moneys successfully deposited!");
                        return true;
                    }else{
                        commandSender.sendMessage(ChatColor.RED + "You don't have enough moneys!");
                        return true;
                    }

                }else{
                    commandSender.sendMessage(ChatColor.RED + "You must specify the amount of moneys!");
                    return true;
                }
            }else{
                noPermissionWarning(commandSender);
                return true;
            }
        }

        //WITHDRAW
        if(args[0].equalsIgnoreCase("withdraw")){
            //Permissions check
            if(!commandSender.hasPermission("town.withdraw")){
                noPermissionWarning(commandSender);
                return true;
            }
            Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(player.getName());
            Town town = Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown());
            //Check if he is a resident of a town
            if(town == null){
                commandSender.sendMessage(ChatColor.RED + "You are not a citizen of any town!");
                return true;
            }
            //Check if he is the mayor of his town or an assistant
            if(!(town.getMayorName().equalsIgnoreCase(resident.getName())) || town.getAssistantsList().contains(resident.getName())){
                commandSender.sendMessage(ChatColor.RED + "You are not an assistant neither the mayor of that city!");
                return true;
            }
            if(args.length < 2){
                commandSender.sendMessage(ChatColor.RED + "You must specify the amount of moneys!");
                return true;
            }
            double amount = Double.parseDouble(args[1]);
            double townBalance = town.getBalance();
            if(townBalance >= amount){
                //TODO Avvisare tutti i cittadini del prelievo
                //Tolgo i soldi alla banca cittadina
                Towny3D.getDataManager().getTownManager().setMoney(town,townBalance-amount);
                //Aggiungo i soldi all'utente
                Towny3D.economy.depositPlayer(Bukkit.getOfflinePlayer(((Player) commandSender).getUniqueId()),amount);
                //Avviso il giocatore dell'avvenuto prelievo
                commandSender.sendMessage(ChatColor.GREEN + "Moneys successfully withdrawn!");
                return true;
            }else{
                commandSender.sendMessage(ChatColor.RED + "The town does not have enough moneys!");
                return true;
            }
        }

        //SETHOME
        if(args[0].equalsIgnoreCase("setHome")){
            if(commandSender.hasPermission("town.sethome")){
                Region region = Towny3D.getDataManager().getRegionManager().getRegion(player.getLocation());
                //Se il giocatore è in wild (region == null) lo avviso e termino la funzione
                if(region == null){
                    commandSender.sendMessage(ChatColor.RED + "You are not inside your town borders!");
                    return true;
                }
                Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(commandSender.getName());
                //Se il giocatore non ha una città lo avviso e termino la funzione
                if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()) == null){
                    commandSender.sendMessage(ChatColor.RED + "You are not a citizen of any town!");
                    return true;
                }
                if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getMayorUuid().equals(player.getUniqueId()) || Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getAssistantsList().contains(player.getName())){
                    //Prendo la vecchia home region per impostarla come type "normal"
                    Town town = Towny3D.getDataManager().getTownManager().getTownList().get(region.getTown());
                    Location townHomeLocation = town.getHome();
                    //Imposto il tipo della vecchia home region come "normal"
                    Towny3D.getDataManager().getRegionManager().setRegionProperty(townHomeLocation,"type","normal");
                    //Imposto il tipo della nuova home regione come "home"
                    Towny3D.getDataManager().getRegionManager().setRegionProperty(player.getLocation(),"type","home");
                    commandSender.sendMessage(ChatColor.GREEN + "Town home successfully changed!");
                    return true;
                }
            }else{
                noPermissionWarning(commandSender);
                return true;
            }
        }

        //ONLINE
        if(args[0].equalsIgnoreCase("online")){
            if(commandSender.hasPermission("town.online")){
                Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(commandSender.getName());
                List<String> onlineResidents = new ArrayList<String>();
                if(resident.getTown() != null){
                    Iterator<String> residentsIterator = Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getResidents().iterator();
                    while(residentsIterator.hasNext()){
                        String resName = residentsIterator.next();
                        for(Player p : Bukkit.getServer().getOnlinePlayers()){
                            if(resName.equalsIgnoreCase(p.getName())){
                                onlineResidents.add(resName);
                                break;
                            }
                        }
                    }
                    if(onlineResidents.size() > 1){
                        commandSender.sendMessage(ChatColor.GREEN + "=========[ Online players ]=========");
                        String onlineResString = "";
                        for(int i = 0; i < onlineResidents.size(); i++){
                            onlineResString += onlineResidents.get(i) + ((i+1) == onlineResidents.size() ? "" : ", ");
                        }
                        commandSender.sendMessage(ChatColor.GREEN + onlineResString);
                        return true;
                    }else{
                        commandSender.sendMessage(ChatColor.YELLOW + "No online citizens found except you");
                        return true;
                    }
                }else{
                    commandSender.sendMessage(ChatColor.RED + "You do not belong to any town!");
                    return true;
                }
            }else{
                noPermissionWarning(commandSender);
                return true;
            }
        }

        //HERE
        if(args[0].equalsIgnoreCase("here")){
            if(commandSender.hasPermission("town.here")){
                Region region = Towny3D.getDataManager().getRegionManager().getRegion(((Player) commandSender).getLocation());
                if(region.getTown() != null){
                    printTownInfo(region.getTown(),commandSender);
                    return true;
                }else{
                    commandSender.sendMessage(ChatColor.RED + "You are not inside any town!");
                    return true;
                }
            }else{
                noPermissionWarning(commandSender);
                return true;
            }
        }

        //SPAWN
        if(args[0].equalsIgnoreCase("spawn")){
            if(commandSender.hasPermission("town.spawn")){
                Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(commandSender.getName());
                if(resident.getTown() != null){
                    //TODO T spawn cost
                    Town town = Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown());
                    player.teleport(town.getHome());
                    return true;
                }else{
                    commandSender.sendMessage(ChatColor.RED + "You do not belong to any town!");
                    return true;
                }
            }else{
                noPermissionWarning(commandSender);
                return true;
            }
        }

        //KICK
        if(args[0].equalsIgnoreCase("kick")){
            if(commandSender.hasPermission("town.kick")){
                //Controllo che abbia specificato il nome del giocatore da kickare
                if(args.length < 2){
                    commandSender.sendMessage(ChatColor.RED + "You must specify the name of the player to be kicked from the town!");
                    return true;
                }

                Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(commandSender.getName());
                //Se il giocatore non fa parte di nessuna citta' termino la funzione e lo avviso
                if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()) == null){
                    commandSender.sendMessage(ChatColor.RED + "You are not a citizen of any town!");
                    return true;
                }
                Town town = Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown());
                //Controllo che sia il sindaco o un assistente
                if(town.getAssistantsList().contains(commandSender.getName()) || town.getMayorName().equalsIgnoreCase(commandSender.getName())){
                    Resident kickedResident = Towny3D.getDataManager().getResidentManager().getResidentList().get(args[1]);
                    //Controllo che il giocatore da kickare sia nella stessa città del commandSender
                    if(kickedResident.getTown() != null && kickedResident.getTown().equalsIgnoreCase(town.getName())){
                        //Se il giocatore da kickare è online lo avviso
                        if(Bukkit.getPlayer(resident.getUuid()) != null){
                            Bukkit.getPlayer(resident.getUuid()).sendMessage(ChatColor.RED + "You have been kicked from " + town.getName() + " !");
                        }
                        //Lo tolgo dai residentds della città
                        Towny3D.getDataManager().getTownManager().removeTownResident(town,args[1]);
                        //Modifico il suo config (town = null)
                        Towny3D.getDataManager().getResidentManager().setResidentTown(Towny3D.getDataManager().getResidentManager().getResidentList().get(args[1]),null);
                        commandSender.sendMessage(ChatColor.GREEN + args[1] + " have been kicked from your the town!");
                        return true;
                    }else{
                        commandSender.sendMessage(ChatColor.RED + "The specified player does not belong to your town!");
                        return true;
                    }
                }else{
                    player.sendMessage(ChatColor.RED + "You are not an assistant neither the mayor of that city!");
                    return true;
                }
            }
        }

        //DISBAND
        if(args[0].equalsIgnoreCase("disband")){
            if(commandSender.hasPermission("town.disband")){
                Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(commandSender.getName());
                //Controllo che il giocatore appartenga ad una città
                if(resident.getTown() == null){
                    commandSender.sendMessage(ChatColor.RED + "You do not belong to any town!");
                    return true;
                }
                //Controllo che sia il sindaco di quella città
                Town town = Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown());
                if(town.getMayorName().equalsIgnoreCase(resident.getName())){
                    //TODO Chiedere conferma (con JSON si fa subito)
                    Towny3D.getDataManager().getTownManager().deleteTown(town.getName(),player);
                    commandSender.sendMessage(ChatColor.YELLOW + "The town has been deleted");
                    commandSender.sendMessage(ChatColor.YELLOW + "The town balance has been sent to your");
                    return true;
                }else{
                    commandSender.sendMessage(ChatColor.RED + "You are not the mayor of your town!");
                    return true;
                }

            }else{
                noPermissionWarning(commandSender);
                return true;
            }
        }

        // /town townName
        if(Towny3D.getDataManager().getTownManager().getTownList().containsKey(args[1])){
            if(commandSender.hasPermission("town.info")){
                printTownInfo(args[0],commandSender);
                return true;
            }else{
                noPermissionWarning(commandSender);
                return true;
            }
        }else{
            commandSender.sendMessage(ChatColor.RED + args[1] + " does not exist!");
            return true;
        }
    }

    private void printTownHelp(@NotNull CommandSender sender){
        sender.sendMessage(ChatColor.DARK_GREEN + "=========[ Town Help ]=========");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/town ? (or) help " + ChatColor.RESET + "" + ChatColor.GREEN + "to see the full commands list");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/town list " + ChatColor.RESET + "" + ChatColor.GREEN + "to see the list of towns");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/town create <townName> " + ChatColor.RESET + "" + ChatColor.GREEN + "to create a new town");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/town mayor ? " + ChatColor.RESET + "" + ChatColor.GREEN + "to see the mayor commands list");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/town claim [chunk] " + ChatColor.RESET + "" + ChatColor.GREEN + "to claim the selected region. Add 'chunk' to claim the entire chunk");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/town unclaim " + ChatColor.RESET + "" + ChatColor.GREEN + "to unclaim the region you are inside");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/town invite (or) add <playerName> " + ChatColor.RESET + "" + ChatColor.GREEN + "to invite a player to the town");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/town deposit <amount> " + ChatColor.RESET + "" + ChatColor.GREEN + "to deposit moneys in the town bank");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/town withdraw <amount> " + ChatColor.RESET + "" + ChatColor.GREEN + "to withdraw moneys from the town bank");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/town setHome " + ChatColor.RESET + "" + ChatColor.GREEN + "to change/set the town home");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/town online " + ChatColor.RESET + "" + ChatColor.GREEN + "to see list of the online residents in your town");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/town here " + ChatColor.RESET + "" + ChatColor.GREEN + "to see info of the town you are standing in");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/town spawn" + ChatColor.RESET + "" + ChatColor.GREEN + "to teleport to the town home");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/town kick " + ChatColor.RESET + "" + ChatColor.GREEN + "to kick a player from the town");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/town disband " + ChatColor.RESET + "" + ChatColor.GREEN + "to disband(delete) the town");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/town <townName> " + ChatColor.RESET + "" + ChatColor.GREEN + "to see the info of the specified town");
        sender.sendMessage(ChatColor.DARK_GREEN + "=====[ Made by Tito Tigi ]=====");
    }

    private void printMayorHelp(@NotNull CommandSender sender){
        sender.sendMessage(ChatColor.DARK_GREEN + "=========[ Mayor Help ]=========");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/town mayor ? (or) help " + ChatColor.RESET + "" + ChatColor.GREEN + "to see the mayor commands list");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/town mayor getClaimerStick (or) gcs " + ChatColor.RESET + "" + ChatColor.GREEN + "to receive the claimer item");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/town mayor addAssistant <playerName> " + ChatColor.RESET + "" + ChatColor.GREEN + "to promote a player to assistant");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/town mayor removeAssistant <playerName> " + ChatColor.RESET + "" + ChatColor.GREEN + "to demote a player from assistant");
        sender.sendMessage(ChatColor.DARK_GREEN + "=====[ Made by Tito Tigi ]=====");
    }

    private void noPermissionWarning(@NotNull CommandSender sender){
        sender.sendMessage(ChatColor.RED + "You don't have the permissions to run that command!");
    }

    private void printTownInfo(String townName,CommandSender commandSender){
        Town town = Towny3D.getDataManager().getTownManager().getTownList().get(townName);
        commandSender.sendMessage(ChatColor.YELLOW + "-----[ " + ChatColor.GOLD + town.getName() + ChatColor.YELLOW + " ]-----");
        commandSender.sendMessage(ChatColor.YELLOW + "Nation: " + ChatColor.GOLD + ((town.getNationName() == null) ? "None" : town.getNationName()));
        commandSender.sendMessage(ChatColor.YELLOW + "Mayor: " + ChatColor.GOLD + town.getMayorName());
        commandSender.sendMessage(ChatColor.YELLOW + "Balance: " + ChatColor.GOLD + town.getBalance());
        String assistantsList = "";
        Iterator iterator = town.getAssistantsList().iterator();
        while(iterator.hasNext()){
            assistantsList += iterator.next();
            if(iterator.hasNext()){
                assistantsList += ",";
            }
        }
        commandSender.sendMessage(ChatColor.YELLOW + "Assistants: " + ChatColor.GOLD + assistantsList);
        String residentsList = "";
        iterator = town.getResidents().iterator();
        int count = 1;
        while(iterator.hasNext()){
            residentsList += iterator.next()+ ((count < 10) ? ", " : " ");
            if(count == 10) {
                residentsList += "and " + (town.getResidents().size() - count) + " more";
                break;
            }
        }
        commandSender.sendMessage(ChatColor.YELLOW + "Citizens: " + ChatColor.GOLD + residentsList);
        String endString = "-------";
        for(int i = 0; i < town.getName().length(); i++){
            endString += "-";
        }
        endString += "-------";
        commandSender.sendMessage(ChatColor.YELLOW + endString);
    }
}
