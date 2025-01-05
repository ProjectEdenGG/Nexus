package gg.projecteden.nexus.models.rainbowbeacon;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Entity(value = "rainbow_beacon", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class RainbowBeacon implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Location location;
	private transient Integer taskId;

	public void start() {
		AtomicInteger i = new AtomicInteger(0);
		taskId = Tasks.repeat(0, TickTime.SECOND, () -> {
			if (location == null)
				return;
			if (!location.isChunkLoaded())
				return;

			if (location.getBlock().getRelative(BlockFace.DOWN).getType() != Material.BEACON) {
				Tasks.cancel(taskId);
				taskId = null;
				return;
			}

			location.getBlock().setType(COLORS.get(i.getAndIncrement()));
			if (i.get() == 8)
				i.set(0);
		});
	}

	public void stop() {
		if (taskId != null)
			Tasks.cancel(taskId);

		if (location != null)
			location.getWorld().getChunkAtAsync(location).thenRun(() -> location.getBlock().setType(Material.AIR));
	}

	private static final List<Material> COLORS = List.of(
		Material.RED_STAINED_GLASS_PANE,
		Material.ORANGE_STAINED_GLASS_PANE,
		Material.YELLOW_STAINED_GLASS_PANE,
		Material.LIME_STAINED_GLASS_PANE,
		Material.LIGHT_BLUE_STAINED_GLASS_PANE,
		Material.BLUE_STAINED_GLASS_PANE,
		Material.PURPLE_STAINED_GLASS_PANE,
		Material.MAGENTA_STAINED_GLASS_PANE
	);

}
