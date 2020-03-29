package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.Rank;
import me.pugabyte.bncore.models.hallofhistory.HallOfHistory;
import me.pugabyte.bncore.models.hallofhistory.HallOfHistoryService;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.models.nerd.NerdService;
import me.pugabyte.bncore.models.warps.Warp;
import me.pugabyte.bncore.models.warps.WarpService;
import me.pugabyte.bncore.models.warps.WarpType;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.OfflinePlayer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Aliases("hoh")
public class HallOfHistoryCommand extends CustomCommand {

	WarpService warpService = new WarpService();
	NerdService nerdService = new NerdService();
	HallOfHistoryService hohService = new HallOfHistoryService();

	public HallOfHistoryCommand(CommandEvent event) {
		super(event);
	}

	@Path()
	void warp() {
		Warp warp = warpService.get("hallofhistory", WarpType.NORMAL);
		if (warp == null) error("The HOH warp is not set.");
		warp.teleport(player());
	}

	@Path("view <player>")
	void view(OfflinePlayer target) {
		send("&e&l" + target.getName());
		line();
		for (HallOfHistory hoh : hohService.getHistory(target.getUniqueId())) {
			JsonBuilder builder = new JsonBuilder();
			builder.next("  " + (hoh.isCurrent() ? "&2Current" : "&cFormer") + " " + Rank.valueOf(hoh.getRank()).getPrefix().replaceAll("&[ol]", ""));
			if (player().hasPermission("hoh.edit"))
				builder.next("  &c[x]").command("hoh removerankconfirm " + target.getName() + " " + (hoh.isCurrent() ? "current " : "former ") + hoh.getRank());
			send(builder);
			send("   &3Promotion Date: &3" + StringUtils.shortDateFormat(hoh.getPromotionDate()));
			send("   &3Resignation Date: &3" + StringUtils.shortDateFormat(hoh.getResignationDate()));
		}
		line();
		Nerd nerd = nerdService.get(target.getUniqueId());
		send(" &eAbout me: &3" + nerd.getAbout());
	}

	@Permission("hoh.edit")
	@Path("create <player>")
	void create(String player) {
		runCommand("blockcenter");
		Tasks.wait(5, () -> runCommand("npc create " + player));
	}

	@Permission("hoh.edit")
	@Path("addrank <player> <current|former> <rank> [p:promotionDate] [r:resignationDate]")
	void addRank(OfflinePlayer target, String currentParam, String rankParam) {
		boolean current;
		Rank rank;
		LocalDate promotion = null;
		LocalDate resignation = null;
		if (currentParam.equalsIgnoreCase("current"))
			current = true;
		else if (currentParam.equalsIgnoreCase("former") || currentParam.equalsIgnoreCase("past"))
			current = false;
		else {
			error("Argument 2 must be \"current\" or \"former\"");
			return;
		}
		switch (rankParam.toLowerCase()) {
			case "owner":
				if (!target.getName().equalsIgnoreCase("pugabyte"))
					error("You cannot set that player to owner.");
				rank = Rank.OWNER;
				break;
			case "admin":
			case "administrator":
				rank = Rank.ADMIN;
				break;
			case "op":
			case "operator":
				rank = Rank.OPERATOR;
				break;
			case "mod":
			case "moderator":
				rank = Rank.MODERATOR;
				break;
			case "arch":
			case "architect":
				rank = Rank.ARCHITECT;
				break;
			case "builder":
				rank = Rank.BUILDER;
				break;
			default:
				error("Unknown rank");
				return;
		}
		try {
			if (arg(3) != null && (arg(3).startsWith("p:") || arg(3).startsWith("r:"))) {
				String date = StringUtils.right(arg(3), 10);
				if (arg(3).startsWith("p:")) {
					promotion = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
				} else {
					resignation = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
				}
			}
			if (arg(4) != null && (arg(4).startsWith("p:") || arg(4).startsWith("r:"))) {
				String date = StringUtils.right(arg(4), 10);
				if (arg(3).startsWith("p:")) {
					promotion = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
				} else {
					resignation = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
				}
			}
		} catch (DateTimeParseException ex) {
			error("Invalid date format. Correct format: &e(MM/dd/yyyy)");
		}
		HallOfHistory history = new HallOfHistory(target.getUniqueId().toString(), rank.name(), current, promotion, resignation);
		hohService.save(history);
		send(PREFIX + "Successfully saved data for &e" + target.getName());
	}

	@Permission("hoh.edit")
	@Path("removerank <player> <current|formal> <rank>")
	void removeRank(OfflinePlayer player, String current, String rank) {
		if (current.equalsIgnoreCase("current") || current.equalsIgnoreCase("former"))
			error("Argument 2 must be either \"current\" or \"former\"");
		try {
			Rank.valueOf(rank.toUpperCase());
		} catch (Exception ex) {
			error("Invalid rank");
		}
		removeRankConfirm(player, current, rank);
	}

	@Permission("hoh.edit")
	@Path("removerankConfirm <player> <current|formal> <rank>")
	void removeRankConfirm(OfflinePlayer player, String current, String rank) {
		boolean bol;
		if (current.equalsIgnoreCase("current"))
			bol = true;
		else bol = false;
		MenuUtils.confirmMenu(player(), MenuUtils.ConfirmationMenu.builder()
				.title("Remove rank from " + player.getName() + "?")
				.onConfirm((item) -> {
					hohService.delete(player.getUniqueId(), rank, bol);
					send(PREFIX + "Removed the rank from &e" + player.getName());
				}).build());
	}

	@Permission("hoh.edit")
	@Path("clear <player>")
	void clear(OfflinePlayer player) {
		for (HallOfHistory hoh : hohService.getHistory(player.getUniqueId())) {
			hohService.delete(player.getUniqueId(), hoh.getRank(), hoh.isCurrent());
		}
		send(PREFIX + "Cleared all data for &e" + player.getName());
	}

	@Path("setwarp")
	@Permission("hoh.edit")
	void setWarp() {
		runCommand("blockcenter");
		Tasks.wait(3, () -> {
			Warp warp = warpService.get("hallofhistory", WarpType.NORMAL);
			if (warp == null) {
				warp = new Warp("hallofhistory", player().getLocation(), WarpType.NORMAL.name());
			} else
				warp.setLocation(player().getLocation());
			warpService.save(warp);
			send(PREFIX + "Set the HOH warp to your current location");
		});
	}

	@Path("expand")
	@Permission("hoh.edit")
	void expand() {
		send(PREFIX + "Expanding HOH. &c&lDon't move!");
		int wait = 40;
		Tasks.wait(wait, () -> {
			Warp warp = warpService.get("hallofhistory", WarpType.NORMAL);
			warp.teleport(player());
			warp.setLocation(player().getLocation().clone().add(16, 0, 0));
			warpService.save(warp);
		});
		Tasks.wait(wait += 5, () -> runCommand("/pos1"));
		Tasks.wait(wait += 3, () -> runCommand("/pos2"));
		Tasks.wait(wait += 3, () -> runCommand("/expand 7"));
		Tasks.wait(wait += 3, () -> runCommand("/expand 15 s"));
		Tasks.wait(wait += 3, () -> runCommand("/expand 15 n"));
		Tasks.wait(wait += 3, () -> runCommand("/expand 10 e"));
		Tasks.wait(wait += 3, () -> runCommand("/expandv 10"));
		Tasks.wait(wait += 30, () -> runCommand("/move 16 e"));
		Tasks.wait(wait += 40, () -> {
			Warp warp = warpService.get("hallofhistory", WarpType.NORMAL);
			warp.teleport(player());
		});
		Tasks.wait(wait += 5, () -> runCommand("/schem load hoh-expansion"));
		Tasks.wait(wait += 20, () -> runCommand("/paste"));
		Tasks.wait(wait += 20, () -> runCommand("/contract 17"));
		Tasks.wait(wait += 3, () -> runCommand("/expand 1"));
		Tasks.wait(wait += 3, () -> runCommand("/contract 1"));
		Tasks.wait(wait += 3, () -> runCommand("/contract 12 d"));
		Tasks.wait(wait += 3, () -> runCommand("/contracth 5"));
		Tasks.wait(wait += 30, () -> runCommand("/contract 3 u"));
		Tasks.wait(wait += 3, () -> runCommand("/cut"));
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
		Nerd nerd = nerdService.get(player());
		nerd.setAbout(about);
		nerdService.save(nerd);
		send(PREFIX + "Set your about to: &e" + about);
	}

}
