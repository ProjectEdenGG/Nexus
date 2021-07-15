package me.pugabyte.nexus.features.ambience.managers;

import lombok.Getter;
import me.pugabyte.nexus.features.ambience.effects.particles.common.ParticleEffect;
import me.pugabyte.nexus.features.ambience.effects.particles.common.ParticleEffectConfig;
import me.pugabyte.nexus.features.ambience.effects.particles.common.ParticleEffectType;
import me.pugabyte.nexus.features.ambience.managers.common.AmbienceManager;
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

public class ParticleEffectManager extends AmbienceManager {
	private final Random random = new Random();
	private final Set<ParticleEffectConfig> effects = new HashSet<>();
	@Getter
	protected final Map<UUID, List<ParticleEffect>> activeEffects = new HashMap<>();

	public List<ParticleEffect> getEffects(UUID uuid) {
		return activeEffects.computeIfAbsent(uuid, $ -> new ArrayList<>());
	}

	public void addInstance(AmbienceUser user, ParticleEffect particleEffect) {
		getEffects(user.getUuid()).add(particleEffect);
	}

	public void tick() {
		for (UUID uuid : activeEffects.keySet()) {
			Iterator<ParticleEffect> iterator = getEffects(uuid).iterator();

			while (iterator.hasNext()) {
				ParticleEffect instance = iterator.next();
				instance.tick();

				if (!instance.isAlive())
					iterator.remove();
			}
		}
	}

	@Override
	public void update(AmbienceUser user) {
		if (!user.isParticles())
			return;

		Player player = user.getPlayer();
		if (player == null || !player.isOnline())
			return;

		Location location = player.getLocation();

		final int blockX = location.getBlockX();
		final int blockY = location.getBlockY();
		final int blockZ = location.getBlockZ();

		for (int i = 0; i < 384; i++) {
			int x = blockX + random.nextInt(36 + 1) - 36 / 2; // (0 to 36) - 18
			int y = blockY + random.nextInt(18 + 1) - 18 / 2; // (0 to 18) - 9
			int z = blockZ + random.nextInt(36 + 1) - 36 / 2; // (0 to 36) - 18

			Block block = player.getWorld().getBlockAt(x, y, z);

			for (ParticleEffectConfig config : effects)
				config.update(user, block, x, y, z);
		}
	}

	public void onStart() {
		// Dust Wind
		effects.add(new ParticleEffectConfig(ParticleEffectType.DUST_WIND, Material.SAND, Material.SAND, Material.AIR, null, 5));
		effects.add(new ParticleEffectConfig(ParticleEffectType.DUST_WIND, Material.RED_SAND, Material.RED_SAND, Material.AIR, null, 5));

		// Fireflies
		effects.add(new ParticleEffectConfig(ParticleEffectType.FIREFLIES, null, Material.GRASS_BLOCK, Material.AIR, null, 1));

		// Leaves
		effects.add(new ParticleEffectConfig(ParticleEffectType.FALLING_LEAVES, Material.OAK_LEAVES, Material.OAK_LEAVES, null, Material.AIR, 5));
		effects.add(new ParticleEffectConfig(ParticleEffectType.FALLING_LEAVES, Material.SPRUCE_LEAVES, Material.SPRUCE_LEAVES, null, Material.AIR, 5));
		effects.add(new ParticleEffectConfig(ParticleEffectType.FALLING_LEAVES, Material.BIRCH_LEAVES, Material.BIRCH_LEAVES, null, Material.AIR, 5));
		effects.add(new ParticleEffectConfig(ParticleEffectType.FALLING_LEAVES, Material.JUNGLE_LEAVES, Material.JUNGLE_LEAVES, null, Material.AIR, 5));
		effects.add(new ParticleEffectConfig(ParticleEffectType.FALLING_LEAVES, Material.ACACIA_LEAVES, Material.ACACIA_LEAVES, null, Material.AIR, 5));
		effects.add(new ParticleEffectConfig(ParticleEffectType.FALLING_LEAVES, Material.DARK_OAK_LEAVES, Material.DARK_OAK_LEAVES, null, Material.AIR, 5));
		effects.add(new ParticleEffectConfig(ParticleEffectType.FALLING_LEAVES, Material.AZALEA_LEAVES, Material.AZALEA_LEAVES, null, Material.AIR, 5));
		effects.add(new ParticleEffectConfig(ParticleEffectType.FALLING_LEAVES, Material.FLOWERING_AZALEA_LEAVES, Material.FLOWERING_AZALEA_LEAVES, null, Material.AIR, 5));
	}

}
