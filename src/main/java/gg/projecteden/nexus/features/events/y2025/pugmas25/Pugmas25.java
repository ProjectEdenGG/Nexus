package gg.projecteden.nexus.features.events.y2025.pugmas25;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.commands.DeathMessagesCommand;
import gg.projecteden.nexus.features.commands.staff.operator.HealCommand;
import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.models.EventFishingLoot.EventFishingLootCategory;
import gg.projecteden.nexus.features.events.y2025.pugmas25.advent.Pugmas25Advent;
import gg.projecteden.nexus.features.events.y2025.pugmas25.balloons.Pugmas25BalloonEditor;
import gg.projecteden.nexus.features.events.y2025.pugmas25.balloons.Pugmas25BalloonManager;
import gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.Pugmas25Fairgrounds;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25BoatRace;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Cabin;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Caves;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Districts;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Fishing;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Interactions;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Intro;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25ModelTrain;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Snowmen;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Sidebar;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Train;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25TrainBackground;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Waypoints;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Waypoints.WaypointTarget;
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
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteringRegionEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationInteractData;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.framework.annotations.Date;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.deathmessages.DeathMessages;
import gg.projecteden.nexus.models.deathmessages.DeathMessagesService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.pugmas25.Advent25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25Config;
import gg.projecteden.nexus.models.pugmas25.Pugmas25ConfigService;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.AdventureUtils;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.nexus.utils.Utils.EquipmentSlotGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
	"TODO: RELEASE PUGMAS" <-- CHECK FOR ANY COMMENTS ON WHOLE PROJECT
	"TODO"
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

	public static final LocalDate _25TH = LocalDate.of(2025, 12, 25);
	private LocalDateTime now;

	public static final String LORE = "&ePugmas 2025 Item";
	public final Location warp = location(-688.5, 82, -2964.5, 180, 0);
	public final Location deathLoc = location(-637.5, 66.0, -3260.5, 180, 0);

	private static int entityAgeTask;

	@Getter
	private static final boolean ridesEnabled = true;
	@Getter
	private Pugmas25Sidebar sidebar;

	public Pugmas25() {
		instance = this;
	}

	public static Pugmas25 get() {
		return instance;
	}

	@Override
	public void onStart() {
		super.onStart();

		sidebar = new Pugmas25Sidebar();

		new Pugmas25Districts();
		new Pugmas25Intro();
		new Pugmas25Caves();
		new Pugmas25Advent();
		new Pugmas25Fairgrounds();
		new Pugmas25BalloonManager();
		new Pugmas25Fishing();
		new Pugmas25QuestItemsListener();
		new Pugmas25Waystones();
		new Pugmas25Cabin();
		new Pugmas25BoatRace();
		new Pugmas25Interactions();
		new Pugmas25Snowmen();
		new Pugmas25ModelTrain();
		new Pugmas25Waypoints();

		Pugmas25Train.startup();
		Pugmas25TrainBackground.startup();
		Pugmas25ModelTrain.startup();
		Pugmas25Waypoints.startup();

		Tasks.wait(TickTime.SECOND, () -> getPlayers().forEach(this::onArrive));

		// Prevent stupid mob AI, kill them and let server respawn
		entityAgeTask = Tasks.repeat(TickTime.SECOND.x(5), TickTime.MINUTE, () -> {
			getWorld().getEntities().stream()
				.filter(entity -> entity instanceof Mob)
				.filter(entity -> entity.getEntitySpawnReason() != SpawnReason.CUSTOM)
				.forEach(entity -> {
					if (entity.getTicksLived() > TickTime.HOUR.x(4))
						entity.remove();
				});
		});
	}

	@Override
	public void onStop() {
		if (sidebar != null)
			sidebar.handleEnd();

		Pugmas25ModelTrain.shutdown();
		Tasks.cancel(entityAgeTask);
		Pugmas25Train.shutdown();
		Pugmas25TrainBackground.shutdown();
		Pugmas25BalloonEditor.shutdown();
		Pugmas25Waypoints.shutdown();

		getPlayers().forEach(this::onDepart);
	}

	public void onArrive(Player player) {
		Tasks.wait(5, () -> sidebar.handleJoin(player));
	}

	public void onDepart(Player player) {
		sidebar.handleQuit(player);
	}

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
		registerFishingLoot(EventFishingLootCategory.FISH, EventFishingLootCategory.JUNK);
	}

	@Override
	public void registerInteractHandlers() {
		handleInteract(Pugmas25NPC.BLACKSMITH, (player, npc) -> Pugmas25ShopMenu.BLACKSMITH.open(player));

		handleInteract(Pugmas25NPC.ELF, (player, npc) -> {
			/*
			ADVENT(InteractQuestTask.builder()
		.talkTo(Pugmas25NPC.ELF)
		.dialog(dialog -> dialog
			.npc("Ah! A new visitor beneath the Great Tree! Welcome to Pugmas, traveler!")
			.player("This place is huge... what's going on here?")
			.npc("Pugmas brings many wonders: the village, fairgrounds, warm springs, hidden caves, and more! But you're here for the Advent, aren't you?")
			.player("Advent? How does that work?")
			.npc("Each day until the 25th, a magical present unlocks. Find it, and you may unwrap its holiday magic!")
			.player("What if I come across a present from another day?")
			.npc("You may find any day early, but its magic won’t unlock until its rightful day arrives. The enchantments are very particular.")
			.player("And if I miss a day? Life happens.")
			.npc("The Great Tree is kind! On the 25th, every unopened present unlocks at once. A perfect chance to catch up.")
			.player("What about the final present on the 25th?")
			.npc("That one is special. You must open all the earlier days first—only then will the Great Tree reveal the final gift.")
			.player("Sounds exciting! Where do I start?")
			.npc("Your first present is already hiding somewhere out there. Go on, let the hunt begin, and may the Great Tree guide your steps!")
			.thenRun(quester -> new Pugmas25UserService().edit(quester, user -> user.advent().setUnlockedQuest(true)))
		).reminder(dialog -> dialog
			.npc("Back again? Remember, one Advent Present unlocks each day until the 25th.")
			.npc("You can find future presents early, but you can’t open them until their day.")
			.npc("Miss a day? All unopened ones unlock on the 25th, just open them to unlock the final present.")
		)
		.objective("Find and open all advent presents")
			 */
		});

		// TODO
		handleInteract(Pugmas25NPC.ANGLER, (player, npc) -> {
			final Pugmas25UserService userService = new Pugmas25UserService();
			final Pugmas25User user = userService.get(player);

			final Pugmas25ConfigService configService = new Pugmas25ConfigService();
			final Pugmas25Config config = configService.get0();

			final Dialog dialog = new Dialog(npc);
			Pugmas25QuestItem questFish = config.getAnglerQuestFish();

			if (!user.isReceivedAnglerQuestInstructions()) {
				dialog
					.npc("Catch a new fish for me every 2 hours")
					.player("Ok!")
					.npc("Current fish to catch = " + questFish.getItemBuilder().name())
					.thenRun(quester -> {
						user.setReceivedAnglerQuestInstructions(true);
						userService.save(user);
					});
			}

			if (user.isFinishedAnglerQuest()) {
				dialog.npc("Come back in x minutes for my next fish");
			} else if (Quester.of(player).has(questFish.get())) {
				dialog
					.npc("Thank you!")
					.thenRun(quester -> {
						user.setFinishedAnglerQuest(true);
						userService.save(user);

					})
					.take(questFish.get())
					.npc("Come back in x minutes for my next fish");
			} else {
				dialog.npc("Current fish to catch = " + questFish.getItemBuilder().name());
			}

			dialog.send(player);
		});
	}

	public LocalDateTime now() {
		return now == null ? LocalDateTime.now() : now;
	}

	public void now(LocalDateTime now) {
		this.now = now;
		Advent25User.refreshAllPlayers();
	}

	public boolean is25thOrAfter() {
		return is25thOrAfter(now());
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
		onDeath(player, deathType, null);
	}

	public void onDeath(Player player, @NotNull Pugmas25.Pugmas25DeathCause deathType, String defaultMessage) {
		player.teleportAsync(deathLoc);
		player.setFoodLevel(20);
		player.setHealth(20);

		String message =
			deathType == Pugmas25DeathCause.MONSTER && defaultMessage != null ?
				defaultMessage : deathType.getMessage(player);

		broadcast(message);
		fadeToBlack(player, "&c&lYou died.", 30).thenRun(() -> player.teleportAsync(getRespawnLocation(player)));
	}

	@Override
	public void onPlayerDeath(PlayerDeathEvent event) {
		event.setCancelled(true);
		Pugmas25DeathCause deathCause = Pugmas25DeathCause.from(event);

		String defaultMessage = null;
		Component deathMessageRaw = event.deathMessage();
		if (deathCause == Pugmas25DeathCause.MONSTER && deathMessageRaw instanceof TranslatableComponent deathMessage) {
			DeathMessages deathMessages = new DeathMessagesService().get(event.getPlayer());
			JsonBuilder json = new JsonBuilder(deathMessage.args(deathMessage.args().stream().map(arg -> DeathMessagesCommand.handleArgument(deathMessages, arg)).toList()));
			defaultMessage = AdventureUtils.asPlainText(json.build());
		}

		onDeath(event.getPlayer(), deathCause, defaultMessage);
	}

	@AllArgsConstructor
	public enum Pugmas25DeathCause {
		UNKNOWN("<player> died", "<player> stopped existing for unknown reasons", "<player> has left this plane of existence"),
		GEYSER("<player> was boiled alive", "<player> tried taking a lava bath", "<player> was cooked medium rare", "<player> took a steam bath… permanently"),
		INSTANT_DEATH("<player> had really bad luck", "<player> experienced instant regret", "<player> rolled a natural 1"),
		FALL("<player> forgot fall damage existed", "<player> misjudged that jump", "<player> tested gravity’s loyalty"),
		STARVATION("<player> forgot to eat", "<player> starved to death", "<player> thought hunger was optional", "<player> should’ve packed snacks", "<player> learned you can’t live on Christmas spirit alone"),
		ELYTRA("<player> needs more practice with an elytra", "<player> became one with the wall"),
		TRAIN("<player> found out what happens when unstoppable meets squishable", "<player> became train-shaped paste", "<player> got flattened by holiday cheer", "<player> mistook the tracks for a crosswalk"),
		MONSTER("<player> died fighting a monster");

		final List<String> messages;

		Pugmas25DeathCause(String... messages) {
			this.messages = new ArrayList<>(List.of(messages));
		}

		private static @NotNull Pugmas25DeathCause from(PlayerDeathEvent event) {
			EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();

			Pugmas25DeathCause deathCause = Pugmas25DeathCause.UNKNOWN;
			if (damageEvent != null) {
				deathCause = switch (damageEvent.getCause()) {
					case FALL -> Pugmas25DeathCause.FALL;
					case STARVATION -> Pugmas25DeathCause.STARVATION;
					case FLY_INTO_WALL -> Pugmas25DeathCause.ELYTRA;
					case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK, ENTITY_EXPLOSION, PROJECTILE, MAGIC ->
						Pugmas25DeathCause.MONSTER;
					default -> Pugmas25DeathCause.UNKNOWN;
				};
			}
			return deathCause;
		}

		public String getMessage(Player player) {
			String message = RandomUtils.randomElement(messages);
			return message.replaceAll("<player>", Nickname.of(player));
		}
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		if (!EquipmentSlotGroup.HANDS.applies(event)) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Block block = event.getClickedBlock();
		if (Nullables.isNullOrAir(block) || block.getType() != Material.BARRIER) return;

		var data = new DecorationInteractData(block, BlockFace.UP);
		if (!ItemModelType.NUTCRACKER_SHORT.is(data.getDecoration().getItem(event.getPlayer())))
			return;

		Pugmas25ConfigService configService = new Pugmas25ConfigService();
		Pugmas25Config config = configService.get0();
		if (!config.getNutCrackerLocations().contains(block.getLocation()))
			return;

		new Pugmas25UserService().edit(event.getPlayer(), user -> user.getFoundNutCrackers().add(block.getLocation()));
	}

}
