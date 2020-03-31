package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.menus.MenuUtils.ConfirmationMenu;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.Rank;
import me.pugabyte.bncore.models.hallofhistory.HallOfHistory;
import me.pugabyte.bncore.models.hallofhistory.HallOfHistory.RankHistory;
import me.pugabyte.bncore.models.hallofhistory.HallOfHistoryService;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.models.nerd.NerdService;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static me.pugabyte.bncore.utils.StringUtils.shortDateFormat;
import static me.pugabyte.bncore.utils.StringUtils.stripColor;

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

	@Path("view <player>")
	void view(OfflinePlayer target) {
		line(4);
		send("&e&l" + target.getName());
		line();
		HallOfHistory hallOfHistory = service.get(target.getUniqueId());
		for (RankHistory rankHistory : hallOfHistory.getRankHistory()) {
			JsonBuilder builder = new JsonBuilder();
			builder.next("  " + (rankHistory.isCurrent() ? "&2Current" : "&cFormer") + " " + rankHistory.getRank().getChatColor() + rankHistory.getRank().plain());
			if (isPlayer() && player().hasPermission("hoh.edit"))
				builder.next("  &c[x]").command("/hoh removerank " + target.getName() + " " + getRankCommandArgs(rankHistory));

			send(builder);
			send("    &ePromotion Date: &3" + shortDateFormat(rankHistory.getPromotionDate()));
			if (rankHistory.getResignationDate() != null)
				send("    &eResignation Date: &3" + shortDateFormat(rankHistory.getResignationDate()));
		}

		line();
		Nerd nerd = new NerdService().get(target.getUniqueId());
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
	void create(String player) {
		runCommand("blockcenter");
		Tasks.wait(5, () -> runCommand("npc create " + player));
	}

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

	@Permission("hoh.edit")
	@Path("removeRank <player> <current|former> <rank> <promotionDate> [resignationDate]")
	void removeRankConfirm(OfflinePlayer player, String when, Rank rank, LocalDate promotion, LocalDate resignation) {
		boolean current = "current".equalsIgnoreCase(when);

		HallOfHistory history = service.get(player.getUniqueId());
		MenuUtils.confirmMenu(player(), ConfirmationMenu.builder()
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
				}).build());
	}

	private String getRankCommandArgs(RankHistory rankHistory) {
		String command = (rankHistory.isCurrent() ? "Current" : "Former") + " " + rankHistory.getRank() + " ";
		if (rankHistory.getPromotionDate() != null)
			command += shortDateFormat(rankHistory.getPromotionDate()) + " ";
		if (rankHistory.getResignationDate() != null)
			command += shortDateFormat(rankHistory.getResignationDate());
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

	@Path("expand")
	@Permission("hoh.edit")
	void expand() {
		send(PREFIX + "Expanding HOH. &4&lDon't move!");
		int wait = 40;
		AtomicReference<Location> newLocation = new AtomicReference<>(player().getLocation());
		Tasks.wait(wait, () -> runCommand("/warp hallofhistory"));
		Tasks.wait(wait += 20, () -> newLocation.set(player().getLocation().add(16, 0, 0).clone()));
		Tasks.wait(wait += 3, () -> runCommand("/pos1"));
		Tasks.wait(wait += 3, () -> runCommand("/pos2"));
		Tasks.wait(wait += 3, () -> runCommand("/expand 7"));
		Tasks.wait(wait += 3, () -> runCommand("/expand 15 s"));
		Tasks.wait(wait += 3, () -> runCommand("/expand 15 n"));
		Tasks.wait(wait += 3, () -> runCommand("/expand 10 e"));
		Tasks.wait(wait += 3, () -> runCommand("/expandv 10"));
		Tasks.wait(wait += 3, () -> runCommand("/move 16 e"));
		Tasks.wait(wait += 20, () -> player().teleport(newLocation.get()));
		Tasks.wait(wait += 5, () -> runCommand("/hoh setwarp"));
		Tasks.wait(wait += 5, () -> runCommand("/schem load hoh-expansion"));
		Tasks.wait(wait += 20, () -> runCommand("/paste"));
		Tasks.wait(wait += 20, () -> runCommand("/contract 17"));
		Tasks.wait(wait += 3, () -> runCommand("/expand 1"));
		Tasks.wait(wait += 3, () -> runCommand("/contract 1"));
		Tasks.wait(wait += 3, () -> runCommand("/contract 12 d"));
		Tasks.wait(wait += 3, () -> runCommand("/contracth 5"));
		Tasks.wait(wait += 3, () -> runCommand("/contract 3 u"));
		Tasks.wait(wait += 3, () -> runCommand("/cut"));
		Tasks.wait(wait += 3, () -> runCommand("/expand -1"));
		Tasks.wait(wait += 3, () -> runCommand("/contract -1"));
		Tasks.wait(wait += 3, () -> runCommand("/stack 1"));
		Tasks.wait(wait += 3, () -> runCommand("/expand -15"));
		Tasks.wait(wait += 3, () -> runCommand("/contract -15"));
		Tasks.wait(wait += 3, () -> runCommand("/set stone_slab:8"));
		Tasks.wait(wait += 3, () -> runCommand("/sel"));
		send(PREFIX + "Expansion complete! Took &e" + (wait / 20) + " &3seconds");
	}

	@Path("about <about...>")
	void about(String about) {
		NerdService service = new NerdService();
		Nerd nerd = service.get(player());
		nerd.setAbout(stripColor(about));
		service.save(nerd);
		send(PREFIX + "Set your about to: &e" + nerd.getAbout());
	}

}
