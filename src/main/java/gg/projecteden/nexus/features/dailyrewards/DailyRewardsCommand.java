package gg.projecteden.nexus.features.dailyrewards;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.Confirm;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.CommandCooldownException;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.dailyreward.DailyRewardUser;
import gg.projecteden.nexus.models.dailyreward.DailyRewardUserService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

import static gg.projecteden.api.common.utils.TimeUtils.shortDateTimeFormat;

@Aliases({"dr", "dailyreward"})
public class DailyRewardsCommand extends CustomCommand {
	private final DailyRewardUserService service = new DailyRewardUserService();
	private DailyRewardUser user;

	public DailyRewardsCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@NoLiterals
	@Description("Open the daily rewards menu")
	void main() {
		if (WorldGroup.SURVIVAL != worldGroup())
			error("&cYou must be in the survival worlds to claim this reward.");

		DailyRewardsFeature.menu(player(), user);
	}

	@Path("getLastTaskTime")
	@Permission(Group.ADMIN)
	@Description("View the last time the reward task ran")
	void getLastTaskTime() {
		send(shortDateTimeFormat(DailyRewardsFeature.getLastTaskTime()));
	}

	@Path("streak [player]")
	@Description("View your or another player's streak")
	void streak(@Optional("self") DailyRewardUser user) {
		send(PREFIX + (isSelf(user) ? "Your" : user.getNickname() + "'s") + " streak: &e" + user.getCurrentStreak().getStreak());
	}

	@Path("today [player]")
	@Description("Check if you or another player has received")
	void today(@Optional("self") DailyRewardUser user) {
		boolean earnedToday = user.getCurrentStreak().isEarnedToday();
		send(PREFIX + (isSelf(user) ? "You have " : user.getNickname() + " has ") + (earnedToday ? "&e" : "&cnot ") + "earned &3today's reward");
	}

	@Path("unclaim <player> <day>")
	@Permission(Group.ADMIN)
	@Description("Mark a reward as unclaimed")
	void unclaim(DailyRewardUser user, int day) {
		user.getCurrentStreak().unclaim(day);
		service.save(this.user);
		send(PREFIX + "Unclaimed day " + day + " for player " + user.getNickname());
	}

	@Path("set <player> <day>")
	@Permission(Group.ADMIN)
	@Description("Set a player's streak")
	void setDay(DailyRewardUser user, int day) {
		user.getCurrentStreak().setStreak(day);
		service.save(this.user);
		send(PREFIX + "Streak set to " + this.user.getCurrentStreak().getStreak() + " for player " + user.getNickname());
	}

	private static final String resetCooldownType = "dailyRewards-reset";

	@Confirm
	@Path("reset [player]")
	@Description("Set a player's streak")
	void reset(@Optional("self") @Permission(Group.ADMIN) DailyRewardUser user) {
		try {
			if (!new CooldownService().check(player(), resetCooldownType, TickTime.DAY))
				throw new CommandCooldownException(player(), resetCooldownType);

			user.endStreak();
			service.save(user);
			send(player(), PREFIX + "Your streak has been cleared");
			player().closeInventory();
		} catch (CommandCooldownException ex) {
			send(player(), ex.getMessage());
		}
	}

	@Async
	@Path("top [page]")
	@Description("View the streak leaderboard")
	void top(@Optional("1") int page) {
		final BiFunction<DailyRewardUser, String, JsonBuilder> formatter = (user, index) ->
			json(index + " &e" + user.getNickname() + " &7- " + user.getCurrentStreak().getStreak());

		final List<DailyRewardUser> sorted = service.getAll().stream()
			.filter(user -> user.getCurrentStreak().getStreak() > 0)
			.sorted(Comparator.<DailyRewardUser>comparingInt(user -> user.getCurrentStreak().getStreak()).reversed())
			.toList();

		paginate(sorted, formatter, "/dailyrewards top", page);
	}

}
