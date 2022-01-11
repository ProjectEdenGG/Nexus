package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;

@Permission(Group.STAFF)
public class PlayerAliasesCommand extends CustomCommand {
	private final NerdService service = new NerdService();
	private Nerd nerd;

	public PlayerAliasesCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		list(player());
	}

	@Path("list [player]")
	void list(@Arg("self") OfflinePlayer player) {
		nerd = Nerd.of(player);
		if (nerd.getAliases().isEmpty())
			error("No aliases found");
		send(PREFIX + String.join(", ", nerd.getAliases()));
	}

	@Path("add <player> <alias>")
	void add(OfflinePlayer player, String alias) {
		nerd = Nerd.of(player);
		if (nerd.getAliases().contains(alias))
			error("Alias &e" + alias + " &calready exists for " + player.getName());
		nerd.getAliases().add(alias);
		service.save(nerd);
		send(PREFIX + "Added alias '&e" + alias + "&3' to &e" + player.getName());
	}

	@Path("remove <player> <alias>")
	void remove(OfflinePlayer player, String alias) {
		nerd = Nerd.of(player);
		if (!nerd.getAliases().contains(alias))
			error("Alias &e" + alias + " &cdoes not exist for " + player.getName());
		nerd.getAliases().remove(alias);
		service.save(nerd);
		send(PREFIX + "Removed alias '&e" + alias + "&3' from &e" + player.getName());
	}

}
