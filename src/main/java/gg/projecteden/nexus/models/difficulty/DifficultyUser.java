package gg.projecteden.nexus.models.difficulty;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@Entity(value = "difficulty_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class DifficultyUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Difficulty difficulty = Difficulty.MEDIUM;


	@AllArgsConstructor
	public enum Difficulty {
		EASY(0),
		MEDIUM(33),
		HARD(66),
		EXPERT(100),
		;

		@Getter
		int percentage;
	}
}
