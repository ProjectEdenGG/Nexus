package gg.projecteden.nexus.features.commands.ranks;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;

@HideFromWiki
public class ModeratorCommand extends CustomCommand {

	public ModeratorCommand(CommandEvent event) {
		super(event);
	}

	String modApp = EdenSocialMediaSite.WEBSITE.getUrl() + "/apply/mod";

	@Path
	void moderator() {
		line(5);
		send(Rank.MODERATOR.getChatColor() + "Moderators &3are the first level of staff. They &eanswer any questions &3a player has, &efix grief&3, moderate chat, " +
				"and see too any other basic problems players have.");
		line();
		send(json()
				.next("&3[+] &eHow to achieve&3: ")
				.next("&eApply").url(modApp)
				.hover("&3Click to open the application on the", "&3website (&emust be " + Rank.ELITE.getChatColor() + "Elite &eor above&3)")
				.group());
		send(json("&3[+] &eClick here &3for a list of moderators").command("/moderator list"));
		line();
		RanksCommand.ranksReturn(player());
	}

	@Async
	@Path("list")
	void list() {
		Rank.MODERATOR.getNerds().thenAccept(nerds -> {
			line();
			send("&3All current " + Rank.MODERATOR.getChatColor() + "Moderators &3and the date they were promoted:");
			nerds.forEach(nerd -> send(nerd.getColoredName() + " &7-&e " + TimeUtils.shortDateFormat(nerd.getPromotionDate())));
			line();
			RanksCommand.ranksReturn(player());
		});
	}
}
