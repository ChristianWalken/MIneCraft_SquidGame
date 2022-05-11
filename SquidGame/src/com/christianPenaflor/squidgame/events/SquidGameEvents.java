package com.christianPenaflor.squidgame.events;

import com.christianPenaflor.squidgame.SquidGameGUI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;


public class SquidGameEvents implements Listener {
    //0 or False is Red, 1 or True is Green


    public SquidGameGUI plugin;


    public SquidGameEvents(SquidGameGUI plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        //for player teleport the spawn locations is hard coded to the map we are using.
        Location loc = new Location(Bukkit.getWorld("flat-world"), 10.05, -60.00, -16.56);
        player.teleport(loc);
        player.sendMessage(ChatColor.LIGHT_PURPLE + "Welcome to the Server! :O");
        player.setGameMode(GameMode.ADVENTURE);
    }


    @EventHandler
    public void onRespawnEVENT(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        double x = 10.05;
        double y = -50.00;
        double z = -16.56;
        Location loc = new Location(Bukkit.getWorld("flat-world"), x, y, z);
        event.setRespawnLocation(loc);
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.winners.remove(player.getName());
        plugin.losers.remove(player.getName());
    }

    @EventHandler
    public void onKicked(PlayerKickEvent event) {
        Player player = event.getPlayer();
        plugin.winners.remove(player.getName());
        plugin.losers.remove(player.getName());
    }

    @EventHandler
    public void onPlayerWalk(PlayerMoveEvent event){
        Player player = event.getPlayer();
        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();

        Material block = player.getWorld().getBlockAt(x, y-1, z).getType();
        if ((block == Material.SANDSTONE) & !(plugin.returnLight()) & (plugin.returnGameMode())){
           onRedLightTrigger(player);
        } else if ((block == Material.GOLD_BLOCK)  & (plugin.returnGameMode())){
            onFinishLineTrigger(player);
        }

    }

    public void onRedLightTrigger(Player player){
        player.sendMessage(ChatColor.RED + "You Moved during Red Light. You are Dead!");
        plugin.addLosers(player.getName());
        player.setHealth(0);
        //if winner (for some unknown reason) goes back to the kill area while the game hasn't finished yet. He gets removed from the winners.
        plugin.winners.remove(player.getName());


        //CHECK IF PLAYER DEATH ENDS THE GAME
        //if all the players dies.
        if(plugin.losers.size() == Bukkit.getOnlinePlayers().size() ){
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(ChatColor.RED + "Everyone died!");
            }
            plugin.endGame();
        }
        //If the last player dies.
        if(plugin.losers.size() +plugin.winners.size() == Bukkit.getOnlinePlayers().size()){
            plugin.endGame();
        }


    }

    public void onFinishLineTrigger(Player player){
        //if winner is not yet in the list of winners
        if(!(plugin.winners.contains(player.getName()))){
            plugin.addWinners(player.getName());
        }


        //CHECK IF A PLAYER'S WIN SHOULD END THE GAME
        //If all players win
        if(plugin.winners.size() == Bukkit.getOnlinePlayers().size() ){
            for (Player p : Bukkit.getOnlinePlayers()) {
                player.sendMessage(ChatColor.RED + "Everyone wins! ...That's not canon though.");
            }
            plugin.endGame();
        }else if(plugin.losers.size() +plugin.winners.size() == Bukkit.getOnlinePlayers().size()){
            //If the last player wins
            plugin.endGame();
        }
    }
}
