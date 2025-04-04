package gg.projecteden.nexus.features.minigames.models.statistics.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
public class MinigameStatistic {
	private String id;
	private String title;

	public Object format(int score) {
		return score;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		return Objects.equals(id, ((MinigameStatistic) o).id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
}
