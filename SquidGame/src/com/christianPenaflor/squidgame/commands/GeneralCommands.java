package com.christianPenaflor.squidgame.commands;

import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GeneralCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use that command!");
            return true;
        }

        Player player = (Player) sender;

        //quick heal
        if(cmd.getName().equalsIgnoreCase("heal")){
            healCommand(player);
            return true;
        }

        //feed Command
        if (cmd.getName().equalsIgnoreCase("feed")){
            feedCommand(player);
            return true;
        }
        return true;
    }

    public void healCommand(Player player){
        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue();
        player.setHealth(maxHealth);
        player.sendMessage("§e§l(!) §eYou have been healed!");
    }

    public void feedCommand(Player player){
        player.setFoodLevel(20);
        player.sendMessage("§e§l(!) §eYou have been fed!");
    }

}
