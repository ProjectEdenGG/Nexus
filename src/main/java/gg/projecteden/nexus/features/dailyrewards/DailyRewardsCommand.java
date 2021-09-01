package gg.projecteden.nexus.features.dailyrewards;

import gg.projecteden.annotations.Async;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.CommandCooldownException;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.dailyreward.DailyRewardUser;
import gg.projecteden.nexus.models.dailyreward.DailyRewardUserService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.WorldGroup;
import gg.projecteden.utils.TimeUtils.TickTime;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

import static gg.projecteden.utils.TimeUtils.shortDateTimeFormat;

@Aliases({"dr", "dailyreward"})
@Permission("daily.rewards")
public class DailyRewardsCommand extends CustomCommand {
	private final DailyRewardUserService service = new DailyRewardUserService();
	private DailyRewardUser user;

	public DailyRewardsCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Path
	void main() {
		if (WorldGroup.SURVIVAL != worldGroup())
			error("&cYou must be in the survival worlds to claim this reward.");

		DailyRewardsFeature.menu(player(), user);
	}

	@Path("getLastTaskTime")
	@Permission(value = "group.admin", absolute = true)
	void getLastTaskTime() {
		send(shortDateTimeFormat(DailyRewardsFeature.getLastTaskTime()));
	}

	@Path("streak [player]")
	void streak(@Arg("self") DailyRewardUser user) {
		send(PREFIX + (isSelf(user) ? "Your" : user.getNickname() + "'s") + " streak: &e" + user.getCurrentStreak().getStreak());
	}

	@Path("today [player]")
	void today(@Arg("self") DailyRewardUser user) {
		boolean earnedToday = user.getCurrentStreak().isEarnedToday();
		send(PREFIX + (isSelf(user) ? "You have " : user.getNickname() + " has ") + (earnedToday ? "&e" : "&cnot ") + "earned &3today's reward");
	}

	@Path("unclaim <player> <day>")
	@Permission(value = "group.admin", absolute = true)
	void unclaim(DailyRewardUser user, int day) {
		user.getCurrentStreak().unclaim(day);
		service.save(this.user);
		send(PREFIX + "Unclaimed day " + day + " for player " + user.getNickname());
	}

	@Path("set <player> <day>")
	@Permission(value = "group.admin", absolute = true)
	void setDay(DailyRewardUser user, int day) {
		user.getCurrentStreak().setStreak(day);
		service.save(this.user);
		send(PREFIX + "Streak set to " + this.user.getCurrentStreak().getStreak() + " for player " + user.getNickname());
	}

	private static final String resetCooldownType = "dailyRewards-reset";

	@Confirm
	@Path("reset [player]")
	void reset(@Arg(value = "self", permission = "group.admin") DailyRewardUser user) {
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
	void top(@Arg("1") int page) {
		final BiFunction<DailyRewardUser, String, JsonBuilder> formatter = (user, index) ->
			json(index + " &e" + user.getNickname() + " &7- " + user.getCurrentStreak().getStreak());

		final List<DailyRewardUser> sorted = service.getAll().stream()
			.filter(user -> user.getCurrentStreak().getStreak() > 0)
			.sorted(Comparator.<DailyRewardUser>comparingInt(user -> user.getCurrentStreak().getStreak()).reversed())
			.toList();

		paginate(sorted, formatter, "/dailyrewards top", page);
	}

}
