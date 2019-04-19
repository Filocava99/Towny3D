package it.tigierrei.towny3d.commands;

import it.tigierrei.towny3d.Towny3D;
import it.tigierrei.towny3d.residents.Resident;
import it.tigierrei.towny3d.towns.Town;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NationChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(commandSender.getName());
        if(resident.getTown() == null){
            commandSender.sendMessage(ChatColor.RED+ "Devi essere in una città per usare questo comando!");
            return true;
        }else{
            Town town = Towny3D.getDataManager().getTownManager().getTownList().get(resident.getTown());
            if(town.getNationName() == null){
                commandSender.sendMessage(ChatColor.RED+ "La tua città deve essere in una nazione per usare questo comando!");
                return true;
            }
        }
        if(args.length > 0){
            String message = "";
            for(String s : args){
                message += " " + s;
            }
            resident.setChat("NC");
            ((Player)commandSender).chat(message);
            resident.setChat("GL");
        }else if(resident.getChat().equalsIgnoreCase("nc")){
            resident.setChat("GL");
            commandSender.sendMessage(ChatColor.AQUA + "Chat globale abilitata!");
        }else{
            resident.setChat("NC");
            commandSender.sendMessage(ChatColor.AQUA + "NationChat abilitata!");
        }
        return true;
    }

}
