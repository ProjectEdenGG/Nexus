package gg.projecteden.nexus.features.events.y2025.pugmas25;

import com.destroystokyo.paper.ParticleBuilder;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.FormatType;
import gg.projecteden.nexus.features.commands.DeathMessagesCommand;
import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.models.EventBreakable;
import gg.projecteden.nexus.features.events.models.EventFishingLoot.EventFishingLootCategory;
import gg.projecteden.nexus.features.events.y2025.pugmas25.advent.Pugmas25Advent;
import gg.projecteden.nexus.features.events.y2025.pugmas25.balloons.Pugmas25BalloonEditor;
import gg.projecteden.nexus.features.events.y2025.pugmas25.balloons.Pugmas25BalloonManager;
import gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.Pugmas25Fairgrounds;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25BoatRace;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Cabin;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Caves;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Districts;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Districts.Pugmas25District;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Fishing;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Fishing.Pugmas25AnglerLoot;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Interactions;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Intro;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25ModelTrain;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25SellCrate;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Sidebar;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Snowmen;
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
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationInteractData;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.framework.annotations.Date;
import gg.projecteden.nexus.models.deathmessages.DeathMessages;
import gg.projecteden.nexus.models.deathmessages.DeathMessagesService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.pugmas25.Advent25Config;
import gg.projecteden.nexus.models.pugmas25.Advent25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25Config;
import gg.projecteden.nexus.models.pugmas25.Pugmas25ConfigService;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.AdventureUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.ToolType;
import gg.projecteden.nexus.utils.ToolType.ToolGrade;
import gg.projecteden.nexus.utils.Utils.EquipmentSlotGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
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
	private final Location treeRecordPlayer = location(-683.5, 119.5, -3116.5);

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
		new Pugmas25SellCrate();

		Pugmas25Train.startup();
		Pugmas25TrainBackground.startup();
		Pugmas25ModelTrain.startup();

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

		Tasks.repeat(0, TickTime.SECOND.x(2), () -> {
			new ParticleBuilder(Particle.NOTE)
				.location(treeRecordPlayer)
				.offset(0.2, 0.2, 0.2)
				.count(RandomUtils.randomInt(2, 5))
				.spawn();
		});
	}

	@Override
	public void onStop() {
		super.onStop();

		if (sidebar != null)
			sidebar.handleEnd();

		Pugmas25ModelTrain.shutdown();
		Tasks.cancel(entityAgeTask);
		Pugmas25Train.shutdown();
		Pugmas25TrainBackground.shutdown();
		Pugmas25BalloonEditor.shutdown();
		Pugmas25Caves.shutdown();

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
		handleInteract(Pugmas25NPC.TINKERER, (player, npc) -> Pugmas25ShopMenu.TINKERER.open(player));

		handleInteract(Pugmas25NPC.AERONAUT, (player, npc) -> {
			final Pugmas25UserService userService = new Pugmas25UserService();
			final Pugmas25User user = userService.get(player);

			final Dialog dialog = new Dialog(npc);

		});

		handleInteract(Pugmas25NPC.ELF, (player, npc) -> {
			final Pugmas25UserService userService = new Pugmas25UserService();
			final Pugmas25User user = userService.get(player);

			final Dialog dialog = new Dialog(npc);
			if (user.advent().isUnlockedQuest()) {
				dialog
					.npc("Back again? Remember, one Advent Present unlocks each day until the 25th.")
					.npc("You can find future presents early, but you can’t open them until their day.")
					.npc("Miss a day? All unopened ones unlock on the 25th, just open them to unlock the final present.")
					.send(player);
				return;
			}

			dialog
				.npc("Ah! A new visitor beneath the Great Tree! Welcome to Pugmas, traveler!")
				.player("This place is huge... what's going on here?")
				.npc("Pugmas brings many wonders: the village, fairgrounds, warm springs, hidden caves, and more!")
				.npc("But you're here for the Advent, aren't you?")
				.player("Advent? How does that work?")
				.npc("Each day until the 25th, a magical present unlocks. Find it, and you may unwrap its holiday magic!")
				.player("What if I come across a present from another day?")
				.npc("You may find any day early, but its magic won’t unlock until its rightful day arrives.")
				.npc("The enchantments are very particular.")
				.player("And if I miss a day? Life happens.")
				.npc("The Great Tree is kind! On the 25th, every unopened present unlocks at once. A perfect chance to catch up.")
				.player("What about the final present on the 25th?")
				.npc("That one is special. You must open all the earlier days first—only then will the Great Tree reveal the final gift.")
				.player("Sounds exciting! Where do I start?")
				.npc("Your first present is already hiding somewhere out there.")
				.npc("And take this, it'll help you on your search!")
				.give(Pugmas25QuestItem.ADVENTURE_POCKET_GUIDE)
				.thenRun(quester -> {
					user.advent().setUnlockedQuest(true);
					userService.save(user);
					quester.sendMessage("&7&o[You can now find advent presents]");
				})
				.send(player);
		});

		// TODO: TEST
		handleInteract(Pugmas25NPC.ANGLER, (player, npc) -> {
			final Pugmas25UserService userService = new Pugmas25UserService();
			final Pugmas25User user = userService.get(player);

			final Pugmas25ConfigService configService = new Pugmas25ConfigService();
			final Pugmas25Config config = configService.get0();

			final Dialog dialog = new Dialog(npc);
			Pugmas25AnglerLoot questFish = config.getAnglerQuestFish();
			Pugmas25AnglerLoot receivedQuestFish = user.getReceivedAnglerQuestFish();

			if (!user.isReceivedAnglerQuestInstructions()) {
				// INTRO
				dialog
					.npc("Hey there! Would you like to help to collect some rare for the holiday celebrations?")
					.npc("Every 2 hours, a new fish becomes available to catch.")
					.npc("Each one can only be caught in a specific area, and sometimes only at a certain time of day.")
					.player("So I just catch the fish you want?")
					.npc("Exactly! Catch the fish, bring it back to me, and I'll reward you!")
					.npc("Once time runs out, the fish changes, so make sure to come see me again for the next one!")
					.npc("Take this to get started.")
					.give(Pugmas25QuestItem.FISHING_ROD_WOOD)
					.thenRun(quester -> {
						user.setReceivedAnglerQuestInstructions(true);
						userService.save(user);
						if (interactHandlers.containsKey(npc))
							interactHandlers.get(npc).accept(player, npc); // Force re-talk to npc to get hint & set variables
					})
					.send(player);
				return;
			}

			// DIFFERENT FISH --> THIS MAY NEVER RUN
			if (!user.isCompletedAnglerQuest() && receivedQuestFish != null && questFish != receivedQuestFish) {
				dialog
					.npc("Uh-oh! Time’s up!")
					.npc("The old fish swam off, but a NEW one is ready to be caught!")
					.thenRun(quester -> {
						user.setReceivedAnglerQuestFish(null);
						userService.save(user);
						if (interactHandlers.containsKey(npc))
							interactHandlers.get(npc).accept(player, npc); // Force re-talk to npc to get hint & set variables
					})
					.send(player);
				return;
			}

			LocalDateTime resetDateTime = config.getAnglerQuestResetDateTime();
			var timeUntilReset = TimeUtils.Timespan.of(now(), resetDateTime).format(FormatType.LONG);

			if (user.isCompletedAnglerQuest()) {
				// ALREADY COMPLETED
				dialog.npc("Come back in " + timeUntilReset + " for my next fish request");
			} else if (Quester.of(player).has(questFish.getItem())) {
				// REWARD
				dialog
					.player("I caught the fish you wanted!")
					.take(questFish.getItem());
				Pugmas25Fishing.getAnglerReaction(dialog, questFish);
				dialog
					.thenRun(quester -> {
						user.incrementCompletedAnglerQuests();
						user.setCompletedAnglerQuest(true);
						userService.save(user);
						Pugmas25Fishing.giveRewards(dialog, user, quester);
					});

				dialog
					.player("Happy to help!")
					.npc("Come back in " + timeUntilReset + " for my next fish")
				;
			} else if (questFish == receivedQuestFish) {
				// REMINDER
				dialog
					.npc("Hey! Did you catch my special fish yet?")
					.npc("You still have " + timeUntilReset + " to catch it.")
					.npc("Remember: " + config.getAnglerQuestFishHint())
					.player("I'll keep trying!")
					.thenRun(quester -> {
						quester.sendMessage(new JsonBuilder(Dialog.getNPCPrefix(npc) + "If you need any extra help ").group()
							.next("[hover here]")
							.hover(questFish.getExtraHelp())
						);
					});
			} else {
				// RECEIVED QUEST
				dialog
					.player("What rare fish do you want right now?")
					.npc(config.getAnglerQuestFishHint())
					.thenRun(quester -> {
						user.setReceivedAnglerQuestFish(questFish);
						userService.save(user);
					});
			}

			dialog.send(player);
		});
	}

	@Override
	protected void registerBreakableBlocks() {
		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE)
			.drops(Material.COAL, 1, 3)
			.drops(Pugmas25QuestItem.SUSPICIOUS_DEBRIS, 1, 2)
			.placeholderTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.STONE)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE)
			.drops(Material.RAW_IRON, 1, 2)
			.drops(Pugmas25QuestItem.SUSPICIOUS_DEBRIS, 1, 2)
			.placeholderTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.STONE)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE)
			.drops(Material.RAW_COPPER, 1, 3)
			.drops(Pugmas25QuestItem.SUSPICIOUS_DEBRIS, 1, 2)
			.placeholderTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.IRON)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE)
			.drops(Material.RAW_GOLD, 1, 2)
			.drops(Pugmas25QuestItem.SUSPICIOUS_DEBRIS, 1, 2)
			.placeholderTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.IRON)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE)
			.drops(Material.LAPIS_LAZULI, 2, 5)
			.drops(Pugmas25QuestItem.SUSPICIOUS_DEBRIS, 1, 2)
			.placeholderTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.IRON)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE)
			.drops(Material.DIAMOND, 1, 2)
			.drops(Pugmas25QuestItem.SUSPICIOUS_DEBRIS, 1, 2)
			.placeholderTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.IRON)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE)
			.drops(Material.EMERALD)
			.drops(Pugmas25QuestItem.SUSPICIOUS_DEBRIS, 1, 2)
			.placeholderTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.DIAMOND)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.WHEAT)
			.drops(Material.WHEAT, 1, 3)
			.sound(Sound.BLOCK_CROP_BREAK)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.CARROTS)
			.drops(Material.CARROT, 1, 3)
			.sound(Sound.BLOCK_CROP_BREAK)
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

	public static int getPlayerWorldHeight(Player player) {
		int height = player.getLocation().getBlockY();
		if (Pugmas25Districts.of(player) == Pugmas25District.CAVES)
			height -= 100;

		return height;
	}

	public static int getLuckyAmount(int min, int max, int luck) {
		// Scale luck: every 10 luck increases the min by 1
		int shift = luck / 10;

		int effectiveMin = Math.min(max, min + shift);

		// Roll between effectiveMin and max
		return effectiveMin + (int) (Math.random() * (max - effectiveMin + 1));
	}

	public enum Pugmas25QuestProgress {
		NUTCRACKERS {
			@Override
			String getProgress(Pugmas25User user) {
				int numCollected = user.getFoundNutCrackers().size();
				if (numCollected == 0)
					return "&3 " + getName() + " &7- &cNot started";

				int numTotal = Pugmas25Config.get().getNutCrackerLocations().size();
				if (numCollected == numTotal)
					return "&3 " + getName() + " &7- &aCompleted";

				return "&3 " + getName() + " &7- &eStarted (" + numCollected + "/" + numTotal + " nutcrackers)";
			}
		},

		ADVENT {
			@Override
			String getProgress(Pugmas25User user) {
				var adventUser = user.advent();
				if (!adventUser.isUnlockedQuest())
					return "&3 " + getName() + " &7- &cNot started";

				int numCollected = adventUser.getCollected().size();
				int numTotal = Advent25Config.get().getDays().size();
				if (numCollected == numTotal)
					return "&3 " + getName() + " &7- &aCompleted";

				return "&3 " + getName() + " &7- &eStarted (" + numCollected + "/" + numTotal + " presents)";
			}
		},
		ANGLER {
			@Override
			String getProgress(Pugmas25User user) {
				if (!user.isReceivedAnglerQuestInstructions())
					return "&3 " + getName() + " &7- &cNot started";

				Pugmas25Config config = new Pugmas25ConfigService().get0();
				var timeUntilReset = TimeUtils.Timespan.of(Pugmas25.get().now(), config.getAnglerQuestResetDateTime()).format(FormatType.LONG);

				if (user.isCompletedAnglerQuest())
					return "&3 " + getName() + " &7- &aCompleted (resets in " + timeUntilReset + ")";

				return "&3 " + getName() + " &7- &eStarted (Talk to the Angler for more info)";
			}
		},
		DESIGN_A_BALLOON {
			@Override
			String getProgress(Pugmas25User user) {
				if (!user.isReceivedAeronautInstructions())
					return "&3 " + getName() + " &7- &cNot started";

				if (user.isBalloonSchemExists())
					return "&3 " + getName() + " &7- &aCompleted";

				return "&3 " + getName() + " &7- &eStarted (Save your balloon)";
			}
		}
		;

		abstract String getProgress(Pugmas25User user);

		public String getName() {
			return StringUtils.camelCase(this);
		}

		public void send(Pugmas25User user) {
			String progress = getProgress(user);
			if (progress == null)
				return;

			user.sendMessage(progress);
		}
	}

	// Death

	@Override
	public Location getRespawnLocation(Player player) {
		return new Pugmas25UserService().get(player).getSpawnLocation();
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

		if (!isAtEvent(block))
			return;

		var data = new DecorationInteractData(block, BlockFace.UP);
		if (data.getDecoration() == null)
			return;

		if (!ItemModelType.NUTCRACKER_SHORT.is(data.getDecoration().getItem(event.getPlayer())))
			return;

		Pugmas25ConfigService configService = new Pugmas25ConfigService();
		Pugmas25Config config = configService.get0();
		if (!config.getNutCrackerLocations().contains(block.getLocation()))
			return;

		new Pugmas25UserService().edit(event.getPlayer(), user -> user.getFoundNutCrackers().add(block.getLocation()));
	}

}
