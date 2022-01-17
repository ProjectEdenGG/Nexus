package gg.projecteden.nexus.features.socialmedia.commands;

import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Aliases("yt")
public class YouTubeCommand extends CustomCommand {

	public YouTubeCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		send(json().next("&e" + EdenSocialMediaSite.YOUTUBE.getUrl()));
	}

}
