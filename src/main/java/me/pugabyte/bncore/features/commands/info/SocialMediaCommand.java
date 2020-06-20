package me.pugabyte.bncore.features.commands.info;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class SocialMediaCommand extends CustomCommand {

	public SocialMediaCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		line();
		send(json().urlize("&3Website &7- &ehttps://bnn.gg"));
		send(json().urlize("&cYouTube &7- &ehttps://youtube.bnn.gg"));
		send(json().urlize("&bTwitter &7- &ehttps://twitter.bnn.gg"));
		send(json().urlize("&dInstagram &7- &ehttps://instagram.bnn.gg"));
		send(json().urlize("&6Reddit &7- &ehttps://reddit.bnn.gg"));
		send(json().urlize("&9Steam &7- &ehttps://steam.bnn.gg"));
	}

}
