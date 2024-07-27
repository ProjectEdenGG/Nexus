package gg.projecteden.nexus.features.events;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.features.events.store.EventStoreListener;
import gg.projecteden.nexus.features.events.store.models.EventStoreImage;
import gg.projecteden.nexus.features.events.store.providers.EventStoreProvider;
import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.eventuser.EventUser;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.features.events.Events.STORE_PREFIX;
import static gg.projecteden.nexus.features.events.store.models.EventStoreImage.IMAGES;
import static gg.projecteden.nexus.utils.Nullables.isNotNullOrAir;

@Aliases("event")
public class EventsCommand extends CustomCommand {
	private final EventUserService service = new EventUserService();
	private EventUser user;

	public EventsCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	// Store

	static {
		new EventStoreListener();
	}

	@Override
	public String getPrefix() {
		if ("store".equalsIgnoreCase(arg(1)))
			return STORE_PREFIX;
		return super.getPrefix();
	}

	@Path("store")
	@Description("Open the event store")
	void store() {
		new EventStoreProvider().open(player());
	}

	// Tokens

	private String plural(int tokens) {
		return tokens + plural(" token", tokens);
	}

	@Path("tokens [player]")
	@Description("View your or another player's event token balance")
	void tokens(@Arg("self") EventUser user) {
		if (isSelf(user)) {
			send(PREFIX + "&3Current balance: &e" + plural(user.getTokens()));
			line();
			send("&3Event tokens are currency earned by participating in server events");
			send("&3You can spend them at the &c/event store &3in exchange for unique rewards");
		} else
			send(PREFIX + "&3" + user.getNickname() + "'s current balance: &e" + plural(user.getTokens()));
	}

	@Async
	@Path("tokens top [page]")
	@Description("View the event token leaderboard")
	void tokens_top(@Arg("1") int page) {
		send(PREFIX + "Top Token Earners");
		paginate(service.getTopTokens(), (user, index) -> json(index + " &e" + user.getNickname() + " &7- " + user.getTokens()), "/event tokens top", page);
	}

	/* TODO
	@Path("tokens daily [player]")
	void tokensDaily(@Arg("self") EventUser user) {
		if (isSelf(user))
			send(PREFIX + "&3Daily tokens:");
		else
			send(PREFIX + "&3" + user.getOfflinePlayer().getName() + "'s Daily tokens:");

		for (BFtokensource tokensource : BFtokensource.values()) {
			Map<LocalDate, Integer> dailyMap = user.getTokensReceivedToday().get(tokensource);
			int tokens = 0;
			if (dailyMap != null)
				tokens = dailyMap.getOrDefault(LocalDate.now(), 0);

			int dailyMax = EventUser.DAILY_SOURCE_MAX;
			String sourceColor = tokens == dailyMax ? "&a" : "&3";
			String sourceName = StringUtils.camelCase(tokensource.name());
			send(" " + sourceColor + sourceName + " &7- &e" + tokens + "&3/&e" + dailyMax);
		}
	}
	*/

	@Disabled // TODO 1.19 Re-enable eventually
	@Path("tokens pay <player> <tokens>")
	@Description("Send event tokens to another player")
	void tokens_pay(EventUser toUser, int tokens) {
		EventUser fromUser = service.get(player());
		if (isSelf(toUser))
			error("You cannot pay yourself");

		fromUser.takeTokens(tokens);
		toUser.giveTokens(tokens);

		fromUser.sendMessage(PREFIX + "&e" + tokens + " event tokens &3have been sent to &e" + toUser.getNickname());
		toUser.sendMessage(PREFIX + "&e" + tokens + " event tokens &3have been received from &e" + fromUser.getNickname());

		service.save(fromUser);
		service.save(toUser);
	}

	@Path("tokens give <player> <tokens>")
	@Permission(Group.ADMIN)
	@Description("Modify a player's event token balance")
	void tokens_give(EventUser user, int tokens) {
		user.giveTokens(tokens);
		service.save(user);
		send(PREFIX + "&e" + plural(tokens) + " &3given to &e" + user.getNickname());
	}

	@Path("tokens take <player> <tokens>")
	@Permission(Group.ADMIN)
	@Description("Modify a player's event token balance")
	void tokens_take(EventUser user, int tokens) {
		user.takeTokens(tokens);
		service.save(user);
		send(PREFIX + "&e" + plural(tokens) + " &3taken from &e" + user.getNickname());
	}

	@Path("tokens set <player> <tokens>")
	@Permission(Group.ADMIN)
	@Description("Modify a player's event token balance")
	void tokens_set(EventUser user, int tokens) {
		user.setTokens(tokens);
		service.save(user);
		send(PREFIX + "&3Set &e" + user.getNickname() + "&3's balance to &e" + plural(tokens));
	}

	@Path("tokens reset <player>")
	@Permission(Group.ADMIN)
	@Description("Reset a player's event token balance")
	void tokens_reset(EventUser user) {
		user.setTokens(0);
		user.getTokensReceivedByDate().clear();
		service.save(user);
	}

	// Images

	static {
		EventStoreImage.reload();
	}

	@Path("store images reload")
	@Permission(Group.ADMIN)
	@Description("Reload event store images")
	void store_images_reload() {
		EventStoreImage.reload();
		send(STORE_PREFIX + "Loaded " + IMAGES.size() + " maps");
	}

	@Path("store images get <image...>")
	@Description("Receive an event store image splattermap")
	@Permission(Group.ADMIN)
	void store_images_get(EventStoreImage image) {
		PlayerUtils.giveItem(player(), image.getSplatterMap());
	}

	// Rewards

	private static final List<WorldGroup> CAN_CLAIM_REWARDS_IN = List.of(
		WorldGroup.SURVIVAL,
		WorldGroup.CREATIVE,
		WorldGroup.SKYBLOCK
	);

	@Path("rewards claim")
	@Description("Claim reward items from events")
	@Permission(Group.ADMIN)
	void rewards_claim() {
		if (!CAN_CLAIM_REWARDS_IN.contains(worldGroup()))
			error("You cannot claim event item rewards in this world");

		new EventItemRewardsMenu(player());
	}

	@Title("Event Item Rewards")
	public static class EventItemRewardsMenu implements TemporaryMenuListener {
		@Getter
		private final Player player;

		public EventItemRewardsMenu(Player player) {
			this.player = player;

			EventUserService service = new EventUserService();
			EventUser user = service.get(player);

			if (user.getRewardItems().isEmpty())
				throw new InvalidInputException("No reward items available to claim");

			List<ItemStack> items = new ArrayList<>();
			final int max = Math.min(54, user.getRewardItems().size());
			final Iterator<ItemStack> iterator = user.getRewardItems().iterator();
			while (items.size() < max && iterator.hasNext()) {
				items.add(iterator.next());
				iterator.remove();
			}

			service.save(user);

			open(items);
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			EventUserService service = new EventUserService();
			EventUser user = service.get(player);

			for (ItemStack content : event.getInventory().getContents())
				if (isNotNullOrAir(content))
					user.addRewardItem(content);

			service.save(user);
		}
	}

	@ConverterFor(EventStoreImage.class)
	EventStoreImage convertToEventStoreImage(String value) {
		return EventStoreImage.of(value);
	}

	@TabCompleterFor(EventStoreImage.class)
	List<String> tabCompleteEventStoreImage(String filter) {
		return IMAGES.keySet().stream()
			.filter(id -> id.toLowerCase().startsWith(filter.toLowerCase()))
			.collect(Collectors.toList());
	}

}
