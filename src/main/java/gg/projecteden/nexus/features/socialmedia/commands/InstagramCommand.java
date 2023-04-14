package gg.projecteden.nexus.features.socialmedia.commands;

import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.features.socialmedia.integrations.Instagram;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;

@Aliases({"insta", "ig"})
public class InstagramCommand extends CustomCommand {

	public InstagramCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		if (Nexus.getEnv() == Env.PROD) {
//			Instagram.connect();
			Tasks.repeatAsync(TickTime.MINUTE, TickTime.MINUTE.x(5), () -> {
				Instagram.lookForNewStories();
				Instagram.lookForNewPosts();
			});
		}
	}

	@NoLiterals
	@Description("Receive a link to the server's Instagram")
	void run() {
		send(json().next("&e" + EdenSocialMediaSite.INSTAGRAM.getUrl()));
	}

}
