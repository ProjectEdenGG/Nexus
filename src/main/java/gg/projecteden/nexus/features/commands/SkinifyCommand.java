package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.OfflinePlayer;

@Aliases("skinifier")
public class SkinifyCommand extends CustomCommand {

	public SkinifyCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void skin(@Arg("self") OfflinePlayer player) {
		send(json("&eClick here &3to Project Eden-ify your skin!").url(EdenSocialMediaSite.WEBSITE.getUrl() + "/skins/?uuid=" + player.getUniqueId()));
	}
}
