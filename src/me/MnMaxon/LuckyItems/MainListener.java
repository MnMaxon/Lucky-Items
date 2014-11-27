package me.MnMaxon.LuckyItems;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.CreatureType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

@SuppressWarnings("deprecation")
public class MainListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBreak(BlockBreakEvent e) {
		if (e.getPlayer().getGameMode().equals(GameMode.CREATIVE)
				|| e.getPlayer().getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH) || e.isCancelled())
			return;
		SuperStack ss = null;
		if (e.getBlock().getType().equals(Material.EMERALD_ORE))
			ss = SuperStack.getStack(Main.emeraldChanceMap);
		else if (e.getBlock().getType().equals(Material.DIAMOND_ORE))
			ss = SuperStack.getStack(Main.diamondChanceMap);
		else if (e.getBlock().getType().equals(Material.IRON_ORE))
			ss = SuperStack.getStack(Main.ironChanceMap);
		else if (e.getBlock().getType().equals(Material.GOLD_ORE))
			ss = SuperStack.getStack(Main.goldChanceMap);
		if (ss == null) 
			return;
		if (Main.broadcastFinds)
			Bukkit.broadcastMessage(ChatColor.GOLD + "[" + ChatColor.RED + "LuckyItems" + ChatColor.GOLD + "] "
					+ ChatColor.GREEN + e.getPlayer().getDisplayName() + ChatColor.GREEN + " has found "
					+ ss.getMessageName());
		e.getBlock().getWorld().dropItem(e.getBlock().getLocation().add(.5, 0, .5), ss.getItemStack());
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if (e.getBlockPlaced().getType().equals(Material.MOB_SPAWNER)) {
			((CreatureSpawner) e.getBlockPlaced().getState()).setCreatureTypeByName(CreatureType.fromId(
					e.getItemInHand().getDurability()).getName());
			e.getBlockPlaced().setData((byte) e.getItemInHand().getDurability());
		}
	}
}
