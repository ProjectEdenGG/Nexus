package gg.projecteden.nexus.features.events.store;

import gg.projecteden.api.common.exceptions.EdenException;
import gg.projecteden.api.common.utils.Utils;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.store.models.EventStoreImage;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.eventuser.EventUser;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.mail.Mailer.Mail;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import me.arcaniax.hdb.api.PlayerClickHeadEvent;
import org.bukkit.GameMode;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collections;
import java.util.List;

public class EventStoreListener implements Listener {

	public EventStoreListener() {
		Nexus.registerListener(this);
	}

	private void handleException(Player player, Exception ex) {
		if (ex instanceof EdenException) {
			PlayerUtils.send(player, EdenEvent.PREFIX_STORE + "&c" + ex.getMessage());
		} else {
			ex.printStackTrace();
			PlayerUtils.send(player, EdenEvent.PREFIX_STORE + "An unknown error occurred");
		}
	}

	private static final List<WorldGroup> HDB_ALLOWED = List.of(WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK);
	private static final List<WorldGroup> HDB_BYPASS = List.of(WorldGroup.CREATIVE, WorldGroup.STAFF);

	@EventHandler
	public void onPlayerClickHeadDatabase(PlayerClickHeadEvent event) {
		final Player player = event.getPlayer();
		try {
			final WorldGroup worldGroup = WorldGroup.of(player);
			final Rank rank = Rank.of(player);

			int price = EventStoreItem.DECORATION_HEADS.getPrice();

			if (rank.isSeniorStaff())
				return;

			if (rank.isStaff() && player.getGameMode() == GameMode.CREATIVE)
				return;

			if (HDB_BYPASS.contains(worldGroup))
				return;

			if (!HDB_ALLOWED.contains(worldGroup))
				throw new InvalidInputException("You can not purchase heads in this world");

			new EventUserService().edit(player, user -> user.charge(price));

			event.setCancelled(true);
			PlayerUtils.giveItemsAndMailExcess(player, Collections.singletonList(event.getHead()), WorldGroup.of(player));
			player.closeInventory();
		} catch (Exception ex) {
			event.setCancelled(true);
			handleException(player, ex);
		}
	}

	@EventHandler
	public void onClickSign(PlayerInteractEvent event) {
		final Player player = event.getPlayer();

		try {
			if (!player.getWorld().getName().equalsIgnoreCase("server"))
				return;
			if (Nullables.isNullOrAir(event.getClickedBlock()))
				return;
			if (!MaterialTag.SIGNS.isTagged(event.getClickedBlock().getType()))
				return;
			if (!(event.getClickedBlock().getState() instanceof Sign sign))
				return;

			final String prefix = StringUtils.stripColor(sign.getLine(0));
			if (!prefix.equalsIgnoreCase("[Purchase]"))
				return;

			final String title = (sign.getLine(2).trim() + " " + sign.getLine(3).trim()).trim();
			if (Nullables.isNullOrEmpty(title))
				return;

			final EventStoreImage image = EventStoreImage.of(title);
			if (image == null)
				return;

			final String priceString = StringUtils.stripColor(sign.getLine(1)).split(" ")[0];
			if (!Utils.isInt(priceString))
				return;

			if (true) throw new InvalidInputException("Custom Paintings are temporarily disabled");

			final int price = Integer.parseInt(priceString);

			final EventUserService service = new EventUserService();
			final EventUser user = service.get(player);

			user.checkHasTokens(price);

			MenuUtils.ConfirmationMenu.builder()
				.title("&4&lAre you sure?")
				.confirmText("&aBuy")
				.confirmLore(List.of(
					"&3Painting: &e" + title,
					"&3Price: &e" + StringUtils.stripColor(sign.getLine(1)),
					"&3Balance: &e" + user.getTokens() + " Event Tokens"
				))
				.cancelText("&cCancel")
				.onConfirm(e -> {
					try {
						user.charge(price);
						service.save(user);
						Mail.fromServer(player.getUniqueId(), WorldGroup.SURVIVAL, image.getSplatterMap()).send();
						PlayerUtils.send(player, EdenEvent.PREFIX_STORE + "Your image has been mailed to you in the Survival world");
					} catch (Exception ex) {
						handleException(player, ex);
					}
				})
				.open(player);
		} catch (Exception ex) {
			handleException(player, ex);
		}
	}

}
