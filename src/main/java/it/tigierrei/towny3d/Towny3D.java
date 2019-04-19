package it.tigierrei.towny3d;

import it.tigierrei.towny3d.commands.*;
import it.tigierrei.towny3d.listeners.ChatListener;
import it.tigierrei.towny3d.listeners.MobListener;
import it.tigierrei.towny3d.listeners.PlayerListener;
import it.tigierrei.towny3d.listeners.WorldListener;
import it.tigierrei.towny3d.managers.DataManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Towny3D extends JavaPlugin {

    //TODO  Thread che controlli se dentro i plot CITTADINI ci sono mob non desiderati

    private static DataManager dataManager;

    //Vault
    public static Permission permission = null;
    public static Economy economy = null;
    public static Chat chat = null;

    @Override
    public void onEnable(){

        //Hook with Vault
        if(getServer().getPluginManager().getPlugin("Vault") != null){
            setupChat();
            setupEconomy();
            setupPermissions();
        }else{
            //Se Vault non viene trovato disabilito in plugin
            getServer().getPluginManager().disablePlugin(this);
        }

        dataManager = new DataManager(this);
        dataManager.initializeManagers();

        this.getCommand("town").setExecutor(new TownCommand());
        this.getCommand("nation").setExecutor(new NationCommand());
        this.getCommand("towny").setExecutor(new TownyCommand());
        this.getCommand("plot").setExecutor(new PlotCommand());
        this.getCommand("townyadmin").setExecutor(new TownyAdminCommand());
        this.getCommand("accept").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                Towny3D.getDataManager().getRequestManager().inviteRequestResponse((Player)commandSender,true);
                return true;
            }
        });
        this.getCommand("decline").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                Towny3D.getDataManager().getRequestManager().inviteRequestResponse((Player)commandSender,false);
                return true;
            }
        });
        this.getCommand("townchat").setExecutor(new TownChatCommand());
        this.getCommand("nationchat").setExecutor(new NationChatCommand());

        Bukkit.getPluginManager().registerEvents(new PlayerListener(),this);
        Bukkit.getPluginManager().registerEvents(new MobListener(),this);
        Bukkit.getPluginManager().registerEvents(new WorldListener(),this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(),this);
    }

    @Override
    public void onDisable(){

    }

    public static DataManager getDataManager(){
        return dataManager;
    }



    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    private boolean setupChat()
    {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }

    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
}
