package com.christianPenaflor.squidgame.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.christianPenaflor.squidgame.SquidGameGUI;


public class SquidGameCommands implements CommandExecutor {

    public SquidGameGUI plugin;

    public SquidGameCommands(SquidGameGUI plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use that command!");
            return true;
        }

        if(!(sender.isOp())){
            sender.sendMessage(ChatColor.RED+  "You are not an op!");
            return true;
        }
        Player player = (Player) sender;


        if (cmd.getName().equalsIgnoreCase("start_sgame")){

            //if game Mode is already On you cannot
            if(plugin.returnGameMode()){
                sender.sendMessage(ChatColor.RED+ "You are already in game!");

                return false;
            }

            plugin.startGame();
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("end_sgame")){
            //if game Mode is already On you cannot
            if(!(plugin.returnGameMode())){
                sender.sendMessage(ChatColor.RED+ "The game hasn't even started yet!");
                return false;
            }


            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage("§e§l(!)ANOUNCEMENT(!) §e Squid Game Ended by Moderator!");
            }
            plugin.endGame();
            return true;
        }

        return true;
    }





}
