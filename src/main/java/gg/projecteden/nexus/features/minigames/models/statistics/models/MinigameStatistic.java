package gg.projecteden.nexus.features.minigames.models.statistics.models;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@AllArgsConstructor
public class MinigameStatistic {
	private String id;
	private String title;
	private boolean hidden;

	public MinigameStatistic(String id, String title) {
		this.id = id;
		this.title = title;
	}

	public Object format(double score) {
		return StringUtils.getCnf().format(score);
	}

	public List<Bson> getPipeline(String afterDate, MechanicType mechanic, UUID self, boolean aggregate) {
		List<Bson> filters = new ArrayList<>() {{
			add(Filters.gt("statistics.date", afterDate));
			if (mechanic != null)
				add(Filters.eq("statistics.mechanic", mechanic.name()));
			if (self != null)
				add(Filters.eq("_id", self.toString()));
		}};

		List<Bson> pipeline = new ArrayList<>() {{
			add(Aggregates.unwind("$statistics"));
			add(Aggregates.match(Filters.and(filters)));
			add(Aggregates.group("$_id", Accumulators.sum("total", "$statistics.stats." + getId())));
		}};
		if (!aggregate)
			pipeline.add(Aggregates.sort(Sorts.descending("total")));

		return pipeline;
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
