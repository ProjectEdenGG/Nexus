package gg.projecteden.nexus.features.warps.commands;

import gg.projecteden.nexus.features.warps.Warps;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup.SpawnType;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@NoArgsConstructor
public class SpawnCommand extends CustomCommand implements Listener {

	public SpawnCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("[world]")
	@Description("Teleport to your current world's spawn or Hub if there is none")
	void run(SpawnType spawnType) {
		if (spawnType == null)
			spawnType = worldGroup().getSpawnType();

		if (spawnType == null)
			spawnType = SpawnType.HUB;

		spawnType.teleport(player());
	}

	@Path("force [player]")
	@Permission(Group.STAFF)
	@Description("Force a player to their world's spawn and prevent /back")
	void sudo(Player player) {
		runCommand(player, "spawn");
		runCommand(player, "spawn");
	}

	@EventHandler
	public void onFirstJoin(PlayerJoinEvent event) {
		if (event.getPlayer().hasPlayedBefore())
			return;

		Tasks.wait(1, () -> Warps.hub(event.getPlayer()));
	}

}
