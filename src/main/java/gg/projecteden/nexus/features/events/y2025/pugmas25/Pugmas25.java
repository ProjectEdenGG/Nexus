package gg.projecteden.nexus.features.events.y2025.pugmas25;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.FormatType;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.EventSounds;
import gg.projecteden.nexus.features.events.models.EventBreakable;
import gg.projecteden.nexus.features.events.models.EventFishingLoot.EventFishingLootCategory;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25BoatRace;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25Cabin;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25Caves;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25Death;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25Districts;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25Fishing;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25GiftGiver;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25Interactions;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25Intro;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25Nutcrackers;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25Particles;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25SellCrate;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25Snowmen;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25Trunks;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25Waystones;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.advent.Pugmas25Advent;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.balloons.Pugmas25BalloonEditor;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.balloons.Pugmas25BalloonEditorMenu;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.balloons.Pugmas25BalloonEditorUtils;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.balloons.Pugmas25BalloonManager;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.fairgrounds.Pugmas25Fairgrounds;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.trains.Pugmas25ModelTrain;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.trains.Pugmas25Train;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.trains.Pugmas25TrainBackground;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25AnglerLoot;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25District;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25QuestProgress;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25QuestProgress.Pugmas25QuestStatus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Sidebar;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25Entity;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25NPC;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25Quest;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestItem;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestItemsListener;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestReward;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestTask;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25ShopMenu;
import gg.projecteden.nexus.features.minigolf.models.GolfBallStyle;
import gg.projecteden.nexus.features.quests.QuestConfig;
import gg.projecteden.nexus.features.quests.interactable.instructions.Dialog;
import gg.projecteden.nexus.framework.annotations.Date;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.minigolf.MiniGolfConfigService;
import gg.projecteden.nexus.models.minigolf.MiniGolfUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.pugmas25.Advent25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25Config;
import gg.projecteden.nexus.models.pugmas25.Pugmas25ConfigService;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.models.quests.QuesterService;
import gg.projecteden.nexus.models.trophy.TrophyHolderService;
import gg.projecteden.nexus.models.trophy.TrophyType;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.ToolType;
import gg.projecteden.nexus.utils.ToolType.ToolGrade;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static gg.projecteden.api.common.utils.StringUtils.plural;

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
		new Pugmas25Trunks();
		new Pugmas25GiftGiver();
		new Pugmas25Nutcrackers();
		new Pugmas25Particles();

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
		autoStartQuests(event.getPlayer());

		if (!shouldHandle(event.getPlayer()))
			return;

		onArrive(event.getPlayer());
	}

	private static void autoStartQuests(Player player) {
		var quester = new QuesterService().get(player);
		for (Pugmas25Quest pugmas25Quest : Pugmas25Quest.values())
			pugmas25Quest.assign(quester);
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
		autoStartQuests(player);

		if (fromWorld.equals(toWorld))
			return;

		if (Pugmas25.get().getWorld().getName().equalsIgnoreCase(fromWorld.getName())) {
			onDepart(player);
			return;
		}

		if (Pugmas25.get().getWorld().getName().equalsIgnoreCase(toWorld.getName())) {
			if (!new Pugmas25UserService().get(player).isVisited()) {
				WarpType.PUGMAS25.get("hub").teleportAsync(player);
				PlayerUtils.send(player, Pugmas25.PREFIX + "&cYou must visit the board the train first to visit Pugmas");
				return;
			}
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
		handleInteract(Pugmas25NPC.ADVENTURER, (player, npc) -> Pugmas25ShopMenu.ADVENTURER.open(player));
		handleInteract(Pugmas25NPC.ARTIST, (player, npc) -> Pugmas25ShopMenu.ARTIST.open(player));
		handleInteract(Pugmas25NPC.BOAT_SALESMAN, (player, npc) -> Pugmas25ShopMenu.BOAT_SALESMAN.open(player));

		Pugmas25UserService pugmas25UserService = new Pugmas25UserService();
		MiniGolfUserService miniGolfUserService = new MiniGolfUserService();
		handleInteract(Pugmas25NPC.POWER, (player, npc) -> {
			var pugmas25User = pugmas25UserService.get(player);
			var miniGolfUser = miniGolfUserService.get(player);
			var course = new MiniGolfConfigService().get0().getCourse("pugmas25");

			if (pugmas25User.isStartedMiniGolf()) {
				if (miniGolfUser.hasAllHolesInOne(course)) {
					if (miniGolfUser.getAvailableStyles(course).contains(GolfBallStyle.RAINBOW)) {
						// oops
						if (!new TrophyHolderService().get(player).getEarned().contains(TrophyType.PUGMAS_2025_MINIGOLF)) {
							Pugmas25QuestReward.TROPHY_MINIGOLF.getConsumer().accept(player.getUniqueId(), 1);
						}

						Dialog.genericGreeting(Quester.of(player), npc);
					} else {
						new Dialog(npc)
							.npc("Wow! You've completed all " + course.getHoles().size() + " holes in one!")
							.npc("You're a pro! Here's your Rainbow Golf Ball!")
							.thenRun($ -> {
								miniGolfUser.unlockStyle(course, GolfBallStyle.RAINBOW);
								miniGolfUser.setStyle(course, GolfBallStyle.RAINBOW);
								miniGolfUser.giveGolfBall();
								miniGolfUserService.save(miniGolfUser);
							})
							.reward(Pugmas25QuestReward.TROPHY_MINIGOLF)
							.send(player);
					}
				} else {
					int missing = course.getHoles().size() - miniGolfUser.getHolesInOne(course);
					new Dialog(npc)
						.npc("Hey there, how's it going? Hope you're enjoying the course!")
						.npc("It looks like you're still missing " + missing + " " + plural("hole", missing) + " in one")
						.npc("Check your scorecard and keep trying!")
						.send(player);
				}
			} else {
				pugmas25User.setStartedMiniGolf(true);
				pugmas25UserService.save(pugmas25User);

				new Dialog(npc)
					.npc("Welcome to MiniGolf!")
					.npc("Here are your clubs, ball, and scorecard, as well as a whistle to recall your ball")
					.thenRun($ -> miniGolfUser.giveKit())
					.npc("There are 18 holes, and if you can score a Hole In One at least once on all of them, you will unlock a Rainbow Golf Ball!")
					.npc("Good luck and have fun!")
					.player("Thank you!")
					.send(player);
			}

		});

		handleInteract(Pugmas25NPC.AERONAUT, (player, npc) -> {
			var user = pugmas25UserService.get(player);

			final Dialog dialog = new Dialog(npc);
			if (!user.isReceivedAeronautInstructions()) {
				// INTRO
				dialog
					.npc("Ahoy there, traveler! I'm the Aeronaut, master of the winter skies!")
					.npc("See those hot air balloons drifting above the village? They’re all decorated by visitors like you.")
					.npc("If you'd like your own up there, you're in luck.")
					.player("How do I get started?")
					.npc("First you'll pick one of 5 available templates.")
					.npc("Then you'll be able to fly, and paint your balloon however you wish!")
					.npc("When you're satisfied with your work, talk to me again and I'll help you save your masterpiece.")
					.npc("Once saved, your balloon will have a chance to spawn above the village like the rest.")
					.player("Sounds fun! Let’s do it!")
					.thenRun(quester -> {
						// PRE-EXISTING BALLOONS
						if (Pugmas25BalloonEditor.hasSchematic(user.getUniqueId()))
							user.setBalloonSchemExists(true);

						user.setReceivedAeronautInstructions(true);
						pugmas25UserService.save(user);

						if (interactHandlers.containsKey(npc))
							interactHandlers.get(npc).accept(player, npc); // Force re-talk to npc
					})
					.send(player);
				return;
			}

			if (Pugmas25BalloonEditor.isBeingUsed()) {
				if (!Pugmas25BalloonEditorUtils.isEditing(player)) {
					dialog.npc("Looks like someone is currently decorating their balloon, come back when they're finished.").send(player);
					return;
				}
			} else {
				if (Nexus.isMaintenanceQueued()) {
					dialog.npc("Server maintenance is queued, try again later").send(player);
					return;
				}

				if (!PlayerUtils.hasRoomFor(player, Pugmas25QuestItem.BALLOON_PAINTBRUSH.get())) {
					dialog.npc("You'll need to make some room in your inventory to decorate a balloon.").send(player);
					return;
				}

				Pugmas25BalloonEditor.editBalloon(Nerd.of(player));
			}

			new Pugmas25BalloonEditorMenu().open(player);
		});

		handleInteract(Pugmas25NPC.ELF, (player, npc) -> {
			var user = pugmas25UserService.get(player);

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
					pugmas25UserService.save(user);
					quester.sendMessage("&7&o[You can now find advent presents]");
				})
				.send(player);
		});

		handleInteract(Pugmas25NPC.FARMER, (player, npc) -> {
			var user = pugmas25UserService.get(player);

			final Dialog dialog = new Dialog(npc);
			if (user.isReceivedFarmerInstructions()) {
				dialog
					.npc("Still need crops? Go right ahead, you’ve got permission to harvest anything out in the field.")
					.npc("Anything that doesn’t match gets returned. Nothing lost!")
					.send(player);
				return;
			}

			dialog
				.npc("Howdy there! Busy season, ain’t it? I’ve already harvested everything I need for the village, but I’ve still got some extra crops left in the fields.")
				.npc("Figure it'd be a shame lettin’ them go to waste. So how ‘bout this, you can harvest any of the leftovers!")
				.npc("I’ve gone ahead and given you permission to break my crops. Anything you pick is yours to keep.")
				.player("Seriously? Thanks!")
				.npc("Yep! And if you don't need the crops, you can sell ‘em right here in the sell crate next to me.")
				.npc("Just toss in hay bales and carrots and the crate will check what it can trade for.")
				.npc("If you put in the wrong item or amount the crate won’t make a deal and’ll just give everything back.")
				.npc("Now go on! Harvest away, and happy Pugmas farmin’!")
				.thenRun(quester -> {
					user.setReceivedFarmerInstructions(true);
					pugmas25UserService.save(user);
					quester.sendMessage("&7&o[You can now harvest crops]");
				})
				.send(player);

		});

		handleInteract(Pugmas25NPC.SAFETY_INSTRUCTOR, (player, npc) -> {
			new Dialog(npc)
				.npc("Welcome to Reflection! I’m the Safety Instructor in charge of keeping fingers, faces, and festive cheer intact.")
				.npc("Your goal is simple: fire the laser, reflect it off the mirrors, and hit the target block on the wall.")
				.player("Got it.")
				.npc("Each puzzle may require a minimum number of reflections. If so, you’ll see that displayed before you start.")
				.npc("You can rotate the mirrors using the buttons beneath them. Feel free to adjust them during or between shots.")
				.npc("If the laser hits the back of a mirror, or any block that isn’t the target, the beam fizzles out. Harmless... but dramatically disappointing.")
				.npc("Only the player who fires the laser is credited for completing the puzzle. So make sure it’s your shot!")
				.npc("Solve the puzzle by directing the beam into the target block, and you’ll move on to the next puzzle.")
				.send(player);
		});

		handleInteract(Pugmas25NPC.ANGLER, (player, npc) -> {
			var user = pugmas25UserService.get(player);

			final Pugmas25ConfigService configService = new Pugmas25ConfigService();
			final Pugmas25Config config = configService.get0();

			final Dialog dialog = new Dialog(npc);
			Pugmas25AnglerLoot questFish = config.getAnglerQuestFish();
			Pugmas25AnglerLoot receivedQuestFish = user.getReceivedAnglerQuestFish();

			if (!user.isReceivedAnglerQuestInstructions()) {
				// INTRO
				dialog
					.npc("Hey there! Would you like to help to collect some rare fish for the holiday celebrations?")
					.npc("Every 2 hours, a new fish becomes available to catch.")
					.npc("Each one can only be caught in a specific area, and sometimes only at a certain time of day.")
					.player("So I just catch the fish you want?")
					.npc("Exactly! Catch the fish, bring it back to me, and I'll reward you!")
					.npc("Once time runs out, the fish changes, so make sure to come see me again for the next one!")
					.npc("Take this to get started.")
					.give(Pugmas25QuestItem.FISHING_ROD_WOOD)
					.thenRun(quester -> {
						user.setReceivedAnglerQuestInstructions(true);
						pugmas25UserService.save(user);
						if (interactHandlers.containsKey(npc))
							interactHandlers.get(npc).accept(player, npc); // Force re-talk to npc to get hint & set variables
					})
					.send(player);
				return;
			}

			// DIFFERENT FISH
			if (!user.isCompletedAnglerQuest() && receivedQuestFish != null && questFish != receivedQuestFish) {
				dialog
					.npc("Uh-oh! Time’s up!")
					.npc("The old fish swam off, but a NEW one is ready to be caught!")
					.thenRun(quester -> {
						user.setReceivedAnglerQuestFish(null);
						pugmas25UserService.save(user);
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
						pugmas25UserService.save(user);
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
						pugmas25UserService.save(user);
					});
			}

			dialog.send(player);
		});

		handleInteract(Pugmas25NPC.MAYOR, (player, npc) -> {
			boolean incomplete = false;

			var quester = new QuesterService().get(player);
			for (var pugmas25Quest : Pugmas25Quest.values()) {
				var quest = quester.getQuest(pugmas25Quest);
				if (quest == null || !quest.isComplete()) {
					incomplete = true;
					break;
				}
			}

			for (var fakeQuest : Pugmas25QuestProgress.values()) {
				if (fakeQuest.isRepeatable())
					continue;

				var status = fakeQuest.getStatus(new Pugmas25UserService().get(player));
				if (status != Pugmas25QuestStatus.COMPLETED) {
					incomplete = true;
					break;
				}
			}

			if (incomplete) {
				new Dialog(npc)
					.npc("Welcome to Pugmas Village!")
					.npc("There's still so much to do to get ready for the holidays!")
					.npc("Can you help us out? I would really appreciate it!")
					.player("Sure thing! What can I do for you?")
					.npc("Check the quest board for a list of tasks to complete.")
					.npc("Come talk to me again when you've completed them all!")
					.send(player);
			} else {
				if (new TrophyHolderService().get(player).hasEarned(TrophyType.PUGMAS_2025_ANNIVERSARY_CAKE))
					Dialog.genericGreeting(quester, npc);
				else
					new Dialog(npc)
						.npc("Thanks for helping out the village! We are finally ready for the holidays, thank to you!")
						.npc("Here, I think I have an extra cake you can have, as a token of my appreciation")
						.give(Pugmas25QuestItem.SLOT_MACHINE_TOKEN.get())
						.reward(Pugmas25QuestReward.TROPHY_ANNIVERSARY_CAKE)
						.player("Thank you!")
						.send(player);
			}
		});
	}

	@Override
	protected void registerBreakableBlocks() {
		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE)
			.drops(Material.COAL, 1, 3)
			.drops(Pugmas25QuestItem.SUSPICIOUS_DEBRIS, 0, 1)
			.placeholderTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.STONE)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE)
			.drops(Material.RAW_IRON, 1, 2)
			.drops(Pugmas25QuestItem.SUSPICIOUS_DEBRIS, 0, 1)
			.placeholderTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.STONE)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE)
			.drops(Material.RAW_COPPER, 1, 3)
			.drops(Pugmas25QuestItem.SUSPICIOUS_DEBRIS, 0, 1)
			.placeholderTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.IRON)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE)
			.drops(Material.RAW_GOLD, 1, 2)
			.drops(Pugmas25QuestItem.SUSPICIOUS_DEBRIS, 0, 1)
			.placeholderTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.IRON)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE)
			.drops(Material.LAPIS_LAZULI, 2, 5)
			.drops(Pugmas25QuestItem.SUSPICIOUS_DEBRIS, 0, 1)
			.placeholderTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.IRON)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE)
			.drops(Material.DIAMOND, 1, 2)
			.drops(Pugmas25QuestItem.SUSPICIOUS_DEBRIS, 0, 1)
			.placeholderTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.IRON)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE)
			.drops(Material.EMERALD)
			.drops(Pugmas25QuestItem.SUSPICIOUS_DEBRIS, 0, 1)
			.placeholderTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.DIAMOND)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.WHEAT)
			.drops(Material.WHEAT, 1, 3)
			.regenerationDelay(1, 10)
			.sound(Sound.BLOCK_CROP_BREAK)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.CARROTS)
			.drops(Material.CARROT, 1, 3)
			.requiredTool(ToolType.HOE, ToolGrade.IRON)
			.regenerationDelay(1, 10)
			.sound(Sound.BLOCK_CROP_BREAK)
		);
	}

	@Override
	public boolean breakBlock(BlockBreakEvent event) {
		var player = event.getPlayer();

		try {
			var userService = new Pugmas25UserService();
			var user = userService.get(player);
			var blockType = event.getBlock().getType();
			if (!user.isReceivedFarmerInstructions())
				if (blockType == Material.CARROTS || blockType == Material.WHEAT)
					throw new BreakException("event_break_pugmas25_quest_farmer", "The Farmer has not given you permission to break this yet");
		} catch (BreakException ex) {
			if (CooldownService.isNotOnCooldown(player, ex.getCooldownId(), TickTime.MINUTE)) {
				errorMessage(player, ex.getErrorMessage());
				EventSounds.VILLAGER_NO.play(player);
			}
			return true;
		}

		return super.breakBlock(event);
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

	public static int getLuckyHorseshoeAmount(Player player, int min, int max) {
		if (!Pugmas25QuestItem.LUCKY_HORSESHOE.isInInventoryOf(player))
			return min;

		return getLuckyAmount(min, max, RandomUtils.randomInt(5, 30));
	}

	public static int getLuckyAmount(int min, int max, int luck) {
		// Scale luck: every 10 luck increases the min by 1
		int shift = luck / 10;

		int effectiveMin = Math.min(max, min + shift);

		// Roll between effectiveMin and max
		return effectiveMin + (int) (Math.random() * (max - effectiveMin + 1));
	}

	// Death

	@Override
	public Location getRespawnLocation(Player player) {
		return new Pugmas25UserService().get(player).getSpawnLocation();
	}

	@Override
	public void onPlayerDeath(PlayerDeathEvent event) {
		Pugmas25Death.onPlayerDeath(event);
	}

}

