package it.tigierrei.towny3d.commands;

import it.tigierrei.towny3d.Towny3D;
import it.tigierrei.towny3d.regions.Region;
import it.tigierrei.towny3d.residents.Resident;
import it.tigierrei.towny3d.towns.Town;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlotCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {
        Player player = (Player) commandSender;

        //SENZA PARAMETRI == HELP
        if (args.length == 0) {
            printPlotHelp(commandSender);
            return true;
        }

        //HELP
        if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
            if (commandSender.hasPermission("plot.help")) {
                printPlotHelp(commandSender);
                return true;
            } else {
                noPermissionWarning(commandSender);
                return true;
            }
        }


        /*
        //SETFLAG
        if(args[0].equalsIgnoreCase("setflag") && !(args.length < 3)){
            //Controllo se ha i permessi per settare il flag specificato
            if(commandSender.hasPermission("plot.setflag."+args[1])){
                Region region = Towny3D.getDataManager().getRegionManager().getRegion(((Player) commandSender).getLocation());
                Town town = Towny3D.getDataManager().getTownManager().getTownList().get(region.getTown());
                //Controllo se è il proprietario del plot, se è un assistente o se è il sindaco
                if((region.getOwner()!= null && region.getOwner().equalsIgnoreCase(commandSender.getName())) ||
                        town.getMayorName().equalsIgnoreCase(commandSender.getName()) ||
                        town.getAssistantsList().contains(commandSender.getName())){
                    if(Towny3D.getDataManager().getRegionManager().setRegionProperty(((Player) commandSender).getLocation(),args[1],Boolean.valueOf(args[2]))){
                        commandSender.sendMessage(ChatColor.GREEN + "Flag successfully set!");
                        return true;
                    }else{
                        commandSender.sendMessage(ChatColor.RED + "Impossible to set the flag!");
                        return true;
                    }
                }else{
                    commandSender.sendMessage(ChatColor.RED + "You are neither the plot's owner nor an assistant/mayor of the town!");
                    return true;
                }
            }else{
                noPermissionWarning(commandSender);
            }
        }
        */

        //SET
        if(args[0].equalsIgnoreCase("set")){
            if(args.length < 2){
                commandSender.sendMessage(ChatColor.RED + "You must pass more arguments!");
                return true;
            }
            //EMBASSY
            if(args[1].equalsIgnoreCase("embassy")){
                if(commandSender.hasPermission("plot.embassy")){
                    Region region = Towny3D.getDataManager().getRegionManager().getRegion(player.getLocation());
                    //Se il giocatore è in wild (region == null) lo avviso e termino la funzione
                    if(region == null){
                        commandSender.sendMessage(ChatColor.RED + "You are in wilderness!");
                        return true;
                    }
                    //Controllo che non stia trasformando il plot home in un'embassy
                    if(region.getType().equalsIgnoreCase("home")){
                        commandSender.sendMessage(ChatColor.RED + "You must move your current town home before");
                        return true;
                    }
                    Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(commandSender.getName());
                    //Se il giocatore non ha una città lo avviso e termino la funzione
                    if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()) == null){
                        commandSender.sendMessage(ChatColor.RED + "You are not a citizen of a town so you can't use that command!");
                        return true;
                    }
                    //Controllo se è il proprietario del plot, se è un assistente o se è il sindaco
                    if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getMayorUuid().equals(player.getUniqueId()) || Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getAssistantsList().contains(player.getName())){
                        if(Towny3D.getDataManager().getRegionManager().setRegionProperty(player.getLocation(),"type","embassy")){
                            commandSender.sendMessage(ChatColor.GREEN + "Embassy successfully created!");
                        }else{
                            commandSender.sendMessage(ChatColor.RED + "Error while creating the embassy!");
                        }
                        return true;
                    }else{
                        commandSender.sendMessage(ChatColor.RED + "You are neither the plot's owner nor an assistant/mayor of the town!");
                        return true;
                    }
                }else{
                    noPermissionWarning(commandSender);
                    return true;
                }
            }
            //PERM
            if(args[1].equalsIgnoreCase("perm")){
                if(args.length < 5){
                    commandSender.sendMessage(ChatColor.RED + "You must pass more arguments!");
                    return true;
                }
                //String target = args[2];
                //String perm = args[3];
                //boolean value = Boolean.valueOf(args[4]);
                if(commandSender.hasPermission("plot.perm."+args[2]+"_"+args[3])){
                    Region region = Towny3D.getDataManager().getRegionManager().getRegion(((Player) commandSender).getLocation());
                    Town town = Towny3D.getDataManager().getTownManager().getTownList().get(region.getTown());
                    //Controllo se è il proprietario del plot, se è un assistente o se è il sindaco
                    if((region.getOwner()!= null && region.getOwner().equalsIgnoreCase(commandSender.getName())) ||
                            town.getMayorName().equalsIgnoreCase(commandSender.getName()) ||
                            town.getAssistantsList().contains(commandSender.getName())){
                        if(Towny3D.getDataManager().getRegionManager().setRegionProperty(((Player) commandSender).getLocation(),args[2]+"_"+args[3],Boolean.valueOf(args[4]))){
                            commandSender.sendMessage(ChatColor.GREEN + "Perm successfully set!");
                            return true;
                        }else{
                            commandSender.sendMessage(ChatColor.RED + "Impossible to set the perm!");
                            return true;
                        }
                    }else{
                        commandSender.sendMessage(ChatColor.RED + "You are neither the plot's owner nor an assistant/mayor of the town!");
                        return true;
                    }
                }else{
                    noPermissionWarning(commandSender);
                    return true;
                }
            }

            //RESET
            if(args[1].equalsIgnoreCase("reset")){
                if(commandSender.hasPermission("plot.reset")){
                    Region region = Towny3D.getDataManager().getRegionManager().getRegion(((Player) commandSender).getLocation());
                    Town town = Towny3D.getDataManager().getTownManager().getTownList().get(region.getTown());
                    //Controllo se è il proprietario del plot, se è un assistente o se è il sindaco
                    if((region.getOwner()!= null && region.getOwner().equalsIgnoreCase(commandSender.getName())) ||
                            town.getMayorName().equalsIgnoreCase(commandSender.getName()) ||
                            town.getAssistantsList().contains(commandSender.getName())){
                        if(Towny3D.getDataManager().getRegionManager().setRegionProperty(((Player) commandSender).getLocation(),"type","normal")){
                            commandSender.sendMessage(ChatColor.GREEN + "Plot successfully reset!");
                            return true;
                        }else{
                            commandSender.sendMessage(ChatColor.RED + "Impossible to reset the plot!");
                            return true;
                        }
                    }else{
                        commandSender.sendMessage(ChatColor.RED + "You are neither the plot's owner nor an assistant/mayor of the town!");
                        return true;
                    }
                }else{
                    noPermissionWarning(commandSender);
                    return true;
                }
            }

        }

        //TOGGLE
        if(args[0].equalsIgnoreCase("toggle")){
            if(args.length < 2){
                commandSender.sendMessage(ChatColor.RED + "You mast pass more arguments!");
                return true;
            }
            if(commandSender.hasPermission("plot.toggle."+args[1])){
                Region region = Towny3D.getDataManager().getRegionManager().getRegion(((Player) commandSender).getLocation());
                Town town = Towny3D.getDataManager().getTownManager().getTownList().get(region.getTown());
                //Controllo se è il proprietario del plot, se è un assistente o se è il sindaco
                if((region.getOwner()!= null && region.getOwner().equalsIgnoreCase(commandSender.getName())) ||
                        town.getMayorName().equalsIgnoreCase(commandSender.getName()) ||
                        town.getAssistantsList().contains(commandSender.getName())){
                        boolean value;
                        if(args[1].equalsIgnoreCase("fire")){
                            value = region.isFire();
                        }else if(args[1].equalsIgnoreCase("explosion")){
                            value = region.isExplosion();
                        }else if(args[1].equalsIgnoreCase("friendly_mobs_spawn") || args[1].equalsIgnoreCase("fms")){
                            value = region.isFriendly_mobs_spawn();
                        }else if(args[1].equalsIgnoreCase("hostile_mobs_spawn") || args[1].equalsIgnoreCase("hms")){
                            value = region.isHostile_mobs_spawn();
                        }else{
                            commandSender.sendMessage(ChatColor.RED + "The specified flag does not exist!");
                            return true;
                        }
                    if(Towny3D.getDataManager().getRegionManager().setRegionProperty(((Player) commandSender).getLocation(),args[1],!value)){
                        commandSender.sendMessage(ChatColor.GREEN + "Flag successfully set!");
                        return true;
                    }else{
                        commandSender.sendMessage(ChatColor.RED + "Impossible to set the flag!");
                        return true;
                    }
                }else{
                    commandSender.sendMessage(ChatColor.RED + "You are neither the plot's owner nor an assistant/mayor of the town!");
                    return true;
                }
            }else{
                noPermissionWarning(commandSender);
                return true;
            }
        }

        //FORSALE
        if(args[0].equalsIgnoreCase("forsale") || args[0].equalsIgnoreCase("fs")){
            if(commandSender.hasPermission("plot.sell")){
                Region region = Towny3D.getDataManager().getRegionManager().getRegion(player.getLocation());
                //Se il giocatore è in wild (region == null) lo avviso e termino la funzione
                if(region == null){
                    commandSender.sendMessage(ChatColor.RED + "You are in wilderness!");
                    return true;
                }
                Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(commandSender.getName());
                //Se il giocatore non ha una città lo avviso e termino la funzione
                if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()) == null){
                    commandSender.sendMessage(ChatColor.RED + "You are not a citizen of a town so you can't use that command!");
                    return true;
                }
                if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getMayorUuid().equals(player.getUniqueId()) || Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getAssistantsList().contains(player.getName())){
                    boolean f1 = Towny3D.getDataManager().getRegionManager().setRegionProperty(player.getLocation(),"for_sale",true);
                    double price = (args.length >= 2) ? Double.parseDouble(args[1]) : 0.0;
                    boolean f2 = Towny3D.getDataManager().getRegionManager().setRegionProperty(player.getLocation(),"value",price);
                    if(f1 && f2){
                        player.sendMessage(ChatColor.GREEN + "Plot put for sale for " + price + " $");
                    }else{
                        player.sendMessage(ChatColor.RED + "Error while putting the plot for sale!");
                    }
                    return true;
                }
            }else{
                noPermissionWarning(commandSender);
                return true;
            }
        }

        //NOTFORSALE
        if(args[0].equalsIgnoreCase("notforsale") || args[0].equalsIgnoreCase("nfs")){
            if(commandSender.hasPermission("plot.sell")){
                Region region = Towny3D.getDataManager().getRegionManager().getRegion(player.getLocation());
                //Se il giocatore è in wild (region == null) lo avviso e termino la funzione
                if(region == null){
                    commandSender.sendMessage(ChatColor.RED + "You are in wilderness!");
                    return true;
                }
                Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(commandSender.getName());
                //Se il giocatore non ha una città lo avviso e termino la funzione
                if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()) == null){
                    commandSender.sendMessage(ChatColor.RED + "You are not a citizen of a town so you can't use that command!");
                    return true;
                }
                //Controllo che il giocatore sia il proprietario, il sindaco o un assistente
                if(Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getMayorUuid().equals(player.getUniqueId()) || Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getAssistantsList().contains(player.getName())){
                    boolean f1 = Towny3D.getDataManager().getRegionManager().setRegionProperty(player.getLocation(),"for_sale",false);
                    if(f1){
                        commandSender.sendMessage(ChatColor.GREEN + "Plot no more for sale!");
                    }else{
                        commandSender.sendMessage(ChatColor.RED + "Error while toggling the plot for sale!");
                    }
                    return true;
                }else{
                    commandSender.sendMessage(ChatColor.RED + "You are neither the plot's owner nor an assistant/mayor of the town!");
                    return true;
                }
            }else{
                noPermissionWarning(commandSender);
                return true;
            }
        }

        //CLAIM
        if(args[0].equalsIgnoreCase("claim")){
            if(commandSender.hasPermission("plot.claim")){
                Region region = Towny3D.getDataManager().getRegionManager().getRegion(player.getLocation());
                //Se il giocatore è in wild lo avviso e termino la funzione
                if(region == null){
                    commandSender.sendMessage(ChatColor.RED + "You are not in town's borders!");
                    return true;
                }
                Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(player.getName());
                //Controllo che il plot sia embassy o che il giocatore sia un cittadino della città del plot
                if(region.getType().equalsIgnoreCase("embassy") || (Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()) != null && Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown()).getName().equalsIgnoreCase(region.getTown()))){
                    double price = region.getValue();
                    //Controllo che il giocatore abbia abbastanza soldi
                    if(Towny3D.economy.getBalance(Bukkit.getOfflinePlayer(player.getUniqueId())) >= price){
                        //Tolgo i soldi al giocatore
                        Towny3D.economy.withdrawPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()),price);
                        //Imposto il giocatore come nuovo proprietario del plot
                        Towny3D.getDataManager().getRegionManager().setRegionOwner(player.getLocation(),player.getName());
                        //Aggiungo i soldi alla banca cittadina
                        Town town = Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown());
                        Towny3D.getDataManager().getTownManager().setMoney(town,town.getBalance() + price);
                        //Metto il plot come non più in vendita
                        Towny3D.getDataManager().getRegionManager().setRegionProperty(player.getLocation(),"for_sale",false);
                        commandSender.sendMessage(ChatColor.GREEN + "Plot successfully bought!");
                        return true;
                    }else{
                        commandSender.sendMessage(ChatColor.RED + "You don't have enough moneys to buy this plot!");
                        return true;
                    }
                }else{
                    commandSender.sendMessage(ChatColor.RED + "You are not the citizen of this town or the plot is not an embassy");
                    return true;
                }
            }else{
                noPermissionWarning(commandSender);
                return true;
            }
        }

        //UNCLAIM
        if(args[0].equalsIgnoreCase("unclaim")) {
            if (commandSender.hasPermission("plot.claim")) {
                Region region = Towny3D.getDataManager().getRegionManager().getRegion(player.getLocation());
                //Se il giocatore è in wild lo avviso e termino la funzione
                if (region == null) {
                    commandSender.sendMessage(ChatColor.RED + "You are not in town's borders!");
                    return true;
                }
                Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(player.getName());
                Town regionTown = Towny3D.getDataManager().getTownManager().getTown(region.getTown());
                //Controllo che il giocatore sia il proprietario, il sindaco o un assistente
                if (region.getOwner().equalsIgnoreCase(commandSender.getName()) || regionTown.getMayorUuid().equals(((Player) commandSender).getUniqueId()) || regionTown.getAssistantsList().contains(commandSender.getName())) {

                    Towny3D.getDataManager().getRegionManager().setRegionProperty(player.getLocation(), "owner", null);
                    commandSender.sendMessage(ChatColor.GREEN + "Plot successfully set free!");
                    return true;
                }else{
                    commandSender.sendMessage(ChatColor.RED + "You are neither the plot's owner nor an assistant/mayor of the town!");
                    return true;
                }
            } else {
                noPermissionWarning(commandSender);
                return true;
            }
        }

        //NAME
        if(args[0].equalsIgnoreCase("name")){
            if(commandSender.hasPermission("plot.name")){
                if(args.length < 2){
                    commandSender.sendMessage(ChatColor.RED + "You must pass more arguments!");
                    return true;
                }
                Region region = Towny3D.getDataManager().getRegionManager().getRegion(player.getLocation());
                //Se il giocatore è in wild lo avviso e termino la funzione
                if (region == null) {
                    commandSender.sendMessage(ChatColor.RED + "You are not inside your town's borders!");
                    return true;
                }
                Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(player.getName());
                Town regionTown = Towny3D.getDataManager().getTownManager().getTown(region.getTown());
                //Controllo che il giocatore sia il proprietario, il sindaco o un assistente
                if (region.getOwner().equalsIgnoreCase(commandSender.getName()) || regionTown.getMayorUuid().equals(((Player) commandSender).getUniqueId()) || regionTown.getAssistantsList().contains(commandSender.getName())) {
                    Towny3D.getDataManager().getRegionManager().setRegionName(player.getLocation(), args[1]);
                    commandSender.sendMessage(ChatColor.GREEN + "Plot successfully renamed!");
                    return true;
                }else{
                    commandSender.sendMessage(ChatColor.RED + "You are neither the plot's owner nor an assistant/mayor of the town!");
                    return true;
                }
            }else{
                noPermissionWarning(commandSender);
                return true;
            }
        }

        //INFO
        if(args[0].equalsIgnoreCase("info")){
            if(commandSender.hasPermission("plot.info")){
                Region region = Towny3D.getDataManager().getRegionManager().getRegion(player.getLocation());
                if(region == null){
                    commandSender.sendMessage(ChatColor.YELLOW + "You are in wilderness!");
                }else{
                    commandSender.sendMessage(ChatColor.DARK_GREEN + "-----[ Plot Info ]-----");
                    commandSender.sendMessage(ChatColor.GREEN + "Owner: " + ChatColor.YELLOW + ((region.getOwner() == null) ? "Noone" : region.getOwner()));
                    commandSender.sendMessage(ChatColor.GREEN + "Town: " + ChatColor.YELLOW + region.getTown());
                    commandSender.sendMessage(ChatColor.GREEN + "Name: " + ChatColor.YELLOW + ((region.getName () == null) ? "" : region.getName()));
                    commandSender.sendMessage(ChatColor.GREEN + "Type: " + ChatColor.YELLOW + region.getType());
                    commandSender.sendMessage(ChatColor.GREEN + "Fire: " + ChatColor.YELLOW + region.isFire());
                    commandSender.sendMessage(ChatColor.GREEN + "Explosion: " + ChatColor.YELLOW + region.isExplosion());
                    commandSender.sendMessage(ChatColor.GREEN + "Friendly mobs: " + ChatColor.YELLOW + region.isFriendly_mobs_spawn());
                    commandSender.sendMessage(ChatColor.GREEN + "Hostile mobs: " + ChatColor.YELLOW + region.isHostile_mobs_spawn());
                    commandSender.sendMessage(ChatColor.GREEN + "PERMS: " + ChatColor.YELLOW + "Resident" + ChatColor.GREEN + "/" + ChatColor.YELLOW + "Ally" + ChatColor.GREEN + "/" + ChatColor.YELLOW + "General");
                    commandSender.sendMessage(ChatColor.GREEN + "PvP: " + ChatColor.YELLOW + region.isRes_pvp() + ChatColor.GREEN + "/" + ChatColor.YELLOW + region.isAlly_pvp() + ChatColor.GREEN + "/" + ChatColor.YELLOW + region.isGen_pvp());
                    commandSender.sendMessage(ChatColor.GREEN + "Inventory: " + ChatColor.YELLOW + region.isRes_inventory() + ChatColor.GREEN + "/" + ChatColor.YELLOW + region.isAlly_inventory() + ChatColor.GREEN + "/" + ChatColor.YELLOW + region.isGen_inventory());
                    commandSender.sendMessage(ChatColor.GREEN + "Interact: " + ChatColor.YELLOW + region.isRes_interact() + ChatColor.GREEN + "/" + ChatColor.YELLOW + region.isAlly_interact() + ChatColor.GREEN + "/" + ChatColor.YELLOW + region.isGen_interact());
                    commandSender.sendMessage(ChatColor.GREEN + "Build: " + ChatColor.YELLOW + region.isRes_build() + ChatColor.GREEN + "/" + ChatColor.YELLOW + region.isAlly_build() + ChatColor.GREEN + "/" + ChatColor.YELLOW + region.isGen_build());
                    commandSender.sendMessage(ChatColor.GREEN + "Destroy: " + ChatColor.YELLOW + region.isRes_destroy() + ChatColor.GREEN + "/" + ChatColor.YELLOW + region.isAlly_destroy() + ChatColor.GREEN + "/" + ChatColor.YELLOW + region.isGen_destroy());
                    commandSender.sendMessage(ChatColor.GREEN + "Drop: " + ChatColor.YELLOW + region.isRes_drop() + ChatColor.GREEN + "/" + ChatColor.YELLOW + region.isAlly_drop() + ChatColor.GREEN + "/" + ChatColor.YELLOW + region.isGen_drop());
                    commandSender.sendMessage(ChatColor.GREEN + "Pick: " + ChatColor.YELLOW + region.isRes_pick() + ChatColor.GREEN + "/" + ChatColor.YELLOW + region.isAlly_pick() + ChatColor.GREEN + "/" + ChatColor.YELLOW + region.isGen_pick());
                    commandSender.sendMessage(ChatColor.GREEN + "Move: " + ChatColor.YELLOW + region.isRes_move() + ChatColor.GREEN + "/" + ChatColor.YELLOW + region.isAlly_move() + ChatColor.GREEN + "/" + ChatColor.YELLOW + region.isGen_move());
                    commandSender.sendMessage(ChatColor.DARK_GREEN + "-----------------------");
                }
            }else{
                noPermissionWarning(commandSender);
                return true;
            }
        }

        printPlotHelp(commandSender);
        return true;
    }



    private void printPlotHelp(@NotNull CommandSender sender){
        sender.sendMessage(ChatColor.DARK_GREEN + "=========[ Plot Help ]=========");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/plot ? (or) help" + ChatColor.RESET + "" + ChatColor.GREEN + "to show all available commands for towns");
        //TODO HELP FOR /plot set perm
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/plot toggle <flag> " + ChatColor.RESET + "" + ChatColor.GREEN + "to enable/disable a flag (FIRE,EXPLOSION,etc)");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/plot set perm/reset/embassy " + ChatColor.RESET + "" + ChatColor.GREEN + "to set a perm/to reset the plot/to set the plot as embassy");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/plot forsale (or fs) <price> " + ChatColor.RESET + "" + ChatColor.GREEN + "to put for sale a plot at the specified price");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/plot notforsale (or nfs) " + ChatColor.RESET + "" + ChatColor.GREEN + "to toggle for sale a plot");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/plot claim " + ChatColor.RESET + "" + ChatColor.GREEN + "to claim the plot");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/plot unclaim " + ChatColor.RESET + "" + ChatColor.GREEN + "to unclaim the plot");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/plot name <name> " + ChatColor.RESET + "" + ChatColor.GREEN + "to set the plot name");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "/plot info " + ChatColor.RESET + "" + ChatColor.GREEN + "to see the plot info");
        sender.sendMessage(ChatColor.DARK_GREEN + "=====[ Made by Tito Tigi ]=====");
    }

    private void noPermissionWarning(@NotNull CommandSender sender){
        sender.sendMessage(ChatColor.RED + "You don't have the permission to run that command!");
    }
}
