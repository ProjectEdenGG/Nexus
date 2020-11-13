package me.pugabyte.bncore.features.socialmedia.commands;

import lombok.NonNull;
import me.pugabyte.bncore.features.socialmedia.SocialMedia.BNSocialMediaSite;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Aliases({"insta", "ig"})
public class InstagramCommand extends CustomCommand {

	public InstagramCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		send(json().next("&e" + BNSocialMediaSite.INSTAGRAM.getUrl()));
	}

}
