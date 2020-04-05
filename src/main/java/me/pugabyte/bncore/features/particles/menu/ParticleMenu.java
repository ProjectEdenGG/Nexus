package me.pugabyte.bncore.features.particles.menu;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.models.particle.ParticleSetting;
import me.pugabyte.bncore.models.particle.ParticleType;
import org.bukkit.entity.Player;

public class ParticleMenu {

	public static void openMain(Player player) {
		SmartInventory INV = SmartInventory.builder()
				.title("Particles")
				.size(getSize(player), 9)
				.provider(new ParticleMenuProvider())
				.build();
		INV.open(player);
	}

	public static int getSize(Player player) {
		if (player.hasPermission("particle.shapes"))
			return 6;
		return 3;
	}

	public static void openColor(Player player, ParticleType type, ParticleSetting setting) {
		SmartInventory INV = SmartInventory.builder()
				.title("Set RGB Color")
				.size(5, 9)
				.provider(new ParticleColorMenuProvider(type, setting))
				.build();
		INV.open(player);
	}

	public static void openSettingEditor(Player player, ParticleType type) {
		SmartInventory INV = SmartInventory.builder()
				.title("Particle Settings")
				.size(5, 9)
				.provider(new EffectSettingProvider(type))
				.build();
		INV.open(player);
	}

	public static void openWingsStyle(Player player) {
		SmartInventory INV = SmartInventory.builder()
				.title("Wings Style")
				.size(5, 9)
				.provider(new WingsTypeProvider())
				.build();
		INV.open(player);
	}

}
