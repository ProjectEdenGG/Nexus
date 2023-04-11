package gg.projecteden.nexus.features.minigames.utils;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import tech.blastmc.holograms.api.HologramsAPI;

import java.util.List;
import java.util.function.Consumer;

@Data
public class PowerUpUtils {
	@NonNull
	private Match match;
	@NonNull
	private List<PowerUp> powerUps;

	public PowerUp getRandomPowerUp() {
		return RandomUtils.randomElement(powerUps);
	}

	public void spawn(Location location) {
		spawn(location, false);
	}

	public void spawn(Location location, boolean recurring) {
		spawn(location, recurring, "You picked up a power up!");
	}

	public void spawn(Location location, boolean recurring, String message) {
		spawn(location, recurring, message, getRandomPowerUp());
	}

	public void spawn(Location location, boolean recurring, String message, PowerUp powerUp) {
		tech.blastmc.holograms.api.models.PowerUp hologram = HologramsAPI.powerup();
		hologram.location(location.clone().add(0, 2, 0));
		match.getHolograms().add(hologram);
		hologram.title("&3&lPower Up", powerUp.getName());
		hologram.item(powerUp.getItemStack());

		hologram.onPickup(player -> {
			Minigamer minigamer = Minigamer.of(player);
			if (!minigamer.isPlaying(match)) return;

			if (message != null)
				minigamer.tell(message);

			powerUp.onPickup(Minigamer.of(player));
			match.getHolograms().remove(hologram);
			if (recurring)
				match.getTasks().wait(TickTime.SECOND.x(10), () -> {
					if (!match.isEnded())
						spawn(location, true);
				});
		});

		hologram.spawn();
	}

	@Data
	@RequiredArgsConstructor
	public static class PowerUp {
		final String name;
		final Boolean isPositive;
		final ItemStack itemStack;
		final Consumer<Minigamer> onPickup;

		public PowerUp(String name, boolean isPositive, Material material, Consumer<Minigamer> onPickup) {
			this.name = name;
			this.isPositive = isPositive;
			this.itemStack = new ItemStack(material);
			this.onPickup = onPickup;
		}

		public String getName() {
			return StringUtils.colorize(isPositive != null ? (((isPositive) ? "&a" : "&c") + name) : name);
		}

		public void onPickup(Minigamer minigamer) {
			onPickup.accept(minigamer);
		}
	}
}
