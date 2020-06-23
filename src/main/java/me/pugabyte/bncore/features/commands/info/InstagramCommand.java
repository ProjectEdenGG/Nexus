package me.pugabyte.bncore.features.commands.info;

import lombok.NonNull;
import me.pugabyte.bncore.features.commands.info.SocialMediaCommand.SocialMedia;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Aliases("insta")
public class InstagramCommand extends CustomCommand {

	public InstagramCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		send(json().urlize("&e" + SocialMedia.INSTAGRAM.getUrl()));
	}

}
