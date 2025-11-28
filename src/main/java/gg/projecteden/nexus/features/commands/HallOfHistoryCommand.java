package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.features.commands.AgeCommand.ServerAge;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.costume.Costume;
import gg.projecteden.nexus.models.costume.CostumeUser;
import gg.projecteden.nexus.models.costume.CostumeUserService;
import gg.projecteden.nexus.models.hallofhistory.HallOfHistory;
import gg.projecteden.nexus.models.hallofhistory.HallOfHistory.RankHistory;
import gg.projecteden.nexus.models.hallofhistory.HallOfHistoryService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.ChunkLoader;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.CitizensUtils.NPCFinder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

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

@Aliases("hoh")
public class HallOfHistoryCommand extends CustomCommand {
	private final HallOfHistoryService service = new HallOfHistoryService();
	private final NerdService nerdService = new NerdService();

	public HallOfHistoryCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Teleport to the Hall of History")
	void warp() {
		runCommand("warp hallofhistory");
	}

	@Async
	@Path("view <player>")
	@Description("View a staff member's rank history")
	void view(OfflinePlayer target) {
		line(4);
		Nerd nerd = Nerd.of(target);
		send("&e&l" + nerd.getNickname());

		line();

		if (nerd.hasNickname())
			send("  &eIGN: &3" + nerd.getName());
		if (gg.projecteden.api.common.utils.Nullables.isNotNullOrEmpty(nerd.getPronouns()))
			send("  &ePronouns: &3" + String.join(", ", nerd.getPronouns().stream().map(Enum::toString).toList()));
		if (gg.projecteden.api.common.utils.Nullables.isNotNullOrEmpty(nerd.getFilteredPreferredNames()))
			send(plural("  &ePreferred name", nerd.getFilteredPreferredNames().size()) + ": &3" + String.join(", ", nerd.getFilteredPreferredNames()));

		line();

		HallOfHistory hallOfHistory = service.get(target.getUniqueId());
		for (RankHistory rankHistory : hallOfHistory.getRankHistory()) {
			JsonBuilder builder = new JsonBuilder();
			builder.next("  " + (rankHistory.isCurrent() ? "&2Current" : "&cFormer") + " " + rankHistory.getRank().getChatColor() + rankHistory.getRank().getName());
			if (isStaff())
				builder.next("  &c[x]").command("/hoh removerank " + target.getName() + " " + getRankCommandArgs(rankHistory));

			send(builder);
			send("    &ePromotion Date: &3" + TimeUtils.shortDateFormat(rankHistory.getPromotionDate()));
			if (rankHistory.getResignationDate() != null)
				send("    &eResignation Date: &3" + TimeUtils.shortDateFormat(rankHistory.getResignationDate()));
		}

		line();

		if (!Nullables.isNullOrEmpty(nerd.getAbout()))
			send("  &eAbout me: &3" + nerd.getAbout());
		if (nerd.isMeetMeVideo()) {
			line();
			String url = EdenSocialMediaSite.WEBSITE.getUrl() + "/meet/" + nerd.getName().toLowerCase();
			send(json("  &eMeet Me!&c " + url).url(url));
		}
	}

	@Async
	@Permission(Group.MODERATOR)
	@Path("addRank <player> <current|former> <rank> <promotionDate> [resignationDate]")
	@Description("Add a rank to a staff member's rank history")
	void addRank(OfflinePlayer target, String when, Rank rank, LocalDate promotion, LocalDate resignation) {
		boolean current = "current".equalsIgnoreCase(when);

		if (!current && resignation == null)
			error("Resignation date was not provided");

		service.edit(target, history -> history.getRankHistory().add(new RankHistory(rank, current, promotion, resignation)));
		send(PREFIX + "Successfully saved rank data for &e" + target.getName());
	}

	@Async
	@Permission(Group.MODERATOR)
	@Path("removeRank <player> <current|former> <rank> <promotionDate> [resignationDate]")
	@Description("Remove a rank from a staff member's rank history")
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
			command += TimeUtils.dateFormat(rankHistory.getPromotionDate()) + " ";
		if (rankHistory.getResignationDate() != null)
			command += TimeUtils.dateFormat(rankHistory.getResignationDate());
		return command.trim();
	}

	@Permission(Group.MODERATOR)
	@Path("clear <player>")
	@Description("Clear a player's rank history")
	void clear(OfflinePlayer player) {
		service.edit(player.getUniqueId(), history -> history.getRankHistory().clear());
		send(PREFIX + "Cleared all data for &e" + player.getName());
	}

	@Path("setwarp")
	@Description("Update the warp location")
	@Permission(Group.STAFF)
	void setWarp() {
		runCommand("blockcenter");
		Tasks.wait(3, () -> runCommand("warps set hallofhistory"));
	}

	@Async
	@Path("staffTime [page]")
	@Description("View how long each staff member was staff")
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
			String time = Timespan.ofSeconds(staffTimeMap.get(uuid) * (TickTime.DAY.get() / 20)).format();
			return json(index + " &e" + time + " &7- " + Nerd.of(uuid).getNameFormat());
		};

		new Paginator<UUID>()
			.values(Utils.sortByValueReverse(staffTimeMap).keySet())
			.formatter(formatter)
			.command("/hoh staffTime")
			.page(page)
			.send();
	}

	@Path("promotionTimes [page]")
	@Description("View how long it took all staff members to be promoted")
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
			String time = Timespan.ofSeconds(promotionTimeMap.get(uuid) * (TickTime.DAY.get() / 20)).format();
			return json(index + " &e" + Nickname.of(uuid) + " &7- " + time);
		};

		new Paginator<UUID>()
			.values(Utils.sortByValue(promotionTimeMap).keySet())
			.formatter(formatter)
			.command("/hoh promotionTimes")
			.page(page)
			.send();
	}

	@Path("npcs update")
	@Permission(Group.ADMIN)
	@Description("Update all Hall of History NPCs")
	void npcs_update() {
		var regionName = "hallofhistory";
		var world = Bukkit.getWorld("events");

		ChunkLoader.forceLoad(world, regionName);

		Tasks.wait(40, () -> {
			List<NPC> npcs = NPCFinder.builder()
				.world(world)
				.region(regionName)
				.type(EntityType.PLAYER)
				.predicate(npc -> npc.data().get(NPC.Metadata.NAMEPLATE_VISIBLE))
				.find();

			var wait = 0;
			for (NPC npc : npcs) {
				Tasks.wait(wait += 20, () -> {
					if (!isOnline())
						return;

					String name = CitizensUtils.stripColor(npc.getName());

					Nerd nerd = Nerd.of(CitizensUtils.stripColor(name));
					if (nerd.getOfflinePlayer().hasPlayedBefore())
						CitizensUtils.updateNameAndSkin(npc, nerd);
					else
						CitizensUtils.updateNameAndSkin(npc, name);

					var equipment = npc.getTraitNullable(Equipment.class);
					if (equipment != null) {
						CostumeUser user = new CostumeUserService().get(nerd);
						for (EquipmentSlot value : EquipmentSlot.values())
							equipment.set(value, new ItemStack(Material.AIR));

						user.getActiveDisplayCostumes().forEach((costumeType, costume) ->
							equipment.set(costumeType.getNpcSlot(), user.getCostumeItem(Costume.of(costume))));
					}

					send(PREFIX + "Updated " + nerd.getColoredName());
				});
			}

			Tasks.wait(wait + 100, () -> {
				ChunkLoader.forceLoad(world, regionName, false);
				send(PREFIX + "Done");
			});
		});
	}

}
