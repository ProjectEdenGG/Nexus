package me.pugabyte.bncore.features.minigames.commands;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.SneakyThrows;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.arenas.CheckpointArena;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.utils.WorldEditUtils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

@Aliases("checkpoint")
@Permission("minigames.manage")
public class CheckpointsCommand extends CustomCommand {
	CheckpointArena arena;
	String regionBase;
	WorldEditUtils weUtils;
	WorldGuardUtils wgUtils;

	public CheckpointsCommand(CommandEvent event) {
		super(event);
		Arena found = null;
		try {
			found = ArenaManager.getFromLocation(player().getLocation());
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

		regionBase = arena.getMechanicType().name() + "_" + arena.getName() + "_checkpoint_";

		weUtils = new WorldEditUtils(player());
		wgUtils = new WorldGuardUtils(player());
	}

	@Path
	void help() {
		// TODO
		// Stand in correct spot
		// Select floor of checkpoint area
		// /checkpoint add <number>
	}

	@SneakyThrows
	@Path("(set|add|create) <number>")
	void addCheckpoint(int number) {
		Region selection = weUtils.getPlayerSelection(player());
		selection.expand(weUtils.toBlockVector3(Direction.UP.toVector().multiply(4)));
		String id = regionBase + number;
		ProtectedRegion region = wgUtils.convert(id, selection);
		wgUtils.getManager().addRegion(region);
		wgUtils.getManager().saveChanges();

		arena.setCheckpoint(number, player().getLocation());

		send(PREFIX + "Created checkpoint &e#" + number + " &3in &e" + arena.getDisplayName());
	}

	@Path("(remove|delete) <number>")
	void removeCheckpoint(int number) {
		wgUtils.getManager().removeRegion(regionBase + number);
		arena.removeCheckpoint(number);
		send(PREFIX + "Removed checkpoint &e#" + number + " &3in &e" + arena.getDisplayName());
	}

	@Path("tp <number>")
	void tp(int number) {
		player().teleport(arena.getCheckpoint(number), TeleportCause.COMMAND);
	}

}
