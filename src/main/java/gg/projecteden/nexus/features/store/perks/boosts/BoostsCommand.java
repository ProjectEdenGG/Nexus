package gg.projecteden.nexus.features.store.perks.boosts;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.FormatType;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.votes.party.VoteParty;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.boost.BoostConfig;
import gg.projecteden.nexus.models.boost.BoostConfig.DiscordHandler;
import gg.projecteden.nexus.models.boost.BoostConfigService;
import gg.projecteden.nexus.models.boost.Boostable;
import gg.projecteden.nexus.models.boost.Booster;
import gg.projecteden.nexus.models.boost.Booster.Boost;
import gg.projecteden.nexus.models.boost.BoosterService;
import gg.projecteden.nexus.models.costume.Costume;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
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
@WikiConfig(rank = "Store", feature = "Boosts")
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

			for (Boost boost : new ArrayList<>(config.getPersonalBoosts())) {
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
		if (VoteParty.isFeatureEnabled(player()))
			send(PREFIX + "Active Boosts:");
		if (config.getBoosts().isEmpty())
			send("&c - There are no active server boosts");
		else {
			line();
			send("Active Global Boosts:");

			Map<Boostable, LocalDateTime> timeLeft = new HashMap<>() {{
				for (Boostable boostable : config.getBoosts().keySet())
					put(boostable, config.getBoost(boostable).getExpiration());
			}};

			BiFunction<Boostable, String, JsonBuilder> formatter = (type, index) -> {
				Boost boost = config.getBoost(type);
				return json(" &6" + boost.getMultiplierFormatted() + " &e" + camelCase(type) + " &7- " + boost.getNickname() + " &3(" + boost.getTimeLeft() + ")");
			};

			paginate(Utils.sortByValueReverse(timeLeft).keySet(), formatter, "/boosts", Math.min(page, MathUtils.roundPositive(timeLeft.size() / 10f)));
		}

		if (VoteParty.isFeatureEnabled(player())) {
			if (booster.getActivePersonalBoosts().isEmpty()) {
				line();
				send("&c - You have no active personal boosts");
			} else {
				line();
				send("Active Personal Boosts:");

				Map<Boostable, LocalDateTime> timeLeft = new HashMap<>() {{
					booster.getActivePersonalBoosts().forEach(boost -> {
						put(boost.getType(), boost.getExpiration());
					});
				}};

				BiFunction<Boostable, String, JsonBuilder> formatter = (type, index) -> {
					Boost boost = booster.getActivePersonalBoosts().stream().filter(_boost -> _boost.getType() == type).findFirst().orElse(null);
					return json(" &6" + boost.getMultiplierFormatted() + " &e" + camelCase(boost.getType()) + " &7- " + boost.getNickname() + " &3(" + boost.getTimeLeft() + ")");
				};

				paginate(Utils.sortByValueReverse(timeLeft).keySet(), formatter, "/boosts", Math.min(page, MathUtils.roundPositive(timeLeft.size() / 10f)));
			}
		}

		line();
		send(json("&e&lClick here to view your boosts").command("boosts menu"));
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

	@Path("give <player> <type> <multiplier> <duration> [amount] [--personal]")
	@Permission(Group.ADMIN)
	@Description("Give a player a boost")
	void give(Booster booster, Boostable type, double multiplier, Timespan duration, @Arg("1") int amount, @Switch boolean personal) {
		for (int i = 0; i < amount; i++)
			booster.add(type, multiplier, duration.getOriginal() / 1000, personal);
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

	@AllArgsConstructor
	private static class BoostMenu extends InventoryProvider {
		private final Boostable type;
		private final BoostMenu previousMenu;
		private final Boolean personal;

		public BoostMenu() {
			this(null);
		}

		public BoostMenu(Boolean peronsal) {
			this(null, peronsal);
		}

		public BoostMenu(Boostable type, Boolean peronsal) {
			this(type, null, peronsal);
		}

		@Override
		public String getTitle() {
			if (!VoteParty.isFeatureEnabled(viewer))
				return "&8Boosts";
			if (personal == null)
				return FontUtils.getMenuTexture("禧", 3) + "&8Boosts";
			return "&8" + (personal ? "Personal" : "Global") + " Boosts";
		}

		@Override
		protected int getRows(Integer page) {
			if (!VoteParty.isFeatureEnabled(viewer))
				return 6;
			return personal == null ? 3 : 6;
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

			if (personal == null && VoteParty.isFeatureEnabled(viewer)) {

				contents.set(1, 2, ClickableItem.of(
					new ItemBuilder(CustomMaterial.GUI_BOOSTS_GLOBAL).name("&eGlobal Boosts")
						.lore("&3Available: &e" + (int) booster.getNonExpiredBoosts().stream().filter(boost -> !boost.isPersonal()).count())
						.build(),
					e -> new BoostMenu(null, this, false).open(viewer)
				));

				contents.set(1, 6, ClickableItem.of(
					new ItemBuilder(CustomMaterial.GUI_BOOSTS_PERSONAL).name("&ePersonal Boosts")
						.lore("&3Available: &e" + (int) booster.getNonExpiredBoosts().stream().filter(boost -> boost.isPersonal()).count())
						.build(),
					e -> new BoostMenu(null, this, true).open(viewer)
				));

				return;
			}

			List<ClickableItem> items = new ArrayList<>();

			if (type == null)
				for (Boostable boostable : Boostable.values()) {
					int boosts = (int) booster.getNonExpiredBoosts(boostable).stream().filter(boost -> boost.isPersonal() == (personal != null && personal)).count();
					if (boosts > 0) {
						ItemBuilder item = boostable.getDisplayItem().lore("&3" + StringUtils.plural(boosts + " boost", boosts) + " available");

						if (boostable.isDisabled())
							item.lore("", "&cCannot activate, boost type is disabled");

						items.add(ClickableItem.of(item.build(), e -> new BoostMenu(boostable, this, personal).open(viewer)));
					}
				}
			else
				for (Boost boost : booster.get(type).stream().filter(boost -> boost.isPersonal() == (personal != null && personal)).toList()) {
					ItemBuilder item = boost.getDisplayItem();
					if (boost.isActive()) {
						item.lore("", "&6&lActive &7- &e" + boost.getTimeLeft());
						contents.set(0, 4, ClickableItem.empty(item.build()));
					} else if (boost.canActivate()) {
						if ((!boost.isPersonal() && config.hasBoost(boost.getType()) || (boost.isPersonal() && booster.getActivePersonalBoosts().stream().anyMatch(_boost -> _boost.getType() == boost.getType())))) {
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

			paginate(items);
		}

	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onMcMMOExpGain(McMMOPlayerXpGainEvent event) {
		event.setRawXpGained((float) (event.getRawXpGained() * Booster.getTotalBoost(event.getPlayer(), Boostable.MCMMO_EXPERIENCE)));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onExpGain(PlayerPickupExperienceEvent event) {
		ExperienceOrb orb = event.getExperienceOrb();
		orb.setExperience((int) Math.round(orb.getExperience() * Booster.getTotalBoost(event.getPlayer(), Boostable.EXPERIENCE)));
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

