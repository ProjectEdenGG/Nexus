package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.Schedule;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Districts.Pugmas25District;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Fishing.Pugmas25AnglerLoot;
import gg.projecteden.nexus.models.pugmas25.Pugmas25Config;
import gg.projecteden.nexus.models.pugmas25.Pugmas25ConfigService;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.event.Listener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
@Schedule("0 */2 * * *")
public class Pugmas25AnglerQuestJob extends AbstractJob implements Listener {

	@Override
	protected CompletableFuture<JobStatus> run() {
		if (Pugmas25.get().isAfterEvent())
			return completed();

		// Pick new quest fish
		Pugmas25ConfigService configService = new Pugmas25ConfigService();
		Pugmas25Config config = configService.get0();
		Pugmas25AnglerLoot oldQuestFish = config.getAnglerQuestFish();
		List<Pugmas25AnglerLoot> possibleQuestFish = new ArrayList<>(List.of(Pugmas25AnglerLoot.values()));
		if (oldQuestFish != null) {
			Pugmas25District district = oldQuestFish.getDistrict();
			possibleQuestFish = Arrays.stream(Pugmas25AnglerLoot.values())
				.filter(loot -> loot.getDistrict() != district)
				.collect(Collectors.toList());
		}
		config.setAnglerQuestFish(RandomUtils.randomElement(possibleQuestFish));
		config.setAnglerQuestResetDateTime(LocalDateTime.now().plusHours(2));
		configService.save(config);

		// Reset user quest data
		final Pugmas25UserService userService = new Pugmas25UserService();
		for (Pugmas25User user : userService.cacheAll())
			user.resetAnglerQuest();
		userService.saveCache();

		return completed();
	}
}
