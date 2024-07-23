package gg.projecteden.nexus.features.events.y2024.pugmas24;

import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.y2024.pugmas24.advent.Advent24;
import gg.projecteden.nexus.features.events.y2024.pugmas24.balloons.BalloonEditor;
import gg.projecteden.nexus.features.events.y2024.pugmas24.balloons.BalloonManager;
import gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds.Fairgrounds;
import gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds.Rides;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.Train24;
import gg.projecteden.nexus.features.events.y2024.pugmas24.quests.Pugmas24Entity;
import gg.projecteden.nexus.features.events.y2024.pugmas24.quests.Pugmas24NPC;
import gg.projecteden.nexus.features.events.y2024.pugmas24.quests.Pugmas24Quest;
import gg.projecteden.nexus.features.events.y2024.pugmas24.quests.Pugmas24QuestItem;
import gg.projecteden.nexus.features.events.y2024.pugmas24.quests.Pugmas24QuestReward;
import gg.projecteden.nexus.features.events.y2024.pugmas24.quests.Pugmas24QuestTask;
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
import static gg.projecteden.nexus.features.vanish.Vanish.isVanished;

@QuestConfig(
	quests = Pugmas24Quest.class,
	tasks = Pugmas24QuestTask.class,
	npcs = Pugmas24NPC.class,
	entities = Pugmas24Entity.class,
	items = Pugmas24QuestItem.class,
	rewards = Pugmas24QuestReward.class,
	start = @Date(m = 12, d = 1, y = 2024),
	end = @Date(m = 1, d = 10, y = 2025),
		world = "buildadmin", // TODO FINAL: WORLD
		region = "pugmas24",
	warpType = WarpType.PUGMAS24
)
@Environments(Env.PROD)
public class Pugmas24 extends EdenEvent {
	private static Pugmas24 instance;
	public static final String PREFIX = StringUtils.getPrefix("Pugmas 2024");

	public static final LocalDate _25TH = LocalDate.of(2024, 12, 25);

	public static final String LORE = "&ePugmas 2024 Item";
	public final Location warp = location(-688.5, 82, -2964.5); // TODO FINAL: WARP LOCATION

	@Getter
	private static boolean ridesEnabled = true;

	//

	public Pugmas24() {
		instance = this;
	}

	public static Pugmas24 get() {
		return instance;
	}

	@Override
	public void onStart() {
		new Advent24();
		new Fairgrounds();
		new BalloonManager();
		Rides.startup();
		Train24.startup();
	}

	@Override
	public void onStop() {
		Train24.shutdown();
		Advent24.shutdown();
		BalloonEditor.shutdown();
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
