package de.aestis.betterfishing;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class CommandManager implements CommandExecutor {
	
	FileConfiguration Config = Main.instance.getConfig();
	
	String prefix = ChatColor.GOLD + "[" + ChatColor.GREEN + "BetterFish" + ChatColor.GOLD + "] ";
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] argArr) {

		if (cmd.getName().equalsIgnoreCase("bf")) {
			if (arg.equalsIgnoreCase("bf") && argArr.length == 0) {
				sender.sendMessage(ChatColor.GREEN + "================== " + ChatColor.WHITE + "BetterFishing V0.9.1" + ChatColor.GREEN + " ===================");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GOLD + "/bf create" + ChatColor.WHITE + "                  - Wandelt deine Angel in eine BetterFishing-Angel um");
				sender.sendMessage(ChatColor.GOLD + "/bf info" + ChatColor.WHITE + "                      - Zeigt dir generelle Infos zum Angeln");
				sender.sendMessage(ChatColor.GOLD + "/bf top" + ChatColor.WHITE + "                       - Zeigt dir die Top-3 Liste aller Angler");
				sender.sendMessage(ChatColor.GOLD + "/bf start [duration s]" + ChatColor.WHITE + "    - Startet den 'Längster Fisch' Wettbewerb");
				sender.sendMessage(ChatColor.GOLD + "/bf stop" + ChatColor.WHITE + "                     - Stoppt das laufende Event");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.DARK_GRAY + "By Guerkchen385");
				sender.sendMessage("");
				return true;
			}
			if (argArr[0].equalsIgnoreCase("create")) {
				Player player = Bukkit.getPlayer(sender.getName());
				ItemStack itm = player.getInventory().getItemInMainHand();
				if (itm.getType() == Material.FISHING_ROD) {
					ItemMeta itmMeta = itm.getItemMeta();
					ArrayList<String> lore = new ArrayList<String>();
			        lore.add("BetterFishing");
			        itmMeta.setLore(lore);
			        itm.setItemMeta(itmMeta);
			        sender.sendMessage(prefix + ChatColor.WHITE + "Viel Spaß beim Angeln!");
				} else {
					sender.sendMessage(prefix + ChatColor.RED + "Dafür musst du eine Angel in der Hand halten!");
				}
			}
			if (argArr[0].equalsIgnoreCase("info")) {
				sender.sendMessage(ChatColor.GREEN + "================== " + ChatColor.WHITE + "BetterFishing V0.9.1" + ChatColor.GREEN + " ===================");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GOLD + "Rarity 0:" + ChatColor.GRAY + " Schrott");
				sender.sendMessage(ChatColor.GOLD + "Rarity 1:" + ChatColor.WHITE + " Häufig");
				sender.sendMessage(ChatColor.GOLD + "Rarity 2:" + ChatColor.BLUE + " Ungewöhnlich");
				sender.sendMessage(ChatColor.GOLD + "Rarity 3:" + ChatColor.GOLD + " Selten");
				sender.sendMessage(ChatColor.GOLD + "Rarity 4:" + ChatColor.DARK_PURPLE + " Rar");
				sender.sendMessage(ChatColor.GOLD + "Rarity 5:" + ChatColor.DARK_RED + " Einzigartig");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GOLD + "Items: " + ChatColor.WHITE + Config.getConfigurationSection("bf.loot").getKeys(false).size());
				sender.sendMessage("");
				return true;
			}
			if (argArr[0].equalsIgnoreCase("top")) {
				sender.sendMessage(ChatColor.GREEN + "================== " + ChatColor.WHITE + "BetterFishing V0.9.1" + ChatColor.GREEN + " ===================");
				sender.sendMessage("");
				for (int i = 0; i < 3; i++) {
					sender.sendMessage(ChatColor.GOLD + "Platz " + (i + 1) + ": " + ChatColor.WHITE + Config.getString("bf.ranks." + i + ".player") + " - " + Config.getDouble("bf.ranks." + i + ".length") + "cm");
				}
				sender.sendMessage(ChatColor.GOLD + "Fische gefangen: " + ChatColor.WHITE + Config.getInt("bf.ranks.caught-total"));
				sender.sendMessage("");
				return true;
			}
			if (argArr[0].equalsIgnoreCase("start")) {
				if (sender.hasPermission("bf.event")) {
					new GameEvents();
					GameEvents ge = GameEvents.getInstance();
					if (ge.startBiggestFish(Integer.valueOf(argArr[1]))) {
						sender.sendMessage(prefix + ChatColor.GOLD + "Event gestartet!");
					} else {
						sender.sendMessage(prefix + ChatColor.RED + "Fehler! Du musst eine Zeitbegrenzung festlegen (z.B. /bf start 300)");
						return false;
					}
				} else {
					sender.sendMessage(prefix + ChatColor.RED + "Du hast keine Berechtigung dies zu tun!");
				}
				return true;
			}
			if (argArr[0].equalsIgnoreCase("points")) {
				if (sender.hasPermission("bf.event")) {
					new GameEvents();
					GameEvents ge = GameEvents.getInstance();
					if (ge.startBiggestFish(Integer.valueOf(argArr[1]))) {
						ge.pointsActive = true;
						sender.sendMessage(prefix + ChatColor.GOLD + "Event gestartet!");
					} else {
						sender.sendMessage(prefix + ChatColor.RED + "Fehler! Du musst eine Zeitbegrenzung festlegen (z.B. /bf start 300)");
						return false;
					}
				} else {
					sender.sendMessage(prefix + ChatColor.RED + "Du hast keine Berechtigung dies zu tun!");
				}
				return true;
			}
			if (argArr[0].equalsIgnoreCase("stop")) {
				if (sender.hasPermission("bf.event")) {
					new GameEvents();
					GameEvents ge = GameEvents.getInstance();
					if (ge.eventActive) {
						ge.eventActive = false;
						sender.sendMessage(prefix + ChatColor.GOLD + "Event gestoppt!");
					} else {
						sender.sendMessage(prefix + ChatColor.RED + "Fehler! Derzeit läuft kein Event.");
						return false;
					}
				} else {
					sender.sendMessage(prefix + ChatColor.RED + "Du hast keine Berechtigung dies zu tun!");
				}
				return true;
			}
		}
		return false;
	}
}
