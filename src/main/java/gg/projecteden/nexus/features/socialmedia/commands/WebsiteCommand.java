package gg.projecteden.nexus.features.socialmedia.commands;

import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

@Aliases({"weburl", "web", "weblink"})
public class WebsiteCommand extends CustomCommand {

	public WebsiteCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Receive a link to the server's website")
	void run() {
		send(json()
			.next("&ehttps:&e//&eprojecteden&e.gg") // Color codes to prevent client from auto detecting link
			.url(EdenSocialMediaSite.WEBSITE.getUrl() + "?uuid=" + uuid())
		);
	}

}
