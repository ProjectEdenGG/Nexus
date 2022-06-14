package gg.projecteden.nexus.models.legacy.homes;

import dev.morphia.annotations.Converters;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@Data
@NoArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class, ItemStackConverter.class})
public class LegacyHome implements PlayerOwnedObject {
	@NonNull
	private UUID uuid;
	@NonNull
	private String name;
	@NonNull
	private Location location;
	private ItemStack item;

	@Builder
	public LegacyHome(@NonNull UUID uuid, @NonNull String name, @NonNull Location location, ItemStack item) {
		this.uuid = uuid;
		this.name = name;
		this.location = location;
		this.item = item;

		validateName(name);

		if (getOwner().getHome(name).isPresent())
			throw new InvalidInputException("&cThat legacy home already exists! Please pick a different name");
	}

	public void validateName(String name) {
		if (!name.matches("^[\\w]+$"))
			throw new InvalidInputException("Home names can only contain numbers, letters and underscores");
	}

	public LegacyHomeOwner getOwner() {
		return new LegacyHomeService().get(uuid);
	}

	public void setName(String name) {
		validateName(name);
		this.name = name;
	}

	public void teleportAsync(Player player) {
		Location location = this.location.clone();
		if (isNullOrAir(location.clone().add(0, 2, 0).getBlock()))
			location.add(0, .5, 0);
		player.teleportAsync(location, TeleportCause.COMMAND);
	}

}
