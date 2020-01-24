package me.pugabyte.bncore.features.afk;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Tasks;

@Aliases("away")
public class AFKCommand extends CustomCommand {

	public AFKCommand(CommandEvent event) {
		super(event);
	}

	@Path("[autoreply...]")
	void afk(@Arg String autoreply) {
		AFKPlayer player = AFK.get(player());

		if (player.isAfk())
			player.notAfk();
		else {
			player.setMessage(autoreply);
			player.setForceAfk(true);
			player.afk();
			Tasks.wait(10 * 20, () -> {
				player.setLocation();
				player.setForceAfk(false);
			});
		}
	}

}
