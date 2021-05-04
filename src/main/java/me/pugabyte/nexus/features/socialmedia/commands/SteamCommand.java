package me.pugabyte.nexus.features.socialmedia.commands;

import lombok.NonNull;
import me.pugabyte.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

public class SteamCommand extends CustomCommand {

	public SteamCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		send(json().next("&e" + EdenSocialMediaSite.STEAM.getUrl()));
	}

}
