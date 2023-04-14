package gg.projecteden.nexus.features.socialmedia.commands;

import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;

public class RedditCommand extends CustomCommand {

	public RedditCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("Receive a link to the server's Reddit")
	void run() {
		send(json().next("&e" + EdenSocialMediaSite.REDDIT.getUrl()));
	}

}
