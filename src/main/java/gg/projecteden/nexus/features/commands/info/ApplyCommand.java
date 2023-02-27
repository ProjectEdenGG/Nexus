package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;

public class ApplyCommand extends CustomCommand {

	public ApplyCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Sends information in chat on how to apply for Staff")
	@Override
	public void help() {
		line();
		send("&3Ready to apply for " + Rank.MODERATOR.getPrefix());
		send("&3How does your name look in blue, " + Rank.MODERATOR.getChatColor() + name() + "&3? :)");
		send("&3If you think you are ready for this position, you can fill out an application here:");
		send(json().next("&e" + EdenSocialMediaSite.WEBSITE.getUrl() + "/apply/mod"));
	}

}
