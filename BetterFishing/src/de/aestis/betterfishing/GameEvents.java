package de.aestis.betterfishing;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import java.util.ArrayList;
import java.util.Arrays;

import net.md_5.bungee.api.ChatColor;

public class GameEvents {
	
	private static GameEvents instance;
	String[][] catches = new String[5][2];
	String[][] catchPoints = new String[128][2];
	boolean eventActive = false;
	boolean pointsActive = false;
	public int taskID;
	
	String prefix = ChatColor.GOLD + "[" + ChatColor.GREEN + "BetterFish" + ChatColor.GOLD + "] ";
	
	
	public static GameEvents getInstance() {
		if (instance == null) {
			instance = new GameEvents();
		}
		return instance;
	}
	
	public boolean startBiggestFish(int duration) {
		if (duration == -1) return false;
		catches = new String[5][2];
		for (int i = 0; i < 5; i++) {
			catches[i][0] = "-";
			catches[i][1] = String.valueOf(5 - i);
		}
		
		for (Player pl : Bukkit.getOnlinePlayers()) {
			setScoreboard(pl, duration);
		}
		
		eventActive = true;
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, new Runnable() {
			int totalTime = duration;
			int timer = duration;
			
			@Override
			public void run() {
				if (this.timer == 0 || !eventActive) {
					//Manage Scoreboards
					Bukkit.getScheduler().cancelTask(taskID);
                	for (Player pl : Bukkit.getOnlinePlayers()) {
                		pl.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                	}
                	
                	//Abort
                	if (!eventActive) return;
                	
                	//Show Final Ranks
                	Bukkit.broadcastMessage(prefix + ChatColor.WHITE + "Finale Rangliste (längste Fische):");
                	for (int i = 0; i < 5; i++) {
                		Bukkit.broadcastMessage(ChatColor.GOLD + "Platz " + (i + 1) + ": " + ChatColor.WHITE + catches[i][0] + " (" + catches[i][1] + "cm)");
                	}
                	
                	eventActive = false;
                	return;
                } else if (this.timer == this.totalTime / 2) {
                	Bukkit.broadcastMessage(prefix + ChatColor.GOLD + "Halbzeit!" + ChatColor.WHITE + " Momentan ist " + ChatColor.YELLOW + catches[0][0] + ChatColor.WHITE + " auf dem ersten Platz!");
                }
				if (eventActive && pointsActive) {
					for (int i = 0; i < catchPoints.length; i++) {
						if (catchPoints[i][0] != null) {
							//Bukkit.broadcastMessage(catchPoints[i][0] + ":" + catchPoints[i][1]);
						}
					}
            	}
                this.timer--;
                for (Player pl : Bukkit.getOnlinePlayers()) {
        			setScoreboard(pl, this.timer);
        		}
			}	
		}, 0, 20);
		return true;
	}
	
	public void pushCatch(String playerName, double length) {
		if (eventActive == false) return;

		
		if (eventActive == true) {
			//Sort Highest Scores
			if (length > Double.parseDouble(catches[0][1])) {
				for (int i = 4; i > 0; i--) {
					catches[i][0] = catches[i - 1][0];
					catches[i][1] = catches[i - 1][1];
				}
				catches[0][0] = playerName;
				catches[0][1] = String.valueOf(length);
				
				//Broadcast New 1st
				Bukkit.broadcastMessage(prefix + ChatColor.YELLOW + playerName + ChatColor.WHITE +" ist nun auf dem ersten Platz!");
				return;
			}
			if (length > Double.parseDouble(catches[4][1]) && length < Double.parseDouble(catches[3][1])) {
				catches[4][0] = playerName;
				catches[4][1] = String.valueOf(length);
				return;
			}
			for (int x = 1; x < 4; x++) {
				if (length > Double.parseDouble(catches[x][1]) && length < Double.parseDouble(catches[x - 1][1])) {
					for (int i = 4; i > x; i--) {
						//Fischart auch noch anzeigen
						catches[i][0] = catches[i - 1][0];
						catches[i][1] = catches[i - 1][1];
					}
					catches[x][0] = playerName;
					catches[x][1] = String.valueOf(length);
					return;
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void setScoreboard(Player player, int time) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj;
        if (board.getObjective("BetterFishing") != null) {
        	player.setScoreboard(board);
        	board.getObjective("BetterFishing").setDisplaySlot(DisplaySlot.SIDEBAR);
        	return;
        }
        obj = board.registerNewObjective("BetterFishing", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(">> Frohes Fischen! <<");
        
        obj.getScore(ChatColor.GRAY + "[]======================[]").setScore(13);
        obj.getScore(ChatColor.GRAY + "Verbleibende Zeit: " + prettifyTime(time)).setScore(12);
        obj.getScore(ChatColor.WHITE + "Rangliste:").setScore(11);
        
        for (int i = 0; i < 5; i++) {
        	if (pointsActive == false) {
        		obj.getScore(ChatColor.GREEN + "#" + (i + 1) + ": " + ChatColor.YELLOW + catches[i][0] + " (" + catches[i][1] + "cm)").setScore(10 - i);
        	} else {
        		obj.getScore(ChatColor.GREEN + "#" + (i + 1) + ": " + ChatColor.YELLOW + catchPoints[i][0] + " (" + catchPoints[i][1] + " Punkte)").setScore(10 - i);
        	}
        }
        
        player.setScoreboard(board);
	}
	
	public String prettifyTime(int time) {
		String prettyfied = "";
		int seconds, minutes, hours;
		String sec, min, hour;
		if (time > 59 && time < 3600) {
			minutes = time / 60;
			seconds = time - minutes * 60;
			if (seconds < 10) {
				sec = "0" + seconds;
			} else {
				sec = "" + seconds;
			}
			if (minutes < 10) {
				min = "0" + minutes;
			} else {
				min = "" + minutes;
			}
			prettyfied = min + ":" + sec;
		} else {
			prettyfied = time + "s";
		}
		
		return prettyfied;
	}
	
	public String[][] sortPoints(String[][] input) {
		String[][] output = new String[128][2];
		for (int i = 0; i < 5; i++) {
			output[i][0] = "-";
			output[i][1] = "" + (5 - i);
		}
		for (int i = 0; i < input.length; i++) {
			//FIRST
			if (Double.parseDouble(input[i][1]) > Double.parseDouble(output[0][1])) {
				for (int x = 1; x < 4; x++) {
					output[x][0] = output[x - 1][0];
					output[x][1] = output[x - 1][1];
				}
				output[0][0] = input[i][0];
			}
					
		}
		return input;
	}

}
