package de.aestis.betterfishing;

import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;


public class Main extends JavaPlugin {
	
	public static Main instance;

	
	public void onEnable() {
		
		//init instance
		instance=this;
        
		//init configs, import cmdmgr, evtlistener, etc
		try {
			setupConfigs();
			getServer().getPluginManager().registerEvents((Listener) new EventListener(), this);
			getCommand("bf").setExecutor((CommandExecutor) new CommandManager());
		} catch (Exception ex) {
			System.out.println("Error whilst enabling: " + ex);
			return;
		}
		
		System.out.println("BetterFishing sucessfully enabled.");
	}
	public void onDisable() {
		System.out.println("BetterFishing disabled.");
	}
	
	
	private void setupConfigs() {
		
		//create config.yml if not exist
        FileConfiguration config = getConfig();
        
        if (!config.isSet("fishing-enabled")) {config.set("fishing-enabled", true);}
        if (!config.isSet("debug")) {config.set("debug", false);}
        if (!config.isSet("bf.ranks.0.player")) {config.set("bf.ranks.0.player", "Guerkchen385");}
        if (!config.isSet("bf.ranks.0.length")) {config.set("bf.ranks.0.length", 100.0);}
        if (!config.isSet("bf.ranks.1.player")) {config.set("bf.ranks.1.player", "Guerkchen385");}
        if (!config.isSet("bf.ranks.1.length")) {config.set("bf.ranks.1.length", 90.0);}
        if (!config.isSet("bf.ranks.2.player")) {config.set("bf.ranks.2.player", "Guerkchen385");}
        if (!config.isSet("bf.ranks.2.length")) {config.set("bf.ranks.2.length", 80.0);}
        if (!config.isSet("bf.ranks.caught-total")) {config.set("bf.ranks.caught-total", 0);}
        if (!config.isSet("bf.broadcast.enabled")) {config.set("bf.broadcast.enabled", true);}
        if (!config.isSet("bf.broadcast.range")) {config.set("bf.broadcast.range", 100);}
        if (!config.isSet("bf.item.use-item")) {config.set("bf.item.use-item", true);}
        if (!config.isSet("bf.item.use-item-flag")) {config.set("bf.item.use-item-flag", true);}
        if (!config.isSet("bf.item.item")) {config.set("bf.item.item", "fishing_rod");}
        if (!config.isSet("bf.item.name")) {config.set("bf.item.name", "Jeno-Angel");}
        if (!config.isSet("bf.item.cost")) {config.set("bf.item.cost", 150);}
        
        if (!config.isSet("bf.loot.albacore.display-name")) {config.set("bf.loot.albacore.display-name", "Weisser Thun");}
        if (!config.isSet("bf.loot.albacore.lore")) {config.set("bf.loot.albacore.lore", "Thunnus alalunga");}
        if (!config.isSet("bf.loot.albacore.biome")) {config.set("bf.loot.albacore.biome", "ALL");}
        if (!config.isSet("bf.loot.albacore.rarity")) {config.set("bf.loot.albacore.rarity", 2);}
        if (!config.isSet("bf.loot.albacore.min-length")) {config.set("bf.loot.albacore.min-length", 63);}
        if (!config.isSet("bf.loot.albacore.max-length")) {config.set("bf.loot.albacore.max-length", 141);}
        if (!config.isSet("bf.loot.albacore.use-baby")) {config.set("bf.loot.albacore.use-baby", true);}
        if (!config.isSet("bf.loot.albacore.item-icon")) {config.set("bf.loot.albacore.item-icon", "minecraft:cod");}
        
        saveConfig();
    }
	
}