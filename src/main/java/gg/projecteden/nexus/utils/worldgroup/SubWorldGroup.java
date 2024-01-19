package gg.projecteden.nexus.utils.worldgroup;

import gg.projecteden.nexus.utils.LuckPermsUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.parchment.OptionalLocation;
import lombok.Getter;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum SubWorldGroup implements IWorldGroup {
	LEGACY1("legacy1", "legacy1_nether", "legacy1_the_end"),
	LEGACY2("legacy2", "legacy2_nether", "legacy2_the_end"),
	SURVIVAL("survival", "survival_nether", "survival_the_end", /* test server only */ "world", "world_nether", "world_the_end"),
	RESOURCE("resource", "resource_nether", "resource_the_end"),
	STAFF_SURVIVAL("staff_world", "staff_world_nether", "staff_world_the_end"),
	DEATH_SWAP("deathswap", "deathswap_nether"),
	UHC("uhc", "uhc_nether"),
	BINGO("bingo", "bingo_nether"),
	SKYBLOCK("bskyblock_world", "bskyblock_world_nether", "bskyblock_world_the_end"),
	ONEBLOCK("oneblock_world", "oneblock_world_nether"),
	BUILD_CONTESTS {
		@Override
		public @NotNull List<String> getWorldNames() {
			return Bukkit.getWorlds().stream().map(World::getName).filter(name -> name.startsWith("buildcontest")).toList();
		}
	},
	UNKNOWN;

	@Getter
	private final @NotNull List<String> worldNames;

	SubWorldGroup() {
		this(new String[0]);
	}

	SubWorldGroup(String... worldNames) {
		this.worldNames = Arrays.asList(worldNames);
	}

	@Override
	public String toString() {
		return StringUtils.camelCase(name());
	}

	public static SubWorldGroup of(@Nullable Entity entity) {
		return entity == null ? UNKNOWN : of(entity.getWorld());
	}

	public static SubWorldGroup of(@Nullable OptionalLocation location) {
		if (location == null)
			return UNKNOWN;
		Location loc = location.getLocation();
		return loc == null ? UNKNOWN : of(loc.getWorld());
	}

	public static SubWorldGroup of(@Nullable Location location) {
		return location == null ? UNKNOWN : of(location.getWorld());
	}

	public static SubWorldGroup of(@Nullable World world) {
		return world == null ? UNKNOWN : of(world.getName());
	}

	private static final Map<String, SubWorldGroup> CACHE = new ConcurrentHashMap<>();

	public static SubWorldGroup of(String world) {
		return CACHE.computeIfAbsent(world, $ -> rawOf(world));
	}

	private static SubWorldGroup rawOf(String world) {
		for (SubWorldGroup group : values())
			if (group.contains(world))
				return group;

		return UNKNOWN;
	}

	static {
		LuckPermsUtils.registerContext(new SubWorldGroupCalculator());
	}

	public static class SubWorldGroupCalculator implements ContextCalculator<Player> {

		@Override
		public void calculate(@NotNull Player target, ContextConsumer contextConsumer) {
			contextConsumer.accept("subworldgroup", SubWorldGroup.of(target).name());
		}

		@Override
		public ContextSet estimatePotentialContexts() {
			ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
			for (SubWorldGroup worldGroup : SubWorldGroup.values())
				builder.add("subworldgroup", worldGroup.name().toLowerCase());
			return builder.build();
		}

	}

}
