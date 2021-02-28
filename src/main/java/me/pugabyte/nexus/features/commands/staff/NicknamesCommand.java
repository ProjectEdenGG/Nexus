package me.pugabyte.nexus.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.NerdService;
import org.bukkit.OfflinePlayer;

@Permission("group.staff")
public class NicknamesCommand extends CustomCommand {
	private final NerdService service = new NerdService();
	private Nerd nerd;

	public NicknamesCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		list(player());
	}

	@Path("list [player]")
	void list(@Arg("self") OfflinePlayer player) {
		nerd = service.get(player);
		if (nerd.getNicknames().isEmpty())
			error("No nicknames found");
		send(PREFIX + String.join(", ", nerd.getNicknames()));
	}

	@Path("add <player> <nickname>")
	void add(OfflinePlayer player, String nickname) {
		nerd = service.get(player);
		if (nerd.getNicknames().contains(nickname))
			error("Nickname &e" + nickname + " &calready exists for " + player.getName());
		nerd.getNicknames().add(nickname);
		service.save(nerd);
		send(PREFIX + "Added nickname '&e" + nickname + "&3' to &e" + player.getName());
	}

	@Path("remove <player> <nickname>")
	void remove(OfflinePlayer player, String nickname) {
		nerd = service.get(player);
		if (!nerd.getNicknames().contains(nickname))
			error("Nickname &e" + nickname + " &cdoes not exist for " + player.getName());
		nerd.getNicknames().remove(nickname);
		service.save(nerd);
		send(PREFIX + "Removed nickname '&e" + nickname + "&3' from &e" + player.getName());
	}

}
