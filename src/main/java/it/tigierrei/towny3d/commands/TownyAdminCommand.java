package it.tigierrei.towny3d.commands;

import it.tigierrei.towny3d.Towny3D;
import it.tigierrei.towny3d.residents.Resident;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TownyAdminCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(args.length < 1){
            //TODO Print help
            return true;
        }
        if(args[0].equalsIgnoreCase("chat")){
            if(args.length > 1){
                if(args[1].equalsIgnoreCase("spy")){
                    if(commandSender.hasPermission("towny3d.admin.chat.spy")){
                        Resident resident = Towny3D.getDataManager().getResidentManager().getResident((Player)commandSender);
                        if(resident.isSpyChat()){
                            resident.setSpyChat(false);
                            commandSender.sendMessage(ChatColor.YELLOW + "Spy chat " + ChatColor.GOLD + "disabled");
                            return true;
                        }else{
                            resident.setSpyChat(true);
                            commandSender.sendMessage(ChatColor.YELLOW + "Spy chat " + ChatColor.GOLD + "enabled");
                            return true;
                        }
                    }else{
                        noPermissionWarning(commandSender);
                    }
                }
            }else{
                //TODO Print admin chat help
                return true;
            }
        }
        return true;
    }

    private void noPermissionWarning(@NotNull CommandSender sender){
        sender.sendMessage(ChatColor.RED + "You don't have the permissions to run that command!");
    }
}
