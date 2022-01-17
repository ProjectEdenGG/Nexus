package gg.projecteden.nexus.features.socialmedia.commands;

import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;

public class SteamCommand extends CustomCommand {

	public SteamCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		send(json().next("&e" + EdenSocialMediaSite.STEAM.getUrl()));
	}

}
