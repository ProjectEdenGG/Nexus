package me.pugabyte.bncore.features.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

import static me.pugabyte.bncore.Utils.colorize;

public abstract class MenuUtils {
	protected ItemStack nameItem(ItemStack item, String name) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(colorize(name));
		item.setItemMeta(meta);
		return item;
	}

	protected ItemStack nameItem(ItemStack item, String name, String lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(colorize(name));
		lore = colorize(lore);
		meta.setLore(Arrays.asList(lore.split("\\|\\|")));
		item.setItemMeta(meta);
		return item;
	}

	protected void warp(Player player, String warp) {
		Bukkit.dispatchCommand(player, "essentials:warp " + warp);
	}

	public void command(Player player, String command) {
		Bukkit.dispatchCommand(player, command);
	}

	protected ItemStack backItem() {
		return nameItem(new ItemStack(Material.BARRIER), "&cBack");
	}

	protected ItemStack closeItem() {
		return nameItem(new ItemStack(Material.BARRIER), "&cClose");
	}
}
