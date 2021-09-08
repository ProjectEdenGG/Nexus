package gg.projecteden.nexus.features.minigames.commands;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.arenas.CheckpointArena;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.SneakyThrows;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

@Aliases("checkpoint")
@Permission("minigames.manage")
public class CheckpointsCommand extends CustomCommand {
	CheckpointArena arena;
	String regionBase;
	WorldEditUtils worldedit;
	WorldGuardUtils worldguard;

	public CheckpointsCommand(CommandEvent event) {
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

		if (!(found instanceof CheckpointArena)) {
			send(Minigames.PREFIX + "&cArena is not instance of CheckpointArena. Set mechanic and open custom mechanic settings menu.");
			return;
		}

		this.arena = (CheckpointArena) found;

		regionBase = arena.getRegionBaseName() + "_checkpoint_";

		worldedit = new WorldEditUtils(player());
		worldguard = new WorldGuardUtils(player());
	}

	@Path
	@Override
	public void help() {
		// TODO
		// Stand in correct spot
		// Select floor of checkpoint area
		// /checkpoint add <number>
	}

	@SneakyThrows
	@Path("(set|add|create) <number>")
	void set(@Arg(min = 1) int number) {
		Region selection = worldedit.getPlayerSelection(player());
		selection.expand(worldedit.toBlockVector3(Direction.UP.toVector().multiply(4)));
		String id = regionBase + number;
		ProtectedRegion region = worldguard.convert(id, selection);
		worldguard.getManager().addRegion(region);
		worldguard.getManager().saveChanges();

		arena.setCheckpoint(number, location());

		send(PREFIX + "Created checkpoint &e#" + number + " &3in &e" + arena.getDisplayName());
	}

	@Path("(remove|delete) <number>")
	void remove(int number) {
		worldguard.getManager().removeRegion(regionBase + number);
		arena.removeCheckpoint(number);
		send(PREFIX + "Removed checkpoint &e#" + number + " &3in &e" + arena.getDisplayName());
	}

	@Path("tp <number>")
	void tp(int number) {
		player().teleportAsync(arena.getCheckpoint(number), TeleportCause.COMMAND);
	}

}
