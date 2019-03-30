package de.aestis.betterfishing;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class EventListener implements Listener {
	
	FileConfiguration Config = Main.instance.getConfig();
	private String[] fishes = new String[Config.getConfigurationSection("bf.loot").getKeys(false).size()];

	@EventHandler
	public void onFishing(PlayerFishEvent event) {
		if (fishes[0] == null) {
			int i = 0;
			for(String key : Config.getConfigurationSection("bf.loot").getKeys(false)){
				fishes[i] = key;
				i++;
			}
		}
		
		if (event.getState() == State.CAUGHT_FISH) {
			Boolean debug = Config.getBoolean("debug");
			
			Player player = event.getPlayer();
			Location loc = player.getLocation();
			Biome bio = event.getPlayer().getWorld().getBiome(loc.getBlockX(), loc.getBlockZ());
			int rndFish = ThreadLocalRandom.current().nextInt(0, fishes.length - 1);
			int rndRarity = ThreadLocalRandom.current().nextInt(1, 5);
			int enchRarity = 0;
			double rndHandicap = ThreadLocalRandom.current().nextDouble(0.1, 3.0);
			double enchHandicap = 0;
			double enchLength = 0;
			
			//Enchant Bonus
			ItemStack mainhand = player.getInventory().getItemInMainHand();
			if (mainhand.containsEnchantment(Enchantment.LUCK)) {
				enchRarity = (mainhand.getEnchantmentLevel(Enchantment.LUCK) + 1) / 2;
			}
			if (mainhand.containsEnchantment(Enchantment.DURABILITY)) {
				enchHandicap = (mainhand.getEnchantmentLevel(Enchantment.DURABILITY) + 1) * 2.4;
			}
			if (mainhand.containsEnchantment(Enchantment.LURE)) {
				enchLength = (mainhand.getEnchantmentLevel(Enchantment.LURE) + 1) / 3;
			}
			
			//Get Fish
			boolean found = false;
			for (int t = 0; t < 50; t++) {
				boolean rok = false;
				if (Config.getInt("bf.loot." + fishes[rndFish] + ".rarity") <= (rndRarity + enchRarity)) rok = true;
				boolean bok = false;
				
				String fishBiome = Config.getString("bf.loot." + fishes[rndFish] + ".biome");
				if (debug) Bukkit.broadcastMessage(fishes[rndFish] + " - " + fishBiome);
				if (fishBiome.contains("ALL")) {
					bok = true;
					if (debug) Bukkit.broadcastMessage("Found Biome! (" + fishes[rndFish] + " b:ALL)");
				} else {
					String[] biomes = fishBiome.split(",");
					for (int i = 0; i < biomes.length; i++) {
						Biome fishBio = Biome.valueOf(biomes[i]);
						if (bio.compareTo(fishBio) == 0) {
							if (debug) Bukkit.broadcastMessage("Found Biome! (" + fishes[rndFish] + " b:" + fishBio.name() + ")");
							bok = true;
						}
					}
				}

				if (rok && bok) {
					found = true;
					t = 50;
				} else {
					if (rndFish == fishes.length - 1) {
						rndFish = 0;
					} else {
						rndFish++;
					}
					//rndFish = ThreadLocalRandom.current().nextInt(0, fishes.length - 1);
				}
			}
			
			if (found == false) return;
			if (found) {
				String fishString = "bf.loot." + fishes[rndFish];
				//check if min == max
				double rndFishLength = ((ThreadLocalRandom.current().nextInt(Config.getInt(fishString + ".min-length"), Config.getInt(fishString + ".max-length")) - rndHandicap) + enchHandicap + enchLength);
				double fishLength = Double.parseDouble(String.format(Locale.ENGLISH, "%1.2f", rndFishLength));
				
				//debug
				if (debug) Bukkit.broadcastMessage("Player: " + player.getName() + ", Max-Rarity: " + rndRarity + "(+" + enchRarity + "), Handicap: " + rndHandicap + "(-" + enchHandicap + "), Fish-Caught: " + fishes[rndFish] + ", Fish-Length: " + fishLength);
	
				//Fetch Info
				Item caught = (Item) event.getCaught();		
				Material mat = Material.matchMaterial(Config.getString(fishString + ".item-icon"));
				
				//Baby And Badge
				String displayName = "";
				if (Config.getBoolean(fishString + ".use-baby") && fishLength > Config.getDouble(fishString + ".max-length")) displayName = ChatColor.GRAY + "[Riesig] ";
				if (fishLength < Config.getDouble(fishString + ".min-length")) displayName = ChatColor.GRAY + "[Baby] ";
				
				//Rank And Naming
				switch (Config.getInt(fishString + ".rarity")) {
					case 0:
						displayName += ChatColor.GRAY + "";
						break;
					case 1:
						displayName += ChatColor.WHITE + "";
						break;
					case 2:
						displayName += ChatColor.BLUE + "";
						break;
					case 3:
						displayName += ChatColor.GOLD + "";
						break;
					case 4:
						displayName += ChatColor.DARK_PURPLE + "";
						break;
					case 5:
						displayName += ChatColor.DARK_RED + "";
						break;
				}
				displayName += Config.getString(fishString + ".display-name");
				
				//Custom Lores And Stuff
				String itemStr = ChatColor.DARK_GRAY + "Art: " + ChatColor.ITALIC + Config.getString(fishString + ".lore");
				String lengthStr = ChatColor.DARK_GRAY + "Länge: " + fishLength + "cm";
				String catchedStr = ChatColor.GRAY + "Gefangen von " + player.getName();
				ItemStack itmStk = getItem(displayName, itemStr, lengthStr, catchedStr, mat);
				
				//Ranks (Dirty)
				Config.set("bf.ranks.caught-total", Config.getInt("bf.ranks.caught-total") + 1);
				Main.instance.saveConfig();
				if (Config.getDouble("bf.ranks.0.length") < fishLength) {
					Config.set("bf.ranks.2.player", Config.getString("bf.ranks.1.player"));
					Config.set("bf.ranks.2.length", Config.getDouble("bf.ranks.1.length"));
					Config.set("bf.ranks.1.player", Config.getString("bf.ranks.0.player"));
					Config.set("bf.ranks.1.length", Config.getDouble("bf.ranks.0.length"));
					
					Config.set("bf.ranks.0.player", player.getName());
					Config.set("bf.ranks.0.length", fishLength);
					Main.instance.saveConfig();
				} else if (Config.getDouble("bf.ranks.1.length") < fishLength) {
					Config.set("bf.ranks.2.player", Config.getString("bf.ranks.1.player"));
					Config.set("bf.ranks.2.length", Config.getDouble("bf.ranks.1.length"));
					
					Config.set("bf.ranks.1.player", player.getName());
					Config.set("bf.ranks.1.length", fishLength);
					Main.instance.saveConfig();
				} else if (Config.getDouble("bf.ranks.2.length") < fishLength) {
					Config.set("bf.ranks.2.player", player.getName());
					Config.set("bf.ranks.2.length", fishLength);
					Main.instance.saveConfig();
				}
				
				//Replace Fish
				caught.setItemStack(itmStk);
				
				//Broadcast Message
				if (Config.getBoolean("bf.broadcast.enabled")) {
					for (Player pl : Bukkit.getOnlinePlayers()) {
						if (pl.getLocation().distance(loc) < 100) {
							pl.sendMessage(ChatColor.GOLD + event.getPlayer().getName() + ChatColor.WHITE + " hat " + ChatColor.BOLD + displayName + ChatColor.WHITE + " (" + fishLength + "cm) gefangen!");
						}
					}
				}
			}
		}
	}
	
	public ItemStack getItem(String customName, String info, String length, String catched, Material material){
        ItemStack is= new ItemStack(material);
        ItemMeta isMeta= is.getItemMeta();
        isMeta.setDisplayName(customName);
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(info);
        lore.add(length);
        lore.add(catched);
        isMeta.setLore(lore);
        is.setItemMeta(isMeta);
        return is;
    }

}
