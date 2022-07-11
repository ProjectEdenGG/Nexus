package gg.projecteden.nexus.features.minigames.commands.mechanics;


import com.sk89q.worldedit.regions.Region;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.HideAndSeek;
import gg.projecteden.nexus.features.minigames.mechanics.Infection;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;

@Aliases("hideandseek")
@Permission("minigames.manage")
public class InfectionCommand extends CustomCommand {

	Arena arena;
	WorldEditUtils worldedit;
	WorldGuardUtils worldguard;

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

		this.arena = found;

		worldedit = new WorldEditUtils(player());
		worldguard = new WorldGuardUtils(player());
	}

	@Path("kit")
	void kit() {
		if (!getAliasUsed().equalsIgnoreCase("hideandseek"))
			error("No kit available for &e" + camelCase(getAliasUsed()));

		giveItem(HideAndSeek.RADAR);
		giveItem(HideAndSeek.SELECTOR_ITEM);
		giveItem(HideAndSeek.STUN_GRENADE);
		send(PREFIX + "Giving Hide and Seek kit");
	}

	@Path("expansion create <open> [id]")
	void createExpansion(boolean open, Integer id) {
		createRegionAndSchem("expansion", open, id);
	}

	@Path("spawnDoor create <open> <id>")
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
		createSchem(schemName);

		send(PREFIX + camelCase(type) + " created with id: " + id);
	}

	void createSchem(String name) {
		GameMode originalGameMode = player().getGameMode();
		Location originalLocation = location().clone();
		Location location = worldedit.toLocation(worldedit.getPlayerSelection(player()).getMinimumPoint());
		player().setGameMode(GameMode.SPECTATOR);
		player().teleportAsync(location);
		runCommand("mcmd /copy ;; wait 10 ;; /schem save " + name + " -f");
		Tasks.wait(20, () -> {
			player().teleportAsync(originalLocation);
			player().setGameMode(originalGameMode);
		});
	}



}
