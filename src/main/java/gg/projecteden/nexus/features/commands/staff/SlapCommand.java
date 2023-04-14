package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.entity.Player;

@Permission(Group.STAFF)
public class SlapCommand extends CustomCommand {

	public SlapCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Slap a player")
	void run(Nerd player) {
		if (Minigamer.of(player).isPlaying())
			error("Cannot slap " + player.getNickname() + ", they are in minigames");

		final Player slapped = player.getOnlinePlayer();
		slapped.setVelocity(slapped.getLocation().getDirection().multiply(-2).setY(slapped.getEyeLocation().getPitch() > 0 ? 1.5 : -1.5));
		PlayerUtils.send(player, "&6You have been slapped!");
	}
}
