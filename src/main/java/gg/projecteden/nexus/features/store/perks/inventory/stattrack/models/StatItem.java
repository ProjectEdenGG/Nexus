package gg.projecteden.nexus.features.store.perks.inventory.stattrack.models;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

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

	public void parse() {
		final List<String> lore = item.getItemMeta().getLore();

		if (!isEnabled()) {
			id = UUID.randomUUID();
			return;
		}

		id = UUID.fromString(nbt().getString(NBT_KEY));

		if (!Nullables.isNullOrEmpty(lore))
			lore.stream()
					.filter(line -> line.contains(": "))
					.forEach(line -> {
						String[] split = StringUtils.stripColor(line).split(": ");
						stats.put(Stat.of(split[0]), Integer.parseInt(split[1]));
					});
	}

	public StatItem write() {
		item = new ItemBuilder(item)
				.nbt(nbt -> nbt.setString(NBT_KEY, id.toString()))
				.setLore(getLore())
				.build();

		return this;
	}

	public ItemStack update() {
		write();
		return getItem();
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
		return nbt().hasKey(NBT_KEY);
	}

	@NotNull
	private NBTItem nbt() {
		return new NBTItem(item);
	}

	public int find(Player player) {
		return find(player, item);
	}

	public static int find(Player player, ItemStack item1) {
		int notFound = -1;
		int slot = notFound;

		if (Nullables.isNullOrAir(item1))
			return notFound;

		try {
			for (ItemStack item2 : player.getInventory().getContents()) {
				++slot;
				if (Nullables.isNullOrAir(item2)) continue;
				if (item1.getType() != item2.getType()) continue;

				final NBTItem nbtItem1 = new NBTItem(item1);
				final NBTItem nbtItem2 = new NBTItem(item2);

				if (!nbtItem1.hasKey(NBT_KEY)) continue;
				if (!nbtItem2.hasKey(NBT_KEY)) continue;

				final String id1 = nbtItem1.getString(NBT_KEY);
				final String id2 = nbtItem2.getString(NBT_KEY);
				if (Nullables.isNullOrEmpty(id1)) continue;
				if (Nullables.isNullOrEmpty(id2)) continue;

				if (!id1.equals(id2)) continue;

				return slot;
			}
		} catch (NullPointerException ignore) {}

		return notFound;
	}

}
