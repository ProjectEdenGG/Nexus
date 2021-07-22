package gg.projecteden.nexus.models.warps;

import com.dieselpoint.norm.serialize.DbSerializer;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.persistence.serializer.mysql.LocationSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Warp {
	private String name;
	@DbSerializer(LocationSerializer.class)
	private Location location;
	private String type;

	public void teleport(Player player) {
		if (location == null)
			throw new InvalidInputException("Location is null!");
		if (location.getWorld() == null)
			throw new InvalidInputException("World is null!");

		player.teleportAsync(location, TeleportCause.COMMAND);
	}

}

