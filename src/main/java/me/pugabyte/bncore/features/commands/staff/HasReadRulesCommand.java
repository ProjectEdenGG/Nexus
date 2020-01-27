package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.rules.HasReadRules;
import me.pugabyte.bncore.models.rules.RulesService;
import org.bukkit.entity.Player;

@Permission("group.staff")
@Aliases("hrr")
public class HasReadRulesCommand extends CustomCommand {
	RulesService service = new RulesService();
	HasReadRules hasReadRules;

	public HasReadRulesCommand(CommandEvent event) {
		super(event);
		hasReadRules = service.get(player());
	}

	String CHECK = "&a✔ ";
	String X = "&c✗ ";

	@Path("[player]")
	void hrr(Player player) {
		send("&eCommunity: " + (hasReadRules.isCommunity1() ? CHECK : X) +
				(hasReadRules.isCommunity1() ? CHECK : X) +
				(hasReadRules.isCommunity1() ? CHECK : X));
		send("eStreamers: " + (hasReadRules.isStreaming() ? CHECK : X));
		send("&eSurvival:  " + (hasReadRules.isSurvival() ? CHECK : X));
		send("&eMinigames: " + (hasReadRules.isMinigames() ? CHECK : X));
		send("&eCreative: " + (hasReadRules.isCreative() ? CHECK : X));
		send("&eSkyblock: " + (hasReadRules.isSkyblock() ? CHECK : X));
	}

}
