package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;

@Permission(Group.STAFF)
public class YeetCommand extends CustomCommand {

	public YeetCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Yeet")
	void run(Nerd player) {
		if (Minigamer.of(player).isPlaying())
			error("Cannot yeet " + player.getNickname() + ", they are in minigames");

		int wait = 0;
		for (int i = 0; i < 100; i++)
			Tasks.wait(wait += 3, () -> runCommand("slap " + player.getName()));
	}

}
