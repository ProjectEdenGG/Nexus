package gg.projecteden.nexus.models.mode;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Entity(value = "mode_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class ModeUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<WorldGroup, PlayerMode> modeMap = new HashMap<>();

	public void setGameMode(@NonNull WorldGroup worldGroup, @NonNull GameMode gamemode) {
		PlayerMode mode = getPlayerMode(worldGroup);

		mode.setGameMode(gamemode);
		modeMap.put(worldGroup, mode);
	}

	public void setFlightMode(@NonNull WorldGroup worldGroup) {
		PlayerMode mode = getPlayerMode(worldGroup);

		Player player = getOnlinePlayer();
		mode.setFlightMode(new FlightMode(player.getAllowFlight(), player.isFlying()));
		modeMap.put(worldGroup, mode);
	}

	public @NonNull PlayerMode getPlayerMode(@NonNull WorldGroup worldGroup) {
		if (!modeMap.containsKey(worldGroup))
			return new PlayerMode(worldGroup);

		return modeMap.get(worldGroup);
	}

	public @NonNull GameMode getGamemode(@NonNull WorldGroup worldGroup) {
		return getPlayerMode(worldGroup).getGameMode();
	}

	public @NonNull FlightMode getFlightMode(@NonNull WorldGroup worldGroup) {
		return getPlayerMode(worldGroup).getFlightMode();
	}

	@Data
	@NoArgsConstructor
	public static class PlayerMode {
		private static final List<WorldGroup> CREATIVE_DEFAULT = List.of(WorldGroup.CREATIVE, WorldGroup.STAFF);

		public PlayerMode(WorldGroup worldGroup) {
			if (CREATIVE_DEFAULT.contains(worldGroup)) {
				this.gameMode = GameMode.CREATIVE;
				this.flightMode = new FlightMode(true, true);
			} else {
				this.gameMode = GameMode.SURVIVAL;
				this.flightMode = new FlightMode();
			}
		}

		private GameMode gameMode;
		private FlightMode flightMode;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class FlightMode {
		boolean allowFlight = false;
		boolean flying = false;
	}
}
