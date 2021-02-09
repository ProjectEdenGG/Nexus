package me.pugabyte.nexus.features.crates;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.crates.menus.CrateEditMenu;
import me.pugabyte.nexus.features.crates.models.CrateLoot;
import me.pugabyte.nexus.features.crates.models.CrateType;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
public class Crates extends Feature implements Listener {

	public static final String PREFIX = StringUtils.getPrefix("Crates");
	public static File file = Nexus.getFile("crates.yml");
	public static YamlConfiguration config;

	public static List<CrateLoot> lootCache = new ArrayList<>();

	@Override
	public void startup() {
		ConfigurationSerialization.registerClass(CrateLoot.class);
		config = Nexus.getConfig("crates.yml");
		spawnAllHolograms();
		loadCache();
		Nexus.registerListener(new CrateEditMenu.CrateEditProvider());
	}

	@Override
	public void shutdown() {
		deleteAllHolograms();
	}

	@SneakyThrows
	public static void save() {
		config.save(file);
	}

	@SneakyThrows
	public void spawnAllHolograms() {
		for (CrateType crateType : Arrays.stream(CrateType.values()).filter(crateType -> crateType != CrateType.ALL).collect(Collectors.toList()))
			crateType.getCrateClass().spawnHologram();
	}

	public void deleteAllHolograms() {
		for (CrateType crateType : Arrays.stream(CrateType.values()).filter(crateType -> crateType != CrateType.ALL).collect(Collectors.toList())) {
			crateType.getCrateClass().deleteHologram();
		}
	}

	public void loadCache() {
		config.getConfigurationSection("").getKeys(false).forEach(loot -> {
			CrateLoot crateLoot = (CrateLoot) config.get(loot);
			if (crateLoot == null) return;
			crateLoot.setId(Integer.parseInt(loot));
			lootCache.add(crateLoot);
		});
	}

	public static List<CrateLoot> getLootByType(CrateType type) {
		if (type == CrateType.ALL) return lootCache;
		return lootCache.stream().filter(loot -> loot.getType() == type).collect(Collectors.toList());
	}

	public static int getNextId() {
		int id = 0;
		Set<String> sections = config.getConfigurationSection("").getKeys(false);
		if (sections.size() == 0) return id;
		for (String section : sections) {
			try {
				int savedId = Integer.parseInt(section);
				if (savedId >= id) id = savedId + 1;
			} catch (Exception ex) {
				Nexus.warn("An error occurred while trying to save a Crate to file");
				ex.printStackTrace();
			}
		}
		return id;
	}

	@EventHandler
	public void onClickWithKey(PlayerInteractEvent event) {
		if (!Utils.ActionGroup.CLICK_BLOCK.applies(event)) return;
		if (event.getClickedBlock() == null) return;

		CrateType locationType = CrateType.fromLocation(event.getClickedBlock().getLocation());
		if (locationType == null) return;
		event.setCancelled(true);

		if (event.getHand() == null) return;
		if (!event.getHand().equals(EquipmentSlot.HAND)) return;

		CrateType keyType = CrateType.fromKey(event.getItem());
		if (locationType != keyType && locationType != CrateType.ALL)
			locationType.previewDrops(null).open(event.getPlayer());
		else if (keyType != null)
			if (event.getPlayer().isSneaking())
				keyType.getCrateClass().openMultiple(event.getPlayer(), event.getItem().getAmount());
			else
				keyType.getCrateClass().openCrate(event.getPlayer());
	}
}
