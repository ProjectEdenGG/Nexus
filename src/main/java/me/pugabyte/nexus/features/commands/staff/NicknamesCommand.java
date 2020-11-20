package me.pugabyte.nexus.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.NerdService;
import org.bukkit.OfflinePlayer;

import java.util.List;

@Permission("group.staff")
public class NicknamesCommand extends CustomCommand {
	NerdService service = new NerdService();

	public NicknamesCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		list(player());
	}

	@Path("list [player]")
	void list(@Arg("self") OfflinePlayer player) {
		List<String> nicknames = service.getNicknames(player.getUniqueId());
		if (nicknames.size() == 0)
			error("No nicknames found");
		send(PREFIX + String.join(", ", nicknames));
	}

	@Path("add <player> <nickname>")
	void add(OfflinePlayer player, String nickname) {
		service.addNickname(player.getUniqueId(), nickname);
		send(PREFIX + "Added nickname '&e" + nickname + "&3' to &e" + player.getName());
	}

	@Path("remove <player> <nickname>")
	void remove(OfflinePlayer player, String nickname) {
		service.removeNickname(player.getUniqueId(), nickname);
		send(PREFIX + "Removed nickname '&e" + nickname + "&3' from &e" + player.getName());
	}

}
