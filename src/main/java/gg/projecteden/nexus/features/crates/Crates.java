package gg.projecteden.nexus.features.crates;

import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.EnumUtils;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.staff.admin.RebootCommand;
import gg.projecteden.nexus.features.crates.menus.CrateEditMenu.CrateEditProvider;
import gg.projecteden.nexus.features.crates.menus.CratePreviewProvider;
import gg.projecteden.nexus.models.crate.CrateConfig.CrateLoot;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.framework.exceptions.postconfigured.CrateOpeningException;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@Environments(Env.PROD)
public class Crates extends Feature implements Listener {

	public static final String PREFIX = StringUtils.getPrefix("Crates");

	public static List<CrateLoot> lootCache = new ArrayList<>();

	@Getter
	@Setter
	private static boolean enabled = false;

	@Override
	public void onStart() {
		Tasks.async(() -> {
			loadCache();

			Tasks.sync(() -> {
				spawnAllHolograms();
				Nexus.registerListener(new CrateEditProvider());
			});
		});

	}

	@Override
	public void onStop() {
		deleteAllHolograms();
	}

	@SneakyThrows
	public void spawnAllHolograms() {
		for (CrateType crateType : EnumUtils.valuesExcept(CrateType.class, CrateType.ALL)) {
			if (crateType.getLocation() == null)
				continue;
			try {
				crateType.getCrateClass().spawnHologram();
			} catch (Exception ex) {
				Nexus.log("Could not setup crate: " + crateType.name());
				ex.printStackTrace();
			}
		}
	}

	public void deleteAllHolograms() {
		for (CrateType crateType : EnumUtils.valuesExcept(CrateType.class, CrateType.ALL)) {
			try {
				crateType.getCrateClass().deleteHologram();
			} catch (Exception ex) {
				Nexus.log("Could not setup crate: " + crateType.name());
				ex.printStackTrace();
			}
		}
	}

	public void loadCache() {
		// TODO
	}

	public static List<CrateLoot> getLootByType(CrateType type) {
		if (type == CrateType.ALL) return lootCache;
		return lootCache.stream().filter(loot -> loot.getType() == type).collect(Collectors.toList());
	}

	public static int getNextId() {

	}

	@EventHandler
	public void onClickWithKey(PlayerInteractEvent event) {
		try {
			if (!Utils.ActionGroup.CLICK_BLOCK.applies(event)) return;
			if (event.getClickedBlock() == null) return;

			CrateType locationType = CrateType.fromLocation(event.getClickedBlock().getLocation());
			if (locationType == null) return;
			event.setCancelled(true);
			Location location = LocationUtils.getCenteredLocation(event.getClickedBlock().getLocation());

			if (event.getHand() == null) return;
			if (!event.getHand().equals(EquipmentSlot.HAND)) return;

			if (!enabled)
				throw new CrateOpeningException("Crates are temporarily disabled");
			if (RebootCommand.isQueued())
				throw new CrateOpeningException("Server reboot is queued, cannot open crates");

			CrateType keyType = CrateType.fromKey(event.getItem());
			if (locationType != keyType && locationType != CrateType.ALL)
				if (Crates.getLootByType(locationType).stream().filter(CrateLoot::isActive).toArray().length == 0)
					throw new CrateOpeningException("&3Coming soon...");
				else
					new CratePreviewProvider(locationType, null).open(event.getPlayer());
			else if (keyType != null)
				try {
					if (event.getPlayer().isSneaking() && event.getItem().getAmount() > 1)
						keyType.getCrateClass().openMultiple(location, event.getPlayer(), event.getItem().getAmount());
					else
						keyType.getCrateClass().openCrate(location, event.getPlayer());
				} catch (CrateOpeningException ex) {
					if (ex.getMessage() != null)
						PlayerUtils.send(event.getPlayer(), Crates.PREFIX + ex.getMessage());
					keyType.getCrateClass().reset();
				}
		} catch (NexusException ex) {
			PlayerUtils.send(event.getPlayer(), ex.withPrefix(Crates.PREFIX));
		}
	}

	@EventHandler
	public void onPlaceKey(BlockPlaceEvent event) {
		if (CrateType.fromKey(event.getItemInHand()) == null) return;
		event.setCancelled(true);
	}
}
