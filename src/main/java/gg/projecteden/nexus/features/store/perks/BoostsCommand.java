package gg.projecteden.nexus.features.store.perks;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.FormatType;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.boost.BoostConfig;
import gg.projecteden.nexus.models.boost.BoostConfig.DiscordHandler;
import gg.projecteden.nexus.models.boost.BoostConfigService;
import gg.projecteden.nexus.models.boost.Boostable;
import gg.projecteden.nexus.models.boost.Booster;
import gg.projecteden.nexus.models.boost.Booster.Boost;
import gg.projecteden.nexus.models.boost.BoosterService;
import gg.projecteden.nexus.models.costume.Costume;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

@Aliases("boost")
@NoArgsConstructor
public class BoostsCommand extends CustomCommand implements Listener {
	private final BoostConfigService configService = new BoostConfigService();
	private final BoostConfig config = configService.get0();
	private final BoosterService service = new BoosterService();
	private Booster booster;

	public BoostsCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			booster = service.get(player());
	}

	static {
		Tasks.repeatAsync(0, 5, () -> {
			BoostConfig config = BoostConfig.get();

			for (Boostable boostable : new HashSet<>(config.getBoosts().keySet())) {
				Boost boost = config.getBoost(boostable);
				if (boost.isExpired())
					boost.expire();
			}
		});
		Tasks.repeatAsync(TickTime.MINUTE, TickTime.MINUTE, () -> {
			BoostConfig config = BoostConfig.get();
			if (config.getBoosts().isEmpty())
				return;

			DiscordHandler.editMessage();
		});
	}

	@Path("[page]")
	@Description("View active server boosts")
	void list(@Arg("1") int page) {
		if (config.getBoosts().isEmpty())
			error("There are no active server boosts");

		line();
		send(PREFIX + "Active server boosts");

		Map<Boostable, LocalDateTime> timeLeft = new HashMap<>() {{
			for (Boostable boostable : config.getBoosts().keySet())
				put(boostable, config.getBoost(boostable).getExpiration());
		}};

		BiFunction<Boostable, String, JsonBuilder> formatter = (type, index) -> {
			Boost boost = config.getBoost(type);
			return json(" &6" + boost.getMultiplierFormatted() + " &e" + camelCase(type) + " &7- " + boost.getNickname() + " &3(" + boost.getTimeLeft() + ")");
		};

		paginate(Utils.sortByValueReverse(timeLeft).keySet(), formatter, "/boosts", page);
	}

	@Path("menu")
	@Description("Open the boosts menu")
	void menu() {
		if (booster.getNonExpiredBoosts().isEmpty())
			error("You do not have any boosts! Purchase them at &e" +  Costume.STORE_URL);

		new BoostMenu().open(player());
	}

	@Path("listOwners <type>")
	@Permission(Group.ADMIN)
	@Description("List players that own boosts")
	void listOwners(Boostable type) {
		for (Booster booster : new BoosterService().getAll()) {
			final List<Boost> boosts = booster.getBoosts().stream().filter(boost -> boost.getType() == type && boost.canActivateIfEnabled()).toList();
			if (!boosts.isEmpty())
				send(PREFIX + "&e" + booster.getNickname() + " &3has &e" + boosts.size() + " &3" + camelCase(type) + " boosts");
		}
	}

	@Path("give <player> <type> <multiplier> <duration> [amount]")
	@Permission(Group.ADMIN)
	@Description("Give a player a boost")
	void give(Booster booster, Boostable type, double multiplier, Timespan duration, @Arg("1") int amount) {
		for (int i = 0; i < amount; i++)
			booster.add(type, multiplier, duration.getOriginal() / 1000);
		service.save(booster);

		send(PREFIX + "Gave " + amount + " " + plural(camelCase(type) + " boost", amount) + " to " + booster.getNickname());
	}

	@Path("cancel <player> <id>")
	@Permission(Group.ADMIN)
	@Description("Cancel a player's owned boost and prevent it from being activated")
	void cancel(Booster booster, int id) {
		final Optional<Boost> boost = booster.getBoosts(_boost -> _boost.getId() == id).stream().findFirst();
		if (boost.isEmpty())
			error("Boost &e#" + id + " &cfor &e" + booster.getNickname() + " &cnot found");

		boost.get().setCancelled(true);
		service.save(booster);
		send(PREFIX + "Cancelled boost &e#" + id + " &3for &e" + booster.getNickname());
	}

	@Path("cancelType <player> <type>")
	@Permission(Group.ADMIN)
	@Description("Cancel all of a player's boosts of a certain type")
	void cancelType(Booster booster, Boostable type) {
		final List<Boost> boosts = booster.getBoosts(boost -> boost.getType() == type && boost.canActivateIfEnabled());
		final long count = boosts.size();
		if (count == 0)
			error(booster.getNickname() + " does not own any " + camelCase(type) + " boosts");

		booster.getBoosts(type).forEach(boost -> boost.setCancelled(true));
		service.save(booster);
		send(PREFIX + "Cancelled &e" + count + " &3" + camelCase(type) + " boosts for &e" + booster.getNickname());
	}

	@Confirm
	@Permission(Group.SENIOR_STAFF)
	@Path("cancel <type> [--refund]")
	@Description("Cancel an active boost and optionally refund the time left")
	void cancel(Boostable type, @Switch boolean refund) {
		if (!config.hasBoost(type))
			error("There is no active " + camelCase(type) + " boost");

		Boost boost = config.getBoost(type);
		boost.cancel();

		if (refund)
			boost.getBooster().add(type, boost.getMultiplier(), boost.getDurationLeft());

		service.save(boost.getBooster());

		send(PREFIX + "Cancelled " + boost.getNickname() + "'s " + boost.getMultiplierFormatted() + " "
				+ camelCase(type) + " boost" + (refund ? " and refunded the time left" : ""));
	}

	@Confirm
	@Permission(Group.SENIOR_STAFF)
	@Path("start <type> <multiplier> <duration>")
	@Description("Start a boost")
	void start(Boostable type, double multiplier, Timespan duration) {
		if (config.hasBoost(type))
			cancel(type, true);

		Booster booster = service.get(Dev.KODA.getUuid());
		Boost boost = booster.add(type, multiplier, duration.getOriginal() / 1000);
		boost.activate();

		service.save(booster);

		send(PREFIX + "Started a server " + boost.getMultiplierFormatted() + " " + camelCase(type)
				+ " boost for " + Timespan.ofSeconds(boost.getDuration()).format(FormatType.LONG));
	}

	@Title("Boosts")
	@AllArgsConstructor
	private static class BoostMenu extends InventoryProvider {
		private final Boostable type;
		private final BoostMenu previousMenu;

		public BoostMenu() {
			this(null);
		}

		public BoostMenu(Boostable type) {
			this.type = type;
			this.previousMenu = null;
		}

		@Override
		public void init() {
			final BoostConfigService configService = new BoostConfigService();
			final BoostConfig config = configService.get0();
			final BoosterService service = new BoosterService();
			final Booster booster = service.get(viewer);

			if (previousMenu == null)
				addCloseItem();
			else
				addBackItem(e -> previousMenu.open(viewer));

			List<ClickableItem> items = new ArrayList<>();

			if (type == null)
				for (Boostable boostable : Boostable.values()) {
					int boosts = booster.getNonExpiredBoosts(boostable).size();
					if (boosts > 0) {
						ItemBuilder item = boostable.getDisplayItem().lore("&3" + StringUtils.plural(boosts + " boost", boosts) + " available");

						if (boostable.isDisabled())
							item.lore("", "&cCannot activate, boost type is disabled");

						items.add(ClickableItem.of(item.build(), e -> new BoostMenu(boostable, this).open(viewer)));
					}
				}
			else
				for (Boost boost : booster.get(type)) {
					ItemBuilder item = boost.getDisplayItem();
					if (boost.isActive()) {
						item.lore("", "&6&lActive &7- &e" + boost.getTimeLeft());
						contents.set(0, 4, ClickableItem.empty(item.build()));
					} else if (boost.canActivate()) {
						if (config.hasBoost(boost.getType())) {
							item.lore("", "&cCannot activate, another boost is already active");
							items.add(ClickableItem.empty(item.build()));
						} else {
							item.lore("&3Duration: &e" + Timespan.ofSeconds(boost.getDuration()).format(FormatType.LONG), "", "&eClick to activate");
							items.add(ClickableItem.of(item.build(), e -> ConfirmationMenu.builder()
								.title("Activate " + StringUtils.camelCase(boost.getType()) + " Boost")
								.onConfirm(e2 -> {
									boost.activate();
									open(viewer);
								})
								.onCancel(e2 -> open(viewer))
								.open(viewer)));
						}
					} else if (boost.getType().isDisabled()) {
						item.lore("", "&cCannot activate, boost type is disabled");
						items.add(ClickableItem.empty(item.build()));
					}
				}

			paginator().items(items).build();
		}

	}

	private double get(Boostable experience) {
		return BoostConfig.multiplierOf(experience);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onMcMMOExpGain(McMMOPlayerXpGainEvent event) {
		event.setRawXpGained((float) (event.getRawXpGained() * get(Boostable.MCMMO_EXPERIENCE)));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onExpGain(PlayerPickupExperienceEvent event) {
		ExperienceOrb orb = event.getExperienceOrb();
		orb.setExperience((int) Math.round(orb.getExperience() * get(Boostable.EXPERIENCE)));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event) {
		Tasks.wait(TickTime.SECOND.x(2), () -> {
			if (!event.getPlayer().isOnline())
				return;

			Set<Boostable> boosts = BoostConfig.get().getBoosts().keySet();
			if (boosts.isEmpty())
				return;

			PlayerUtils.runCommand(event.getPlayer(), "boosts");
		});
	}

}
