package me.pugabyte.nexus.features.commands.staff.moderator;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.rule.HasReadRules;
import me.pugabyte.nexus.models.rule.HasReadRules.RulesSection;
import me.pugabyte.nexus.models.rule.HasReadRulesService;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.OfflinePlayer;

@Aliases("hrr")
@Permission("group.moderator")
public class HasReadRulesCommand extends CustomCommand {
	private final HasReadRulesService service = new HasReadRulesService();

	public HasReadRulesCommand(CommandEvent event) {
		super(event);
	}

	String CHECK = StringUtils.getCHECK() + " ";
	String X = StringUtils.getX() + " ";

	@Path("<player>")
	void hrr(OfflinePlayer player) {
		HasReadRules hasReadRules = service.get(player);

		line();
		send(PREFIX + player.getName());
		send("&3Main: " + (hasReadRules.hasRead(RulesSection.MAIN) ? CHECK : X));
		send("&3Community: " + (hasReadRules.hasRead(RulesSection.COMMUNITY1) ? CHECK : X) +
				(hasReadRules.hasRead(RulesSection.COMMUNITY2) ? CHECK : X) +
				(hasReadRules.hasRead(RulesSection.COMMUNITY3) ? CHECK : X));
		send("&3Streamers: " + (hasReadRules.hasRead(RulesSection.STREAMING) ? CHECK : X));
		send("&3Survival:  " + (hasReadRules.hasRead(RulesSection.SURVIVAL) ? CHECK : X));
		send("&3Minigames: " + (hasReadRules.hasRead(RulesSection.MINIGAMES) ? CHECK : X));
		send("&3Creative: " + (hasReadRules.hasRead(RulesSection.CREATIVE) ? CHECK : X));
		send("&3Skyblock: " + (hasReadRules.hasRead(RulesSection.SKYBLOCK) ? CHECK : X));
	}

}

