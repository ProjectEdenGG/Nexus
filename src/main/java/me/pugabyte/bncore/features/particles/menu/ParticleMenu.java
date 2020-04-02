package me.pugabyte.bncore.features.particles.menu;

import fr.minuskube.inv.SmartInventory;
import org.bukkit.entity.Player;

public class ParticleMenu {

	public static void openMain(Player player, int page) {
		SmartInventory INV = SmartInventory.builder()
				.title("Particles")
				.size(5, 9)
				.provider(new ParticleMenuProvider())
				.build();
		INV.open(player, page);
	}

	public static void openColor(Player player) {
		SmartInventory INV = SmartInventory.builder()
				.title("Set RGB Color")
				.size(5, 9)
				.provider(new ParticleColorMenuProvider())
				.build();
		INV.open(player);
	}

}
