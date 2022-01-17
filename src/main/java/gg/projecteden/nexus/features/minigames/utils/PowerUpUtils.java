package gg.projecteden.nexus.features.minigames.utils;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.managers.PlayerManager;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
		Hologram hologram = HologramsAPI.createHologram(Nexus.getInstance(), location.clone().add(0, 2, 0));
		match.getHolograms().add(hologram);
		hologram.appendTextLine(StringUtils.colorize("&3&lPower Up"));
		hologram.insertTextLine(1, powerUp.getName());
		ItemLine itemLine = hologram.appendItemLine(powerUp.getItemStack());

		itemLine.setPickupHandler(player -> {
			Minigamer minigamer = PlayerManager.get(player);
			if (!minigamer.isPlaying(match)) return;

			minigamer.tell("You picked up a power up!");
			powerUp.onPickup(PlayerManager.get(player));
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
