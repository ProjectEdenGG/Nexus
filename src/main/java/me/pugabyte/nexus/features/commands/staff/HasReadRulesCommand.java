package me.pugabyte.nexus.features.commands.staff;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.rule.HasReadRules;
import me.pugabyte.nexus.models.rule.RuleService;
import org.bukkit.OfflinePlayer;

@Aliases("hrr")
@Permission("group.moderator")
public class HasReadRulesCommand extends CustomCommand {
	RuleService service = new RuleService();
	HasReadRules hasReadRules;

	public HasReadRulesCommand(CommandEvent event) {
		super(event);
	}

	String CHECK = "&a✔ ";
	String X = "&c✗ ";

	@Path("<player>")
	void hrr(OfflinePlayer player) {
		hasReadRules = service.get(player);

		line();
		send(PREFIX + player.getName());
		send("&3Main: " + (hasReadRules.isMain() ? CHECK : X));
		send("&3Community: " + (hasReadRules.isCommunity1() ? CHECK : X) +
				(hasReadRules.isCommunity2() ? CHECK : X) +
				(hasReadRules.isCommunity3() ? CHECK : X));
		send("&3Streamers: " + (hasReadRules.isStreaming() ? CHECK : X));
		send("&3Survival:  " + (hasReadRules.isSurvival() ? CHECK : X));
		send("&3Minigames: " + (hasReadRules.isMinigames() ? CHECK : X));
		send("&3Creative: " + (hasReadRules.isCreative() ? CHECK : X));
		send("&3Skyblock: " + (hasReadRules.isSkyblock() ? CHECK : X));
	}

}

