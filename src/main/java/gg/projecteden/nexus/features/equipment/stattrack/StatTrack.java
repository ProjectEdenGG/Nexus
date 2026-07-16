package gg.projecteden.nexus.features.equipment.stattrack;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.api.common.utils.ReflectionUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.itemtags.ItemTagsUtils;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.stattrack.StatTrackItem;
import gg.projecteden.nexus.models.stattrack.StatTrackItemService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class StatTrack extends Feature implements Listener {

	private static final String NBT_KEY = "StatTrack";
	public static final NamespacedKey TRACKING_KEY = new NamespacedKey(Nexus.getInstance(), "stat_track_id");
	public static final StatTrackItemService SERVICE = new StatTrackItemService();
	@Getter
	private static final ItemStack template = new ItemBuilder(Material.PAPER).model("todo/stattrack").name("&eStatTrack").build();

	private static final List<StatTrackStatistic> STATISTICS = new ArrayList<>();

	@Override
	public void onStart() {
		for (Class<? extends StatTrackStatistic> clazz : ReflectionUtils.subTypesOf(StatTrackStatistic.class, getClass().getPackageName())) {
			try {
				Constructor<? extends StatTrackStatistic> constructor = clazz.getConstructor();
				StatTrackStatistic statistic = constructor.newInstance();
				Nexus.registerListener(statistic);
				STATISTICS.add(statistic);
			} catch (Exception e) {
				Nexus.log("Failed to enable StatTrackStatistic");
				e.printStackTrace();
			}
		}
		STATISTICS.sort(Comparator.comparing(StatTrackStatistic::getDisplayName));

		Tasks.repeat(TickTime.SECOND.x(1), TickTime.SECOND.x(1), () -> {
			for (Player player : OnlinePlayers.getAll())
				update(player);
		});

		Nexus.registerListener(new CommonListeners());
	}

	public static String idToDisplay(String id) {
		StatTrackStatistic statistic = STATISTICS.stream().filter(stat -> stat.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
		return statistic == null ? null : statistic.getDisplayName();
	}

	public static boolean isApplicableItem(ItemStack cursor) {
		return STATISTICS.stream().anyMatch(stat -> stat.canTrack(cursor));
	}

	public static List<StatTrackStatistic> getValidStatsFor(ItemStack tool) {
		return STATISTICS.stream().filter(stat -> stat.canTrack(tool)).toList();
	}

	public static UUID getStatTrackId(ItemStack item) {
		NBTItem nbt = new NBTItem(item);
		if (!nbt.hasTag(NBT_KEY))
			return null;

		NBTCompound compound = nbt.getCompound(NBT_KEY);
		return UUID.fromString(compound.getString("id"));
	}

	public static boolean isEnabledOn(ItemStack item) {
		if (item == null) return false;
		return getStatTrackId(item) != null;
	}

	public static ItemStack enableFor(ItemStack item) {
		NBTItem nbt = new NBTItem(item);
		if (nbt.hasTag(NBT_KEY))
			return item;

		StatTrackStatistic defaultStat = STATISTICS.stream().filter(stat -> stat.canTrack(item)).findFirst().orElse(null);
		if (defaultStat == null)
			throw new InvalidInputException("Unsupported item type");

		NBTCompound compound = nbt.addCompound(NBT_KEY);
		compound.setString("id", UUID.randomUUID().toString());
		compound.setString("display", defaultStat.getId());
		ItemStack updated = nbt.getItem();
		updateLore(updated, defaultStat, null);

		return updated;
	}

	public static ItemStack disableFor(ItemStack item) {
		if (item == null) return null;

		UUID uuid = getStatTrackId(item);
		if (uuid == null) return item;

		StatTrackItem statTrackItem = SERVICE.get(uuid);
		SERVICE.delete(statTrackItem);

		NBTItem nbt = new NBTItem(item);
		String display = nbt.getCompound(NBT_KEY).getString("display");
		StatTrackStatistic old = STATISTICS.stream().filter(stat -> stat.getId().equalsIgnoreCase(display)).findFirst().orElse(null);
		nbt.removeKey(NBT_KEY);

		ItemStack updated = nbt.getItem();
		updateLore(updated, null, old);

		return updated;
	}

	public static ItemStack setDisplayedStat(ItemStack item, StatTrackStatistic stat) {
		NBTItem nbt = new NBTItem(item);

		NBTCompound compound = nbt.getCompound(NBT_KEY);
		String display = compound.getString("display");

		StatTrackStatistic old = null;
		if (display != null)
			old = STATISTICS.stream().filter(_stat -> _stat.getId().equalsIgnoreCase(display)).findFirst().orElse(null);
		compound.setString("display", stat.getId());
		ItemStack updated = nbt.getItem();
		updateLore(updated, stat, old);

		return updated;
	}

	public static StatTrackStatistic getDisplayedStat(ItemStack item) {
		NBTItem nbt = new NBTItem(item);

		NBTCompound compound = nbt.getCompound(NBT_KEY);
		String display = compound.getString("display");
		return STATISTICS.stream().filter(stat -> stat.getId().equalsIgnoreCase(display)).findFirst().orElse(null);
	}

	private static void updateLore(ItemStack item, StatTrackStatistic stat, StatTrackStatistic old) {
		// Find the existing lore
		String toFind = null;
		if (old != null)
			toFind = old.getDisplayName();
		else if (stat != null)
			toFind = stat.getDisplayName();

		List<String> lore = item.getLore();
		if (lore == null)
			lore = new ArrayList<>();

		int found = -1;
		for (int i = 0; i < lore.size(); i++)
			if (StringUtils.stripColor(lore.get(i)).contains(toFind))
				found = i;

		int amount = 0;
		if (stat != null) {
			StatTrackItem statTrackItem = SERVICE.get(getStatTrackId(item));
			amount = statTrackItem.getValues().getOrDefault(stat.getId(), 0.0).intValue();
		}
		String formattedAmount = String.format("%,d", amount);

		// Not found, add all
		if (found == -1) {
			lore.add(" ");
			lore.add(StringUtils.colorize("&3" + stat.getDisplayName() + ": &e" + formattedAmount));
			lore.add(" ");
		}

		// Found, but removing
		else if (stat == null) {
			lore.remove(found + 1);
			lore.remove(found);
			lore.remove(found - 1);
		}

		// Found, update in place
		else
			lore.set(found, StringUtils.colorize("&3" + stat.getDisplayName() + ": &e" + formattedAmount));

		item.setLore(lore);

		// Push item tags to bottom
		ItemTagsUtils.update(item);
	}

	@EventHandler
	public void onOpenInventory(InventoryOpenEvent event) {
		if (!Dev.BLAST.is(event.getPlayer()))
			return;
		update((Player) event.getPlayer());
	}

	private void update(Player player) {
		for (ItemStack item : player.getInventory().getContents())
			if (isEnabledOn(item))
				updateLore(item, getDisplayedStat(item), null);
	}

}
