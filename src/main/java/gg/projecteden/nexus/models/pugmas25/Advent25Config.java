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
import gg.projecteden.nexus.utils.nms.NMSUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.waypoints.WaypointStyleAssets;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ArmorStand.LockType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.EulerAngle;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
		var frameEntity = ClientSideItemFrame.builder()
			.location(location)
			.blockFace(BlockFace.UP)
			.content(new ItemBuilder(ItemModelType.PUGMAS_PRESENT_ADVENT).build())
			.invisible(true)
			.build();

		ClientSideConfig.createEntity(frameEntity);
		ClientSideConfig.save();

		days.put(day, new Advent25Present(day, location, frameEntity.getUuid()));
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
