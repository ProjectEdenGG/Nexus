package me.pugabyte.nexus.features.ambience.particles.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.models.ambience.AmbienceUser;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class ParticleEffects {
	private static final Random random = new Random();
	private static final Set<ParticleEffect> effects = new HashSet<>();
	@Getter
	private static final Map<UUID, List<ParticleEffectInstance>> activeEffects = new HashMap<>();

	@AllArgsConstructor
	public enum AmbienceEffect {
		FALLING_LEAVES(),
		DUST_WIND(),
		FIREFLIES(),
	}

	public static List<ParticleEffectInstance> getEffects(AmbienceUser user) {
		return activeEffects.getOrDefault(user.getUuid(), new ArrayList<>());
	}

	public static void addEffect(ParticleEffect particleEffect) {
		effects.add(particleEffect);
	}

	public static void addInstance(AmbienceUser user, ParticleEffectInstance particleEffectInstance) {
		List<ParticleEffectInstance> effects = getEffects(user);
		effects.add(particleEffectInstance);
		activeEffects.put(user.getUuid(), effects);
	}

	public static void tick() {
		for (UUID uuid : activeEffects.keySet()) {
			List<ParticleEffectInstance> instances = activeEffects.get(uuid);
			Iterator<ParticleEffectInstance> iter = instances.iterator();
			while (iter.hasNext()) {
				ParticleEffectInstance instance = iter.next();
				instance.tick();

				if (!instance.isAlive()) {
					iter.remove();
				}
			}
			activeEffects.put(uuid, instances);
		}
	}

	public static void update(AmbienceUser user) {
		Player player = user.getPlayer();
		if (player == null || !player.isOnline())
			return;

		Location location = player.getLocation().toCenterLocation();

		double locX = location.getX();
		double locY = location.getY();
		double locZ = location.getZ();
		for (int i = 0; i < 384; i++) {
			double randomX = random.nextInt(36 + 1) - 36 / 2.0; // (0 to 36) - 18
			double randomY = random.nextInt(18 + 1) - 18 / 2.0; // (0 to 18) - 9
			double randomZ = random.nextInt(36 + 1) - 36 / 2.0; // (0 to 36) - 18

			double x = locX + randomX;
			double y = locY + randomY;
			double z = locZ + randomZ;
			int blockX = (int) x;
			int blockY = (int) y;
			int blockZ = (int) z;

			Block block = player.getWorld().getBlockAt(blockX, blockY, blockZ);

			for (ParticleEffect particleEffect : effects) {
				particleEffect.update(user, block, x, y, z);
			}
		}
	}

	public static void loadEffects() {
		// Dust Wind
		addEffect(new ParticleEffect(AmbienceEffect.DUST_WIND, Material.SAND, Material.SAND, Material.AIR, null, 5));
		addEffect(new ParticleEffect(AmbienceEffect.DUST_WIND, Material.RED_SAND, Material.RED_SAND, Material.AIR, null, 5));

		// Fireflies
		addEffect(new ParticleEffect(AmbienceEffect.FIREFLIES, null, Material.GRASS_BLOCK, Material.AIR, null, 1));

		// Leaves
		addEffect(new ParticleEffect(AmbienceEffect.FALLING_LEAVES, Material.OAK_LEAVES, Material.OAK_LEAVES, null, Material.AIR, 5));
		addEffect(new ParticleEffect(AmbienceEffect.FALLING_LEAVES, Material.SPRUCE_LEAVES, Material.SPRUCE_LEAVES, null, Material.AIR, 5));
		addEffect(new ParticleEffect(AmbienceEffect.FALLING_LEAVES, Material.BIRCH_LEAVES, Material.BIRCH_LEAVES, null, Material.AIR, 5));
		addEffect(new ParticleEffect(AmbienceEffect.FALLING_LEAVES, Material.JUNGLE_LEAVES, Material.JUNGLE_LEAVES, null, Material.AIR, 5));
		addEffect(new ParticleEffect(AmbienceEffect.FALLING_LEAVES, Material.ACACIA_LEAVES, Material.ACACIA_LEAVES, null, Material.AIR, 5));
		addEffect(new ParticleEffect(AmbienceEffect.FALLING_LEAVES, Material.DARK_OAK_LEAVES, Material.DARK_OAK_LEAVES, null, Material.AIR, 5));
		addEffect(new ParticleEffect(AmbienceEffect.FALLING_LEAVES, Material.AZALEA_LEAVES, Material.AZALEA_LEAVES, null, Material.AIR, 5));
		addEffect(new ParticleEffect(AmbienceEffect.FALLING_LEAVES, Material.FLOWERING_AZALEA_LEAVES, Material.FLOWERING_AZALEA_LEAVES, null, Material.AIR, 5));
	}
}
