package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.nerd.Rank;

public class ApplyCommand extends CustomCommand {

	public ApplyCommand(CommandEvent event) {
		super(event);
	}

	@Override
	@NoLiterals
	@Description("Learn how to apply for a Staff position")
	public void help() {
		line();
		send("&3Ready to apply for " + Rank.MODERATOR.getPrefix());
		send("&3How does your name look in blue, " + Rank.MODERATOR.getChatColor() + name() + "&3? :)");
		send("&3If you think you are ready for this position, you can fill out an application here:");
		send(json().next("&e" + EdenSocialMediaSite.WEBSITE.getUrl() + "/apply/mod"));
	}

}
