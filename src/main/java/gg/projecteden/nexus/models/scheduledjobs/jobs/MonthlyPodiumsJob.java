package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.Schedule;
import gg.projecteden.nexus.models.badge.BadgeUser;
import gg.projecteden.nexus.models.badge.BadgeUserService;
import gg.projecteden.nexus.models.monthlypodium.MonthlyPodiumUser;
import gg.projecteden.nexus.models.monthlypodium.MonthlyPodiumUser.MonthlyPodiumData;
import gg.projecteden.nexus.models.monthlypodium.MonthlyPodiumUser.MonthlyPodiumType;
import gg.projecteden.nexus.models.monthlypodium.MonthlyPodiumUser.PodiumSpot;
import gg.projecteden.nexus.models.monthlypodium.MonthlyPodiumUserService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Async
@Schedule("0 0 1 * *")
public class MonthlyPodiumsJob extends AbstractJob {

	@Override
	protected CompletableFuture<JobStatus> run() {
		final BadgeUserService badgeService = new BadgeUserService();
		final MonthlyPodiumUserService podiumUserService = new MonthlyPodiumUserService();

		for (BadgeUser uuid : badgeService.getAll())
			for (PodiumSpot spot : PodiumSpot.values())
				if (uuid.owns(spot.getBadge()))
					badgeService.edit(uuid, badgeUser -> badgeUser.take(spot.getBadge()));

		podiumUserService.deleteAllSync();

		for (MonthlyPodiumType type : MonthlyPodiumType.values()) {
			int spot = 0;
			for (var entry : type.getPodium().getTopWithTiesLastMonth().entrySet()) {
				String text = entry.getKey();
				List<UUID> uuids = entry.getValue();

				for (UUID uuid : uuids) {
					var user = podiumUserService.get(uuid);
					user.getPodiums().add(new MonthlyPodiumData(type, PodiumSpot.values()[spot], text));
					podiumUserService.save(user);
				}

				if (++spot == 3)
					break;
			}
		}

		for (MonthlyPodiumUser podiumUser : podiumUserService.getAll()) {
			final MonthlyPodiumData best = Collections.min(podiumUser.getPodiums(), Comparator.comparing(data -> data.getSpot().ordinal()));
			badgeService.edit(podiumUser, badgeUser -> {
				badgeUser.give(best.getSpot().getBadge());
				if (!badgeUser.hasBadge())
					badgeUser.setActive(best.getSpot().getBadge());
			});
		}

		return completed();
	}

}
