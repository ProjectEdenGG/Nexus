package me.pugabyte.bncore.models.homes;

import com.dieselpoint.norm.serialize.DbSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.persistence.serializer.LocationSerializer;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.persistence.Transient;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Home {
	private String uuid;
	private String name;
	@DbSerializer(LocationSerializer.class)
	private Location location;
	private boolean locked;
	private ItemStack item;
	@Transient
	List<String> accessList;

	public HomeOwner getOwner() {
		return new HomeService().get(uuid);
	}

	public void teleport(Player player) {
		player.teleport(location.clone().add(0, .5, 0));
	}

	public boolean hasAccess(Player player) {
		// TODO
		return true;
	}

	public void allow(OfflinePlayer player) {
		// TODO
	}

	public void remove(OfflinePlayer player) {
		// TODO
	}

	@Data
	private static class PermissionMap {
		private HomeOwner.PermissionType type;
		private String uuid;
	}

}
