package me.pugabyte.bncore.features.minigames.utils;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class PowerUpUtils {
	private Match match;

	public PowerUpUtils(Match match) {
		this.match = match;
	}

	public void spawn(Location location, final PowerUp powerUp) {
		Hologram hologram = HologramsAPI.createHologram(BNCore.getInstance(), location.clone().add(0, 2, 0));
		match.getHolograms().add(hologram);
		hologram.appendTextLine(Utils.colorize("&3&lPower Up"));
		hologram.insertTextLine(1, powerUp.getName());
		ItemLine itemLine = hologram.appendItemLine(powerUp.getItemStack());

		itemLine.setPickupHandler(player -> {
			player.sendMessage(Minigames.PREFIX + "You picked up a power up!");
			powerUp.onPickup(PlayerManager.get(player));
			match.getHolograms().remove(hologram);
			hologram.delete();
			match.getTasks().wait(10 * 20, () -> {
				if (!match.isEnded()) spawn(location, powerUp);
			});
		});
	}

	@Data
	@RequiredArgsConstructor
	public static class PowerUp {
		@NonNull
		public String name;
		@NonNull
		boolean isPositive;
		@NonNull
		ItemStack itemStack;
		@NonNull
		Consumer<Minigamer> onPickup;

		public PowerUp(@NonNull String name, @NonNull boolean isPositive, @NonNull Material material, @NonNull Consumer<Minigamer> onPickup) {
			this.name = name;
			this.isPositive = isPositive;
			this.itemStack = new ItemStack(material);
			this.onPickup = onPickup;
		}

		public String getName() {
			return Utils.colorize(((isPositive) ? "&a" : "&c") + name);
		}

		public void onPickup(Minigamer minigamer) {
			onPickup.accept(minigamer);
		}
	}
}
