package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.spawnlimits.SpawnLimits;
import gg.projecteden.nexus.models.spawnlimits.SpawnLimits.SpawnLimitType;
import gg.projecteden.nexus.models.spawnlimits.SpawnLimitsService;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import static gg.projecteden.nexus.utils.StringUtils.getWorldDisplayName;
import static gg.projecteden.utils.UUIDUtils.UUID0;

@Permission(Group.ADMIN)
public class SpawnLimitsCommand extends CustomCommand {
	private final SpawnLimitsService service = new SpawnLimitsService();
	private final SpawnLimits limits = service.get0();

	public SpawnLimitsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		try {
			for (World world : Bukkit.getWorlds())
				for (SpawnLimitType type : SpawnLimitType.values())
					type.set(world, type.getDefaultValue());

			final SpawnLimitsService service = new SpawnLimitsService();
			final SpawnLimits limits = service.get0();

			limits.getSettings().forEach((world, settings) ->
				settings.forEach((type, value) ->
					type.set(world, value)));

			World survival = Bukkit.getWorld("survival");
			World resource = Bukkit.getWorld("resource");
			if (survival != null && resource != null)
				resource.setMonsterSpawnLimit((int) (survival.getMonsterSpawnLimit() * 1.5));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Path("defaults")
	void defaults() {
		send(PREFIX + "Default spawn limits");
		for (SpawnLimitType type : SpawnLimitType.values())
			send("&e" + camelCase(type) + " &7- " + type.getDefaultValue());
	}

	@Path("of [world]")
	void values(@Arg("current") World world) {
		send(PREFIX + "&3" + getWorldDisplayName(world));

		for (SpawnLimitType type : SpawnLimitType.values()) {
			final int value = type.get(world);
			final int defaultValue = type.getDefaultValue();
			String diff = getDiff(defaultValue, value);

			send(" &e" + camelCase(type) + " &7- " + value + diff);
		}
	}

	@Path("set <type> <value> [world]")
	void set(SpawnLimitType type, int value, @Arg("current") World world) {
		final int before = type.get(world);
		type.set(world, value);
		send(PREFIX + getWorldDisplayName(world.getName()) + " " + camelCase(type) + " spawn limit set to " + value + getDiff(before, value));
	}

	@Path("multiply <type> <multiplier> <fromWorld> <toWorld>")
	void multiply(SpawnLimitType type, double multiplier, @Arg("current") World fromWorld, @Arg("current") World toWorld) {
		set(type, (int) (type.get(fromWorld) * multiplier), toWorld);
	}

	@Path("copy <type> <fromWorld> <toWorld>")
	void copy(SpawnLimitType type, World fromWorld, World toWorld) {
		set(type, type.get(fromWorld), toWorld);
	}

	@Path("reset <type> [world]")
	void reset(SpawnLimitType type, @Arg("current") World world) {
		final int before = type.get(world);
		final int value = type.getDefaultValue();
		type.set(world, value);
		send(PREFIX + getWorldDisplayName(world.getName()) + " " + camelCase(type) + " spawn limit reset to " + value + getDiff(before, value));
	}

	@Path("reset all")
	void reset_all() {
		for (World world : Bukkit.getWorlds())
			for (SpawnLimitType type : SpawnLimitType.values())
				type.set(world, type.getDefaultValue());

		send(PREFIX + "All spawn limits reset to default");
	}

	@Path("reset allTypes [world]")
	void reset_allTypes(@Arg("current") World world) {
		for (SpawnLimitType value : SpawnLimitType.values())
			value.set(world, value.getDefaultValue());

		send(PREFIX + "All " + getWorldDisplayName(world) + " spawn limits reset to default");
	}

	@Path("reset allWorlds [type]")
	void reset_allWorlds(SpawnLimitType type) {
		for (World world : Bukkit.getWorlds())
			if (type == null)
				for (SpawnLimitType value : SpawnLimitType.values())
					value.set(world, value.getDefaultValue());
			else
				type.set(world, type.getDefaultValue());

		send(PREFIX + "All " + (type == null ? "" : camelCase(type) + " ") + "spawn limits reset to default");
	}

	@Path("save")
	void save() {
		for (World world : Bukkit.getWorlds()) {
			var limits = this.limits.get(world);
			for (SpawnLimitType type : SpawnLimitType.values())
				if (type.isDefaultFor(world))
					limits.remove(type);
				else
					limits.put(type, type.get(world));
		}

		service.save(limits);
		send(PREFIX + "Saved");
		send(service.asPrettyJson(UUID0));
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
