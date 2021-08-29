package gg.projecteden.nexus.features.commands.staff;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.Region;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Permission("group.staff")
public class WorldEditUtilsCommand extends CustomCommand {
	private WorldEditUtils worldEditUtils;

	public WorldEditUtilsCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			worldEditUtils = new WorldEditUtils(player());
	}

	@Confirm
	@Path("breakNaturally")
	void breakNaturally() {
		Region selection = worldEditUtils.getPlayerSelection(player());
		if (selection.getArea() > 50000)
			error("Max selection size is 50000");

		for (Block block : worldEditUtils.getBlocks(selection)) {
			if (block.getType() == Material.AIR)
				continue;

			block.breakNaturally();
		}
	}

	@Path("smartReplace <from> <to>")
	void smartReplace(Material from, Material to) {
		Region selection = worldEditUtils.getPlayerSelection(player());

		for (Block block : worldEditUtils.getBlocks(selection)) {
			if (block.getType() != from)
				continue;

			block.setBlockData(Bukkit.createBlockData(block.getBlockData().getAsString().replace(from.name().toLowerCase(), to.name().toLowerCase())));
		}
	}

	@Path("schem buildQueue <schematic> <seconds>")
	void schemBuildQueue(String schematic, int seconds) {
		worldEditUtils.paster()
			.file(schematic)
			.at(location().add(-10, 0, 0))
			.duration(TickTime.SECOND.x(seconds))
			.buildQueue();
	}

	@Path("schem saveReal <name>")
	void schemSaveReal(String name) {
		worldEditUtils.save(name, worldEditUtils.getPlayerSelection(player()));
		send("Saved schematic " + name);
	}

	@Path("schem save <name>")
	void schemSave(String name) {
		GameMode originalGameMode = player().getGameMode();
		Location originalLocation = location().clone();
		Location location = worldEditUtils.toLocation(worldEditUtils.getPlayerSelection(player()).getMinimumPoint());
		player().setGameMode(GameMode.SPECTATOR);
		player().teleportAsync(location);
		runCommand("mcmd /copy ;; wait 10 ;; /schem save " + name + " -f");
		Tasks.wait(20, () -> {
			player().teleportAsync(originalLocation);
			player().setGameMode(originalGameMode);
		});

		send("Saved schematic " + name);
	}

	@Path("schem paste <name>")
	void schemPaste(String name) {
		worldEditUtils.paster().file(name).at(location()).pasteAsync();
		send("Pasted schematic " + name);
	}

	private static final Map<UUID, Clipboard> clipboards = new HashMap<>();

	@Path("clipboard copy")
	void clipboardCopy() {
		worldEditUtils.copy(worldEditUtils.getPlayerSelection(player())).thenAccept(clipboard -> {
			clipboards.put(uuid(), clipboard);
			send("Copied selection");
		});
	}

	@Path("clipboard paste")
	void clipboardPaste() {
		if (!clipboards.containsKey(uuid()))
			error("You have not copied anything");

		worldEditUtils.paster().clipboard(clipboards.get(uuid())).at(location()).pasteAsync();
		send("Pasted clipboard");
	}

}
