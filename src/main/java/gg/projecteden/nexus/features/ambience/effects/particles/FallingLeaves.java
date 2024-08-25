package gg.projecteden.nexus.features.ambience.effects.particles;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.ambience.effects.particles.common.ParticleEffect;
import gg.projecteden.nexus.features.ambience.effects.particles.common.ParticleEffectType;
import gg.projecteden.nexus.features.events.DebugDotCommand;
import gg.projecteden.nexus.models.ambience.AmbienceUser;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor
public class FallingLeaves extends ParticleEffect {
	private LeavesParticle leavesParticle;
	private double x;
	private double y;
	private double z;

	public static final long LIFE = TickTime.SECOND.x(6);

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
			DebugDotCommand.play(player, new Location(player.getWorld(), xRange, yRange, zRange), ColorType.RED);
	}

	private enum LeavesParticle {
		OAK(Material.OAK_LEAVES, Material.GREEN_CONCRETE, Material.GREEN_TERRACOTTA),
		SPRUCE(Material.SPRUCE_LEAVES, Material.SPRUCE_LEAVES),
		BIRCH(Material.BIRCH_LEAVES, Material.GREEN_TERRACOTTA),
		JUNGLE(Material.JUNGLE_LEAVES, Material.GREEN_CONCRETE, Material.GREEN_TERRACOTTA),
		ACACIA(Material.ACACIA_LEAVES, Material.GREEN_CONCRETE, Material.GREEN_TERRACOTTA),
		DARK_OAK(Material.DARK_OAK_LEAVES, Material.GREEN_TERRACOTTA),
		AZALEA(Material.AZALEA_LEAVES, Material.GREEN_CONCRETE),
		FLOWERING_AZALEA(Material.FLOWERING_AZALEA_LEAVES, Material.GREEN_CONCRETE),
		MANGROVE(Material.MANGROVE_LEAVES, Material.GREEN_CONCRETE),
		CAVE_VINES(Material.CAVE_VINES, Material.GREEN_CONCRETE),
		;

		@Getter
		@NotNull
		private final Material originMaterial;
		@Getter
		@NonNull
		private final Material particleMaterial;
		@Getter
		private Material swampParticleMaterial = null;

		LeavesParticle(@NotNull Material origin, @NotNull Material particle, @Nullable Material swamp) {
			this.originMaterial = origin;
			this.particleMaterial = particle;
			this.swampParticleMaterial = swamp;
		}

		LeavesParticle(@NotNull Material origin, @NotNull Material particle) {
			this.originMaterial = origin;
			this.particleMaterial = particle;
		}

		public static @NonNull LeavesParticle of(Material material) {
			for (LeavesParticle leavesParticle : LeavesParticle.values()) {
				if (leavesParticle.getOriginMaterial() == material)
					return leavesParticle;
			}

			return OAK;
		}

		public @NonNull Material getBiomeMaterial(Player player) {
			return getParticleMaterial(player.getLocation().getBlock().getBiome());
		}

		private @NonNull Material getParticleMaterial(Biome biome) {
			Material result = this.particleMaterial;

			if (this.swampParticleMaterial != null && biome == Biome.SWAMP)
				result = this.swampParticleMaterial;
			//...

			return result;
		}

	}

}
