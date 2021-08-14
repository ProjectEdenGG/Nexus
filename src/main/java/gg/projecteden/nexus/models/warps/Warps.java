package gg.projecteden.nexus.models.warps;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static gg.projecteden.utils.StringUtils.camelCase;

@Data
@Builder
@Entity(value = "warps", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Warps implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<WarpType, List<Warp>> warps = new HashMap<>();

	public List<Warp> getAll(WarpType type) {
		return warps.get(type);
	}

	public Warp get(WarpType type, String name) {
		return getAll(type).stream().filter(warp -> warp.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public void add(Warp warp) {
		warps.computeIfAbsent(warp.getType(), $ -> new ArrayList<>()).add(warp);
	}

	public void delete(WarpType type, String name) {
		getAll(type).removeIf(warp -> warp.getName().equalsIgnoreCase(name));
	}

	public void delete(Warp warp) {
		getAll(warp.getType()).remove(warp);
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Converters(LocationConverter.class)
	public static class Warp {
		private String name;
		private WarpType type;
		private Location location;

		public @NotNull CompletableFuture<Boolean> teleportAsync(Player player) {
			return teleportAsync(player, TeleportCause.COMMAND);
		}

		public @NotNull CompletableFuture<Boolean> teleportAsync(Player player, TeleportCause teleportCause) {
			if (location == null)
				throw new InvalidInputException("Location of " + camelCase(type) + " warp " + name + " is null!");
			if (location.getWorld() == null)
				throw new InvalidInputException("World of " + camelCase(type) + " warp " + name + " is null!");

			return player.teleportAsync(location, teleportCause);
		}
	}

}
