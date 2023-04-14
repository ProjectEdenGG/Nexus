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
public class BuilderCommand extends CustomCommand {
	private static final String APPLICATION_LINK = EdenSocialMediaSite.WEBSITE.getUrl() + "/apply/builder";

	public BuilderCommand(CommandEvent event) {
		super(event);
	}

	@Override
	@NoLiterals
	@Description("Learn about the Builder rank")
	public void help() {
		line(5);
		send(Rank.BUILDER.getChatColor() + "Builders &3help with any build related needs for the server, such as &ewarps&3, &eminigame maps&3, and &eevents&3");
		line();
		send(json()
				.next("&3[+] &eHow to achieve&3: ")
				.next("&eApply").url(APPLICATION_LINK)
				.hover("&3Click to open the application on the", "&3website (&emust be " + Rank.TRUSTED.getChatColor() + "Trusted &eor above&3)")
				.group());
		send(json("&3[+] &eClick here &3for a list of builders").command("/builder list"));
		line();
		RanksCommand.ranksReturn(player());
	}

	@Async
	@Description("List current Builders")
	void list() {
		Rank.BUILDER.getNerds().thenAccept(nerds -> {
			line();
			send("&3All current " + Rank.BUILDER.getChatColor() + "Builders &3and the date they were promoted:");
			nerds.forEach(nerd ->
				send(nerd.getColoredName() + " &7-&e " + shortDateFormat(nerd.getPromotionDate())));
			line();
			RanksCommand.ranksReturn(player());
		});
	}
}
