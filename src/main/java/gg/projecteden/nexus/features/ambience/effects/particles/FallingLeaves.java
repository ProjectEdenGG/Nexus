package gg.projecteden.nexus.features.ambience.effects.particles;

import gg.projecteden.nexus.features.ambience.effects.particles.common.ParticleEffect;
import gg.projecteden.nexus.features.ambience.effects.particles.common.ParticleEffectType;
import gg.projecteden.nexus.features.particles.effects.DotEffect;
import gg.projecteden.nexus.models.ambience.AmbienceUser;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@NoArgsConstructor
public class FallingLeaves extends ParticleEffect {
	private LeavesParticle leavesParticle;
	private double x;
	private double y;
	private double z;

	public static final int LIFE = TickTime.SECOND.x(6);

	public FallingLeaves(AmbienceUser user, Block block, double chance) {
		super(user, ParticleEffectType.FALLING_LEAVES, Particle.FALLING_DUST, LIFE, chance);

		this.leavesParticle = LeavesParticle.of(block.getType());
		this.x = block.getX();
		this.y = block.getY();
		this.z = block.getZ();
	}

	@Override
	public void play() {
		Player player = user.getPlayer();
		if (player == null)
			return;

		double xRange = x + RandomUtils.randomDouble(.2, .8);
		double yRange = y - 0.05;
		double zRange = z + RandomUtils.randomDouble(.2, .8);

		Material particleMaterial = leavesParticle.getBiomeMaterial(player);

		player.spawnParticle(particle, xRange, yRange, zRange, 0, 0, 0, 0, 1, Bukkit.createBlockData(particleMaterial));

		if (user.isDebug())
			DotEffect.debug(player, new Location(player.getWorld(), xRange, yRange, zRange), Color.RED);
	}

	@AllArgsConstructor
	@RequiredArgsConstructor
	private enum LeavesParticle {
		OAK(Material.GREEN_CONCRETE, Material.GREEN_TERRACOTTA),
		SPRUCE(Material.SPRUCE_LEAVES),
		BIRCH(Material.GREEN_TERRACOTTA),
		JUNGLE(Material.GREEN_CONCRETE, Material.GREEN_TERRACOTTA),
		ACACIA(Material.GREEN_CONCRETE, Material.GREEN_TERRACOTTA),
		DARK_OAK(Material.GREEN_TERRACOTTA),
		AZALEA(Material.GREEN_CONCRETE),
		FLOWERING_AZALEA(Material.GREEN_CONCRETE),
		;

		@Getter
		@NonNull
		private final Material material;
		@Getter
		private Material swampMaterial = null;

		public static @NonNull LeavesParticle of(Material material) {
			String leavesStr = material.name().toUpperCase().replace("_LEAVES", "");

			for (LeavesParticle leavesParticle : LeavesParticle.values()) {
				if (leavesParticle.name().equalsIgnoreCase(leavesStr))
					return leavesParticle;
			}

			return OAK;
		}

		public Material getBiomeMaterial(Player player) {
			return getMaterial(player.getLocation().getBlock().getBiome());
		}

		private Material getMaterial(Biome biome) {
			Material result = null;

			if (biome == Biome.SWAMP)
				result = this.swampMaterial;
			//...

			return Nullables.isNullOrAir(result) ? this.material : result;
		}

	}

}
