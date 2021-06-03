package me.pugabyte.nexus.features.store.perks.stattrack.models;

import de.tr7zw.nbtapi.NBTItem;
import lombok.Data;
import lombok.NonNull;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@Data
public class StatItem {
	@NonNull
	private ItemStack item;
	private UUID id;
	private Map<Stat, Integer> stats = new HashMap<>();

	public static final String NBT_KEY = "StatTrackId";

	public StatItem(@NonNull ItemStack item) {
		this.item = item;
		parse();
	}

	public static int find(Player player, ItemStack item1) {
		int notFound = -1;
		int slot = notFound;

		if (isNullOrAir(item1))
			return notFound;

		try {
			for (ItemStack item2 : player.getInventory().getContents()) {
				++slot;
				if (isNullOrAir(item2)) continue;
				if (item1.getType() != item2.getType()) continue;

				final NBTItem nbtItem1 = new NBTItem(item1);
				final NBTItem nbtItem2 = new NBTItem(item2);

				if (!nbtItem1.hasKey(NBT_KEY)) continue;
				if (!nbtItem2.hasKey(NBT_KEY)) continue;

				final String id1 = nbtItem1.getString(NBT_KEY);
				final String id2 = nbtItem2.getString(NBT_KEY);
				if (StringUtils.isNullOrEmpty(id1)) continue;
				if (StringUtils.isNullOrEmpty(id2)) continue;

				if (!id1.equals(id2)) continue;

				return slot;
			}
		} catch (NullPointerException ignore) {}

		return notFound;
	}

	public void parse() {
		ItemMeta meta = item.getItemMeta();
		stats = new HashMap<>();
		if (isEnabled()) {
			meta.getLore().stream()
					.filter(line -> line.contains(": "))
					.forEach((line) -> {
						String[] split = stripColor(line).split(": ");
						Stat stat = Stat.valueOf(split[0].replace(" ", "_").toUpperCase());
						int value = Integer.parseInt(split[1]);
						stats.put(stat, value);
					});

		} else
			id = UUID.randomUUID();
	}

	public StatItem write() {
		item = new ItemBuilder(item)
				.nbt(nbt -> nbt.setString(NBT_KEY, id.toString()))
				.setLore(getLore())
				.build();

		return this;
	}

	private List<String> getLore() {
		return stats.entrySet().stream()
				.sorted(Entry.comparingByKey())
				.map(entry -> "&3" + entry.getKey() + ": &e" + entry.getValue())
				.toList();
	}

	public StatItem increaseStat(Stat stat, int value) {
		stats.put(stat, stats.getOrDefault(stat, 0) + value);
		return this;
	}

	public boolean isEnabled() {
		final NBTItem nbtItem = new NBTItem(item);
		if (!nbtItem.hasKey(NBT_KEY))
			return false;

		id = UUID.fromString(nbtItem.getString(NBT_KEY));
		return true;
	}
}
