package gg.projecteden.nexus.features.store.gallery;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.RandomUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.Utils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.models.costume.Costume;
import gg.projecteden.nexus.models.costume.Costume.CostumeType;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class StoreGalleryNPCs {
	private static final String regionRegex = "store_gallery_npcdisplays_\\d+";
	@Getter
	private static final List<DisplaySet> displays = new ArrayList<>();

	public StoreGalleryNPCs() {
		loadDisplays();

		// Update costumes
		Tasks.repeat(TickTime.SECOND, TickTime.SECOND.x(10), () -> {
			for (DisplaySet displaySet : displays) {
				Display display = displaySet.getNext();
				if (display == null || display.skinName == null)
					continue;

				HumanEntity humanEntity = display.getHumanEntity();
				if (humanEntity == null)
					continue;

				AtomicReference<Costume> costume = new AtomicReference<>();

				Utils.attempt(50, () -> {
					costume.set(RandomUtils.randomElement(Costume.values()));
					return canUse(costume.get());
				});

				for (CostumeType type : CostumeType.values())
					humanEntity.getInventory().setItem(type.getSlot(), new ItemStack(Material.AIR));
				display.setItem(costume.get());
				displaySet.setLastUpdatedIndex(displaySet.getDisplays().indexOf(display));
			}
		});
	}

	private boolean canUse(Costume costume) {
		if (costume == null)
			return false;

		for (DisplaySet _displaySet : displays)
			for (Display _display : _displaySet.getDisplays())
				for (ItemStack item : _display.getItems().values())
					if (!Nullables.isNullOrAir(item))
						if (item.isSimilar(costume.getItem()))
							return false;

		return true;
	}

	public static void updateSkins() {
		List<String> modelNames = OnlinePlayers.getAll().stream()
			.filter(player -> !Vanish.isVanished(player))
			.map(HumanEntity::getName)
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

	private static WorldGuardUtils worldguard() {
		World world = Bukkit.getWorld("server");
		if (world != null)
			return new WorldGuardUtils(world);
		return null;
	}

	private void loadDisplays() {
		WorldGuardUtils worldguard = worldguard();
		if (worldguard == null)
			return;

		for (ProtectedRegion region : worldguard.getRegionsLike(regionRegex)) {
			AtomicInteger ndx = new AtomicInteger(1);
			Collection<NPC> npcs = worldguard.getNPCsInRegion(region);

			if (npcs.isEmpty()) {
				Nexus.warn("Could not find any gallery NPCs in region " + region.getId());
				continue;
			}

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
	public static class Display {
		@NonNull
		private final Integer id;
		private String skinName = null;

		private void setSkin(String skinName) {
			this.skinName = skinName;

			Map<EquipmentSlot, ItemStack> items = getItems();

			CitizensUtils.updateSkin(id, this.skinName);

			Tasks.wait(TickTime.SECOND, () -> {
				final HumanEntity entity = getHumanEntity();
				if (entity != null)
					items.forEach((slot, item) -> entity.getInventory().setItem(slot, item));
			});
		}

		@NotNull
		private Map<EquipmentSlot, ItemStack> getItems() {
			return new HashMap<>() {{
				final HumanEntity entity = getHumanEntity();
				if (entity != null)
					for (CostumeType value : CostumeType.values())
						put(value.getSlot(), entity.getInventory().getItem(value.getSlot()));
			}};
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

		public void setItem(Costume costume) {
			final HumanEntity entity = getHumanEntity();
			if (entity == null)
				return;

			ItemBuilder item = new ItemBuilder(costume.getItem());

			if (costume.isDyeable())
				item.dyeColor(RandomUtils.randomElement(ColorType.values()));

			try {
				if (item.material() == Material.PLAYER_HEAD)
					item.skullOwner(PlayerUtils.getPlayer(skinName));
			} catch (NullPointerException ignore) {}

			entity.getInventory().setItem(costume.getType().getSlot(), item.build());
		}

	}
}
