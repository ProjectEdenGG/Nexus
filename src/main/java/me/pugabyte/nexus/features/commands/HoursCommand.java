package me.pugabyte.nexus.features.commands;

import lombok.Data;
import lombok.ToString;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.afk.AFK;
import me.pugabyte.nexus.features.chat.Koda;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Description;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.hours.Hours;
import me.pugabyte.nexus.models.hours.HoursService;
import me.pugabyte.nexus.models.hours.HoursService.PageResult;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.SoundUtils.Jingle;
import me.pugabyte.nexus.utils.StringUtils.Timespan;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Aliases({"playtime", "days", "minutes", "seconds"})
public class HoursCommand extends CustomCommand {
	private final HoursService service = new HoursService();

	public HoursCommand(CommandEvent event) {
		super(event);
	}

	private static final int DAY = Time.DAY.get() / 20;

	@Async
	@Path("[player]")
	void player(@Arg("self") Hours hours) {
		OfflinePlayer player = hours.getOfflinePlayer();
		boolean isSelf = isSelf(player);

		send("");
		send(PREFIX + (isSelf ? "Your" : "&e" + player.getName() + "&3's") + " playtime");
		send("&3Total: &e" + Timespan.of(hours.getTotal()).noneDisplay(true).format());
		send("&7- &3Today: &e" + Timespan.of(hours.getDaily()).noneDisplay(true).format());
		send("&7- &3This month: &e" + Timespan.of(hours.getMonthly()).noneDisplay(true).format());
		send("&7- &3This year: &e" + Timespan.of(hours.getYearly()).noneDisplay(true).format());

		if (Rank.of(player) == Rank.GUEST) {

			String who = (isSelf ? "You need" : player.getName() + " needs") + " ";
			String left = Timespan.of(DAY - hours.getTotal()).format();

			line();
			send("&3" + who + "&e" + left + " more in-game play time &3to achieve &fMember&3.");
		}
	}

	@Permission("group.seniorstaff")
	@Path("debug [player]")
	void debug(@Arg("self") OfflinePlayer player) {
		send(service.get(player).toString());
	}

	// TODO Update paginate to support database-level pagination
	@Async
	@Description("View the play time leaderboard for any year, month, or day")
	@Path("top [args...]")
	void top2(@Arg("1") HoursTopArguments args) {
		int page = args.getPage();
		List<PageResult> results = service.getPage(args);
		if (results.size() == 0)
			error("&cNo results on page " + page);

		int totalHours = 0;
		for (PageResult result : results)
			totalHours += result.getTotal();

		send("");
		send(PREFIX + "Total: " + Timespan.of(totalHours).format() + (page > 1 ? "&e  |  &3Page " + page : ""));

		BiFunction<PageResult, String, JsonBuilder> formatter = (result, index) ->
				json("&3" + index + " &e" + result.getOfflinePlayer().getName() + " &7- " + Timespan.of(result.getTotal()).format());

		paginate(results, formatter, "/hours top " + args.getInput(), page);
	}

	@Data
	public static class HoursTopArguments {
		private int year = -1;
		private int month = -1;
		private int day = -1;
		private int page = 1;
		private String input;

		public HoursTopArguments() {
			this("");
		}

		public HoursTopArguments(String input) {
			LocalDate now = LocalDate.now();

			String[] args = input.split(" ");
			String[] split = args[0].split("-");
			this.input = args[0];
			if (Utils.isInt(this.input) && Integer.parseInt(this.input) < 2015) // its page number
				this.input = "";

			switch (args[0]) {
				case "day":
				case "daily":
					day = now.getDayOfMonth();
					month = now.getMonthValue();
					year = now.getYear();
					break;
				case "month":
				case "monthly":
					month = now.getMonthValue();
					year = now.getYear();
					break;
				case "year":
				case "yearly":
					year = now.getYear();
					break;
				default:
					if (split[0].length() > 0) {
						if (Utils.isInt(split[0])) {
							int yearInput = Integer.parseInt(split[0]);
							if (yearInput >= 2015)
								if (yearInput <= 2019)
									throw new InvalidInputException("Years 2015-2019 are not supported");
								else if (yearInput > now.getYear())
									throw new InvalidInputException("Year &e" + yearInput + " &cis in the future");
								else
									year = yearInput;
							else {
								page = yearInput;
								break;
							}

							if (split.length >= 2) {
								if (split[1].length() > 0 && Utils.isInt(split[1])) {
									int monthInput = Integer.parseInt(split[1]);
									if (monthInput >= 1 && monthInput <= 12)
										if (YearMonth.of(year, monthInput).isAfter(YearMonth.now()))
											throw new InvalidInputException("Month &e" + yearInput + "-" + monthInput + " &cis in the future");
										else
											month = monthInput;
									else
										throw new InvalidInputException("Invalid month &e" + monthInput);
								} else
									throw new InvalidInputException("Invalid month &e" + split[1]);

								if (split.length >= 3) {
									if (split[2].length() > 0 && Utils.isInt(split[2])) {
										int dayInput = Integer.parseInt(split[2]);
										if (YearMonth.of(year, month).isValidDay(dayInput))
											if (LocalDate.of(year, month, dayInput).isAfter(now))
												throw new InvalidInputException("Day &e" + year + "-" + month + "-" + dayInput + " &cis in the future");
											else
												day = dayInput;
										else
											throw new InvalidInputException("Invalid day of month &e" + dayInput);
									} else
										throw new InvalidInputException("Invalid day &e" + split[2]);
								}
							}
						} else
							throw new InvalidInputException("Invalid year &e" + split[0]);
					}
			}

			if (args.length >= 2 && Utils.isInt(args[1]))
				page = Integer.parseInt(args[1]);

			if (year == 2020) {
				if (month == -1)
					throw new InvalidInputException("Year 2020 is not supported");
				else if (month <= 5)
					throw new InvalidInputException("Months Jan-May of 2020 are not supported");
			}

			if (page < 1)
				throw new InvalidInputException("Page cannot be less than 1");
		}

		@ToString.Include
		public String getRegex() {
			if (year <= 0)
				return ".*";

			String regex = year + "-";
			if (month > 0) {
				regex += String.format("%02d", month) + "-";
				if (day > 0)
					regex += String.format("%02d", day);
				else
					regex += ".*";
			} else
				regex += ".*";

			return regex;
		}
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
		Tasks.repeatAsync(10, Time.SECOND.x(INTERVAL), () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				try {
					if (AFK.get(player).isAfk()) continue;

					HoursService service = new HoursService();
					Hours hours = service.get(player);
					hours.increment(INTERVAL);
					service.update(hours);

					if (Rank.of(player) == Rank.GUEST) {
						if (player.hasPermission("set.my.rank"))
							continue;

						if (hours.getTotal() > DAY) {
							Tasks.sync(() -> {
								PlayerUtils.runCommandAsConsole("lp user " + player.getName() + " parent set " + Rank.MEMBER.name());
								Koda.say("Congrats on Member rank, " + Nickname.of(player) + "!");
								Jingle.RANKUP.play(player);
								PlayerUtils.send(player, "");
								PlayerUtils.send(player, "");
								PlayerUtils.send(player, "&e&lCongratulations! &3You have been promoted to &fMember&3 for " +
										"playing for &e24 hours &3in-game. You are now eligible for &c/trusted&3.");
								PlayerUtils.send(player, "");
								PlayerUtils.send(player, "&6&lThank you for flying Bear Nation!");
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


