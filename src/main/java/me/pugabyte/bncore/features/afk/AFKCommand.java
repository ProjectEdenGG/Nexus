package me.pugabyte.bncore.features.afk;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.afk.AFKPlayer;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;

@Aliases("away")
public class AFKCommand extends CustomCommand {

	public AFKCommand(CommandEvent event) {
		super(event);
	}

	@Path("[autoreply...]")
	void afk(String autoreply) {
		AFKPlayer player = AFK.get(player());

		if (player.isAfk())
			player.notAfk();
		else {
			player.setMessage(autoreply);
			player.setForceAfk(true);
			player.afk();
			Tasks.wait(Time.SECOND.x(10), () -> {
				player.setLocation();
				player.setForceAfk(false);
			});
		}
	}

}
