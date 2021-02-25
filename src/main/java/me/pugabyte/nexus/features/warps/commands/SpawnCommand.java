package me.pugabyte.nexus.features.warps.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.warps.Warp;
import me.pugabyte.nexus.models.warps.WarpService;
import me.pugabyte.nexus.models.warps.WarpType;
import org.bukkit.entity.Player;

public class SpawnCommand extends CustomCommand {
	private WarpService service = new WarpService();

	public SpawnCommand(CommandEvent event) {
		super(event);
	}

	public enum SpawnType {
		HUB,
		SURVIVAL,
		MINIGAMES,
		CREATIVE
	}

	@Path("[world]")
	void run(@Arg("survival") SpawnType spawnType) {
		Warp warp = service.get(spawnType.name(), WarpType.NORMAL);
		warp.teleport(player());
	}

	@Path("force [player]")
	@Permission("group.staff")
	void sudo(Player player) {
		runCommand(player, "spawn");
		runCommand(player, "spawn");
	}

}
