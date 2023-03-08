package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.assetcompetition.AssetCompetition;
import gg.projecteden.nexus.models.assetcompetition.AssetCompetitionService;
import gg.projecteden.nexus.utils.JsonBuilder;
import lombok.NonNull;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.List;

@HideFromWiki
@Aliases("assetcomp")
public class AssetCompetitionCommand extends CustomCommand {
	AssetCompetitionService service = new AssetCompetitionService();
	AssetCompetition assetCompetition;

	public AssetCompetitionCommand(@NonNull CommandEvent event) {
		super(event);
		assetCompetition = service.get(player());
	}

	@Path("submit")
	void submit() {
		assetCompetition.setLocation(location());
		service.save(assetCompetition);
		send(PREFIX + "You have submitted your build for the asset competition. The judges will be by soon! Thank you and good luck!");
	}

	@Path("list")
	@Permission(Group.STAFF)
	void list() {
		List<AssetCompetition> all = service.getAll();
		if (all.size() == 0)
			error("There are no available submissions");

		JsonBuilder builder = new JsonBuilder();
		for (AssetCompetition assetCompetition : all) {
			if (!builder.isInitialized())
				builder.initialize();
			else
				builder.next("&e, ").group();

			builder.next("&3" + assetCompetition.getName())
					.command(getAliasUsed() + " tp " + assetCompetition.getName())
					.group();
		}

		line();
		send(PREFIX + "&3List of submissions &e(Click to teleport)");
		send(builder);
	}

	@Path("(view|tp) <player>")
	@Permission(Group.STAFF)
	void view(AssetCompetition assetCompetition) {
		if (assetCompetition.getLocation() == null)
			error("That player has not submitted an asset");

		player().teleportAsync(assetCompetition.getLocation(), TeleportCause.COMMAND);
	}

	@Async
	@Confirm
	@Path("clear")
	@Permission(Group.SENIOR_STAFF)
	void clear() {
		service.deleteAll();
		send(PREFIX + "All submissions cleared");
	}

}
