package gg.projecteden.nexus.features.minigames.commands.mechanics;


import com.sk89q.worldedit.regions.Region;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.HideAndSeek;
import gg.projecteden.nexus.features.minigames.mechanics.Infection;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;

@Aliases("hideandseek")
@Permission("minigames.manage")
public class InfectionCommand extends CustomCommand {
	private Arena arena;
	private WorldEditUtils worldedit;
	private WorldGuardUtils worldguard;

	public InfectionCommand(CommandEvent event) {
		super(event);

		if (!isPlayerCommandEvent())
			return;

		Arena found = null;
		try {
			found = ArenaManager.getFromLocation(location());
		} catch (InvalidInputException ignore) {}

		if (found == null) {
			send(Minigames.PREFIX + "&cNo arena found at your location; did you create the main region and set the mechanic?");
			return;
		}

		if (!(found.getMechanic() instanceof Infection) && !(found.getMechanic() instanceof HideAndSeek)) {
			send(Minigames.PREFIX + "&cArena is not instance of Infection or HideAndSeek");
			return;
		}

		arena = found;
		worldedit = new WorldEditUtils(player());
		worldguard = new WorldGuardUtils(player());
	}

	@Path("expansion create <open> [id]")
	@Description("Create an arena expansion region and schematic")
	void createExpansion(boolean open, Integer id) {
		createRegionAndSchem("expansion", open, id);
	}

	@Path("spawnDoor create <open> <id>")
	@Description("Create a spawn door region and schematic")
	void createSpawnDoor(boolean open, Integer id) {
		createRegionAndSchem("spawndoor", open, id);
	}

	void createRegionAndSchem(String type, boolean open, Integer id) {
		Region selection = worldedit.getPlayerSelection(player());

		if (selection == null)
			error("You must have a selection to create a region");

		String regionName = arena.getRegionBaseName() + "_" + type + "_" + id;
		String schemName = arena.getSchematicName(type + "_" + id + (open ? "_open" : ""));
		if (!open) {
			try {
				worldguard.getRegion(regionName);
				runCommand("rg redefine " + regionName);
			} catch (Exception ignore) {
				runCommand("rg define " + regionName);
			}
		}

		runCommand("worldeditutils schem save " + schemName);

		send(PREFIX + camelCase(type) + " created with id: " + id);
	}

}
