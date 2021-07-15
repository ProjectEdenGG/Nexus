package me.pugabyte.nexus.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.utils.BiomeTag.BiomeTagBuilder.MatchMode;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;

@Getter
@AllArgsConstructor
public enum BiomeTag {
	OCEAN(Material.WATER_BUCKET, new BiomeTagBuilder("OCEAN", MatchMode.CONTAINS)),
	FORESTS(Material.OAK_LEAVES, new BiomeTagBuilder("FOREST", MatchMode.CONTAINS)
		.exclude(Biome.CRIMSON_FOREST, Biome.WARPED_FOREST)),
	SWAMP(Material.VINE, new BiomeTagBuilder("SWAMP", MatchMode.CONTAINS)),
	TAIGA(Material.SPRUCE_LOG, new BiomeTagBuilder("TAIGA", MatchMode.CONTAINS)),
	JUNGLE(Material.JUNGLE_LOG, new BiomeTagBuilder("JUNGLE", MatchMode.CONTAINS)),
	MESA(Material.TERRACOTTA, new BiomeTagBuilder("BADLANDS", MatchMode.CONTAINS)),
	ICE_SPIKES(Material.PACKED_ICE, new BiomeTagBuilder(Biome.ICE_SPIKES)),
	PLAINS(Material.GRASS_BLOCK, new BiomeTagBuilder("PLAINS", MatchMode.CONTAINS)),
	ALL_FORESTS(Material.BIRCH_LEAVES, new BiomeTagBuilder(FORESTS)
		.append("GIANT_SPRUCE_TAIGA", MatchMode.CONTAINS)),
	ALL_NETHER(Material.NETHERRACK, new BiomeTagBuilder(Biome.NETHER_WASTES, Biome.BASALT_DELTAS, Biome.CRIMSON_FOREST, Biome.WARPED_FOREST, Biome.SOUL_SAND_VALLEY)),
	ALL_DESERT(Material.SAND, new BiomeTagBuilder("DESERT", MatchMode.CONTAINS)),
	;

	private final Material material;
	private final BiomeTagBuilder tag;

	public Set<Biome> getValues() {
		return tag.getValues();
	}

	public Biome[] toArray() {
		return tag.toArray();
	}

	public boolean isTagged(@NotNull Biome biome) {
		return tag.isTagged(biome);
	}

	public boolean isTagged(@NotNull Block block) {
		return tag.isTagged(block);
	}

	@Override
	public String toString() {
		return tag.toString();
	}

	protected static class BiomeTagBuilder {

		public BiomeTagBuilder() {
			this.biomes = EnumSet.noneOf(Biome.class);
		}

		public BiomeTagBuilder(Biome... biomes) {
			this.biomes = EnumSet.noneOf(Biome.class);
			append(biomes);
		}

		public BiomeTagBuilder(BiomeTag... biomeTags) {
			this.biomes = EnumSet.noneOf(Biome.class);
			append(biomeTags);
		}

		public BiomeTagBuilder(BiomeTagBuilder... biomeTags) {
			this.biomes = EnumSet.noneOf(Biome.class);
			append(biomeTags);
		}

		public BiomeTagBuilder(Predicate<Biome> predicate) {
			this.biomes = EnumSet.noneOf(Biome.class);
			append(predicate);
		}

		public BiomeTagBuilder(String segment, MatchMode mode) {
			this.biomes = EnumSet.noneOf(Biome.class);
			append(segment, mode);
		}

		public BiomeTagBuilder(String segment, MatchMode mode, Biome... biomes) {
			this.biomes = EnumSet.noneOf(Biome.class);
			append(segment, mode, biomes);
		}

		private final EnumSet<Biome> biomes;

		public BiomeTagBuilder append(Biome... biomes) {
			this.biomes.addAll(Arrays.asList(biomes));
			return this;
		}

		public final BiomeTagBuilder append(BiomeTag... biomeTags) {
			for (BiomeTag biomeTag : biomeTags)
				this.biomes.addAll(biomeTag.getValues());

			return this;
		}

		public final BiomeTagBuilder append(BiomeTagBuilder... biomeTags) {
			for (BiomeTagBuilder biomeTag : biomeTags)
				this.biomes.addAll(biomeTag.getValues());

			return this;
		}

		public BiomeTagBuilder append(Predicate<Biome> predicate) {
			for (Biome m : Biome.values())
				if (predicate.test(m))
					this.biomes.add(m);
			return this;
		}

		public BiomeTagBuilder append(String segment, MatchMode mode) {
			append(segment, mode, Biome.values());
			return this;
		}

		public BiomeTagBuilder append(String segment, MatchMode mode, Biome... biomes) {
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

		public BiomeTagBuilder exclude(Biome... biomes) {
			for (Biome m : biomes)
				this.biomes.remove(m);

			return this;
		}

		public final BiomeTagBuilder exclude(BiomeTagBuilder... biomeTags) {
			for (BiomeTagBuilder biomeTag : biomeTags)
				this.biomes.removeAll(biomeTag.getValues());

			return this;
		}

		public BiomeTagBuilder exclude(Predicate<Biome> predicate) {
			biomes.removeIf(predicate);
			return this;
		}

		public BiomeTagBuilder exclude(String segment, MatchMode mode) {
			exclude(segment, mode, Biome.values());
			return this;
		}

		public BiomeTagBuilder exclude(String segment, MatchMode mode, Biome... biomes) {
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

		public Set<Biome> getValues() {
			return biomes;
		}

		public Biome[] toArray() {
			return new ArrayList<>(biomes).toArray(Biome[]::new);
		}

		public boolean isTagged(@NotNull Biome biome) {
			return biomes.contains(biome);
		}

		public boolean isTagged(@NotNull Block block) {
			return isTagged(block.getBiome());
		}

		@Override
		public String toString() {
			return biomes.toString();
		}

		public enum MatchMode {
			PREFIX,
			SUFFIX,
			CONTAINS
		}
	}
}
