package me.pugabyte.nexus.features.listeners;

import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.mcMMO;
import com.vexsoftware.votifier.model.VotifierEvent;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.commands.HoursCommand.HoursTopArguments;
import me.pugabyte.nexus.features.store.Package;
import me.pugabyte.nexus.features.votes.EndOfMonth.TopVoterData;
import me.pugabyte.nexus.framework.annotations.Environments;
import me.pugabyte.nexus.models.banker.Banker;
import me.pugabyte.nexus.models.banker.BankerService;
import me.pugabyte.nexus.models.cooldown.CooldownService;
import me.pugabyte.nexus.models.hours.Hours;
import me.pugabyte.nexus.models.hours.HoursService;
import me.pugabyte.nexus.models.hours.HoursService.PageResult;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.purchase.PurchaseService;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.utils.CitizensUtils;
import me.pugabyte.nexus.utils.Env;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils.Timespan;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
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
								hours -> Timespan.of(service.<Hours>get(hours.getUuid()).getMonthly()).format(),
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
				return new BankerService().<Banker>getAll().stream()
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
		RECENT_DONATOR(2772, 2773, 2774) {
			@Override
			Map<UUID, String> getTop() {
				return new PurchaseService().getRecent(3).stream()
						.collect(Collectors.toMap(
								purchase -> PlayerUtils.getPlayer(purchase.getUuid()).getUniqueId(),
								purchase -> {
									String name = "";
									Package purchasedPackage = Package.getPackage(purchase.getPackageId());
									if (purchasedPackage != null) {
										String category = purchasedPackage.getCategory();
										if (!isNullOrEmpty(category))
											name += category + " - ";
									}
									name += purchase.getPackageName().split("\\[")[0].trim();
									return name + " (" + NumberFormat.getCurrencyInstance().format(purchase.getPrice()) + ")";
								},
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
				Map<UUID, String> top = getTop();
				if (top.size() != 3) {
					Nexus.warn(name() + " leaderboard top query did not return 3 results (" + top.size() + ")");
					return;
				}

				if (!new CooldownService().check(Nexus.getUUID0(), "leaderboards_" + name(), Time.MINUTE.x(5)))
					return;

				Tasks.sync(() -> {
					AtomicInteger i = new AtomicInteger(0);
					top.entrySet().iterator().forEachRemaining(entry -> {
						Nerd nerd = Nerd.of(entry.getKey());
						CitizensUtils.updateSkin(ids[i.get()], nerd.getOfflinePlayer().getName());
						CitizensUtils.updateName(ids[i.get()], colorize("&e" + entry.getValue()));
						runCommandAsConsole("hd setline leaderboards_" + name().toLowerCase() + "_" + i.incrementAndGet() + " 1 " + decolorize(colorize(nerd.getNicknameFormat())));
					});
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
