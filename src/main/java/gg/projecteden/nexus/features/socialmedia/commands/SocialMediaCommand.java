package gg.projecteden.nexus.features.socialmedia.commands;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.BookBuilder.WrittenBookMenu;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.SocialMediaSite;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUser;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUser.Connection;
import gg.projecteden.nexus.models.socialmedia.SocialMediaUserService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@NoArgsConstructor
public class SocialMediaCommand extends CustomCommand implements Listener {
	private static final SocialMediaUserService service = new SocialMediaUserService();

	public SocialMediaCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Description("View links to all of our social media sites")
	void run() {
		line();
		for (EdenSocialMediaSite site : EdenSocialMediaSite.values())
			send(json().next(site.getName() + " &7- &e" + site.getUrl()));
	}

	@Path("getNamedItem <site>")
	@Permission(Group.ADMIN)
	@Description("Spawn a social media site's custom item")
	void getNamedItem(SocialMediaSite site) {
		PlayerUtils.giveItem(player(), site.getNamedItem());
	}

	@NoLiterals
	@Path("[player]")
	@Description("View a player's linked social media accounts")
	void menu(@Optional("self") Nerd target) {
		open(player(), target.getOfflinePlayer(), null);
	}

	public static void open(@NotNull Player viewer, OfflinePlayer target, @Nullable String backCommand) {
		SocialMediaUser user = service.get(target);

		if (user.getConnections().isEmpty()) {
			PlayerUtils.send(viewer, "&c" + ((PlayerUtils.isSelf(viewer, user) ? "You have" : user.getNickname() + " has") + " not linked any social media accounts"));
			return;
		}

		final JsonBuilder page = new JsonBuilder("&3&lSocial Media").newline().newline().group();

		for (SocialMediaSite site : SocialMediaSite.values()) {
			final Connection connection = user.getConnection(site);
			if (connection == null)
				continue;

			page.next("&f" + site.getEmoji() + " " + site.getLabel())
				.hover(connection.getUrl());

			if ("%s".equals(site.getProfileUrl()))
				page.copy(connection.getUrl())
					.hover("&f")
					.hover("&eClick to copy");
			else
				page.url(connection.getUrl())
					.hover("&f")
					.hover("&eClick to open");

			page.group().newline();
		}

		if (backCommand != null)
			page.newline().next("&c&l<&c&m &m &c &lBack").hover("&cClick to go back").command(backCommand);

		new WrittenBookMenu().addPage(page).open(viewer);
	}

	@Path("link <site> <username> [player]")
	@Description("Link your social media account")
	void link(SocialMediaSite site, String username, @Optional("self") @Permission(Group.SENIOR_STAFF) SocialMediaUser player) {
		username = username.replaceAll("(http(s)?://)?(www.)?" + site.getProfileUrl().replace("https://", "").replace("%s", ""), "");
		player.addConnection(site, username);
		service.save(player);
		send(PREFIX + "Linked to &e" + player.getConnection(site).getUrl());
	}

	@Path("unlink <site> [player]")
	@Description("Unlink your social media account")
	void unlink(SocialMediaSite site, @Optional("self") @Permission(Group.STAFF) SocialMediaUser player) {
		player.removeConnection(site);
		service.save(player);
		send(PREFIX + "Unlinked from &e" + camelCase(site));
	}

	@TabCompleteIgnore
	@Path("mature [player]")
	@Description("Mark social media accounts as 18+ only")
	void mature(@Optional("self") @Permission(Group.STAFF) SocialMediaUser player) {
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

	@EventHandler
	public void on(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if (!ActionGroup.CLICK_BLOCK.applies(event))
			return;

		if (event.getHand() != EquipmentSlot.HAND)
			return;

		final Block block = event.getClickedBlock();
		if (isNullOrAir(block) || block.getType() != Material.PLAYER_HEAD)
			return;

		final String id = Nexus.getHeadAPI().getBlockID(block);
		if (isNullOrEmpty(id))
			return;

		EdenSocialMediaSite site = EdenSocialMediaSite.ofHeadId(id);
		if (site == null)
			return;

		PlayerUtils.send(player, new JsonBuilder("&e" + site.getUrl()).url(site.getUrl()));
	}

}
