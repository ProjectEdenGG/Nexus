package gg.projecteden.nexus.features.store.gallery;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.costume.Costume;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.utils.RandomUtils;
import gg.projecteden.utils.TimeUtils.Time;
import gg.projecteden.utils.Utils;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class NPCDisplays {
	private static final String regionRegex = "store_gallery_npcdisplays_\\d+";
	@Getter
	private static final List<DisplaySet> displays = new ArrayList<>();

	public NPCDisplays() {
		loadDisplays();

		// Update skins
		Nexus.getCron().schedule("0 * * * *", NPCDisplays::updateSkins);

		// Update costumes
		Tasks.repeat(Time.TICK.x(10), Time.SECOND.x(15), () -> {
			for (DisplaySet displaySet : displays) {
				Display display = displaySet.getNext();
				if (display == null)
					continue;

				HumanEntity humanEntity = display.getHumanEntity();
				if (humanEntity == null)
					continue;

				Costume costume = RandomUtils.randomElement(Costume.values());
				humanEntity.getInventory().setItem(costume.getType().getSlot(), costume.getModel().getItem());
				displaySet.setLastUpdatedIndex(displaySet.getDisplays().indexOf(display));
			}
		});
	}

	static void updateSkins() {
		List<String> modelNames = PlayerUtils.getOnlinePlayers().stream()
			.filter(player -> !PlayerUtils.isVanished(player)).map(HumanEntity::getName)
			.toList();

		List<String> skinNames = new ArrayList<>(modelNames);
		List<String> backups = new ArrayList<>();
		for (DisplaySet displaySet : displays) {
			for (Display display : displaySet.getDisplays()) {
				if (display == null)
					continue;

				String name = getUniqueName(skinNames, backups);
				if (name != null)
					display.setSkin(name);
			}
		}
	}

	private static WorldGuardUtils getWGUtils() {
		World world = Bukkit.getWorld("buildadmin"); // TODO: fix world
		if (world != null)
			return new WorldGuardUtils(world);
		return null;
	}

	private void loadDisplays() {
		WorldGuardUtils WGUtils = getWGUtils();
		if (WGUtils == null)
			return;

		for (ProtectedRegion region : WGUtils.getRegionsLike(regionRegex)) {
			AtomicInteger ndx = new AtomicInteger(1);
			Collection<NPC> npcs = WGUtils.getNPCsInRegion(region);

			if (npcs.isEmpty())
				continue;

			DisplaySet displaySet = new DisplaySet();
			displaySet.setId(displays.size() + 1);

			npcs.stream()
				.sorted(Comparator.<NPC>comparingDouble(npc -> npc.getStoredLocation().getZ()).reversed())
				.forEach(npc -> displaySet.setDisplayNPC(ndx.getAndIncrement(), npc.getId()));

			displays.add(displaySet);
		}
	}


	private static String getUniqueName(List<String> skinNames, List<String> backups) {
		AtomicReference<String> name = new AtomicReference<>(null);
		if (skinNames.size() != 0) {
			name.set(RandomUtils.randomElement(skinNames));
			skinNames.remove(name.get());
		} else {
			List<UUID> activePlayers = new HoursService().getActivePlayers();
			Utils.attempt(10, () -> {
				name.set(PlayerUtils.getPlayer(RandomUtils.randomElement(activePlayers)).getName());
				return !backups.contains(name.get());
			});

			backups.add(name.get());
		}

		return name.get();
	}

	@Data
	public static class DisplaySet {
		int id;
		int lastUpdatedIndex = -1;
		Display display1 = null;
		Display display2 = null;
		Display display3 = null;

		private Display getNext() {
			int ndx = lastUpdatedIndex + 1;
			if (ndx > (getDisplays().size() - 1))
				ndx = 0;

			return getDisplays().get(ndx);
		}

		private List<Display> getDisplays() {
			return Arrays.asList(display1, display2, display3);
		}

		public void setDisplayNPC(int index, int id) {
			switch (index) {
				case 1 -> display1 = new Display(id);
				case 2 -> display2 = new Display(id);
				case 3 -> display3 = new Display(id);
			}
		}
	}

	@Data
	private static class Display {
		@NonNull Integer id;
		String skinName = null;

		private void setSkin(String skinName) {
			this.skinName = skinName;

			CitizensUtils.updateSkin(id, this.skinName);
		}

		private HumanEntity getHumanEntity() {
			NPC npc = CitizensUtils.getNPC(id);
			if (npc == null)
				return null;

			if (!npc.isSpawned())
				return null;

			if (npc.getStoredLocation() == null)
				return null;

			if (!(npc.getEntity() instanceof HumanEntity humanEntity))
				return null;

			return humanEntity;
		}
	}
}
