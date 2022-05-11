package com.christianPenaflor.squidgame;

import com.christianPenaflor.squidgame.commands.GeneralCommands;
import com.christianPenaflor.squidgame.commands.SquidGameCommands;
import com.christianPenaflor.squidgame.events.SquidGameEvents;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SquidGameGUI extends JavaPlugin{
    public boolean lightstatus = true;
    public boolean gameMode = false;
    public  int second = (int) 20L;
    public int dollId, counter, randDelay, timerId;
    public Random rand = new Random();
    public boolean intervalStarted = false;
    public List<String> winners = new ArrayList<>();
    public List<String> losers = new ArrayList<>();
    public int timeLeft = (int) 1800L;
    public Score score;
    public Scoreboard scoreboard;
    public ScoreboardManager manager;
    public Objective objective;


    @Override
    public void onEnable(){
        GeneralCommands generalCommands = new GeneralCommands();
        SquidGameCommands squidCommands = new SquidGameCommands(this);
        getServer().getPluginManager().registerEvents(new SquidGameEvents(this ), this);
        getCommand( "heal").setExecutor(generalCommands);
        getCommand( "feed").setExecutor(generalCommands);
        getCommand( "start_sgame").setExecutor(squidCommands);
        getCommand( "end_sgame").setExecutor(squidCommands);
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[SquidGame]: Plugin is enabled!");
    }

    @Override
    public void onDisable(){
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[SquidGame]: Plugin is disabled!");
    }

    //Represents the doll in game
    //will swithch light according to random interval
    //will also hold a timer for the game
    public void gameDoll(){
        lightstatus = true;
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(ChatColor.GREEN + "Green Light!", null, 1, 10, 1);
        }

        timerId = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                if(timeLeft==0){
                    endGame();
                }
                scoreboard.resetScores("Time Remaining: "  + returnTime(timeLeft));
                timeLeft = timeLeft - 20;
                score = objective.getScore("Time Remaining: "  + returnTime(timeLeft));
                score.setScore(3);

            }
            }, second, second);

        dollId = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {

                 if(intervalStarted){ //continuing an interval
                     counter ++;//Increment the interval
                     if((counter +4 == randDelay)&gameMode){ //Warn early if its gonna be Red Light by a second.
                        lightWarning();

                     } else  if((counter +1 == randDelay)&!gameMode){ //Warn early if its gonna be Green light sooner by 1/4 of a second
                         lightWarning();

                     } else if(counter == randDelay){    //if the counter has reached the delay
                        intervalStarted =false; //We start the interval again
                        //perform the action of switching
                        switchLight();

                    }

                } else{ //This is start of the interval
                    intervalStarted = true; //start the interval
                    randDelay= rand.nextInt( 16) + 10;//generate a random int that will be the delay.
                    counter = 0; //begin the counter as zero

                }
            }
        }, 0L, second/4);
    }


    //switches the light from red to green/ green to red
    //Messages afterwards
    public void lightWarning() {
        if (lightstatus) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle(ChatColor.RED + "Red Light!", null, 1, 10, 1);
            }
        } else {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle(ChatColor.GREEN + "Green Light!", null, 1, 10, 1);
            }
        }
    }


    public void startGame(){
        timeLeft = (int) 1200L;
        //Initiate creating the scoreboard
        createScoreboard();

        for (Player p : Bukkit.getOnlinePlayers()) {
            //teleport to spawn are in the map
            p.teleport(new Location(Bukkit.getWorld("flat-world"), 10.05, -60.00, -16.56));

            //Hal to full-health and max feed
            double maxHealth = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue();
            p.setHealth(maxHealth);
            p.setFoodLevel(20);
            //Start Scoreboard Timer
            p.sendMessage("§e§l(!)ANOUNCEMENT(!) §e Squid Game Started!");
            p.setScoreboard(scoreboard);
        }

        gameOn();
        gameDoll();

        winners.clear();
        losers.clear();
    }

    public void endGame(){
        for (Player p : Bukkit.getOnlinePlayers()) {
            Location loc = new Location(Bukkit.getWorld("flat-world"), 10.05, -60.00, -16.56);
            p.teleport(loc);
        }

        gameOff();
        killDoll();

        printWinners();


        winners.clear();
        losers.clear();
    }



    //switches the light from red to green/ green to red
    //Messages afterwards
    public void switchLight(){
        if(lightstatus){
            lightstatus = false;
        } else{
            lightstatus = true;
        }
    }
    //kills the doll
    //Used for ending
    public void killDoll(){
        this.getServer().getScheduler().cancelTask(dollId);
        this.getServer().getScheduler().cancelTask(timerId);
        //Besides killing the scheduler, return the interval Started to initial value of false
        intervalStarted = false;
    }


    //Given ticks (which is 20L - second) return the coressponding minute time format
    public String returnTime(int t){
        int seconds = t/20;
        int minutes = seconds/60;
        seconds = seconds - (minutes * 60);

        if(seconds < 10){
            return (minutes + ":0" + seconds);
        }
        return (minutes + ":" + seconds);
    }

    //Create the timer in the upper right. Will start showing when the first squid game shows up.
    public void createScoreboard(){
        manager = this.getServer().getScoreboardManager();
        scoreboard = manager.getNewScoreboard();
        objective = scoreboard.registerNewObjective("Test", "dummy",ChatColor.YELLOW + "ScoreBoard");

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        scoreboard.resetScores("Time Remaining: "  + returnTime(timeLeft));
        score = objective.getScore("Time Remaining: "  + returnTime(timeLeft));
        score.setScore(3);

    }

    public void printWinners(){
        //If there is a winner
        if(!(winners.isEmpty())) {

            scoreboard.resetScores("Remaining: "  + returnTime(timeLeft));

            int countWinners = 1;
            score = objective.getScore("§e§l(!)Squid Game Winners!");
            for (String winner : winners) {
                score = objective.getScore(ChatColor.WHITE + (countWinners + ": " + winner));
                score.setScore(1);
                countWinners ++;
            }

        }else{
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage("§e§l(!)ANOUNCEMENT(!) §e There are no Winners!");
            }
        }
    }


    public boolean returnLight(){return lightstatus;}
    public void gameOn(){
        gameMode = true;
    }
    public void gameOff(){
        gameMode = false;
    }
    public boolean returnGameMode(){
        return gameMode;
    }
    public void addWinners(String winner){winners.add(winner);}
    public void addLosers(String loser){losers.add(loser);}

}
