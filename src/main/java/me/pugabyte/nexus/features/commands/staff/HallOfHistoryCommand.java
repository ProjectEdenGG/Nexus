package me.pugabyte.nexus.features.commands.staff;

import me.pugabyte.nexus.features.commands.AgeCommand.ServerAge;
import me.pugabyte.nexus.features.menus.MenuUtils.ConfirmationMenu;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.PlayerNotFoundException;
import me.pugabyte.nexus.models.hallofhistory.HallOfHistory;
import me.pugabyte.nexus.models.hallofhistory.HallOfHistory.RankHistory;
import me.pugabyte.nexus.models.hallofhistory.HallOfHistoryService;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.NerdService;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import me.pugabyte.nexus.utils.TimeUtils.Timespan;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.OfflinePlayer;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.UUID;
import java.util.function.BiFunction;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;
import static me.pugabyte.nexus.utils.TimeUtils.dateFormat;
import static me.pugabyte.nexus.utils.TimeUtils.shortDateFormat;

@Aliases("hoh")
public class HallOfHistoryCommand extends CustomCommand {
	HallOfHistoryService service = new HallOfHistoryService();

	public HallOfHistoryCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void warp() {
		runCommand("warp hallofhistory");
	}

	@Path("clearCache")
	@Permission("group.seniorstaff")
	void clearCache() {
		service.clearCache();
		send(PREFIX + "Successfully cleared cache");
	}

	@Async
	@Path("view <player>")
	void view(OfflinePlayer target) {
		line(4);
		send("&e&l" + target.getName());
		line();
		HallOfHistory hallOfHistory = service.get(target.getUniqueId());
		for (RankHistory rankHistory : hallOfHistory.getRankHistory()) {
			JsonBuilder builder = new JsonBuilder();
			builder.next("  " + (rankHistory.isCurrent() ? "&2Current" : "&cFormer") + " " + rankHistory.getRank().getColor() + rankHistory.getRank().plain());
			if (isPlayer() && player().hasPermission("hoh.edit"))
				builder.next("  &c[x]").command("/hoh removerank " + target.getName() + " " + getRankCommandArgs(rankHistory));

			send(builder);
			send("    &ePromotion Date: &3" + shortDateFormat(rankHistory.getPromotionDate()));
			if (rankHistory.getResignationDate() != null)
				send("    &eResignation Date: &3" + shortDateFormat(rankHistory.getResignationDate()));
		}

		line();
		Nerd nerd = Nerd.of(target);
		if (!isNullOrEmpty(nerd.getAbout()))
			send("  &eAbout me: &3" + nerd.getAbout());
		if (nerd.isMeetMeVideo()) {
			line();
			String url = "https://bnn.gg/meet/" + nerd.getName().toLowerCase();
			send(json("  &eMeet Me!&c " + url).url(url));
		}
	}

	@Permission("hoh.edit")
	@Path("create <player>")
	void create(@Arg(tabCompleter = Nerd.class) String player) {
		runCommand("blockcenter");
		String name;
		String skin;
		try {
			Nerd nerd = Nerd.of(convertToOfflinePlayer(player));
			name = nerd.getNicknameFormat();
			skin = nerd.getName();
		} catch (PlayerNotFoundException e) {
			// probably a veteran
			name = Rank.VETERAN.getHex() + player;
			skin = player;
		}
		// is there a better workaround for this? :P
		final String name1 = name;
		final String skin1 = skin;
		Tasks.wait(5, () -> runCommand("npc create " + name1));
		Tasks.wait(10, () -> runCommand("npc skin " + skin1));
	}

	@Async
	@Permission("hoh.edit")
	@Path("addRank <player> <current|former> <rank> <promotionDate> [resignationDate]")
	void addRank(OfflinePlayer target, String when, Rank rank, LocalDate promotion, LocalDate resignation) {
		boolean current = "current".equalsIgnoreCase(when);

		if (!current && resignation == null)
			error("Resignation date was not provided");

		HallOfHistory history = service.get(target);
		history.getRankHistory().add(new RankHistory(rank, current, promotion, resignation));
		service.save(history);
		send(PREFIX + "Successfully saved rank data for &e" + target.getName());
	}

	@Async
	@Permission("hoh.edit")
	@Path("removeRank <player> <current|former> <rank> <promotionDate> [resignationDate]")
	void removeRankConfirm(OfflinePlayer player, String when, Rank rank, LocalDate promotion, LocalDate resignation) {
		boolean current = "current".equalsIgnoreCase(when);

		HallOfHistory history = service.get(player.getUniqueId());
		ConfirmationMenu.builder()
				.title("Remove rank from " + player.getName() + "?")
				.onConfirm((item) -> {
					for (RankHistory rankHistory : new ArrayList<>(history.getRankHistory())) {
						if (!new RankHistory(rank, current, promotion, resignation).equals(rankHistory)) continue;

						history.getRankHistory().remove(rankHistory);
						service.save(history);
						send(PREFIX + "Removed the rank from &e" + player.getName());
						send(json(PREFIX + "&eClick here &3to generate a command to re-add rank")
								.suggest("/hoh addrank " + player.getName() + " " + getRankCommandArgs(rankHistory)));
						return;
					}
					send(PREFIX + "Could not find the rank to delete");
				})
				.open(player());
	}

	private String getRankCommandArgs(RankHistory rankHistory) {
		String command = (rankHistory.isCurrent() ? "Current" : "Former") + " " + rankHistory.getRank() + " ";
		if (rankHistory.getPromotionDate() != null)
			command += dateFormat(rankHistory.getPromotionDate()) + " ";
		if (rankHistory.getResignationDate() != null)
			command += dateFormat(rankHistory.getResignationDate());
		return command.trim();
	}

	@Permission("hoh.edit")
	@Path("clear <player>")
	void clear(OfflinePlayer player) {
		HallOfHistory history = service.get(player.getUniqueId());
		history.getRankHistory().clear();
		service.save(history);
		send(PREFIX + "Cleared all data for &e" + player.getName());
	}

	@Path("setwarp")
	@Permission("hoh.edit")
	void setWarp() {
		runCommand("blockcenter");
		Tasks.wait(3, () -> runCommand("warps set hallofhistory"));
	}

//	@Path("expand")
//	@Permission("hoh.edit")
//	void expand() {
//		send(PREFIX + "Expanding HOH. &4&lDon't move!");
//		int wait = 40;
//		AtomicReference<Location> newLocation = new AtomicReference<>(location());
//		Tasks.wait(wait, () -> runCommand("/warp hallofhistory"));
//		Tasks.wait(wait += 20, () -> newLocation.set(location().add(16, 0, 0).clone()));
//		Tasks.wait(wait += 3, () -> runCommand("/pos1"));
//		Tasks.wait(wait += 3, () -> runCommand("/pos2"));
//		Tasks.wait(wait += 3, () -> runCommand("/expand 7"));
//		Tasks.wait(wait += 3, () -> runCommand("/expand 15 s"));
//		Tasks.wait(wait += 3, () -> runCommand("/expand 15 n"));
//		Tasks.wait(wait += 3, () -> runCommand("/expand 10 e"));
//		Tasks.wait(wait += 3, () -> runCommand("/expandv 10"));
//		Tasks.wait(wait += 3, () -> runCommand("/move 16 e"));
//		Tasks.wait(wait += 20, () -> player().teleport(newLocation.get()));
//		Tasks.wait(wait += 5, () -> runCommand("/hoh setwarp"));
//		Tasks.wait(wait += 5, () -> runCommand("/schem load hoh-expansion"));
//		Tasks.wait(wait += 20, () -> runCommand("/paste"));
//		Tasks.wait(wait += 20, () -> runCommand("/contract 17"));
//		Tasks.wait(wait += 3, () -> runCommand("/expand 1"));
//		Tasks.wait(wait += 3, () -> runCommand("/contract 1"));
//		Tasks.wait(wait += 3, () -> runCommand("/contract 12 d"));
//		Tasks.wait(wait += 3, () -> runCommand("/contracth 5"));
//		Tasks.wait(wait += 3, () -> runCommand("/contract 3 u"));
//		Tasks.wait(wait += 3, () -> runCommand("/cut"));
//		Tasks.wait(wait += 3, () -> runCommand("/expand -1"));
//		Tasks.wait(wait += 3, () -> runCommand("/contract -1"));
//		Tasks.wait(wait += 3, () -> runCommand("/stack 1"));
//		Tasks.wait(wait += 3, () -> runCommand("/expand -15"));
//		Tasks.wait(wait += 3, () -> runCommand("/contract -15"));
//		Tasks.wait(wait += 3, () -> runCommand("/set stone_slab:8"));
//		Tasks.wait(wait += 3, () -> runCommand("/desel"));
//		send(PREFIX + "Expansion complete! Took &e" + (wait / 20) + " &3seconds");
//	}

	@Path("about <about...>")
	void about(String about) {
		NerdService service = new NerdService();
		Nerd nerd = Nerd.of(player());
		nerd.setAbout(stripColor(about));
		service.save(nerd);
		send(PREFIX + "Set your about to: &e" + nerd.getAbout());
	}

	@Path("preferredName <name...>")
	void preferredName(String name) {
		NerdService service = new NerdService();
		Nerd nerd = Nerd.of(player());
		nerd.setPreferredName(stripColor(name));
		service.save(nerd);
		send(PREFIX + "Set your preferred name to: &e" + nerd.getPreferredName());
	}

	@Async
	@Path("staffTime [page]")
	public void staffTime(@Arg("1") int page) {
		LocalDate now = LocalDate.now();
		HallOfHistoryService service = new HallOfHistoryService();
		Map<UUID, Long> staffTimeMap = new HashMap<>();

		for (HallOfHistory hallOfHistory : service.<HallOfHistory>getAll()) {
			long days = 0;
			days: for (LocalDate date = ServerAge.getEpoch().toLocalDate(); date.isBefore(now); date = date.plusDays(1)) {
				for (RankHistory rankHistory : hallOfHistory.getRankHistory()) {
					LocalDate from = rankHistory.getPromotionDate();
					LocalDate to = rankHistory.getResignationDate();

					if (from == null)
						continue;
					if (to == null)
						to = now;

					if (Utils.isBetween(date, from, to)) {
						++days;
						continue days;
					}
				}
			}

			if (days == 0)
				continue;

			staffTimeMap.put(hallOfHistory.getUuid(), days);
		}

		send(PREFIX + "Staff times");
		BiFunction<UUID, String, JsonBuilder> formatter = (uuid, index) -> {
			String time = Timespan.of(staffTimeMap.get(uuid) * (Time.DAY.get() / 20)).format();
			return json("&3" + index + " &e" + time + " &7- " + Nerd.of(uuid).getNameFormat());
		};

		paginate(new ArrayList<>(Utils.sortByValueReverse(staffTimeMap).keySet()), formatter, "/hoh staffTime", page);
	}

	@Path("promotionTimes [page]")
	void promotionTimes(@Arg("1") int page) {
		NerdService nerdService = new NerdService();
		HallOfHistoryService service = new HallOfHistoryService();
		Map<UUID, Long> promotionTimeMap = new HashMap<>();

		for (HallOfHistory hallOfHistory : service.<HallOfHistory>getAll()) {
			Nerd nerd = Nerd.of(hallOfHistory.getUuid());
			List<RankHistory> history = hallOfHistory.getRankHistory();
			history.sort(Comparator.comparing(RankHistory::getPromotionDate));

			if (nerd.getFirstJoin().isBefore(ServerAge.getEpoch().minusYears(1)))
				continue;

			long days = nerd.getFirstJoin().toLocalDate().until(history.get(0).getPromotionDate(), ChronoUnit.DAYS);

			if (days > 0)
				promotionTimeMap.put(hallOfHistory.getUuid(), days);
		}

		OptionalDouble average = promotionTimeMap.values().stream().mapToLong(Long::valueOf).average();

		send(PREFIX + "Promotion times  |  Average: " + StringUtils.getNf().format(average.orElse(0)) + " days");
		BiFunction<UUID, String, JsonBuilder> formatter = (uuid, index) -> {
			String time = Timespan.of(promotionTimeMap.get(uuid) * (Time.DAY.get() / 20)).format();
			return json("&3" + index + " &e" + Nickname.of(uuid) + " &7- " + time);
		};

		paginate(new ArrayList<>(Utils.sortByValue(promotionTimeMap).keySet()), formatter, "/hoh promotionTimes", page);
	}

}
