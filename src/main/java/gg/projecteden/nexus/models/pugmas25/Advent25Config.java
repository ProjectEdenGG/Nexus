package gg.projecteden.nexus.models.pugmas25;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.clientside.models.ClientSideItemFrame;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
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
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Data
@Entity(value = "advent24_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class, ItemStackConverter.class})
public class Advent25Config implements DatabaseObject {
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

	public void remove(Advent25Present present) {
		days.remove(present.getDay());
	}

	public Collection<Advent25Present> getPresents() {
		return days.values();
	}

	public void set(int day, Location location) {
		var entity = ClientSideItemFrame.builder()
			.location(location)
			.blockFace(BlockFace.UP)
			.content(new ItemBuilder(ItemModelType.PUGMAS_PRESENT_ADVENT).build())
			.invisible(true)
			.build();

		ClientSideConfig.createEntity(entity);
		ClientSideConfig.save();

		days.put(day, new Advent25Present(day, location, entity.getUuid()));
	}

	public Advent25Present get(Location location) {
		for (Advent25Present present : getPresents())
			if (present.getLocation().equals(location.toBlockLocation()))
				return present;
		return null;
	}

	public static Advent25Present getPresent(ClientSideItemFrame itemFrame) {
		for (Advent25Present present : get().getPresents())
			if (Objects.equals(present.getEntityUuid(), itemFrame.getUuid()))
				return present;

		return null;
	}
}
