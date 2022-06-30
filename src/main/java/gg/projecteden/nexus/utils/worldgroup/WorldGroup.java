package gg.projecteden.nexus.utils.worldgroup;

import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import me.lexikiq.OptionalLocation;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gg.projecteden.nexus.utils.worldgroup.SubWorldGroup.BINGO;
import static gg.projecteden.nexus.utils.worldgroup.SubWorldGroup.DEATH_SWAP;
import static gg.projecteden.nexus.utils.worldgroup.SubWorldGroup.LEGACY1;
import static gg.projecteden.nexus.utils.worldgroup.SubWorldGroup.LEGACY2;
import static gg.projecteden.nexus.utils.worldgroup.SubWorldGroup.ONEBLOCK;
import static gg.projecteden.nexus.utils.worldgroup.SubWorldGroup.RESOURCE;
import static gg.projecteden.nexus.utils.worldgroup.SubWorldGroup.STAFF_SURVIVAL;
import static gg.projecteden.nexus.utils.worldgroup.SubWorldGroup.UHC;

public enum WorldGroup implements IWorldGroup {
	SERVER("server"),
	LEGACY(LEGACY1, LEGACY2),
	SURVIVAL(List.of("safepvp", "events"), List.of(SubWorldGroup.SURVIVAL, SubWorldGroup.LEGACY, RESOURCE, STAFF_SURVIVAL)),
	CREATIVE("creative", "buildcontest"),
	MINIGAMES(List.of("gameworld"), List.of(DEATH_SWAP, UHC, BINGO)),
	SKYBLOCK(SubWorldGroup.SKYBLOCK, ONEBLOCK),
	ADVENTURE("stranded", "aeveon_project"),
	EVENTS("bearfair21", "pugmas21"),
	STAFF("buildadmin", "jail", "pirate", "tiger"),
	UNKNOWN;

	@Getter
	private final @NotNull List<String> worldNames = new ArrayList<>();

	WorldGroup() {
		this(new String[0]);
	}

	WorldGroup(String... worldNames) {
		this.worldNames.addAll(Arrays.asList(worldNames));
	}

	WorldGroup(SubWorldGroup... subWorldGroups) {
		for (SubWorldGroup subWorldGroup : subWorldGroups)
			this.worldNames.addAll(subWorldGroup.getWorldNames());
	}

	WorldGroup(List<String> worldNames, List<SubWorldGroup> subWorldGroups) {
		for (SubWorldGroup subWorldGroup : subWorldGroups)
			this.worldNames.addAll(subWorldGroup.getWorldNames());

		this.worldNames.addAll(worldNames);
	}

	@Override
	public String toString() {
		return StringUtils.camelCase(name());
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
