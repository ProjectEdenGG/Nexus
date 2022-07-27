package gg.projecteden.nexus.models.testquestuser;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.events.models.Quest;
import gg.projecteden.nexus.features.events.models.QuestProgress;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.QuestConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@Entity(value = "test_quest_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, QuestConverter.class})
public class TestQuestUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	@Embedded
	private List<QuestProgress> quests = new ArrayList<>();

	public QuestProgress getQuestProgress(Quest quest) {
		Optional<QuestProgress> first = quests.stream().filter(questProgress -> questProgress.getQuest().equals(quest)).findFirst();
		if (!first.isPresent()) {
			first = Optional.of(new QuestProgress(quest));
			quests.add(first.get());
		}

		return first.get();
	}

	public void setQuestProgress(QuestProgress questProgress) {
		quests.removeIf(_questProgress -> _questProgress.getQuest().equals(questProgress.getQuest()));
		quests.add(questProgress);
	}

}
