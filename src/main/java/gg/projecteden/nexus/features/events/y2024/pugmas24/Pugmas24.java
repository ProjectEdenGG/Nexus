package gg.projecteden.nexus.features.events.y2024.pugmas24;

import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.y2024.pugmas24.advent.Pugmas24Advent;
import gg.projecteden.nexus.features.events.y2024.pugmas24.balloons.Pugmas24BalloonEditor;
import gg.projecteden.nexus.features.events.y2024.pugmas24.balloons.Pugmas24BalloonManager;
import gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds.Pugmas24Fairgrounds;
import gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds.Pugmas24Rides;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.Pugmas24Fishing;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.Pugmas24SlotMachine;
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
import gg.projecteden.nexus.models.godmode.GodmodeService;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.LocalDate;

import static gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand.canWorldGuardEdit;
import static gg.projecteden.nexus.features.events.models.EventFishingLoot.EventFishingLootCategory.FISH;
import static gg.projecteden.nexus.features.events.models.EventFishingLoot.EventFishingLootCategory.JUNK;
import static gg.projecteden.nexus.features.vanish.Vanish.isVanished;

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

	public static final LocalDate _25TH = LocalDate.of(2024, 12, 25);

	public static final String LORE = "&ePugmas 2024 Item";
	public final Location warp = location(-688.5, 82, -2964.5);

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

		new Pugmas24Advent();
		new Pugmas24Fairgrounds();
		new Pugmas24BalloonManager();
		new Pugmas24Fishing();
		new Pugmas24SlotMachine();
		new Pugmas24QuestItemsListener();
		new Pugmas24Waystones();

		Pugmas24Rides.startup();
		Pugmas24Train.startup();
	}

	@Override
	public void onStop() {
		Pugmas24Train.shutdown();
		Pugmas24Advent.shutdown();
		Pugmas24BalloonEditor.shutdown();
	}

	@Override
	protected void registerFishingLoot() {
		registerFishingLoot(FISH, JUNK);
	}

	@Override
	public void registerInteractHandlers() {
		handleInteract(Pugmas24NPC.BLACKSMITH, (player, npc) -> Pugmas24ShopMenu.BLACKSMITH.open(player));
	}

	public static void send(String message, Player to) {
		PlayerUtils.send(to, message);
	}

	public static String isCheatingMsg(Player player) {
		if (canWorldGuardEdit(player)) return "wgedit";
		if (!player.getGameMode().equals(GameMode.SURVIVAL)) return "creative";
		if (player.isFlying()) return "fly";
		if (isVanished(player)) return "vanish";
		if (new GodmodeService().get(player).isActive()) return "godmode";

		return null;
	}

	public static boolean isAdventActive(LocalDate date) {
		return get().isEventActive() && !date.isAfter(_25TH);
	}

	public static boolean is25thOrAfter() {
		return is25thOrAfter(LocalDate.now());
	}

	public static boolean is25thOrAfter(LocalDate date) {
		return date.isAfter(_25TH.plusDays(-1));
	}
}
