package gg.projecteden.nexus.models.easter22;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.events.y2022.easter22.Easter22;
import gg.projecteden.nexus.features.events.y2022.easter22.quests.Easter22QuestTask;
import gg.projecteden.nexus.features.quests.CommonQuestReward;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.quests.Quest;
import gg.projecteden.nexus.models.quests.QuesterService;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity(value = "easter22", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Easter22User implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Set<Location> found = new HashSet<>();

	private static final String PREFIX = StringUtils.getPrefix("Easter22");

	public static Easter22User of(HasUniqueId player) {
		return new Easter22UserService().get(player);
	}

	public Quest getQuest() {
		for (Quest quest : new QuesterService().get(this).getQuests())
			if (quest.getCurrentTaskProgress().getTask() == Easter22QuestTask.MAIN)
				return quest;
		return null;
	}

	public void found(Location location) {
		if (found.contains(location)) {
			sendMessage(PREFIX + "You have already found this egg!");
			return;
		}

		found.add(location);

		sendMessage(PREFIX + "You found an egg! &7(" + found.size() + "/" + Easter22.TOTAL_EASTER_EGGS + ")");

		switch (found.size()) {
			case 5 -> {
				CommonQuestReward.SURVIVAL_MONEY.apply(this, 5000);
				sendMessage(PREFIX + "You have received &e$5,000 &3for finding &e5 easter eggs");
				CommonQuestReward.EVENT_TOKENS.apply(this, 25);
			}
			case 10 -> {
				CommonQuestReward.VOTE_POINTS.apply(this, 25);
				sendMessage(PREFIX + "You have received &e25 vote points &3for finding &e10 easter eggs");
				CommonQuestReward.EVENT_TOKENS.apply(this, 25);
			}
			case 15 -> {
				CommonQuestReward.SURVIVAL_MONEY.apply(this, 15000);
				sendMessage(PREFIX + "You have received &e$15,000 &3for finding &e15 easter eggs");
				CommonQuestReward.EVENT_TOKENS.apply(this, 25);
			}
			case 20 -> CommonQuestReward.EVENT_TOKENS.apply(this, 125);
			default -> CommonQuestReward.EVENT_TOKENS.apply(this, 15);
		}
	}

}
