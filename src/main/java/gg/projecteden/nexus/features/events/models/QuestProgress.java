package gg.projecteden.nexus.features.events.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

// TODO Needs better name

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class QuestProgress {
	@NonNull
	private Quest quest;
	private QuestStage questStage;

}
