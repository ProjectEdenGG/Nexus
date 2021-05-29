package me.pugabyte.nexus.features.listeners;

import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.mcMMO;
import com.vexsoftware.votifier.model.VotifierEvent;
import eden.annotations.Environments;
import eden.utils.Env;
import eden.utils.TimeUtils.Time;
import eden.utils.TimeUtils.Timespan;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.store.Package;
import me.pugabyte.nexus.features.store.annotations.Category.StoreCategory;
import me.pugabyte.nexus.features.votes.EndOfMonth.TopVoterData;
import me.pugabyte.nexus.models.banker.Banker;
import me.pugabyte.nexus.models.banker.BankerService;
import me.pugabyte.nexus.models.contributor.Contributor;
import me.pugabyte.nexus.models.contributor.Contributor.Purchase;
import me.pugabyte.nexus.models.contributor.ContributorService;
import me.pugabyte.nexus.models.cooldown.CooldownService;
import me.pugabyte.nexus.models.hours.HoursService;
import me.pugabyte.nexus.models.hours.HoursService.HoursTopArguments;
import me.pugabyte.nexus.models.hours.HoursService.PageResult;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.utils.CitizensUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static eden.utils.StringUtils.camelCase;
import static java.util.stream.Collectors.toList;
import static me.pugabyte.nexus.utils.PlayerUtils.runCommandAsConsole;
import static me.pugabyte.nexus.utils.StringUtils.colorize;
import static me.pugabyte.nexus.utils.StringUtils.decolorize;

@NoArgsConstructor
@Environments(Env.PROD)
public class Leaderboards implements Listener {

	public enum Leaderboard {
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
				return new TopVoterData(LocalDateTime.now().getMonth()).getTopVoters().subList(0, 3).stream()
						.collect(Collectors.toMap(
								topVoter -> UUID.fromString(topVoter.getUuid()),
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
				Iterator<Purchase> recent = new ContributorService().getRecent(20).iterator();
				Map<UUID, String> top = new LinkedHashMap<>();
				while (top.size() != 3 && recent.hasNext()) {
					Purchase purchase = recent.next();

					String name = "";
					Package purchasedPackage = Package.getPackage(purchase.getPackageId());
					if (purchasedPackage != null) {
						StoreCategory category = purchasedPackage.getCategory();
						if (List.of(StoreCategory.PETS, StoreCategory.DISGUISES).contains(category))
							name += camelCase(category) + " - ";
					}

					name += purchase.getPackageName().split("\\[")[0].trim();
					name += " (" + NumberFormat.getCurrencyInstance().format(purchase.getPackagePrice()) + ")";
					top.put(purchase.getUuid(), name);
				}

				return top;
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

		int[] ids;

		Leaderboard(int... ids) {
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

				if (!new CooldownService().check(StringUtils.getUUID0(), "leaderboards_" + name(), Time.MINUTE.x(5)))
					return;

				updateActual();
			});
		}

		@Nullable
		private Map<UUID, String> validateGetTop() {
			Map<UUID, String> top = getTop();
			if (top.size() != 3) {
				if (this != VOTES || LocalDate.now().getDayOfMonth() != 1) // Ignore votes for the first day of the month
					Nexus.warn(name() + " leaderboard top query did not return 3 results (" + top.size() + ")");
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
					CitizensUtils.updateSkin(ids[i.get()], nerd.getOfflinePlayer().getName());
					CitizensUtils.updateName(ids[i.get()], colorize("&e" + entry.getValue()));
					runCommandAsConsole("hd setline leaderboards_" + name().toLowerCase() + "_" + i.incrementAndGet() + " 1 " + decolorize(colorize(nerd.getColoredName())));
				});
			});
		}
	}

	static {
		if (Nexus.getEnv() == Env.PROD)
			for (Leaderboard value : Leaderboard.values())
				Tasks.repeat(10, Time.HOUR, value::update);
	}

	@EventHandler
	public void onVote(VotifierEvent event) {
		Tasks.wait(1, Leaderboard.VOTES::update);
	}

	@EventHandler
	public void onMcMMOLevelUp(McMMOPlayerLevelUpEvent event) {
		Leaderboard.MCMMO.update();
	}
}
