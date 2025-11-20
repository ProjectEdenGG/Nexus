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
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Snowmen;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Sidebar;
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
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.framework.annotations.Date;
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
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.MathUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import kotlin.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.util.TriState;
import net.minecraft.world.waypoints.WaypointTransmitter;
import net.minecraft.world.waypoints.WaypointTransmitter.Connection;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ArmorStand.LockType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/*
	"TODO: RELEASE" <-- CHECK FOR ANY COMMENTS
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

	@Getter
	final Pugmas25UserService userService = new Pugmas25UserService();

	public static final LocalDate _25TH = LocalDate.of(2025, 12, 25);
	private LocalDateTime now;

	public static final String LORE = "&ePugmas 2025 Item";
	public final Location warp = location(-688.5, 82, -2964.5, 180, 0);
	public final Location deathLoc = location(-637.5, 66.0, -3260.5, 180, 0);

	private static int entityAgeTask;

	final Location treeCenter = location(-679.0, 115.0, -3117.0);
	private static int modelTrainCheckerTask = -1;

	@Getter
	private static boolean ridesEnabled = true;
	@Getter
	private Pugmas25Sidebar sidebar;

	public static final Map<WaypointTransmitter, List<Pair<Player, Connection>>> waypointConnections = new HashMap<>();

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

		Pugmas25Train.startup();
		Pugmas25TrainBackground.startup();

		Tasks.wait(TickTime.SECOND, () -> getPlayers().forEach(this::onArrive));

		modelTrainCheckerTask = Tasks.repeat(5, TickTime.SECOND.x(2), () -> {
			if (getOnlinePlayers().radius(treeCenter, 30).get().isEmpty()) {
				if (modelTrainStarted)
					stopModelTrain();
			} else {
				if (!modelTrainStarted)
					startModelTrain();
			}
		});

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

		// TODO: CREATE WAYPOINT CONNECTIONS OR SAVE UUID IN CONFIG?

		Tasks.repeat(5, TickTime.SECOND.x(5), () -> {
			waypointConnections.keySet().forEach(waypoint -> {
				net.minecraft.world.entity.Entity entity = (net.minecraft.world.entity.Entity) waypoint;
				if (entity.isAlive())
					return;

				var connections = waypointConnections.get(waypoint);
				connections.forEach(pair -> {
					pair.getSecond().disconnect();
				});
			});
		});

	}

	@Override
	public void onStop() {
		if (sidebar != null)
			sidebar.handleEnd();

		Tasks.cancel(modelTrainCheckerTask);
		stopModelTrain();
		Tasks.cancel(entityAgeTask);
		Pugmas25Train.shutdown();
		Pugmas25TrainBackground.shutdown();
		Pugmas25BalloonEditor.shutdown();

		getPlayers().forEach(this::onDepart);

		waypointConnections.keySet().forEach(waypoint -> {
			var connections = waypointConnections.get(waypoint);
			connections.forEach(pair -> {
				pair.getSecond().disconnect();
			});
		});
		waypointConnections.clear();
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

	@EventHandler
	public void on(VehicleEntityCollisionEvent event) {
		Entity entity = event.getVehicle();

		// Skip server event location checks since it checks UUID

		treeMinecarts.forEach(minecart -> {
			if (entity.getUniqueId().equals(minecart.getUniqueId())) {
				event.setCancelled(true);
			}
		});
	}

	//

	@Override
	protected void registerFishingLoot() {
		registerFishingLoot(EventFishingLootCategory.FISH, EventFishingLootCategory.JUNK);
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

	private static final List<ItemModelType> TREE_MINECART_MODELS = List.of(ItemModelType.PUGMAS_TRAIN_SET_ENGINE, ItemModelType.PUGMAS_TRAIN_SET_PASSENGER, ItemModelType.PUGMAS_TRAIN_SET_PASSENGER, ItemModelType.PUGMAS_TRAIN_SET_CARGO);
	private final Location treeMinecartStandSpawnLoc = location(-680.5, 115.25, -3110.5);
	private static final List<ArmorStand> treeMinecartStands = new ArrayList<>();
	private final Location treeMinecartSpawnLoc = location(-680.5, 116.25, -3111.5);
	private static final List<Minecart> treeMinecarts = new ArrayList<>();
	private static int treeMinecartStandTask = -1;
	private static final Map<UUID, Float> previousYaw = new HashMap<>();
	private static boolean modelTrainStarted = false;

	private void stopModelTrain() {
		modelTrainStarted = false;
		Tasks.cancel(treeMinecartStandTask);
		treeMinecartStands.forEach(Entity::remove);
		treeMinecarts.forEach(Entity::remove);
		treeMinecartStands.clear();
		treeMinecarts.clear();
	}

	private void startModelTrain() {
		modelTrainStarted = true;

		final int TRAIN_SIZE = TREE_MINECART_MODELS.size();
		for (int i = 0; i < TRAIN_SIZE; i++) {
			int finalI = i;
			ArmorStand armorStand = getWorld().spawn(treeMinecartStandSpawnLoc, ArmorStand.class, _stand -> {
				ItemStack cart = new ItemBuilder(TREE_MINECART_MODELS.get(finalI)).dyeColor(ColorType.PURPLE.getBukkitColor()).build();
				_stand.setHelmet(cart);
				_stand.setVisible(false);
				_stand.setInvulnerable(true);

				for (EquipmentSlot slot : EquipmentSlot.values()) {
					_stand.addEquipmentLock(slot, LockType.ADDING_OR_CHANGING);
					_stand.addEquipmentLock(slot, LockType.REMOVING_OR_CHANGING);
				}
			});
			treeMinecartStands.add(armorStand);
		}

		for (int i = 0; i < TRAIN_SIZE; i++) {
			ArmorStand armorStand = treeMinecartStands.get(i);

			Tasks.wait(i * 18L, () -> {
				Minecart minecart = getWorld().spawn(treeMinecartSpawnLoc, Minecart.class, _minecart -> {
					_minecart.setMaxSpeed(0.1);
					_minecart.setSlowWhenEmpty(false);
					_minecart.setInvulnerable(true);
					_minecart.setFrictionState(TriState.FALSE);
					_minecart.setVelocity(BlockFace.WEST.getDirection().multiply(0.1));
				});

				treeMinecarts.add(minecart);
				Tasks.wait(5L, () -> minecart.addPassenger(armorStand));
			});
		}

		treeMinecartStandTask = Tasks.repeat(TickTime.TICK.x(5), TickTime.TICK.x(2), () -> {
			if (!modelTrainStarted)
				return;

			treeMinecarts.forEach(minecart -> {
				Vector velocity = minecart.getVelocity();

				if (velocity.lengthSquared() <= 0.0001) // avoids NaN when stopped
					return;

				float globalYaw = (float) Math.toDegrees(Math.atan2(-velocity.getX(), velocity.getZ()));

				minecart.getPassengers().forEach(passenger -> {
					if (!(passenger instanceof ArmorStand armorStand))
						return;

					UUID uuid = armorStand.getUniqueId();
					float prevYaw = previousYaw.getOrDefault(uuid, globalYaw);
					float smoothedYaw = MathUtils.rotLerp(0.5f, prevYaw, globalYaw);
					float deltaYaw = smoothedYaw - prevYaw;

					// Get normalized head pose
					EulerAngle currentAngle = armorStand.getHeadPose();
					double normalizedHeadYaw = MathUtils.wrapRadians(currentAngle.getY());

					// Apply delta
					double newHeadYaw = normalizedHeadYaw + Math.toRadians(deltaYaw);

					EulerAngle newAngle = new EulerAngle(currentAngle.getX(), newHeadYaw, currentAngle.getZ());

					armorStand.setHeadPose(newAngle);
					previousYaw.put(uuid, smoothedYaw);
				});
			});
		});
	}



}
