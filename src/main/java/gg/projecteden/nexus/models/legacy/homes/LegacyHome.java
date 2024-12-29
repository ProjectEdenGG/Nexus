package gg.projecteden.nexus.models.legacy.homes;

import dev.morphia.annotations.Converters;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.utils.Nullables;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.Function;

@Data
@NoArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class, ItemStackConverter.class})
public class LegacyHome implements PlayerOwnedObject {
	@NonNull
	private UUID uuid;
	@NonNull
	@Getter
	private String name;
	@NonNull
	private Location location;
	private ItemStack item;

	@Builder
	public LegacyHome(@NonNull UUID uuid, @NonNull String name, @NonNull Location location, ItemStack item) {
		this.uuid = uuid;
		this.name = name;

		final Function<String, Location> convert = (worldName) ->
			new Location(Bukkit.getWorld(worldName), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

		this.location = switch (location.getWorld().getName()) {
			case "world" -> convert.apply("legacy1");
			case "world_nether" -> convert.apply("legacy1_nether");
			case "world_the_end" -> convert.apply("legacy1_the_end");
			case "survival" -> convert.apply("legacy2");
			case "survival_nether" -> convert.apply("legacy2_nether");
			case "survival_the_end" -> convert.apply("legacy2_the_end");
			default -> {
				Nexus.log("Unsupported world " + location.getWorld().getName() + " for " + getNickname() + ":" + name);
				yield location;
			}
		};

		this.item = item;

		validateName(name);

		if (getOwner().getHome(name).isPresent())
			throw new InvalidInputException("&cThat legacy home already exists! Please pick a different name");
	}

	public static class LegacyHomeBuilder {

		public String getName() {
			return name;
		}

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
		if (Nullables.isNullOrAir(location.clone().add(0, 2, 0).getBlock()))
			location.add(0, .5, 0);
		player.teleportAsync(location, TeleportCause.COMMAND);
	}

}
