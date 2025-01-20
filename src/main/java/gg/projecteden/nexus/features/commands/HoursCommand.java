package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.api.common.utils.TimeUtils.Timespan.TimespanBuilder;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.hours.HoursService.HoursTopArguments;
import gg.projecteden.nexus.models.hours.HoursService.PageResult;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.LuckPermsUtils.GroupChange;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.SoundUtils.Jingle;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static gg.projecteden.api.common.utils.Nullables.isNotNullOrEmpty;

@Aliases({"playtime", "days", "minutes", "seconds"})
public class HoursCommand extends CustomCommand {
	private final HoursService service = new HoursService();

	public HoursCommand(CommandEvent event) {
		super(event);
	}

	private static final long DAY = TickTime.DAY.get() / 20;

	@Async
	@Path("[player]")
	@Description("View a player's play time on the server, excluding AFK time")
	void player(@Arg("self") Hours hours) {
		boolean isSelf = isSelf(hours);

		send("");
		send(PREFIX + (isSelf ? "Your" : "&e" + hours.getNickname() + "&3's") + " playtime");
		send("&3Total: &e" + TimespanBuilder.ofSeconds(hours.getTotal()).noneDisplay(true).format());
		send("&7- &3Today: &e" + TimespanBuilder.ofSeconds(hours.getDaily()).noneDisplay(true).format());
		send("&7- &3This month: &e" + TimespanBuilder.ofSeconds(hours.getMonthly()).noneDisplay(true).format());
		send("&7- &3This year: &e" + TimespanBuilder.ofSeconds(hours.getYearly()).noneDisplay(true).format());

		if (Rank.of(hours) == Rank.GUEST) {
			String who = (isSelf ? "You need" : hours.getNickname() + " needs") + " ";
			String left = Timespan.ofSeconds(DAY - hours.getTotal()).format();

			line();
			send("&3" + who + "&e" + left + " more in-game play time &3to achieve &fMember&3.");
		}
	}

	// TODO Update paginate to support database-level pagination
	@Async
	@Path("top [args...]")
	@Description("View the play time leaderboard for any year, month, or day")
	void top(
		@Arg("1") HoursTopArguments args,
		@Switch boolean staff,
		@Switch @Arg(permission = Group.STAFF) String countryCode
	) {
		int page = args.getPage();
		List<PageResult> results = service.getPage(args);

		String onlyStaffSwitch = "";
		if (staff) {
			onlyStaffSwitch = " --onlyStaff";
			results.removeIf(result -> !Rank.of(result.getUuid()).isStaff());
		}

		String countryCodeSwitch = "";
		if (isNotNullOrEmpty(countryCode)) {
			var code = countryCode.toUpperCase();
			countryCodeSwitch = " --countryCode=" + code;
			GeoIPService geoipService = new GeoIPService();
			results.removeIf(result -> !code.equals(geoipService.get(result.getUuid()).getCountryCode()));
		}

		if (results.size() == 0)
			error("&cNo results on page " + page);

		int totalHours = 0;
		for (PageResult result : results)
			totalHours += result.getTotal();

		send("");
		send(PREFIX + "Total: " + Timespan.ofSeconds(totalHours).format() + (page > 1 ? "&e  |  &3Page " + page : ""));

		BiFunction<PageResult, String, JsonBuilder> formatter = (result, index) ->
			json(index + " &e" + Nerd.of(result.getUuid()).getColoredName() + " &7- " + Timespan.ofSeconds(result.getTotal()).format());

		paginate(results, formatter, "/hours top " + args.getInput() + onlyStaffSwitch + countryCodeSwitch, page);
	}

	@ConverterFor(HoursTopArguments.class)
	HoursTopArguments convertToHoursTopArgument(String value) {
		return new HoursTopArguments(value);
	}

	@TabCompleterFor(HoursTopArguments.class)
	List<String> tabCompleteHoursTopArgument(String filter) {
		if (filter.contains(" "))
			return new ArrayList<>();

		Set<String> completions = new HashSet<>();
		LocalDate now = LocalDate.now();
		LocalDate start = LocalDate.of(2020, 6, 1);
		while (!start.isAfter(now)) {
			completions.add(String.valueOf(start.getYear()));
			completions.add(start.getYear() + "-" + String.format("%02d", start.getMonthValue()));
			completions.add(start.getYear() + "-" + String.format("%02d", start.getMonthValue()) + "-" + String.format("%02d", start.getDayOfMonth()));
			start = start.plusDays(1);
		}

		completions.add("daily");
		completions.add("monthly");
		completions.add("yearly");
		completions.remove("2020");

		return completions.stream().filter(completion -> completion.toLowerCase().startsWith(filter)).collect(Collectors.toList());
	}

	private static final int INTERVAL = 5;

	static {
		Tasks.repeatAsync(10, TickTime.SECOND.x(INTERVAL), () -> {
			for (Player player : OnlinePlayers.getAll()) {
				try {
					if (AFK.get(player).isAfk()) continue;

					HoursService service = new HoursService();
					Hours hours = service.get(player.getUniqueId());
					hours.increment(INTERVAL);
					service.update(hours);

					if (Rank.of(player) == Rank.GUEST) {
						if (Dev.WAKKA.is(player)) // I'm somehow broken
							continue;

						if (player.hasPermission("set.my.rank"))
							continue;

						if (hours.getTotal() > DAY) {
							Tasks.sync(() -> {
								GroupChange.set().player(player).group(Rank.MEMBER).runAsync();
								Koda.say("Congrats on Member rank, " + Nickname.of(player) + "!");
								Jingle.RANKUP.play(player);
								PlayerUtils.send(player, "");
								PlayerUtils.send(player, "");
								PlayerUtils.send(player, "&e&lCongratulations! &3You have been promoted to &fMember&3 for " +
										"playing for &e24 hours &3in-game. You are now eligible for &c/trusted&3.");
								PlayerUtils.send(player, "");
								PlayerUtils.send(player, "&6&lThank you for flying Project Eden!");
							});
						}
					}
				} catch (Exception ex) {
					Nexus.warn("Error in Hours scheduler: " + ex.getMessage());
				}
			}
		});
	}
}

