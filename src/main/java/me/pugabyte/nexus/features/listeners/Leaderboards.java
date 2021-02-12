package me.pugabyte.nexus.features.listeners;

import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.mcMMO;
import com.vexsoftware.votifier.model.VotifierEvent;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.store.Package;
import me.pugabyte.nexus.features.votes.EndOfMonth.TopVoterData;
import me.pugabyte.nexus.models.cooldown.CooldownService;
import me.pugabyte.nexus.models.hours.HoursService;
import me.pugabyte.nexus.models.hours.HoursService.PageResult;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.purchase.PurchaseService;
import me.pugabyte.nexus.utils.CitizensUtils;
import me.pugabyte.nexus.utils.Env;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils.TimespanFormatter;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static me.pugabyte.nexus.utils.PlayerUtils.runCommandAsConsole;
import static me.pugabyte.nexus.utils.StringUtils.colorize;
import static me.pugabyte.nexus.utils.StringUtils.decolorize;
import static me.pugabyte.nexus.utils.StringUtils.prettyMoney;

@NoArgsConstructor
public class Leaderboards implements Listener {

	public enum Leaderboard {
		PLAYTIME_TOTAL(2709, 2708, 2707) {
			@Override
			Map<UUID, String> getTop() {
				return new HoursService().getPage().subList(0, 3).stream()
						.collect(Collectors.toMap(
								PageResult::getUuid,
								hours -> TimespanFormatter.of(hours.getTotal()).format(),
								(h1, h2) -> h1, LinkedHashMap::new
						));
			}
		},
//		PLAYTIME_MONTHLY(2712, 2711, 2710) {
//			@Override
//			Map<UUID, String> getTop() {
//				return new HoursService().getPage(1).subList(0, 3).stream()
//						.collect(Collectors.toMap(
//								hours -> UUID.fromString(hours.getUuid()),
//								hours -> StringUtils.timespanFormat(hours.getMonthly()),
//								(h1, h2) -> h1, LinkedHashMap::new
//						));
//			}
//		},
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
				return new HoursService().getActivePlayers().stream()
						.collect(Collectors.toMap(OfflinePlayer::getUniqueId, player -> Nexus.getEcon().getBalance(PlayerUtils.getPlayer(player.getUniqueId()))))
						.entrySet()
						.stream()
						.sorted(Entry.<UUID, Double>comparingByValue().reversed())
						.limit(3)
						.collect(Collectors.toMap(
								Entry::getKey,
								entry -> prettyMoney(entry.getValue()),
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
				if (!new CooldownService().check(Nexus.getUUID0(), "leaderboards_" + name(), Time.MINUTE.x(5)))
					return;

				Map<UUID, String> top = getTop();
				if (top.size() != 3) {
					Nexus.warn(name() + " leaderboard top query did not return 3 results (" + top.size() + ")");
					return;
				}

				Tasks.sync(() -> {
					AtomicInteger i = new AtomicInteger(0);
					top.entrySet().iterator().forEachRemaining(entry -> {
						Nerd nerd = new Nerd(entry.getKey());
						CitizensUtils.updateSkin(ids[i.get()], nerd.getName());
						CitizensUtils.updateName(ids[i.get()], colorize("&e" + entry.getValue()));
						runCommandAsConsole("hd setline leaderboards_" + name().toLowerCase() + "_" + i.incrementAndGet() + " 1 " + decolorize(colorize(nerd.getRankFormat())));
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
	public void onMoneyChange(UserBalanceUpdateEvent event) {
		if (event.getNewBalance().doubleValue() >= 1000000)
			Leaderboard.BALANCE.update();
	}

	@EventHandler
	public void onMcMMOLevelUp(McMMOPlayerLevelUpEvent event) {
		Leaderboard.MCMMO.update();
	}
}
