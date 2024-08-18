package gg.projecteden.nexus.features.events.y2024.pugmas24;

import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.features.commands.staff.HealCommand;
import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.y2024.pugmas24.advent.Pugmas24Advent;
import gg.projecteden.nexus.features.events.y2024.pugmas24.balloons.Pugmas24BalloonEditor;
import gg.projecteden.nexus.features.events.y2024.pugmas24.balloons.Pugmas24BalloonManager;
import gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds.Pugmas24Fairgrounds;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.Pugmas24Districts;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.Pugmas24Fishing;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.Pugmas24Train;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.Pugmas24Waystones;
import gg.projecteden.nexus.features.events.y2024.pugmas24.quests.Pugmas24Entity;
import gg.projecteden.nexus.features.events.y2024.pugmas24.quests.Pugmas24NPC;
import gg.projecteden.nexus.features.events.y2024.pugmas24.quests.Pugmas24Quest;
import gg.projecteden.nexus.features.events.y2024.pugmas24.quests.Pugmas24QuestItem;
import gg.projecteden.nexus.features.events.y2024.pugmas24.quests.Pugmas24QuestItemsListener;
import gg.projecteden.nexus.features.events.y2024.pugmas24.quests.Pugmas24QuestReward;
import gg.projecteden.nexus.features.events.y2024.pugmas24.quests.Pugmas24QuestTask;
import gg.projecteden.nexus.features.events.y2024.pugmas24.quests.Pugmas24ShopMenu;
import gg.projecteden.nexus.features.quests.QuestConfig;
import gg.projecteden.nexus.framework.annotations.Date;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.pugmas24.Advent24Config;
import gg.projecteden.nexus.models.pugmas24.Advent24Present;
import gg.projecteden.nexus.models.pugmas24.Pugmas24User;
import gg.projecteden.nexus.models.pugmas24.Pugmas24UserService;
import gg.projecteden.nexus.models.warps.WarpType;
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
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

import static gg.projecteden.nexus.features.events.models.EventFishingLoot.EventFishingLootCategory.FISH;
import static gg.projecteden.nexus.features.events.models.EventFishingLoot.EventFishingLootCategory.JUNK;

/*
	TODO:
		FIND THE NUT CRACKERS
 */
@QuestConfig(
	quests = Pugmas24Quest.class,
	tasks = Pugmas24QuestTask.class,
	npcs = Pugmas24NPC.class,
	entities = Pugmas24Entity.class,
	items = Pugmas24QuestItem.class,
	rewards = Pugmas24QuestReward.class,
	effects = Pugmas24Effects.class,
	start = @Date(m = 12, d = 1, y = 2024),
	end = @Date(m = 1, d = 10, y = 2025),
	world = "pugmas24",
	region = "pugmas24",
	warpType = WarpType.PUGMAS24
)
@Environments(Env.PROD)
public class Pugmas24 extends EdenEvent {
	private static Pugmas24 instance;
	public static final String PREFIX = StringUtils.getPrefix("Pugmas 2024");

	@Getter
	final Pugmas24UserService userService = new Pugmas24UserService();

	public static final LocalDate _25TH = LocalDate.of(2024, 12, 25);

	public static final String LORE = "&ePugmas 2024 Item";
	public final Location warp = location(-688.5, 82, -2964.5, 180, 0);
	public final Location deathLoc = location(-637.5, 66.0, -3260.5, 180, 0);

	@Getter
	private static boolean ridesEnabled = true;

	public Pugmas24() {
		instance = this;
	}

	public static Pugmas24 get() {
		return instance;
	}

	@Override
	public void onStart() {
		super.onStart();

		new Pugmas24Districts();
		new Pugmas24Advent();
		new Pugmas24Fairgrounds();
		new Pugmas24BalloonManager();
		new Pugmas24Fishing();
		new Pugmas24QuestItemsListener();
		new Pugmas24Waystones();

		Pugmas24Train.startup();

		getPlayers().forEach(this::onArrive);
	}

	@Override
	public void onStop() {
		Pugmas24Train.shutdown();
		Pugmas24BalloonEditor.shutdown();

		getPlayers().forEach(this::onDepart);
	}

	public void onArrive(Player player) {
		Tasks.wait(1, () -> {
			Pugmas24User user = userService.get(player);
			user.updateHealth();

			Pugmas24Advent.sendPackets(player);
		});
	}

	public void onDepart(Player player) {
		resetHealth(player);

		final Pugmas24User user = userService.get(player);
		for (Advent24Present present : Advent24Config.get().getPresents())
			user.advent().hide(present);
	}

	@EventHandler
	public void on(PlayerLoginEvent event) {
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

		if (Pugmas24.get().getWorld().getName().equalsIgnoreCase(fromWorld.getName())) {
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
		registerFishingLoot(FISH, JUNK);
	}

	@Override
	public void registerInteractHandlers() {
		handleInteract(Pugmas24NPC.BLACKSMITH, (player, npc) -> Pugmas24ShopMenu.BLACKSMITH.open(player));
	}

	public boolean is25thOrAfter() {
		return is25thOrAfter(LocalDate.now());
	}

	public boolean is25thOrAfter(LocalDate date) {
		return date.isAfter(_25TH.plusDays(-1));
	}

	// Health

	public void addMaxHealth(Player player, double amount) {
		setMaxHealthAttribute(player, HealCommand.getMaxHealth(player) + amount, true);
	}

	public void subtractMaxHealth(Player player, double amount) {
		setMaxHealthAttribute(player, Math.max(HealCommand.getMaxHealth(player) - amount, 1.0), true);
	}

	public void setMaxHealth(Player player, double amount) {
		setMaxHealthAttribute(player, amount, true);
	}

	public void resetHealth(Player player) {
		setMaxHealthAttribute(player, 20.0, false);
	}

	private void setMaxHealthAttribute(Player player, double amount, boolean save) {
		HealCommand.getMaxHealthAttribute(player).setBaseValue(amount);
		if (player.getHealth() > amount)
			player.setHealth(amount);

		if (save) {
			Pugmas24User user = userService.get(player);
			user.setMaxHealth(amount);
			userService.save(user);
		}
	}

	// Death

	@Override
	public Location getRespawnLocation(Player player) {
		return warp; // TODO: GET PLAYER CABIN, IF SET
	}

	public void onDeath(Player player, @NotNull Pugmas24.Pugmas24DeathCause deathType) {
		player.teleportAsync(deathLoc);
		broadcast(deathType.getMessage(player));

		fadeToBlack(player, "&cYou died.", 30).thenRun(() -> player.teleportAsync(getRespawnLocation(player)));
	}

	@Override
	public void onPlayerDeath(PlayerDeathEvent event) {
		event.setCancelled(true);
		EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();

		Pugmas24DeathCause deathCause = Pugmas24DeathCause.UNKNOWN;
		if (damageEvent != null) {
			deathCause = switch (damageEvent.getCause()) {
				case FALL -> Pugmas24DeathCause.FALL;
				case STARVATION -> Pugmas24DeathCause.STARVATION;
				case FLY_INTO_WALL -> Pugmas24DeathCause.ELYTRA;
				default -> Pugmas24DeathCause.UNKNOWN;
			};
		}


		onDeath(event.getPlayer(), deathCause);
	}

	@AllArgsConstructor
	public enum Pugmas24DeathCause {
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
