package me.pugabyte.nexus.features.socialmedia.commands;

import lombok.NonNull;
import me.pugabyte.nexus.features.socialmedia.SocialMedia;
import me.pugabyte.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import me.pugabyte.nexus.features.socialmedia.SocialMedia.SocialMediaSite;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.socialmedia.SocialMediaService;
import me.pugabyte.nexus.utils.PlayerUtils;

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
