package me.pugabyte.bncore.models.warps;

import com.dieselpoint.norm.serialize.DbSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.persistence.serializer.mysql.LocationSerializer;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Warp {
	private String name;
	@DbSerializer(LocationSerializer.class)
	private Location location;
	private String type;

	public void teleport(Player player) {
		player.teleport(location);
		player.sendMessage(StringUtils.colorize("&3Warping to &e" + name));
	}

}

