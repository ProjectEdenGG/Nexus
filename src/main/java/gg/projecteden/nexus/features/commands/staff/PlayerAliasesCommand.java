package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
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

	@NoLiterals
	@Description("List your aliases")
	void run() {
		list(player());
	}

	@Description("List a player's aliases")
	void list(@Optional("self") OfflinePlayer player) {
		nerd = Nerd.of(player);
		if (nerd.getAliases().isEmpty())
			error("No aliases found");
		send(PREFIX + String.join(", ", nerd.getAliases()));
	}

	@Description("Define an alias for a player")
	void add(OfflinePlayer player, String alias) {
		nerd = Nerd.of(player);
		alias = alias.toLowerCase();
		if (nerd.getAliases().contains(alias))
			error("Alias &e" + alias + " &calready exists for " + player.getName());
		nerd.getAliases().add(alias);
		service.save(nerd);
		send(PREFIX + "Added alias '&e" + alias + "&3' to &e" + player.getName());
	}

	@Description("Remove an alias from a player")
	void remove(OfflinePlayer player, String alias) {
		nerd = Nerd.of(player);
		alias = alias.toLowerCase();
		if (!nerd.getAliases().contains(alias))
			error("Alias &e" + alias + " &cdoes not exist for " + player.getName());
		nerd.getAliases().remove(alias);
		service.save(nerd);
		send(PREFIX + "Removed alias '&e" + alias + "&3' from &e" + player.getName());
	}

}
