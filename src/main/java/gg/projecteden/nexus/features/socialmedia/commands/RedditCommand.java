package gg.projecteden.nexus.features.socialmedia.commands;

import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

public class RedditCommand extends CustomCommand {

	public RedditCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Receive a link to the server's Reddit")
	void run() {
		send(json().next("&e" + EdenSocialMediaSite.REDDIT.getUrl()));
	}

}
