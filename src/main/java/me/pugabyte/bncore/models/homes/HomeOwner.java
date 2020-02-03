package me.pugabyte.bncore.models.homes;

import lombok.Builder;
import lombok.Data;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class HomeOwner {
	private String uuid;
	private List<Home> homes;
	private List<String> fullAccessList;

	public OfflinePlayer getOfflinePlayer() {
		return Bukkit.getPlayer(uuid);
	}

	public Player getPlayer() {
		return getPlayer().getPlayer();
	}

	public List<String> getNames() {
		return homes.stream().map(Home::getName).collect(Collectors.toList());
	}

	public Home getHome(String name) {
		return homes.stream()
				.filter(home -> home.getName().equalsIgnoreCase(name))
				.findFirst()
				.orElseThrow(() -> new InvalidInputException("That home does not exist"));
	}

	public void allowAll(OfflinePlayer player) {
		fullAccessList.add(player.getUniqueId().toString());
	}

	public void removeAll(OfflinePlayer player) {
		fullAccessList.remove(player.getUniqueId().toString());
		homes.forEach(home -> home.remove(player));
	}

	private boolean hasFullAccess(OfflinePlayer player) {
		return fullAccessList.contains(player.getUniqueId().toString());
	}

	enum PermissionType {
		ALLOW,
		ALLOW_ALL
	}

}
