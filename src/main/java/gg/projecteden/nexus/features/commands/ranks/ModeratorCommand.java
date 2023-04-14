package gg.projecteden.nexus.features.commands.ranks;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.nerd.Rank;

import static gg.projecteden.api.common.utils.TimeUtils.shortDateFormat;

@HideFromWiki
public class ModeratorCommand extends CustomCommand {
	private static final String APPLICATION_LINK = EdenSocialMediaSite.WEBSITE.getUrl() + "/apply/mod";

	public ModeratorCommand(CommandEvent event) {
		super(event);
	}

	@Override
	@NoLiterals
	@Description("Learn about the Moderator rank")
	public void help() {
		line(5);
		send(Rank.MODERATOR.getChatColor() + "Moderators &3are the first level of staff. They &eanswer any questions &3a player has, &efix grief&3, moderate chat, " +
				"and see too any other basic problems players have.");
		line();
		send(json()
				.next("&3[+] &eHow to achieve&3: ")
				.next("&eApply").url(APPLICATION_LINK)
				.hover("&3Click to open the application on the", "&3website (&emust be " + Rank.ELITE.getChatColor() + "Elite &eor above&3)")
				.group());
		send(json("&3[+] &eClick here &3for a list of moderators").command("/moderator list"));
		line();
		RanksCommand.ranksReturn(player());
	}

	@Async
	@Description("List current Moderators")
	void list() {
		Rank.MODERATOR.getNerds().thenAccept(nerds -> {
			line();
			send("&3All current " + Rank.MODERATOR.getChatColor() + "Moderators &3and the date they were promoted:");
			nerds.forEach(nerd -> send(nerd.getColoredName() + " &7-&e " + shortDateFormat(nerd.getPromotionDate())));
			line();
			RanksCommand.ranksReturn(player());
		});
	}
}
