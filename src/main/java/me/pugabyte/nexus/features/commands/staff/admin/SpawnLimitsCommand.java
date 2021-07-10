package me.pugabyte.nexus.features.commands.staff.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Permission("group.admin")
public class SpawnLimitsCommand extends CustomCommand {

	public SpawnLimitsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Getter
	@AllArgsConstructor
	private enum SpawnLimitType {
		AMBIENT(Bukkit.getAmbientSpawnLimit(), World::getAmbientSpawnLimit, World::setAmbientSpawnLimit),
		ANIMALS(Bukkit.getAnimalSpawnLimit(), World::getAnimalSpawnLimit, World::setAnimalSpawnLimit),
		MONSTERS(Bukkit.getMonsterSpawnLimit(), World::getMonsterSpawnLimit, World::setMonsterSpawnLimit),
		WATER_AMBIENT(Bukkit.getWaterAmbientSpawnLimit(), World::getWaterAmbientSpawnLimit, World::setWaterAmbientSpawnLimit),
		WATER_ANIMALS(Bukkit.getWaterAnimalSpawnLimit(), World::getWaterAnimalSpawnLimit, World::setWaterAnimalSpawnLimit),
		;

		private final int defaultValue;
		private final Function<World, Integer> getter;
		private final BiConsumer<World, Integer> setter;

	}

	@Path("defaults")
	void defaults() {
		send(PREFIX + "Default spawn limits");
		for (SpawnLimitType type : SpawnLimitType.values())
			send("&e" + camelCase(type) + " &7- " + type.getDefaultValue());
	}

	@Path("of [world]")
	void values(@Arg("current") World world) {
		send(PREFIX + "&3" + StringUtils.getWorldDisplayName(world));

		for (SpawnLimitType type : SpawnLimitType.values()) {
			final int value = type.getter.apply(world);
			final int defaultValue = type.getDefaultValue();
			String diff = getDiff(defaultValue, value);

			send(" &e" + camelCase(type) + " &7- " + value + diff);
		}
	}

	@Path("set <type> <value> [world]")
	void set(SpawnLimitType type, int value, @Arg("current") World world) {
		final int before = type.getter.apply(world);
		type.setter.accept(world, value);
		send(PREFIX + camelCase(type) + " spawn limit set to " + value + getDiff(before, value));
	}

	@Path("reset <type> [world]")
	void set(SpawnLimitType type, @Arg("current") World world) {
		final int before = type.getter.apply(world);
		final int value = type.getDefaultValue();
		type.setter.accept(world, value);
		send(PREFIX + camelCase(type) + " spawn limit reset to " + value + getDiff(before, value));
	}

	@NotNull
	private String getDiff(int before, int after) {
		String diff = "";
		if (before == after)
			return diff;

		diff += " &3(";
		if (after < before)
			diff += "&c-" + (before - after);
		else
			diff += "&a+" + (after - before);
		diff += "&3)";

		return diff;
	}

}
