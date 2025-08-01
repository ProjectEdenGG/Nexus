package gg.projecteden.nexus.features.events.y2025.pugmas25;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.features.commands.staff.HealCommand;
import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.models.EventFishingLoot;
import gg.projecteden.nexus.features.events.y2025.pugmas25.advent.Pugmas25Advent;
import gg.projecteden.nexus.features.events.y2025.pugmas25.balloons.Pugmas25BalloonEditor;
import gg.projecteden.nexus.features.events.y2025.pugmas25.balloons.Pugmas25BalloonManager;
import gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.Pugmas25Fairgrounds;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25BoatRace;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Cabin;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Districts;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Fishing;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Interactions;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Intro;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Train;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25TrainBackground;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Waystones;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25Entity;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25NPC;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25Quest;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestItem;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestItemsListener;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestReward;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestTask;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25ShopMenu;
import gg.projecteden.nexus.features.quests.QuestConfig;
import gg.projecteden.nexus.features.quests.interactable.instructions.Dialog;
import gg.projecteden.nexus.framework.annotations.Date;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.pugmas25.Advent25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

/*
	TODO:
		FIND THE NUT CRACKERS

	"TODO: RELEASE" <-- CHECK FOR ANY COMMENTS
 */
@QuestConfig(
	quests = Pugmas25Quest.class,
	tasks = Pugmas25QuestTask.class,
	npcs = Pugmas25NPC.class,
	entities = Pugmas25Entity.class,
	items = Pugmas25QuestItem.class,
	rewards = Pugmas25QuestReward.class,
	effects = Pugmas25Effects.class,
	start = @Date(m = 12, d = 1, y = 2025),
	end = @Date(m = 1, d = 10, y = 2026),
	world = "pugmas25",
	region = "pugmas25",
	warpType = WarpType.PUGMAS25
)
@Environments({Env.PROD, Env.UPDATE})
public class Pugmas25 extends EdenEvent {
	private static Pugmas25 instance;
	public static final String PREFIX = StringUtils.getPrefix("Pugmas 2025");
	public static final String EVENT_NAME = "Pugmas Village";

	@Getter
	final Pugmas25UserService userService = new Pugmas25UserService();

	public static final LocalDate _25TH = LocalDate.of(2025, 12, 25);
	private LocalDateTime now;

	public static final String LORE = "&ePugmas 2025 Item";
	public final Location warp = location(-688.5, 82, -2964.5, 180, 0);
	public final Location deathLoc = location(-637.5, 66.0, -3260.5, 180, 0);

	@Getter
	private static boolean ridesEnabled = true;

	public Pugmas25() {
		instance = this;
	}

	public static Pugmas25 get() {
		return instance;
	}

	@Override
	public void onStart() {
		super.onStart();

		new Pugmas25Districts();
		new Pugmas25Advent();
		new Pugmas25Fairgrounds();
		new Pugmas25BalloonManager();
		new Pugmas25Fishing();
		new Pugmas25QuestItemsListener();
		new Pugmas25Waystones();
		new Pugmas25Cabin();
		new Pugmas25BoatRace();
		new Pugmas25Interactions();

		Pugmas25Train.startup();
		Pugmas25TrainBackground.startup();

		getPlayers().forEach(this::onArrive);
	}

	@Override
	public void onStop() {
		Pugmas25Train.shutdown();
		Pugmas25TrainBackground.shutdown();
		Pugmas25BalloonEditor.shutdown();

		getPlayers().forEach(this::onDepart);
	}

	public void onArrive(Player player) {
		Tasks.wait(1, () -> {
			Pugmas25User user = userService.get(player);
			user.updateHealth();
		});
	}

	public void onDepart(Player player) {}

	@EventHandler
	public void on(PlayerJoinEvent event) {
		if (!shouldHandle(event.getPlayer()))
			return;

		onArrive(event.getPlayer());
	}

	@EventHandler
	public void on(PlayerQuitEvent event) {
		if (!shouldHandle(event.getPlayer()))
			return;

		onDepart(event.getPlayer());
	}

	@EventHandler
	public void on(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		World fromWorld = event.getFrom();
		World toWorld = player.getWorld();

		if (fromWorld.equals(toWorld))
			return;

		if (Pugmas25.get().getWorld().getName().equalsIgnoreCase(fromWorld.getName())) {
			onDepart(player);
			return;
		}

		if (shouldHandle(player)) {
			onArrive(player);
		}
	}

	//

	@Override
	protected void registerFishingLoot() {
		registerFishingLoot(EventFishingLoot.EventFishingLootCategory.FISH, EventFishingLoot.EventFishingLootCategory.JUNK);
	}

	@Override
	public void registerInteractHandlers() {
		handleInteract(Pugmas25NPC.BLACKSMITH, (player, npc) -> Pugmas25ShopMenu.BLACKSMITH.open(player));

		handleInteract(Pugmas25NPC.TICKET_MASTER_HUB, (player, npc) -> {
			if (PlayerUtils.playerHas(player, Pugmas25Intro.getTicketItem())) {
					new Dialog(npc)
						.npc("You already bought a ticket, make sure to board the train!")
						.send(player);
					return;
				}

				new Dialog(npc)
					.npc("Hello! Where would you like to travel to?")
					.player("1 Ticket to " + EVENT_NAME + ", please.")
					.npc("Oh, it's wonderful there this time of year. Here you go.")
					.give(Pugmas25Intro.getTicketItem())
					.send(player);
			}
		);
	}

	public LocalDateTime now() {
		return now == null ? LocalDateTime.now() : now;
	}

	public void now(LocalDateTime now) {
		this.now = now;
		Advent25User.refreshAllPlayers();
	}

	public boolean is25thOrAfter() {
		return is25thOrAfter(now);
	}

	public boolean is25thOrAfter(LocalDateTime date) {
		return date.isAfter(_25TH.atStartOfDay().plusSeconds(-1));
	}

	@Override
	public OnlinePlayers getOnlinePlayers() {
		return super.getOnlinePlayers().filter(player -> {
			for (ProtectedRegion region : worldguard().getRegionsAt(player.getLocation())) {
				if (region.getId().startsWith("pugmas25_transition_"))
					return false;
			}
			return true;
		});
	}

	// Health

	@Deprecated
	public void resetHealth(Player player) {
		setMaxHealthAttribute(player, 20.0);
	}

	@Deprecated
	private void setMaxHealthAttribute(Player player, double amount) {
		HealCommand.getMaxHealthAttribute(player).setBaseValue(amount);
		player.setHealth(amount);
	}

	// Death

	@Override
	public Location getRespawnLocation(Player player) {
		return warp; // TODO: GET PLAYER CABIN, IF SET
	}

	public void onDeath(Player player, @NotNull Pugmas25.Pugmas25DeathCause deathType) {
		player.teleportAsync(deathLoc);
		broadcast(deathType.getMessage(player));

		fadeToBlack(player, "&cYou died.", 30).thenRun(() -> player.teleportAsync(getRespawnLocation(player)));
	}

	@Override
	public void onPlayerDeath(PlayerDeathEvent event) {
		event.setCancelled(true);
		EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();

		Pugmas25DeathCause deathCause = Pugmas25DeathCause.UNKNOWN;
		if (damageEvent != null) {
			deathCause = switch (damageEvent.getCause()) {
				case FALL -> Pugmas25DeathCause.FALL;
				case STARVATION -> Pugmas25DeathCause.STARVATION;
				case FLY_INTO_WALL -> Pugmas25DeathCause.ELYTRA;
				default -> Pugmas25DeathCause.UNKNOWN;
			};
		}

		onDeath(event.getPlayer(), deathCause);
	}

	@AllArgsConstructor
	public enum Pugmas25DeathCause {
		UNKNOWN("<player> died"),
		GEYSER("<player> was boiled alive"),
		INSTANT_DEATH("<player> had really bad luck"),
		FALL("<player> forgot fall damage existed"),
		STARVATION("<player> forgot to eat"),
		ELYTRA("<player> needs more practice with an elytra");

		final String message;

		public String getMessage(Player player) {
			return message.replaceAll("<player>", Nickname.of(player));
		}
	}



}
