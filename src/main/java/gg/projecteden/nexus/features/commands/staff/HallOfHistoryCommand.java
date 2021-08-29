package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.features.commands.AgeCommand.ServerAge;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Async;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotFoundException;
import gg.projecteden.nexus.models.hallofhistory.HallOfHistory;
import gg.projecteden.nexus.models.hallofhistory.HallOfHistory.RankHistory;
import gg.projecteden.nexus.models.hallofhistory.HallOfHistoryService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.utils.TimeUtils.TickTime;
import gg.projecteden.utils.TimeUtils.Timespan;
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

import static gg.projecteden.nexus.utils.StringUtils.stripColor;
import static gg.projecteden.utils.TimeUtils.dateFormat;
import static gg.projecteden.utils.TimeUtils.shortDateFormat;

@Aliases("hoh")
public class HallOfHistoryCommand extends CustomCommand {
	private final HallOfHistoryService service = new HallOfHistoryService();
	private final NerdService nerdService = new NerdService();

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
		Nerd nerd = Nerd.of(target);
		send("&e&l" + nerd.getNickname());
		line();
		if (!nerd.getNickname().equals(nerd.getName()))
			send("  &eIGN: &3" + nerd.getName());
		if (!nerd.getPronouns().isEmpty())
			send("  &ePronouns: &3" + String.join(", ", nerd.getPronouns().stream().map(Enum::toString).toList()));
		line();
		HallOfHistory hallOfHistory = service.get(target.getUniqueId());
		for (RankHistory rankHistory : hallOfHistory.getRankHistory()) {
			JsonBuilder builder = new JsonBuilder();
			builder.next("  " + (rankHistory.isCurrent() ? "&2Current" : "&cFormer") + " " + rankHistory.getRank().getChatColor() + rankHistory.getRank().getName());
			if (isPlayer() && player().hasPermission("hoh.edit"))
				builder.next("  &c[x]").command("/hoh removerank " + target.getName() + " " + getRankCommandArgs(rankHistory));

			send(builder);
			send("    &ePromotion Date: &3" + shortDateFormat(rankHistory.getPromotionDate()));
			if (rankHistory.getResignationDate() != null)
				send("    &eResignation Date: &3" + shortDateFormat(rankHistory.getResignationDate()));
		}

		line();
		if (!isNullOrEmpty(nerd.getAbout()))
			send("  &eAbout me: &3" + nerd.getAbout());
		if (nerd.isMeetMeVideo()) {
			line();
			String url = EdenSocialMediaSite.WEBSITE.getUrl() + "/meet/" + nerd.getName().toLowerCase();
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
			name = nerd.getColoredName();
			skin = nerd.getName();
		} catch (PlayerNotFoundException e) {
			// probably a veteran
			name = Rank.VETERAN.colored().getHex() + player;
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

		service.edit(target, history -> history.getRankHistory().add(new RankHistory(rank, current, promotion, resignation)));
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
		service.edit(player.getUniqueId(), history -> history.getRankHistory().clear());
		send(PREFIX + "Cleared all data for &e" + player.getName());
	}

	@Path("setwarp")
	@Permission("hoh.edit")
	void setWarp() {
		runCommand("blockcenter");
		Tasks.wait(3, () -> runCommand("warps set hallofhistory"));
	}

	@Path("about <about...>")
	void about(String about) {
		nerdService.edit(player(), nerd -> nerd.setAbout(stripColor(about)));
		send(PREFIX + "Set your about to: &e" + nerd().getAbout());
	}

	@Path("preferredName <name...>")
	void preferredName(String name) {
		nerdService.edit(player(), nerd -> nerd.setPreferredName(stripColor(name)));
		send(PREFIX + "Set your preferred name to: &e" + nerd().getPreferredName());
	}

	@Async
	@Path("staffTime [page]")
	public void staffTime(@Arg("1") int page) {
		LocalDate now = LocalDate.now();
		HallOfHistoryService service = new HallOfHistoryService();
		Map<UUID, Long> staffTimeMap = new HashMap<>();

		for (HallOfHistory hallOfHistory : service.getAll()) {
			long days = 0;
			days: for (LocalDate date = ServerAge.getEPOCH().toLocalDate(); date.isBefore(now); date = date.plusDays(1)) {
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
			String time = Timespan.of(staffTimeMap.get(uuid) * (TickTime.DAY.get() / 20)).format();
			return json(index + " &e" + time + " &7- " + Nerd.of(uuid).getNameFormat());
		};

		paginate(Utils.sortByValueReverse(staffTimeMap).keySet(), formatter, "/hoh staffTime", page);
	}

	@Path("promotionTimes [page]")
	void promotionTimes(@Arg("1") int page) {
		HallOfHistoryService service = new HallOfHistoryService();
		Map<UUID, Long> promotionTimeMap = new HashMap<>();

		for (HallOfHistory hallOfHistory : service.getAll()) {
			Nerd nerd = Nerd.of(hallOfHistory.getUuid());
			List<RankHistory> history = hallOfHistory.getRankHistory();
			history.sort(Comparator.comparing(RankHistory::getPromotionDate));

			if (nerd.getFirstJoin().isBefore(ServerAge.getEPOCH().minusYears(1)))
				continue;

			long days = nerd.getFirstJoin().toLocalDate().until(history.get(0).getPromotionDate(), ChronoUnit.DAYS);

			if (days > 0)
				promotionTimeMap.put(hallOfHistory.getUuid(), days);
		}

		OptionalDouble average = promotionTimeMap.values().stream().mapToLong(Long::valueOf).average();

		send(PREFIX + "Promotion times  |  Average: " + StringUtils.getNf().format(average.orElse(0)) + " days");
		BiFunction<UUID, String, JsonBuilder> formatter = (uuid, index) -> {
			String time = Timespan.of(promotionTimeMap.get(uuid) * (TickTime.DAY.get() / 20)).format();
			return json(index + " &e" + Nickname.of(uuid) + " &7- " + time);
		};

		paginate(Utils.sortByValue(promotionTimeMap).keySet(), formatter, "/hoh promotionTimes", page);
	}

}
