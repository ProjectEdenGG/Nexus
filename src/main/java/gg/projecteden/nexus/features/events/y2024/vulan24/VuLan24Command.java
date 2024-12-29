package gg.projecteden.nexus.features.events.y2024.vulan24;

import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.IEventCommand;
import gg.projecteden.nexus.features.events.y2024.vulan24.lantern.VuLan24LanternAnimation;
import gg.projecteden.nexus.framework.commands.models.annotations.*;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.models.vulan24.VuLan24Config;
import gg.projecteden.nexus.models.vulan24.VuLan24ConfigService;
import gg.projecteden.nexus.models.vulan24.VuLan24User;
import gg.projecteden.nexus.models.vulan24.VuLan24UserService;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.StringUtils.ProgressBar;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;

@Aliases("vulan")
@NoArgsConstructor
public class VuLan24Command extends IEventCommand {
	private final VuLan24ConfigService configService = new VuLan24ConfigService();
	private final VuLan24Config config = configService.get0();
	private final VuLan24UserService userService = new VuLan24UserService();
	private VuLan24User user;

	public VuLan24Command(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = userService.get(player());
	}

	@Override
	public EdenEvent getEdenEvent() {
		return VuLan24.get();
	}

	@Override
	@Path("quest progress [player]")
	protected void quest_progress(@Arg(value = "self", permission = Group.STAFF) Quester quester) {
		super.quest_progress(quester);

		line();
		send(json(" &3Community daily quest goal: " + getCommunityQuestProgressBar() + " &e" + config.getCompletedDailyQuests() + "&3/&e" + VuLan24.DAILY_QUEST_GOAL)
			.hover("&eReach the goal together for a reward!"));
		if (!user.isFinishedDailyQuest())
			send("&7   - Talk to Anh at the concession stand");
	}

	@Permission(Group.ADMIN)
	@Path("stats quest progress [page]")
	@Description("Print all users' quest progress")
	protected void stats_quest_progress(@Arg("1") int page) {
		super.stats_quest_progress(page);

		if (page == 1) {
			line();
			send(json(" &3Community daily quest goal: " + getCommunityQuestProgressBar() + " &e" + config.getCompletedDailyQuests() + "&3/&e" + VuLan24.DAILY_QUEST_GOAL));
		}
	}

	private String getCommunityQuestProgressBar() {
		return ProgressBar.builder()
			.progress(config.getCompletedDailyQuests())
			.goal(VuLan24.DAILY_QUEST_GOAL)
			.summaryStyle(ProgressBar.SummaryStyle.NONE)
			.length(300)
			.seamless(true)
			.build();
	}

	@Path
	void warp() {
		if (!user.isVisited()) {
			WarpType.VULAN24.get("Avontyre").teleportAsync(player());
		} else {
			WarpType.VULAN24.get("VinhLuc").teleportAsync(player());
		}
	}

	@Path("lantern animation debug path <id>")
	@Permission(Group.ADMIN)
	void lantern_animation_debug_path(int id) {
		if (VuLan24LanternAnimation.getInstance() == null)
			VuLan24LanternAnimation.builder().build();

		for (List<Location> path : VuLan24LanternAnimation.getInstance().getPaths()) {
			Location loc = path.get(id);
			player().sendBlockChange(loc, Material.RED_CONCRETE.createBlockData());
		}
	}

	@Path("lantern animation start [--count] [--moveSpeed]")
	@Permission(Group.ADMIN)
	void lantern_animation_start(
		@Switch @Arg("10") int count,
		@Switch @Arg("2") int moveSpeed,
		@Switch @Arg("5") int lightLevel
	) {
		if (VuLan24LanternAnimation.getInstance() != null)
			error("There is already an active animation");

		VuLan24LanternAnimation.builder()
			.startingLanterns(count)
			.moveSpeed(moveSpeed)
			.lightLevel(lightLevel)
			.start();
	}

	@Path("lantern animation stop")
	@Permission(Group.ADMIN)
	void lantern_animation_stop() {
		if (VuLan24LanternAnimation.getInstance() == null)
			error("There is no active animation");

		VuLan24LanternAnimation.getInstance().cleanup();
	}

}
