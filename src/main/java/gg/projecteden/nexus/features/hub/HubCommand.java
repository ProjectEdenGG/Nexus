package gg.projecteden.nexus.features.hub;

import gg.projecteden.nexus.features.warps.commands._WarpSubCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Redirects.Redirect;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.hub.HubTreasureHunter;
import gg.projecteden.nexus.models.hub.HubTreasureHunterService;
import gg.projecteden.nexus.models.warps.WarpType;
import lombok.NonNull;

import static gg.projecteden.nexus.features.hub.HubTreasureHunt.TOTAL_TREASURE_CHESTS;

@Redirect(from = {"/tphub", "/lobby"}, to = "/hub")
public class HubCommand extends _WarpSubCommand {

	public HubCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.HUB;
	}

	@NoLiterals
	@Description("Teleport to the hub")
	void hub() {
		teleport(WarpType.NORMAL.get("hub"));
	}

	@Path("treasurehunt")
	@Description("View your treasure hunt progress")
	void treasurehunt() {
		final HubTreasureHunterService service = new HubTreasureHunterService();
		final HubTreasureHunter hunter = service.get(player());
		final int found = hunter.getFound().size();
		if (found != TOTAL_TREASURE_CHESTS)
			send(PREFIX + "You found %s of %s treasure chests".formatted(found, TOTAL_TREASURE_CHESTS));
		else
			send(PREFIX + "You found all the treasure chests");
	}

}
