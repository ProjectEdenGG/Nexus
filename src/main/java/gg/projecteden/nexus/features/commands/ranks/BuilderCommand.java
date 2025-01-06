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
public class BuilderCommand extends CustomCommand {

	public BuilderCommand(CommandEvent event) {
		super(event);
	}

	String builderApp = EdenSocialMediaSite.WEBSITE.getUrl() + "/apply/builder";

	@Path
	void builder() {
		line(5);
		send(Rank.BUILDER.getChatColor() + "Builders &3help with any build related needs for the server, such as &ewarps&3, &eminigame maps&3, and &eevents&3");
		line();
		send(json()
				.next("&3[+] &eHow to achieve&3: ")
				.next("&eApply").url(builderApp)
				.hover("&3Click to open the application on the", "&3website (&emust be " + Rank.TRUSTED.getChatColor() + "Trusted &eor above&3)")
				.group());
		send(json("&3[+] &eClick here &3for a list of builders").command("/builder list"));
		line();
		RanksCommand.ranksReturn(player());
	}

	@Async
	@Path("list")
	void list() {
		Rank.BUILDER.getNerds().thenAccept(nerds -> {
			line();
			send("&3All current " + Rank.BUILDER.getChatColor() + "Builders &3and the date they were promoted:");
			nerds.forEach(nerd ->
				send(nerd.getColoredName() + " &7-&e " + TimeUtils.shortDateFormat(nerd.getPromotionDate())));
			line();
			RanksCommand.ranksReturn(player());
		});
	}
}
