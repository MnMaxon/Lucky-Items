package me.MnMaxon.LuckyItems;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SuperStack {
	private double chance;
	private String messageName;
	private ItemStack itemStack;

	public SuperStack(ItemStack itemStack, Double chance, String messageName) {
		setChance(chance);
		setMessageName(messageName);
		setItemStack(itemStack);
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public double getChance() {
		return chance;
	}

	public void setChance(double chance) {
		this.chance = chance;
	}

	public String getMessageName() {
		return messageName;
	}

	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}

	public void addToConfig() {
		String path = "Items." + getMessageName();
		Main.config.set(path + ".Chance", chance);
		Main.config.set(path + ".Amount", itemStack.getAmount());
		Main.config.set(path + ".Material", itemStack.getType().name());
		Main.config.set(path + ".Data", itemStack.getDurability());
		for (Entry<Enchantment, Integer> enchs : itemStack.getEnchantments().entrySet())
			Main.config.set(path + ".Enchantments." + enchs.getKey().getName(), enchs.getValue());
		if (itemStack.getItemMeta() != null) {
			ItemMeta im = itemStack.getItemMeta();
			if (im.getLore() != null)
				Main.config.set(path + ".Lore", im.getLore());
			if (im.getDisplayName() != null)
				Main.config.set(path + ".Name", im.getDisplayName());
		}
		Main.config.save();
	}

	public static void Async(final Runnable run) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, run, 1L);
	}

	public static Map<NumRange, SuperStack> setupHashMap(double chance) {
		if (chance == 0)
			return null;
		Map<NumRange, SuperStack> itemMap = new HashMap<NumRange, SuperStack>();
		double maxChance = 0;
		for (SuperStack ss : Main.itemsToGive) {
			double min = maxChance;
			maxChance = maxChance + (ss.getChance() * chance / 100);
			itemMap.put(new NumRange(min, maxChance), ss);
		}
		return itemMap;
	}

	public static SuperStack getStack(Map<NumRange, SuperStack> emeraldChanceMap) {
		if (emeraldChanceMap == null)
			return null;
		double randomNum = new Random().nextDouble() * 100;
		for (Entry<NumRange, SuperStack> entry : emeraldChanceMap.entrySet())
			if (entry.getKey().contains(randomNum))
				return entry.getValue();
		return null;
	}
}