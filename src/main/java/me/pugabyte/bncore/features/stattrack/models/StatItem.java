package me.pugabyte.bncore.features.stattrack.models;

import me.pugabyte.bncore.features.stattrack.utils.HiddenLore;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class StatItem {
	ItemStack item;
	private String id;
	private Map<Stat, Integer> stats;

	public StatItem(final ItemStack item) {
		this.item = item;
	}

	public StatItem(final ItemStack item, final String id, final Map<Stat, Integer> stats) {
		this.item = item;
		this.id = id;
		this.stats = stats;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<Stat, Integer> getStats() {
		return stats;
	}

	public void setStats(Map<Stat, Integer> stats) {
		this.stats = stats;
	}

	public ItemStack getItem() {
		return item;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	public void parse() {
		ItemMeta meta = item.getItemMeta();
		if (meta.getLore() != null && HiddenLore.isEncoded(meta.getLore().get(0))) {
			List<String> lore = meta.getLore();
			setId(HiddenLore.decode(meta.getLore().get(0).replace("ID:", "")));

			Map<Stat, Integer> newStats = new HashMap<>();

			lore.stream()
					.filter(line -> line.contains(": "))
					.forEach((line) -> {
						String[] split = ChatColor.stripColor(line).split(": ");
						Stat stat = Stat.valueOf(split[0].replace(" ", "_").toUpperCase());
						int value = Integer.parseInt(split[1]);
						newStats.put(stat, value);
					});

			setStats(newStats);
		} else {
			setId(UUID.randomUUID().toString());
			stats = new HashMap<>();
		}
	}

	public void write() {
		List<String> lore = new ArrayList<>();
		String id = HiddenLore.encode("ID:" + this.id);
		lore.add(id);
		stats.forEach((stat, value) -> lore.add(ChatColor.DARK_AQUA + stat.toString() + ": " + ChatColor.YELLOW + value));

		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		item.setItemMeta(meta);
	}
}
