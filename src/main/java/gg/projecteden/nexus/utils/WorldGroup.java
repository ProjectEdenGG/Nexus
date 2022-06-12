package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import lombok.Getter;
import me.lexikiq.HasLocation;
import me.lexikiq.OptionalLocation;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public enum WorldGroup {
	SERVER("server"),
	LEGACY(
		"legacy1", "legacy1_nether", "legacy1_the_end",
		"legacy2", "legacy2_nether", "legacy2_the_end"
	),
	SURVIVAL(
		"survival", "survival_nether", "survival_the_end",
		"world", "world_nether", "world_the_end", // TODO 1.19 Remove
		"resource", "resource_nether", "resource_the_end",
		"staff_world", "staff_world_nether", "staff_world_the_end",
		"safepvp", "events"
	),
	CREATIVE("creative", "buildcontest"),
	MINIGAMES("gameworld", "blockball", "deathswap", "deathswap_nether", "uhc", "uhc_nether", "bingo", "bingo_nether"),
	SKYBLOCK("bskyblock_world", "bskyblock_world_nether", "bskyblock_world_the_end"),
	ONEBLOCK("oneblock_world", "oneblock_world_nether"),
	ADVENTURE("stranded", "aeveon_project"),
	EVENTS("bearfair21", "pugmas21"),
	STAFF("buildadmin", "jail", "pirate", "tiger"),
	UNKNOWN;

	private final @NotNull List<String> worldNames;

	WorldGroup() {
		this(new String[0]);
	}

	WorldGroup(String... worldNames) {
		this.worldNames = Arrays.asList(worldNames);
	}

	@Override
	public String toString() {
		return StringUtils.camelCase(name());
	}

	public boolean contains(World world) {
		return contains(world.getName());
	}

	public boolean contains(String world) {
		return worldNames.contains(world);
	}

	public List<World> getWorlds() {
		return worldNames.stream().map(Bukkit::getWorld).filter(Objects::nonNull).collect(Collectors.toList());
	}

	public List<Player> getPlayers() {
		return getWorlds().stream().map(world -> OnlinePlayers.where().world(world).get()).flatMap(Collection::stream).toList();
	}

	public boolean isMinigames() {
		return this.equals(MINIGAMES);
	}

	public static WorldGroup of(@Nullable Entity entity) {
		return entity == null ? UNKNOWN : of(entity.getWorld());
	}

	public static WorldGroup of(@Nullable OptionalLocation location) {
		if (location == null)
			return UNKNOWN;
		Location loc = location.getLocation();
		return loc == null ? UNKNOWN : of(loc.getWorld());
	}

	public static WorldGroup of(@Nullable Location location) {
		return location == null ? UNKNOWN : of(location.getWorld());
	}

	public static WorldGroup of(@Nullable World world) {
		return world == null ? UNKNOWN : of(world.getName());
	}

	private static final Map<String, WorldGroup> CACHE = new HashMap<>();

	public static WorldGroup of(String world) {
		return CACHE.computeIfAbsent(world, $ -> rawOf(world));
	}

	private static WorldGroup rawOf(String world) {
		for (WorldGroup group : values())
			if (group.contains(world))
				return group;

		if (world.toLowerCase().startsWith("build"))
			return CREATIVE;

		return UNKNOWN;
	}

	public static boolean isResourceWorld(HasLocation location) {
		return isResourceWorld(location.getLocation().getWorld());
	}

	public static boolean isResourceWorld(World world) {
		return isResourceWorld(world.getName());
	}

	public static boolean isResourceWorld(String world) {
		return world.toLowerCase().startsWith("resource");
	}

	static {
		Nexus.getLuckPerms().getContextManager().registerCalculator(new WorldGroupCalculator());
	}

	public static class WorldGroupCalculator implements ContextCalculator<Player> {

		@Override
		public void calculate(@NotNull Player target, ContextConsumer contextConsumer) {
			contextConsumer.accept("worldgroup", WorldGroup.of(target).name());
		}

		@Override
		public ContextSet estimatePotentialContexts() {
			ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
			for (WorldGroup worldGroup : WorldGroup.values())
				builder.add("worldgroup", worldGroup.name().toLowerCase());
			return builder.build();
		}

	}

}
