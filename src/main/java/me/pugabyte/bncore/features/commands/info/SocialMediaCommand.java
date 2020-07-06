package me.pugabyte.bncore.features.commands.info;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.socialmedia.SocialMedia.BNSocialMediaSite;
import me.pugabyte.bncore.models.socialmedia.SocialMedia.SocialMediaSite;
import me.pugabyte.bncore.models.socialmedia.SocialMediaService;
import me.pugabyte.bncore.utils.Utils;

public class SocialMediaCommand extends CustomCommand {
	private final SocialMediaService service = new SocialMediaService();

	public SocialMediaCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		line();
		for (BNSocialMediaSite site : BNSocialMediaSite.values())
			send(json().urlize(site.getName() + " &7- &e" + site.getUrl()));
	}

	@Path("getItem <site>")
	@Permission("group.admin")
	void getItem(SocialMediaSite site) {
		Utils.giveItem(player(), site.getHead());
	}

}
