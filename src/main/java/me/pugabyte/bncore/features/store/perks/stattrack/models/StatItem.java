package me.pugabyte.bncore.features.store.perks.stattrack.models;

import static com.google.common.base.Strings.isNullOrEmpty;
import static me.pugabyte.bncore.utils.StringUtils.stripColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Data;
import lombok.NonNull;
import me.pugabyte.bncore.features.store.perks.stattrack.utils.HiddenLore;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Data
public class StatItem {
	@NonNull
	private final ItemStack item;
	private UUID id;
	private Map<Stat, Integer> stats = new HashMap<>();

	private static final String ID_PREFIX = "StatTrackId:";

	public StatItem(@NonNull ItemStack item) {
		this.item = item;
		parse();
	}

	public void parse() {
		ItemMeta meta = item.getItemMeta();
		stats = new HashMap<>();
		if (isEnabled()) {
			List<String> lore = meta.getLore();

			id = UUID.fromString(HiddenLore.decode(lore.get(0)).replace(ID_PREFIX, ""));

			lore.stream()
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
		List<String> lore = new ArrayList<>();
		String id = HiddenLore.encode(ID_PREFIX + this.id);
		lore.add(id);
		stats.entrySet().stream().sorted(Map.Entry.comparingByKey())
			.forEachOrdered(entry -> lore.add(ChatColor.DARK_AQUA + entry.getKey().toString() + ": " + ChatColor.YELLOW + entry.getValue()));

		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		item.setItemMeta(meta);
		return this;
	}

	public void increaseStat(Stat stat, int value) {
		stats.put(stat, stats.getOrDefault(stat, 0) + value);
	}

	public boolean isEnabled() {
		List<String> lore = item.getItemMeta().getLore();
		if (lore != null && !lore.isEmpty()) {
			if (HiddenLore.isEncoded(lore.get(0))) {
				String decoded = HiddenLore.decode(lore.get(0));
				return !isNullOrEmpty(decoded) && decoded.matches(ID_PREFIX + StringUtils.UUID_REGEX);
			}
		}
		return false;
	}
}
