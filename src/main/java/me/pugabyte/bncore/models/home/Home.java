package me.pugabyte.bncore.models.home;

import dev.morphia.annotations.Converters;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.ItemStackConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static me.pugabyte.bncore.features.homes.HomesFeature.PREFIX;
import static me.pugabyte.bncore.utils.StringUtils.colorize;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class, ItemStackConverter.class})
public class Home extends PlayerOwnedObject {
	@NonNull
	private UUID uuid;
	@NonNull
	private String name;
	@NonNull
	private Location location;
	private boolean locked;
	private ItemStack item;
	private Set<UUID> accessList = new HashSet<>();

	@Builder
	public Home(@NonNull UUID uuid, @NonNull String name, @NonNull Location location, ItemStack item) {
		this.uuid = uuid;
		this.name = name;
		this.location = location;
		this.locked = getOwner().isAutoLock();
		this.item = item;

		if (getOwner().getHome(name).isPresent())
			throw new InvalidInputException("&cThat home already exists! Please pick a different name");

		if (!name.matches("^[a-zA-Z0-9_]*$"))
			throw new InvalidInputException("Home names can only contain numbers, letters and underscores");
	}

	public HomeOwner getOwner() {
		return new HomeService().get(uuid);
	}

	public void teleport(Player player) {
		if (hasAccess(player))
			player.teleport(location.clone().add(0, .5, 0), TeleportCause.COMMAND);
		else
			player.sendMessage(PREFIX + colorize("&cYou don't have access to that home"));
	}

	public boolean hasAccess(Player player) {
		if (!locked)
			return true;
		if (player.hasPermission("group.staff"))
			return true;
		if (player.getUniqueId().equals(getOfflinePlayer().getUniqueId()))
			return true;

		return getOwner().hasGivenAccessTo(player) || accessList.contains(player.getUniqueId());
	}

	public void allow(OfflinePlayer player) {
		accessList.add(player.getUniqueId());
	}

	public void remove(OfflinePlayer player) {
		accessList.remove(player.getUniqueId());
	}

}
