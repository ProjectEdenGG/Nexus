package me.pugabyte.bncore.features.socialmedia.commands;

import lombok.NonNull;
import me.pugabyte.bncore.features.socialmedia.SocialMedia.BNSocialMediaSite;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class SteamCommand extends CustomCommand {

	public SteamCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		send(json().next("&e" + BNSocialMediaSite.STEAM.getUrl()));
	}

}
