package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.dnd.DNDUser;
import gg.projecteden.nexus.models.dnd.DNDUserService;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@NoArgsConstructor
public class DNDCommand extends CustomCommand implements Listener {
	private static final DNDUserService service = new DNDUserService();
	private DNDUser user;

	public DNDCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Path("[state]")
	public void toggle(Boolean state) {
		if (state == null)
			state = !user.isDnd();
		user.setDnd(state);
		service.save(user);

		if (state)
			send(PREFIX + "You have &aenabled &3Do Not Disturb mode! Make sure you manage what's muted in &e/mutemenu&3.");
		else
			send(PREFIX + "You have &cdisabled &3Do Not Disturb mode!");
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		DNDUser user = new DNDUserService().get(event.getPlayer());
		if (user.isDnd())
			PlayerUtils.send(user.getPlayer(), PREFIX + "You currently have Do Not Disturb mode &aenabled&3.");
	}
}
