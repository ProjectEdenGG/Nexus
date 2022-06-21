package gg.projecteden.nexus.features.minigames.utils;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.line.ItemHologramLine;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

@Data
public class PowerUpUtils {
	@NonNull
	private Match match;
	@NonNull
	private List<PowerUp> powerUps;

	public PowerUp getRandomPowerUp() {
		return powerUps.get(new Random().nextInt(powerUps.size()));
	}

	public void spawn(Location location) {
		spawn(location, false);
	}

	public void spawn(Location location, boolean recurring) {
		PowerUp powerUp = getRandomPowerUp();
		Hologram hologram = HolographicDisplaysAPI.get(Nexus.getInstance()).createHologram(location.clone().add(0, 2, 0));
		match.getHolograms().add(hologram);
		hologram.getLines().appendText(StringUtils.colorize("&3&lPower Up"));
		hologram.getLines().insertText(1, powerUp.getName());
		ItemHologramLine itemLine = hologram.getLines().appendItem(powerUp.getItemStack());

		itemLine.setPickupListener(listener -> {
			Minigamer minigamer = Minigamer.of(listener.getPlayer());
			if (!minigamer.isPlaying(match)) return;

			minigamer.tell("You picked up a power up!");
			powerUp.onPickup(Minigamer.of(listener.getPlayer()));
			match.getHolograms().remove(hologram);
			hologram.delete();
			if (recurring)
				match.getTasks().wait(10 * 20, () -> {
					if (!match.isEnded())
						spawn(location, true);
				});
		});
	}

	@Data
	@RequiredArgsConstructor
	public static class PowerUp {
		@NonNull
		public String name;
		@SuppressWarnings("NullableProblems") // makes lombok include the field in RequiredArgsConstructor
		@NonNull
		boolean isPositive;
		@NonNull
		ItemStack itemStack;
		@NonNull
		Consumer<Minigamer> onPickup;

		public PowerUp(String name, boolean isPositive, Material material, Consumer<Minigamer> onPickup) {
			this.name = name;
			this.isPositive = isPositive;
			this.itemStack = new ItemStack(material);
			this.onPickup = onPickup;
		}

		public String getName() {
			return StringUtils.colorize(((isPositive) ? "&a" : "&c") + name);
		}

		public void onPickup(Minigamer minigamer) {
			onPickup.accept(minigamer);
		}
	}
}
