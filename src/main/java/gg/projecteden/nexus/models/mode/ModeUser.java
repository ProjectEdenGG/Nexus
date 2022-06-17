package gg.projecteden.nexus.models.mode;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;

import java.util.HashMap;
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

	public void setFlightMode(@NonNull WorldGroup worldGroup, boolean allowFlight, boolean flying) {
		PlayerMode mode = getPlayerMode(worldGroup);

		mode.setFlightMode(new FlightMode(allowFlight, flying));
		modeMap.put(worldGroup, mode);
	}

	public @NonNull PlayerMode getPlayerMode(@NonNull WorldGroup worldGroup) {
		if (!modeMap.containsKey(worldGroup))
			return new PlayerMode();

		return modeMap.get(worldGroup);
	}

	public @NonNull GameMode getGamemode(@NonNull WorldGroup worldGroup) {
		return getPlayerMode(worldGroup).getGameMode();
	}

	public @NonNull FlightMode getFlightMode(@NonNull WorldGroup worldGroup) {
		return getPlayerMode(worldGroup).getFlightMode();
	}

	@Data
	public static class PlayerMode {
		GameMode gameMode = GameMode.SURVIVAL;
		FlightMode flightMode = new FlightMode();
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class FlightMode {
		boolean allowFlight = false;
		boolean flying = false;
	}
}
