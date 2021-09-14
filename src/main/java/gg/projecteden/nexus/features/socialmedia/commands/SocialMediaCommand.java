package gg.projecteden.nexus.features.socialmedia.commands;

import gg.projecteden.nexus.features.menus.BookBuilder.WrittenBookMenu;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.socialmedia.SocialMedia;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.SocialMediaSite;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUser;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUser.Connection;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUserService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NonNull;

public class SocialMediaCommand extends CustomCommand {
	private final SocialMediaUserService service = new SocialMediaUserService();

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

	@Path("[player]")
	void menu(@Arg("self") SocialMediaUser user) {
		if (user.getConnections().isEmpty())
			error((isSelf(user) ? "You have" : user.getNickname() + " has") + " not linked any social media accounts");

		final JsonBuilder page = new JsonBuilder("&3&lSocial Media").newline().newline();

		for (SocialMediaSite site : SocialMediaSite.values()) {
			final Connection connection = user.getConnection(site);
			if (connection == null)
				continue;

			page.next("&f" + site.getEmoji() + " " + site.getLabel())
				.hover(connection.getUrl());

			if (site.getProfileUrl().equals("%s"))
				page.copy(connection.getUrl())
					.hover("&f")
					.hover("&eClick to copy");
			else
				page.url(connection.getUrl())
					.hover("&f")
					.hover("&eClick to open");

			page.group().newline();
		}

		new WrittenBookMenu().addPage(page).open(player());
	}

	@Path("link <site> <username> [player]")
	void link(SocialMediaSite site, String username, @Arg(value = "self", permission = "group.staff") SocialMediaUser player) {
		username = username
			.replace(site.getProfileUrl().replaceFirst("%s", ""), "")
			.replace(site.getUrl(), "")
			.replace(site.getUrl().replace("https://", "https://www."), "");

		if (site == SocialMediaSite.YOUTUBE && (username.length() != 24 || !username.startsWith("UC")))
			error("You must provide your 24 character YouTube channel id");

		player.addConnection(site, username);
		service.save(player);
		send(PREFIX + "Linked to &e" + player.getConnection(site).getUrl());
	}

	@Path("unlink <site> [player]")
	void unlink(SocialMediaSite site, @Arg(value = "self", permission = "group.staff") SocialMediaUser player) {
		player.removeConnection(site);
		service.save(player);
		send(PREFIX + "Unlinked from &e" + camelCase(site));
	}

	@TabCompleteIgnore
	@Path("mature [player]")
	@Description("Mark social media accounts as 18+ only")
	void mature(@Arg(value = "self", permission = "group.staff") SocialMediaUser player) {
		if (player.isMature() && !isStaff())
			error("Only staff can remove 18+ status");

		ConfirmationMenu.builder()
			.title("&4" + (player.isMature() ? "Unmark" : "Mark") + " accounts as 18+ only?")
			.onConfirm(e -> {
				player.setMature(!player.isMature());
				service.save(player);
				send(PREFIX + "&e" + (player.isMature() ? "Marked" : "Unmarked") + " &3" +
					(isSelf(player) ? "your" : player.getNickname() + "'s") + " social media accounts as &c18+ only");
			})
			.open(player());
	}

}
