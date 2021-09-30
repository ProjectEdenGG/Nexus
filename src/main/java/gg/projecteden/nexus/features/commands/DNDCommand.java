package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.dnd.DNDUser;
import gg.projecteden.nexus.models.dnd.DNDUserService;
import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@NoArgsConstructor
public class DNDCommand extends CustomCommand implements Listener {
	private static final DNDUserService service = new DNDUserService();

	public DNDCommand(CommandEvent event) {
		super(event);
	}

	@Path("[state]")
	public void toggle(Boolean state) {
		DNDUser user = service.get(event.getPlayer());

		if (state == null)
			state = !user.isDnd();
		user.setDnd(state);
		service.save(user);

		if (state)
			send(PREFIX + "Your status is now set to Do Not Disturb! Make sure you manage what's muted in &e/mutemenu&3.");
		else
			send(PREFIX + "Your status is no longer set to Do Not Disturb!");
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		// TODO
	}
}
