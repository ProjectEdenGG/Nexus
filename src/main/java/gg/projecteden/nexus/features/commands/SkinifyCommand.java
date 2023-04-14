package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import org.bukkit.OfflinePlayer;

@Aliases("skinifier")
public class SkinifyCommand extends CustomCommand {

	public SkinifyCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("[player]")
	@Description("Open a link to our skin generator")
	void skin(@Optional("self") OfflinePlayer player) {
		send(json("&eClick here &3to Project Eden-ify your skin!").url(EdenSocialMediaSite.WEBSITE.getUrl() + "/skins/?uuid=" + player.getUniqueId()));
	}
}
