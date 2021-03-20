package me.pugabyte.nexus.features.socialmedia.commands;

import lombok.NonNull;
import me.pugabyte.nexus.features.socialmedia.SocialMedia.BNSocialMediaSite;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

@Aliases("dubtrack")
public class QueupCommand extends CustomCommand {

	public QueupCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		send(json().next("&e" + BNSocialMediaSite.QUEUP.getUrl()));
	}

}
