package gg.projecteden.nexus.models.bigdoor;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.LocalDateTimeConverter;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.bigdoors.BigDoorManager;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.WorldUtils.TimeQuadrant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nl.pim16aap2.bigDoors.Door;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Entity(value = "big_door_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocalDateTimeConverter.class})
public class BigDoorConfig implements DatabaseObject {
	@Id
	@NonNull
	private UUID uuid;
	private String doorName;
	private long doorId;

	private String toggleRegion;

	private DoorAction queuedDoorAction;

	private int gracePeriodSeconds;
	private LocalDateTime gracePeriodStart;

	private Map<TimeQuadrant, DoorAction> timeState = new HashMap<>();
	private DoorAction timeQueuedDoorAction;

	public BigDoorConfig(Door door) {
		this.doorName = door.getName();
		this.doorId = door.getDoorUID();
		this.uuid = UUID.nameUUIDFromBytes(this.doorName.getBytes());
	}

	public enum DoorAction {
		OPEN,
		CLOSE,
		;
	}

	public Door getDoor() {
		return BigDoorManager.getDoor(this.doorId);
	}

	public List<Player> getPlayerInToggleRegion(Player player) {
		return OnlinePlayers.where()
			.world(getDoor().getWorld())
			.region(toggleRegion)
			.exclude(player)
			.vanished(false)
			.filter(_player -> GameMode.SPECTATOR != _player.getGameMode())
			.get();
	}
}
