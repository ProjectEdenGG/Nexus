package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.socialmedia.SocialMediaUser.BNSocialMediaSite;

@Aliases({"weburl", "web", "weblink"})
public class WebsiteCommand extends CustomCommand {

	public WebsiteCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		send(json().urlize("&e" + BNSocialMediaSite.WEBSITE.getUrl()));
	}

}
