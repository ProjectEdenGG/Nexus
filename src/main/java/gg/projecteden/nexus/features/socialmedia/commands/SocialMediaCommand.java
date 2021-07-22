package gg.projecteden.nexus.features.socialmedia.commands;

import gg.projecteden.nexus.features.socialmedia.SocialMedia;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.SocialMediaSite;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.socialmedia.SocialMediaService;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NonNull;

public class SocialMediaCommand extends CustomCommand {
	private final SocialMediaService service = new SocialMediaService();

	public SocialMediaCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		line();
		for (EdenSocialMediaSite site : EdenSocialMediaSite.values())
			send(json().next(site.getName() + " &7- &e" + site.getUrl()));
	}

	@Path("reload")
	@Permission("group.admin")
	void reload() {
		SocialMedia.SocialMediaSite.reload();
		send(PREFIX + "Reloaded");
	}

	@Path("getItem <site>")
	@Permission("group.admin")
	void getItem(SocialMediaSite site) {
		PlayerUtils.giveItem(player(), site.getHead());
	}

}
