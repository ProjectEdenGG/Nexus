package gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Particle;

@AllArgsConstructor
public enum MiniGolfParticle {
	NONE(null, Material.TNT),
	REDSTONE(Particle.REDSTONE, Material.REDSTONE),
	FLAME(Particle.FLAME, Material.TORCH),
	SOUL_FLAME(Particle.SOUL_FIRE_FLAME, Material.SOUL_TORCH),
	CLOUD(Particle.CLOUD, Material.COBWEB),
	SNEEZE(Particle.SNEEZE, Material.PANDA_SPAWN_EGG),
	COMPOSTER(Particle.COMPOSTER, Material.COMPOSTER),
	CRIT(Particle.CRIT, Material.DIAMOND_SWORD),
	CRIT_MAGIC(Particle.CRIT_MAGIC, Material.NETHERITE_SWORD),
	WATER_SPLASH(Particle.WATER_SPLASH, Material.WATER_BUCKET),
	DOLPHIN(Particle.DOLPHIN, Material.DOLPHIN_SPAWN_EGG),
	DRAGON_BREATH(Particle.DRAGON_BREATH, Material.DRAGON_BREATH),
	ENCHANTING_TABLE(Particle.ENCHANTMENT_TABLE, Material.ENCHANTING_TABLE),
	FIREWORK(Particle.SPELL_INSTANT, Material.FIREWORK_ROCKET),
	HEART(Particle.HEART, Material.HEART_OF_THE_SEA),
	REVERSE_PORTAL(Particle.REVERSE_PORTAL, Material.OBSIDIAN),
	SOUL(Particle.SOUL, Material.SOUL_SAND),
	TOTEM(Particle.TOTEM, Material.TOTEM_OF_UNDYING),
	WITCH(Particle.SPELL_WITCH, Material.WITCH_SPAWN_EGG),
	;

	@Getter
	private final Particle particle;
	@Getter
	private final Material display;
}
