package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.BiomeTag.Tag.MatchMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.Range;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;

import static gg.projecteden.nexus.utils.StringUtils.camelCase;

@Getter
@AllArgsConstructor
public enum BiomeTag {
	OCEAN(Material.WATER_BUCKET, new Tag("OCEAN", MatchMode.CONTAINS)),
	FORESTS(Material.OAK_LEAVES, new Tag("FOREST", MatchMode.CONTAINS)
		.exclude(Biome.CRIMSON_FOREST, Biome.WARPED_FOREST)),
	SWAMP(Material.VINE, new Tag("SWAMP", MatchMode.CONTAINS)),
	TAIGA(Material.SPRUCE_LOG, new Tag("TAIGA", MatchMode.CONTAINS)),
	JUNGLE(Material.JUNGLE_LOG, new Tag("JUNGLE", MatchMode.CONTAINS)),
	MESA(Material.TERRACOTTA, new Tag("BADLANDS", MatchMode.CONTAINS)),
	SAVANNA(Material.ACACIA_LOG, new Tag("SAVANNA", MatchMode.CONTAINS)),
	ICE_SPIKES(Material.PACKED_ICE, new Tag(Biome.ICE_SPIKES)),
	PLAINS(Material.GRASS_BLOCK, new Tag("PLAINS", MatchMode.CONTAINS).append(Biome.MEADOW)),
	ALL_FORESTS(Material.BIRCH_LEAVES, new Tag(FORESTS)
		.append("GIANT_SPRUCE_TAIGA", MatchMode.CONTAINS)),
	ALL_NETHER(Material.NETHERRACK, new Tag(Biome.NETHER_WASTES, Biome.BASALT_DELTAS, Biome.CRIMSON_FOREST,
		Biome.WARPED_FOREST, Biome.SOUL_SAND_VALLEY)),
	ALL_DESERT(Material.SAND, new Tag("DESERT", MatchMode.CONTAINS)),
	;

	private final Material material;
	private final Tag tag;

	public Set<Biome> getValues() {
		return tag.biomes;
	}

	public Biome[] toArray() {
		return new ArrayList<>(tag.biomes).toArray(Biome[]::new);
	}

	public boolean isTagged(@NotNull Biome biome) {
		return tag.biomes.contains(biome);
	}

	public boolean isTagged(@NotNull Block block) {
		return isTagged(block.getBiome());
	}

	@Override
	public String toString() {
		return tag.biomes.toString();
	}

	@Getter
	@AllArgsConstructor
	public enum BiomeClimateType {
		FROZEN(-0.5, 0.05),
		COLD(0.2, 0.3),
		TEMPERATE(0.5, 0.95),
		WARM(1.0, 2.0),
		AQUATIC(null, null),
		;

		private final Double minTemperature;
		private final Double maxTemperature;

		public static @NotNull BiomeClimateType of(Location location) {
			final Block block = location.getBlock();
			final Biome biome = block.getBiome();
			if (BiomeTag.OCEAN.isTagged(biome))
				return AQUATIC;

			double temperature = location.getWorld().getTemperature(block.getX(), block.getY(), block.getZ());
			for (BiomeClimateType type : BiomeClimateType.values())
				if (Range.between(type.getMinTemperature(), type.getMaxTemperature()).contains(temperature))
					return type;

			throw new InvalidInputException("Biome " + camelCase(biome) + " with temperature "
				+ StringUtils.getDf().format(temperature) + " does not match any biome climate type");
		}
	}

	protected static class Tag {

		private final EnumSet<Biome> biomes;

		public Tag() {
			this.biomes = EnumSet.noneOf(Biome.class);
		}

		public Tag(Biome... biomes) {
			this.biomes = EnumSet.noneOf(Biome.class);
			append(biomes);
		}

		public Tag(BiomeTag... biomeTags) {
			this.biomes = EnumSet.noneOf(Biome.class);
			append(biomeTags);
		}

		public Tag(Tag... biomeTags) {
			this.biomes = EnumSet.noneOf(Biome.class);
			append(biomeTags);
		}

		public Tag(Predicate<Biome> predicate) {
			this.biomes = EnumSet.noneOf(Biome.class);
			append(predicate);
		}

		public Tag(String segment, MatchMode mode) {
			this.biomes = EnumSet.noneOf(Biome.class);
			append(segment, mode);
		}

		public Tag(String segment, MatchMode mode, Biome... biomes) {
			this.biomes = EnumSet.noneOf(Biome.class);
			append(segment, mode, biomes);
		}

		public Tag append(Biome... biomes) {
			this.biomes.addAll(Arrays.asList(biomes));
			return this;
		}

		public final Tag append(BiomeTag... biomeTags) {
			for (BiomeTag biomeTag : biomeTags)
				this.biomes.addAll(biomeTag.getValues());

			return this;
		}

		public final Tag append(Tag... biomeTags) {
			for (Tag biomeTag : biomeTags)
				this.biomes.addAll(biomeTag.biomes);

			return this;
		}

		public Tag append(Predicate<Biome> predicate) {
			for (Biome m : Biome.values())
				if (predicate.test(m))
					this.biomes.add(m);
			return this;
		}

		public Tag append(String segment, MatchMode mode) {
			append(segment, mode, Biome.values());
			return this;
		}

		public Tag append(String segment, MatchMode mode, Biome... biomes) {
			segment = segment.toUpperCase();

			switch (mode) {
				case PREFIX:
					for (Biome m : biomes)
						if (m.name().startsWith(segment))
							this.biomes.add(m);
					break;

				case SUFFIX:
					for (Biome m : biomes)
						if (m.name().endsWith(segment))
							this.biomes.add(m);
					break;

				case CONTAINS:
					for (Biome m : biomes)
						if (m.name().contains(segment))
							this.biomes.add(m);
					break;
			}

			return this;
		}

		public Tag exclude(Biome... biomes) {
			for (Biome m : biomes)
				this.biomes.remove(m);

			return this;
		}

		public final Tag exclude(Tag... biomeTags) {
			for (Tag biomeTag : biomeTags)
				this.biomes.removeAll(biomeTag.biomes);

			return this;
		}

		public Tag exclude(Predicate<Biome> predicate) {
			biomes.removeIf(predicate);
			return this;
		}

		public Tag exclude(String segment, MatchMode mode) {
			exclude(segment, mode, Biome.values());
			return this;
		}

		public Tag exclude(String segment, MatchMode mode, Biome... biomes) {
			segment = segment.toUpperCase();

			switch (mode) {
				case PREFIX:
					for (Biome m : biomes)
						if (m.name().startsWith(segment))
							this.biomes.remove(m);
					break;

				case SUFFIX:
					for (Biome m : biomes)
						if (m.name().endsWith(segment))
							this.biomes.remove(m);
					break;

				case CONTAINS:
					for (Biome m : biomes)
						if (m.name().contains(segment))
							this.biomes.remove(m);
					break;
			}

			return this;
		}

		public enum MatchMode {
			PREFIX,
			SUFFIX,
			CONTAINS
		}
	}
}
