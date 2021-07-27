package gg.projecteden.nexus.features.warps.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.warps.Warp;
import gg.projecteden.nexus.models.warps.WarpService;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@NoArgsConstructor
public class SpawnCommand extends CustomCommand implements Listener {
	private final WarpService service = new WarpService();

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
		warp.teleportAsync(player());
	}

	@Path("force [player]")
	@Permission("group.staff")
	void sudo(Player player) {
		runCommand(player, "spawn");
		runCommand(player, "spawn");
	}

	@EventHandler
	public void onFirstJoin(PlayerJoinEvent event) {
		if (event.getPlayer().hasPlayedBefore())
			return;

		Tasks.wait(1, () -> new WarpService().get("spawn", WarpType.NORMAL).teleportAsync(event.getPlayer()));
	}

}
