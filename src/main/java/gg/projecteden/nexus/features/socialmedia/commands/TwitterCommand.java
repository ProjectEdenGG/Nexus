package gg.projecteden.nexus.features.socialmedia.commands;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.features.socialmedia.integrations.Twitter;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Tasks;

public class TwitterCommand extends CustomCommand {

	public TwitterCommand(CommandEvent event) {
		super(event);
	}

	static {
		Twitter.connect();
		if (Nexus.getEnv() == Env.PROD)
			Tasks.repeatAsync(TickTime.MINUTE, TickTime.MINUTE.x(5), Twitter::lookForNewTweets);
	}

	@Path
	@Description("Receive a link to the server's Twitter")
	void run() {
		send(json().next("&e" + EdenSocialMediaSite.TWITTER.getUrl()));
	}

	@Async
	@Path("lookForNewTweets")
	@Permission(Group.ADMIN)
	@Description("Look for new tweets to post in Discord")
	void lookForNewTweets() {
		Twitter.lookForNewTweets();
	}

}
