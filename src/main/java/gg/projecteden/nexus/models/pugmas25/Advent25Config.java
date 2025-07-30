package gg.projecteden.nexus.models.pugmas25;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.clientside.models.ClientSideItemFrame;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.clientside.ClientSideConfig;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Data
@Entity(value = "advent24_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class, ItemStackConverter.class})
public class Advent25Config implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	private Location lootOrigin;
	private Map<Integer, Advent25Present> days = new HashMap<>();

	public static Advent25Config get() {
		return new Advent25ConfigService().get0();
	}

	public Advent25Present get(int day) {
		return days.get(day);
	}

	public Collection<Advent25Present> getPresents() {
		return days.values();
	}

	public void set(int day, Location location) {
		var present = days.computeIfAbsent(day, $ -> new Advent25Present(day, location));

		Function<ItemModelType, ClientSideItemFrame> create = type -> ClientSideItemFrame.builder()
			.location(location)
			.blockFace(BlockFace.UP)
			.content(new ItemBuilder(type).build())
			.invisible(true)
			.build();

		var notFound = create.apply(ItemModelType.PUGMAS_PRESENT_ADVENT);
		var found = create.apply(ItemModelType.PUGMAS_PRESENT_ADVENT_OPENED);

		ClientSideConfig.createEntity(notFound);
		ClientSideConfig.createEntity(found);
		ClientSideConfig.save();

		present.getEntityUuids().addAll(List.of(notFound.getUuid(), found.getUuid()));
	}

	public Advent25Present get(Location location) {
		for (Advent25Present present : getPresents())
			if (present.getLocation().equals(location.toBlockLocation()))
				return present;
		return null;
	}

	public static Advent25Present getPresent(ClientSideItemFrame itemFrame) {
		for (Advent25Present present : get().getPresents())
			if (present.getEntityUuids().contains(itemFrame.getUuid()))
				return present;

		return null;
	}
}
