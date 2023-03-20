package gg.projecteden.nexus.features.minigames.utils;

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
//		Hologram hologram = HolographicDisplaysAPI.get(Nexus.getInstance()).createHologram(location.clone().add(0, 2, 0));
//		match.getHolograms().add(hologram);
//		hologram.getLines().appendText(StringUtils.colorize("&3&lPower Up"));
//		hologram.getLines().insertText(1, powerUp.getName());
//		ItemHologramLine itemLine = hologram.getLines().appendItem(powerUp.getItemStack());
//
//		itemLine.setPickupListener(listener -> {
//			Minigamer minigamer = Minigamer.of(listener.getPlayer());
//			if (!minigamer.isPlaying(match)) return;
//
//			if (message != null)
//				minigamer.tell(message);
//
//			powerUp.onPickup(Minigamer.of(listener.getPlayer()));
//			match.getHolograms().remove(hologram);
//			hologram.delete();
//			if (recurring)
//				match.getTasks().wait(TickTime.SECOND.x(10), () -> {
//					if (!match.isEnded())
//						spawn(location, true);
//				});
//		});
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
