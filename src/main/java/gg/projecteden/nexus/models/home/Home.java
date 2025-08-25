package gg.projecteden.nexus.models.home;

import dev.morphia.annotations.Converters;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.homes.HomesFeature;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import gg.projecteden.parchment.OptionalLocation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class, ItemStackConverter.class})
public class Home implements PlayerOwnedObject, OptionalLocation {
	@NonNull
	private UUID uuid;
	@NonNull
	private String name;
	@NonNull
	private Location location;
	private boolean locked;
	private ItemStack item;
	private Set<UUID> accessList = new HashSet<>();
	private boolean respawn;

	@Builder
	public Home(@NonNull UUID uuid, @NonNull String name, @NonNull Location location, ItemStack item) {
		this.uuid = uuid;
		this.name = name;
		this.location = location;
		this.locked = getOwner().isAutoLock() || Rank.of(this).isStaff();
		this.item = item;

		if (WorldGroup.EVENTS.contains(location.getWorld()) && !getOwner().getRank().isStaff())
			throw new InvalidInputException("&cYou cannot set a home in this world");

		validateName(name);

		if (getOwner().getHome(name).isPresent())
			throw new InvalidInputException("&cThat home already exists! Please pick a different name");
	}

	public void validateName(String name) {
		if (!name.matches("^[\\w]+$"))
			throw new InvalidInputException("Home names can only contain numbers, letters and underscores");
	}

	public HomeOwner getOwner() {
		return new HomeService().get(uuid);
	}

	public void setName(String name) {
		validateName(name);
		this.name = name;
	}

	public boolean hasItem() {
		return this.item != null && this.item.getItemMeta() != null;
	}

	public ItemBuilder getDisplayItemBuilder() {
		ItemBuilder item;
		if (hasItem())
			item = new ItemBuilder(this.item);
		else if (locked)
			item = new ItemBuilder(Material.RED_CONCRETE);
		else
			item = new ItemBuilder(Material.LIME_CONCRETE);

		if (locked)
			item.glow().loreize(false).lore("", "&f&cLocked");
		else
			item.lore("", "&f&aUnlocked");

		item.name("&f" + StringUtils.camelCase(name));

		return item;
	}

	public void setRespawn(boolean respawn) {
		if (respawn) getOwner().getHomes().forEach(home -> home.setRespawn(false));
		this.respawn = respawn;
	}

	public void teleportAsync(Player player) {
		if (hasAccess(player)) {
			Location location = this.location.clone();
			location.getWorld().getChunkAtAsync(location).thenRun(() -> {
				if (Nullables.isNullOrAir(location.clone().add(0, 2, 0).getBlock()))
					location.add(0, .5, 0);
				player.teleportAsync(location, TeleportCause.COMMAND);
			});
		} else
			PlayerUtils.send(player, HomesFeature.PREFIX + "&cYou don't have access to that home");
	}

	public boolean hasAccess(Player player) {
		if (!locked)
			return true;
		if (Rank.of(player).isStaff())
			return true;
		if (player.getUniqueId().equals(getUniqueId()))
			return true;

		return getOwner().hasGivenAccessTo(player) || accessList.contains(player.getUniqueId());
	}

	public void allow(HasUniqueId player) {
		accessList.add(player.getUniqueId());
	}

	public void remove(HasUniqueId player) {
		accessList.remove(player.getUniqueId());
	}

}
