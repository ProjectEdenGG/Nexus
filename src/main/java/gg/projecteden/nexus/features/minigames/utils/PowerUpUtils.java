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

import java.util.ArrayList;
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

	public tech.blastmc.holograms.api.models.PowerUp spawn(Location location) {
		return spawn(location, false);
	}

	public tech.blastmc.holograms.api.models.PowerUp spawn(Location location, boolean recurring) {
		return spawn(location, recurring, "You picked up a power up!");
	}

	public tech.blastmc.holograms.api.models.PowerUp spawn(Location location, boolean recurring, String message) {
		return spawn(location, recurring, message, getRandomPowerUp());
	}

	public tech.blastmc.holograms.api.models.PowerUp spawn(Location location, boolean recurring, String message, PowerUp powerUp) {
		tech.blastmc.holograms.api.models.PowerUp hologram = HologramsAPI.powerup();
		hologram.location(location.clone().add(0, 2, 0));
		match.getHolograms().add(hologram);
		List<String> title = new ArrayList<>();
		title.add("&3&lPower Up");
		if (powerUp.name != null)
			title.add(powerUp.getName());
		hologram.title(title.toArray(new String[0]));
		hologram.item(powerUp.getItemStack());

		hologram.onPickup(player -> {
			Minigamer minigamer = Minigamer.of(player);
			if (!minigamer.isPlaying(match)) return false;
			if (!minigamer.isAlive()) return false;

			if (message != null)
				minigamer.tell(message);

			powerUp.onPickup(Minigamer.of(player));
			match.getHolograms().remove(hologram);
			if (recurring)
				match.getTasks().wait(TickTime.SECOND.x(10), () -> {
					if (!match.isEnded())
						spawn(location, true);
				});
			return true;
		});

		hologram.spawn();
		return hologram;
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
