package me.MnMaxon.LuckyItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
	public static String dataFolder;
	public static Main plugin;
	public static SuperYaml config;
	public static boolean broadcastFinds = false;
	public static ArrayList<SuperStack> itemsToGive = new ArrayList<SuperStack>();
	public static Map<NumRange, SuperStack> emeraldChanceMap;
	public static Map<NumRange, SuperStack> goldChanceMap;
	public static Map<NumRange, SuperStack> diamondChanceMap;
	public static Map<NumRange, SuperStack> ironChanceMap;

	@Override
	public void onEnable() {
		plugin = this;
		dataFolder = this.getDataFolder().getAbsolutePath();
		Main.reloadConfigs();
		getServer().getPluginManager().registerEvents(new MainListener(), this);
	}

	public static void reloadConfigs() {
		itemsToGive = new ArrayList<SuperStack>();
		config = new SuperYaml(dataFolder + "/Config.yml");
		Boolean save = false;
		double emeraldChances = 100.0;
		double goldChances = 100.0;
		double diamondChances = 100.0;
		double ironChances = 100.0;
		if (config.get("BroadCastFinds") != null && config.get("BroadCastFinds") instanceof Boolean)
			broadcastFinds = config.getBoolean("BroadCastFinds");
		else {
			config.set("BroadCastFinds", false);
			save = true;
		}

		if (config.get("Chances.Emerald") != null && config.get("Chances.Emerald") instanceof Double)
			emeraldChances = config.getDouble("Chances.Emerald");
		else {
			config.set("Chances.Emerald", 100.0);
			save = true;
		}
		if (config.get("Chances.Diamond") != null && config.get("Chances.Diamond") instanceof Double)
			diamondChances = config.getDouble("Chances.Diamond");
		else {
			config.set("Chances.Diamond", 100.0);
			save = true;
		}
		if (config.get("Chances.Iron") != null && config.get("Chances.Iron") instanceof Double)
			ironChances = config.getDouble("Chances.Iron");
		else {
			config.set("Chances.Iron", 100.0);
			save = true;
		}
		if (config.get("Chances.Gold") != null && config.get("Chances.Gold") instanceof Double)
			goldChances = config.getDouble("Chances.Gold");
		else {
			config.set("Chances.Gold", 100.0);
			save = true;
		}

		if (config.get("Items") == null) {
			setItemDefaults();
			save = true;
		}
		if (save)
			config.save();
		setItems();
		ironChanceMap = SuperStack.setupHashMap(ironChances);
		diamondChanceMap = SuperStack.setupHashMap(diamondChances);
		goldChanceMap = SuperStack.setupHashMap(goldChances);
		emeraldChanceMap = SuperStack.setupHashMap(emeraldChances);
		Bukkit.getLogger().log(Level.INFO, ".2");
	}

	private static void setItemDefaults() {
		List<String> lore = new ArrayList<String>();
		lore.add("This sword can be used");
		lore.add("to kill mobs or people!");
		ItemStack sword = easyItem("LuckySword", Material.DIAMOND_SWORD, 0, lore, 1);
		sword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
		sword.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 2);
		sword.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
		new SuperStack(sword, 5.0, "A LuckySword").addToConfig();
		ItemStack pick = easyItem("LuckyPick", Material.DIAMOND_PICKAXE, 0, null, 1);
		pick.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 2);
		pick.addUnsafeEnchantment(Enchantment.DIG_SPEED, 1);
		pick.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		new SuperStack(pick, 5.0, "A LuckyPick").addToConfig();
		ItemStack gapples = easyItem("God Apples", Material.GOLDEN_APPLE, 1, null, 2);
		new SuperStack(gapples, 10.0, "Some Gapples").addToConfig();
		ItemStack wool = easyItem(null, Material.WOOL, 5, null, 9);
		new SuperStack(wool, 20.0, "9 Green Wool").addToConfig();
		config.save();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if ((cmd.getName().equalsIgnoreCase("LI") || cmd.getName().equalsIgnoreCase("LuckyItems") || cmd.getName()
				.equalsIgnoreCase("LuckyItem")) && args.length == 1 && args[0].equalsIgnoreCase("reload")) {
			if (sender instanceof ConsoleCommandSender
					|| (sender instanceof Player && ((Player) sender).hasPermission("LuckyItems.reload"))) {
				Main.reloadConfigs();
				sender.sendMessage(ChatColor.GREEN + "LuckyItems config was reloaded!");
			} else
				sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to do this!");
		} else
			displayHelp(sender);
		return false;
	}

	private void displayHelp(CommandSender s) {
		s.sendMessage(ChatColor.AQUA + "========= LuckyItems =========");
		s.sendMessage(ChatColor.DARK_PURPLE + "/li reload");
	}

	@SuppressWarnings("deprecation")
	public static void setItems() {
		Boolean changed = false;
		for (String key : config.getConfigurationSection("Items").getKeys(false)) {
			String path = "Items." + key;
			ItemStack is;
			Material material;
			int amount = 1;
			int durability = -1;
			double chance = -1;
			List<String> lore = null;
			String displayName = null;
			if (config.get(path + ".Material") == null) {
				material = null;
			} else if (config.get(path + ".Material") instanceof String)
				material = Material.matchMaterial(config.getString(path + ".Material"));
			else if (config.get(path + ".Material") instanceof Integer)
				material = Material.getMaterial(config.getInt(path + ".Material"));
			else
				material = null;
			if (material == null) {
				warn("There is a problem in the config.yml at: " + path + ".Material");
				break;
			}
			if (config.get(path + ".Chance") != null)
				chance = config.getDouble(path + ".Chance");
			if (chance < 0) {
				warn("There is a problem in the config.yml at: " + path + ".Chance");
				break;
			}
			if (config.get(path + ".Amount") != null && config.get(path + ".Amount") instanceof Integer)
				amount = config.getInt(path + ".Amount");
			else {
				config.set(path + ".Amount", 1);
				changed = true;
			}
			if (config.get(path + ".Data") != null && config.get(path + ".Data") instanceof Integer)
				durability = config.getInt(path + ".Data");
			else {
				config.set(path + ".Data", 0);
				changed = true;
			}
			if (config.get(path + ".Lore") != null && config.get(path + ".Lore") instanceof List<?>)
				lore = config.getStringList(path + ".Lore");
			if (config.get(path + ".Name") != null && config.get(path + ".Name") instanceof String)
				displayName = config.getString(path + ".Name");
			is = new ItemStack(material);
			if (amount > 1)
				is.setAmount(amount);
			if (durability > 0)
				is.setDurability((short) durability);

			if (config.get(path + ".Enchantments") != null)
				for (String ench : config.getConfigurationSection(path + ".Enchantments").getKeys(false)) {
					String enchPath = path + ".Enchantments." + ench;
					if (Enchantment.getByName(ench) == null) {
						warn(enchPath + "  in the config is not an enchantment");
						warn("Enchantments: ARROW_DAMAGE, ARROW_FIRE, ARROW_INFINITE, ARROW_KNOCKBACK, DAMAGE_ALL, DAMAGE_ARTHROPODS, DAMAGE_UNDEAD, DIG_SPEED, DURABILITY, FIRE_ASPECT, KNOCKBACK, LOOT_BONUS_BLOCKS, LOOT_BONUS_MOBS, LUCK, OXYGEN, PROTECTION_ENVIRONMENTAL, PROTECTION_EXPLOSIONS, PROTECTION_FALL, PROTECTION_FIRE, PROTECTION_PROJECTILE, SILK_TOUCH, THORNS, WATER_WORKER");
					} else if (!(config.get(enchPath) instanceof Integer))
						warn("There is a problem in the config.yml at: " + enchPath);
					else
						is.addUnsafeEnchantment(Enchantment.getByName(ench), config.getInt(enchPath));
				}

			ItemMeta im = is.getItemMeta();
			if (lore != null)
				im.setLore(lore);
			if (displayName != null)
				im.setDisplayName(displayName);
			if (displayName != null || lore != null)
				is.setItemMeta(im);

			itemsToGive.add(new SuperStack(is, chance, key));
		}
		if (changed)
			config.save();
	}

	public static void warn(String message) {
		Bukkit.getServer().getLogger().log(Level.WARNING, ChatColor.RED + "[LuckyItems] " + ChatColor.YELLOW + message);
	}

	public static ItemStack easyItem(String name, Material material, int durability, List<String> lore, int amount) {
		ItemStack is = new ItemStack(material);
		if (durability > 0)
			is.setDurability((short) durability);
		if (amount > 1)
			is.setAmount(amount);
		if (is.getItemMeta() != null) {
			ItemMeta im = is.getItemMeta();
			if (name != null)
				im.setDisplayName(name);
			if (lore != null)
				im.setLore(lore);
			is.setItemMeta(im);
		}
		return is;
	}
}