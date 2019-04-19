package it.tigierrei.towny3d.commands;

import it.tigierrei.towny3d.Towny3D;
import it.tigierrei.towny3d.residents.Resident;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TownChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        Resident resident = Towny3D.getDataManager().getResidentManager().getResidentList().get(commandSender.getName());
        if(resident.getTown() == null){
            commandSender.sendMessage(ChatColor.RED+ "Devi essere in una città per usare questo comando!");
            return true;
        }
        if(args.length > 0){
            String message = "";
            for(int i = 0; i < args.length; i++){
                message += args[i];
                if(i+1 != args.length){
                    message += " ";
                }
            }
            resident.setChat("TC");
            ((Player)commandSender).chat(message);
            resident.setChat("GL");
        }else if(resident.getChat().equalsIgnoreCase("tc")){
            resident.setChat("GL");
            commandSender.sendMessage(ChatColor.AQUA + "Chat globale abilitata!");
        }else{
            resident.setChat("TC");
            commandSender.sendMessage(ChatColor.AQUA + "TownChat abilitata!");
        }
        return true;
    }

}
