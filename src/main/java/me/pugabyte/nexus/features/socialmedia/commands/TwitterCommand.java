package me.pugabyte.nexus.features.socialmedia.commands;

import me.pugabyte.nexus.features.socialmedia.SocialMedia.BNSocialMediaSite;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

public class TwitterCommand extends CustomCommand {

	public TwitterCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		send(json().next("&e" + BNSocialMediaSite.TWITTER.getUrl()));
	}

}