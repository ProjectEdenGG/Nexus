package gg.projecteden.nexus.features.listeners;

import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.mcMMO;
import com.vexsoftware.votifier.model.VotifierEvent;
import gg.projecteden.annotations.Environments;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.votes.EndOfMonth.TopVoterData;
import gg.projecteden.nexus.models.banker.Banker;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.dailyreward.DailyRewardUser;
import gg.projecteden.nexus.models.dailyreward.DailyRewardUserService;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.hours.HoursService.HoursTopArguments;
import gg.projecteden.nexus.models.hours.HoursService.PageResult;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.models.store.Contributor;
import gg.projecteden.nexus.models.store.ContributorService;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.utils.EnumUtils;
import gg.projecteden.utils.Env;
import gg.projecteden.utils.TimeUtils.TickTime;
import gg.projecteden.utils.TimeUtils.Timespan;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.PlayerUtils.runCommandAsConsole;
import static gg.projecteden.nexus.utils.StringUtils.colorize;
import static gg.projecteden.nexus.utils.StringUtils.decolorize;
import static gg.projecteden.utils.UUIDUtils.UUID0;
import static java.util.stream.Collectors.toList;

@NoArgsConstructor
@Environments(Env.PROD)
public class Podiums implements Listener {

	static {
		Nexus.getCron().schedule("0 * * * *", () -> {
			int wait = 0;
			for (Podium podium : Podium.values())
				Tasks.wait(wait += TickTime.SECOND.x(10), podium::update);
		});
	}

	@EventHandler
	public void onVote(VotifierEvent event) {
		Tasks.wait(1, Podium.VOTES::update);
	}

	@EventHandler
	public void onMcMMOLevelUp(McMMOPlayerLevelUpEvent event) {
		Podium.MCMMO.update();
	}

	public enum Podium {
		PLAYTIME_TOTAL(4639, 4640, 4641) {
			@Override
			Map<UUID, String> getTop() {
				return new HoursService().getPage().subList(0, 3).stream()
					.collect(Collectors.toMap(
						PageResult::getUuid,
						hours -> Timespan.ofSeconds(hours.getTotal()).format(),
						(h1, h2) -> h1, LinkedHashMap::new
					));
			}
		},
		PLAYTIME_MONTHLY(4636, 4637, 4638) {
			@Override
			Map<UUID, String> getTop() {
				HoursService service = new HoursService();
				return service.getPage(new HoursTopArguments("monthly")).subList(0, 3).stream()
					.collect(Collectors.toMap(
						PageResult::getUuid,
						hours -> Timespan.ofSeconds(service.get(hours.getUuid()).getMonthly()).format(),
						(h1, h2) -> h1, LinkedHashMap::new
					));
			}
		},
		VOTES(4651, 4652, 4653) {
			@Override
			Map<UUID, String> getTop() {
				return new TopVoterData(YearMonth.now()).getTopVoters().subList(0, 3).stream()
					.collect(Collectors.toMap(
						topVoter -> topVoter.getVoter().getUuid(),
						topVoter -> NumberFormat.getInstance().format(topVoter.getCount()),
						(h1, h2) -> h1, LinkedHashMap::new
					));
			}
		},
		BALANCE(4654, 4655, 4656) {
			@Override
			Map<UUID, String> getTop() {
				return new BankerService().getAll().stream()
					.sorted(Comparator.comparing(banker -> banker.getBalance(ShopGroup.SURVIVAL), Comparator.reverseOrder()))
					.collect(toList())
					.subList(0, 3).stream()
					.collect(Collectors.toMap(
						Banker::getUuid,
						banker -> banker.getBalanceFormatted(ShopGroup.SURVIVAL),
						(h1, h2) -> h1, LinkedHashMap::new
					));
			}
		},
		MCMMO(4648, 4649, 4650) {
			@Override
			Map<UUID, String> getTop() {
				return mcMMO.getDatabaseManager().readLeaderboard(null, 1, 3).subList(0, 3).stream()
					.collect(Collectors.toMap(
						playerStat -> PlayerUtils.getPlayer(playerStat.name).getUniqueId(),
						playerStat -> NumberFormat.getInstance().format(playerStat.statVal),
						(h1, h2) -> h1, LinkedHashMap::new
					));
			}
		},
		BLOCKS_BROKEN(4628, 4629, 4630) {
			@Override
			Map<UUID, String> getTop() {
				final Map<UUID, Integer> blocksBroken = new HashMap<>();
				for (UUID uuid : new ArrayList<>(new HoursService().getActivePlayers().subList(0, 30))) {
					final OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
					int total = 0;
					for (Material material : Material.values())
						if (material.isBlock())
							total += player.getStatistic(Statistic.MINE_BLOCK, material);

					blocksBroken.put(uuid, total);
				}

				return new ArrayList<>(Utils.sortByValueReverse(blocksBroken).keySet()).subList(0, 3).stream()
					.collect(Collectors.toMap(
						uuid -> uuid,
						uuid -> NumberFormat.getInstance().format(blocksBroken.get(uuid)),
						(h1, h2) -> h1, LinkedHashMap::new
					));
			}
		},
		MOBS_KILLED(4625, 4626, 4627) {
			@Override
			Map<UUID, String> getTop() {
				final Map<UUID, Integer> entitiesKilled = new HashMap<>();
				for (UUID uuid : new ArrayList<>(new HoursService().getActivePlayers().subList(0, 50))) {
					final OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
					int total = 0;
					for (EntityType entityType : EnumUtils.valuesExcept(EntityType.class, EntityType.PLAYER, EntityType.ARMOR_STAND))
						if (entityType.isAlive())
							total += player.getStatistic(Statistic.KILL_ENTITY, entityType);

					entitiesKilled.put(uuid, total);
				}

				return new ArrayList<>(Utils.sortByValueReverse(entitiesKilled).keySet()).subList(0, 3).stream()
					.collect(Collectors.toMap(
						uuid -> uuid,
						uuid -> NumberFormat.getInstance().format(entitiesKilled.get(uuid)),
						(h1, h2) -> h1, LinkedHashMap::new
					));
			}
		},
		DAILY_LOGIN_STREAK(4632, 4633, 4634) {
			@Override
			Map<UUID, String> getTop() {
				final DailyRewardUserService service = new DailyRewardUserService();
				return service.getAll().stream()
					.filter(user -> user.getCurrentStreak().getStreak() > 0)
					.sorted(Comparator.<DailyRewardUser>comparingInt(user -> user.getCurrentStreak().getStreak()).reversed())
					.toList()
					.subList(0, 3)
					.stream()
					.collect(Collectors.toMap(
						DailyRewardUser::getUuid,
						user -> NumberFormat.getInstance().format(user.getCurrentStreak().getStreak()),
						(h1, h2) -> h1, LinkedHashMap::new
					));
			}
		},
		TOP_MONTHLY_CONTRIBUTORS(4642, 4643, 4644) {
			@Override
			Map<UUID, String> getTop() {
				return new ContributorService().getMonthlyTop(YearMonth.now(), 3).stream()
					.collect(Collectors.toMap(
						Contributor::getUuid,
						contributor -> contributor.getMonthlySumFormatted(YearMonth.now()),
						(h1, h2) -> h1, LinkedHashMap::new
					));
			}

			@Nullable
			@Override
			Map<UUID, String> validateGetTop() {
				Map<UUID, String> top = getTop();
				if (top.size() != 3)
					return null;
				return top;
			}
		},
		TOP_CONTRIBUTORS(4645, 4646, 4647) {
			@Override
			Map<UUID, String> getTop() {
				return new ContributorService().getTop(3).stream()
					.collect(Collectors.toMap(
						Contributor::getUuid,
						Contributor::getSumFormatted,
						(h1, h2) -> h1, LinkedHashMap::new
					));
			}
		},
		GALLERY_TOP_MONTHLY_CONTRIBUTORS(4537, 4538, 4539) {
			@Override
			Map<UUID, String> getTop() {
				return TOP_MONTHLY_CONTRIBUTORS.getTop();
			}

			@Nullable
			@Override
			Map<UUID, String> validateGetTop() {
				return TOP_MONTHLY_CONTRIBUTORS.validateGetTop();
			}
		},
		GALLERY_TOP_CONTRIBUTORS(4540, 4541, 4542) {
			@Override
			Map<UUID, String> getTop() {
				return TOP_CONTRIBUTORS.getTop();
			}
		};

		private final int[] ids;

		Podium(int... ids) {
			this.ids = ids;
			if (ids.length != 3)
				Nexus.warn(name() + " did not define 3 NPC ids (" + ids.length + ")");
		}

		abstract Map<UUID, String> getTop();

		public void update() {
			// Ignore votes for the first day of the month
			if (this == VOTES && LocalDate.now().getDayOfMonth() == 1)
				return;

			Tasks.async(() -> {
				if (!new CooldownService().check(UUID0, "podiums_" + name(), TickTime.MINUTE.x(5)))
					return;

				updateActual();
			});
		}

		@Nullable
		Map<UUID, String> validateGetTop() {
			Map<UUID, String> top = getTop();
			if (top.size() != 3) {
				if (List.of(GALLERY_TOP_MONTHLY_CONTRIBUTORS, TOP_MONTHLY_CONTRIBUTORS).contains(this))
					return null;

				return null;
			}
			return top;
		}

		public void updateActual() {
			Map<UUID, String> top = validateGetTop();
			if (top == null)
				return;

			Tasks.sync(() -> {
				AtomicInteger i = new AtomicInteger(0);
				top.entrySet().iterator().forEachRemaining(entry -> {
					Nerd nerd = Nerd.of(entry.getKey());
					CitizensUtils.updateName(ids[i.get()], colorize("&e" + entry.getValue()));
					CitizensUtils.updateSkin(ids[i.get()], nerd.getName());
					runCommandAsConsole("hd setline podiums_" + name().toLowerCase() + "_" + i.incrementAndGet() + " 1 " + decolorize(colorize(nerd.getColoredName())));
				});
				Tasks.wait(3, () -> runCommandAsConsole("hd reload"));
			});
		}
	}

}
