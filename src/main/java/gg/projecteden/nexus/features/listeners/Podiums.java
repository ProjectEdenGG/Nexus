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
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.hours.HoursService.HoursTopArguments;
import gg.projecteden.nexus.models.hours.HoursService.PageResult;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.models.store.Contributor;
import gg.projecteden.nexus.models.store.Contributor.Purchase;
import gg.projecteden.nexus.models.store.ContributorService;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.Env;
import gg.projecteden.utils.TimeUtils.TickTime;
import gg.projecteden.utils.TimeUtils.Timespan;
import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.PlayerUtils.runCommandAsConsole;
import static gg.projecteden.nexus.utils.StringUtils.colorize;
import static gg.projecteden.nexus.utils.StringUtils.decolorize;
import static java.util.stream.Collectors.toList;

@NoArgsConstructor
@Environments(Env.PROD)
public class Podiums implements Listener {

	public enum Podium {
		PLAYTIME_TOTAL(2709, 2708, 2707) {
			@Override
			Map<UUID, String> getTop() {
				return new HoursService().getPage().subList(0, 3).stream()
						.collect(Collectors.toMap(
								PageResult::getUuid,
								hours -> Timespan.of(hours.getTotal()).format(),
								(h1, h2) -> h1, LinkedHashMap::new
						));
			}
		},
		PLAYTIME_MONTHLY(2712, 2711, 2710) {
			@Override
			Map<UUID, String> getTop() {
				HoursService service = new HoursService();
				return service.getPage(new HoursTopArguments("monthly")).subList(0, 3).stream()
						.collect(Collectors.toMap(
								PageResult::getUuid,
								hours -> Timespan.of(service.get(hours.getUuid()).getMonthly()).format(),
								(h1, h2) -> h1, LinkedHashMap::new
						));
			}
		},
		VOTES(2700, 2699, 2698) {
			@Override
			Map<UUID, String> getTop() {
				return new TopVoterData(YearMonth.now()).getTopVoters().subList(0, 3).stream()
						.collect(Collectors.toMap(
								topVoter -> topVoter.getVoter().getUuid(),
								topVoter -> String.valueOf(topVoter.getCount()),
								(h1, h2) -> h1, LinkedHashMap::new
						));
			}
		},
		BALANCE(2703, 2702, 2701) {
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
		MCMMO(2706, 2705, 2704) {
			@Override
			Map<UUID, String> getTop() {
				return mcMMO.getDatabaseManager().readLeaderboard(null, 1, 3).subList(0, 3).stream()
						.collect(Collectors.toMap(
								playerStat -> PlayerUtils.getPlayer(playerStat.name).getUniqueId(),
								playerStat -> String.valueOf(playerStat.statVal),
								(h1, h2) -> h1, LinkedHashMap::new
						));
			}
		},
		RECENT_PURCHASES(2772, 2773, 2774) {
			@Override
			Map<UUID, String> getTop() {
				Iterator<Purchase> iterator = new ContributorService().getRecent(50).iterator();

				Map<UUID, Map<String, Double>> recent = new LinkedHashMap<>();
				UUID last = null;
				while (iterator.hasNext()) {
					Purchase purchase = iterator.next();
					if (purchase.getPrice() == 0)
						continue;
					UUID key = purchase.getPurchaserUuid();

					if (recent.size() == 3 && last != key)
						break;

					recent.computeIfAbsent(key, $ -> new HashMap<>()).put(purchase.getTransactionId(), purchase.getPrice());

					last = purchase.getPurchaserUuid();
				}

				return new LinkedHashMap<>() {{
					recent.forEach((uuid, txns) -> {
						final double sum = txns.values().stream().mapToDouble(Double::valueOf).sum();
						put(uuid, StringUtils.prettyMoney(sum));
					});
				}};
			}
		},
		TOP_CONTRIBUTORS(3835, 3837, 3836) {
			@Override
			Map<UUID, String> getTop() {
				return new ContributorService().getTop(3).stream()
						.collect(Collectors.toMap(
								Contributor::getUuid,
								Contributor::getSumFormatted,
								(h1, h2) -> h1, LinkedHashMap::new
						));
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
			Tasks.async(() -> {
				Map<UUID, String> top = validateGetTop();
				if (top == null)
					return;

				if (!new CooldownService().check(StringUtils.getUUID0(), "podiums_" + name(), TickTime.MINUTE.x(5)))
					return;

				updateActual();
			});
		}

		@Nullable
		private Map<UUID, String> validateGetTop() {
			Map<UUID, String> top = getTop();
			if (top.size() != 3) {
				if (this != VOTES || LocalDate.now().getDayOfMonth() != 1) // Ignore votes for the first day of the month
					Nexus.warn(name() + " podium query did not return 3 results (" + top.size() + ")");
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
			});
		}
	}

	static {
		if (Nexus.getEnv() == Env.PROD)
			for (Podium value : Podium.values())
				Tasks.repeat(10, TickTime.HOUR, value::update);
	}

	@EventHandler
	public void onVote(VotifierEvent event) {
		Tasks.wait(1, Podium.VOTES::update);
	}

	@EventHandler
	public void onMcMMOLevelUp(McMMOPlayerLevelUpEvent event) {
		Podium.MCMMO.update();
	}
}
