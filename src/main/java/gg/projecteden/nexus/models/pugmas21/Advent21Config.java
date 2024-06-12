package gg.projecteden.nexus.models.pugmas21;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.clientside.models.ClientSideItemFrame;
import gg.projecteden.nexus.features.events.y2021.pugmas21.Pugmas21;
import gg.projecteden.nexus.features.events.y2021.pugmas21.models.District;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.pugmas21.Pugmas21User.Advent21User;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.parchment.HasLocation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.decoration.ItemFrame;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Entity(value = "advent21_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class, ItemStackConverter.class})
public class Advent21Config implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Location lootOrigin;
	private Map<Integer, AdventPresent> days = new HashMap<>();

	public static Advent21Config get() {
		return new Advent21ConfigService().get0();
	}

	public AdventPresent get(int day) {
		return days.get(day);
	}

	public Collection<AdventPresent> getPresents() {
		return days.values();
	}

	public void set(int day, Location location) {
		days.put(day, new AdventPresent(day, location));
	}

	public AdventPresent get(Location location) {
		for (AdventPresent present : getPresents())
			if (present.getLocation().equals(location.toBlockLocation()))
				return present;
		return null;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AdventPresent implements HasLocation {
		private int day;
		private Location location;
		private List<ItemStack> contents = new ArrayList<>();

		public AdventPresent(int day, Location location) {
			this.day = day;
			this.location = location;
		}

		public Location getLocation() {
			return location.clone();
		}

		public ItemBuilder getItem() {
			return new ItemBuilder(CustomMaterial.PUGMAS_PRESENT_ADVENT).name("Advent Present").lore("&eDay #" + day, "&f", Pugmas21.LORE);
		}

		public District getDistrict() {
			return District.of(getLocation());
		}

		@NotNull ItemFrame sendPacket(Advent21User user) {
			final CustomMaterial material = user.hasCollected(day) ? CustomMaterial.PUGMAS_PRESENT_ADVENT_OPENED : CustomMaterial.PUGMAS_PRESENT_ADVENT;
			return ClientSideItemFrame.builder()
				.location(getLocation())
				.blockFace(BlockFace.UP)
				.content(new ItemBuilder(material).build())
				.invisible(true)
				.send(user.getOnlinePlayer())
				.entity();
		}

	}

}
