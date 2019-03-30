package de.aestis.betterfishing;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import net.md_5.bungee.api.ChatColor;

public class CommandManager implements CommandExecutor {
	
	FileConfiguration Config = Main.instance.getConfig();
	
	public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] argArr) {

		if (cmd.getName().equalsIgnoreCase("bf")) {
			if (arg.equalsIgnoreCase("bf") && argArr.length == 0) {
				sender.sendMessage(ChatColor.GREEN + "===================================================");
				sender.sendMessage(ChatColor.GREEN + "================= " + ChatColor.WHITE + "BetterFishing v1.0" + ChatColor.GREEN + " ==================");
				sender.sendMessage(ChatColor.GREEN + "===================================================");
				sender.sendMessage(ChatColor.GOLD + "/bf info" + ChatColor.WHITE + " - Zeigt dir generelle Infos zum Angeln");
				sender.sendMessage(ChatColor.GOLD + "/bf top" + ChatColor.WHITE + " - Zeigt dir die Top-3 Liste aller Angler");
				sender.sendMessage(ChatColor.DARK_GRAY + "- by Aestis.de for JenoMiners.de");
				return true;
			}
			if (argArr[0].equalsIgnoreCase("info")) {
				
				//show team info
				return true;
			}
			if (argArr[0].equalsIgnoreCase("top")) {
				sender.sendMessage("Platz 1: " + Config.getString("bf.ranks.0.player") + " - " + Config.getDouble("bf.ranks.0.length") + "cm");
				sender.sendMessage("Platz 2: " + Config.getString("bf.ranks.1.player") + " - " + Config.getDouble("bf.ranks.1.length") + "cm");
				sender.sendMessage("Platz 3: " + Config.getString("bf.ranks.2.player") + " - " + Config.getDouble("bf.ranks.2.length") + "cm");
				sender.sendMessage(ChatColor.DARK_GRAY + "Fische gefangen: " + Config.getInt("bf.ranks.caught-total"));
				//show team info
				return true;
			}
		}
		return false;
	}
}
