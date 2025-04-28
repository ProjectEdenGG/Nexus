package gg.projecteden.nexus.features.minigames.models.statistics.models;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import gg.projecteden.nexus.features.minigames.models.mechanics.MechanicType;
import gg.projecteden.nexus.utils.Debug;
import gg.projecteden.nexus.utils.Debug.DebugType;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FormulaStatistic extends MinigameStatistic {

	private final Formula formula;

	public FormulaStatistic(String id, String title, Formula formula) {
		super(id, title);
		this.formula = formula;
	}

	@Override
	public List<Bson> getPipeline(String afterDate, MechanicType mechanic, UUID self, boolean aggregate) {
		List<Bson> filters = new ArrayList<>() {{
			add(Filters.gt("statistics.date", afterDate));
			if (mechanic != null)
				add(Filters.eq("statistics.mechanic", mechanic.name()));
			if (self != null)
				add(Filters.eq("_id", self.toString()));
		}};

		List<BsonField> groupFields = new ArrayList<>();
		for (String field : formula.collectFields()) {
			Debug.log(DebugType.MINIGAMES, "Adding field: " + field);
			groupFields.add(Accumulators.sum(field, "$statistics.stats." + field));
		}

		List<Bson> pipeline = new ArrayList<>() {{
			add(Aggregates.unwind("$statistics"));
			add(Aggregates.match(Filters.and(filters)));
			add(Aggregates.group(
				"$_id",
				groupFields.toArray(new BsonField[0])
			));
			add(Aggregates.project(Projections.fields(
				Projections.computed("total", formula.toBson())
			)));
		}};

		if (!aggregate)
			pipeline.add(Aggregates.sort(Sorts.descending("total")));

		Debug.log(DebugType.MINIGAMES, pipeline.stream()
				.map(stage -> stage.toBsonDocument(Document.class, MongoClientSettings.getDefaultCodecRegistry()).toJson())
				.reduce((a, b) -> a + ",\n" + b)
				.map(json -> "[\n" + json + "\n]")
				.orElse("[]")
		);

		return pipeline;
	}

	public static class Formula {
		private final Object expression;
		private final Set<String> fieldsUsed = new HashSet<>();

		private Formula(Object expression) {
			this.expression = expression;
		}

		private Formula(Object expression, Set<String> fieldsUsed) {
			this.expression = expression;
			this.fieldsUsed.addAll(fieldsUsed);
		}

		public static Formula of(MinigameStatistic stat) {
			Formula formula = new Formula(stat.getId());
			formula.fieldsUsed.add(stat.getId());
			return formula;
		}

		public static Formula constant(Number number) {
			return new Formula(number);
		}

		public Formula add(MinigameStatistic other) {
			return add(of(other));
		}

		public Formula add(Formula other) {
			Set<String> newFields = new HashSet<>(this.fieldsUsed);
			newFields.addAll(other.fieldsUsed);
			return new Formula(new Document("$add", List.of(this.toExpression(), other.toExpression())), newFields);
		}

		public Formula subtract(MinigameStatistic other) {
			return subtract(of(other));
		}

		public Formula subtract(Formula other) {
			Set<String> newFields = new HashSet<>(this.fieldsUsed);
			newFields.addAll(other.fieldsUsed);
			return new Formula(new Document("$subtract", List.of(this.toExpression(), other.toExpression())), newFields);
		}

		public Formula multiply(MinigameStatistic other) {
			return multiply(of(other));
		}

		public Formula multiply(Formula other) {
			Set<String> newFields = new HashSet<>(this.fieldsUsed);
			newFields.addAll(other.fieldsUsed);
			return new Formula(new Document("$multiply", List.of(this.toExpression(), other.toExpression())), newFields);
		}

		public Formula divide(MinigameStatistic other) {
			return divide(other, Formula.constant(0));
		}

		public Formula divide(MinigameStatistic other, Formula zeroValue) {
			return divide(of(other), zeroValue);
		}

		public Formula divide(Formula other, Formula zeroValue) {
			Set<String> newFields = new HashSet<>(this.fieldsUsed);
			newFields.addAll(other.fieldsUsed);
			return new Formula(new Document("$cond", List.of(
				new Document("$eq", List.of(other.toExpression(), 0)),
				zeroValue.toExpression(),
				new Document("$divide", List.of(this.toExpression(), other.toExpression()))
			)), newFields);
		}

		public Set<String> collectFields() {
			return fieldsUsed;
		}

		private Object toExpression() {
			if (expression instanceof String path) {
				return new Document("$ifNull", List.of("$" + path, 0));
			}
			return expression;
		}

		public Bson toBson() {
			Object expr = toExpression();
			if (expr instanceof Bson bson)
				return bson;
			else if (expr instanceof Document doc)
				return doc;
			else
				return new Document(expr.toString(), 1);
		}
	}






}
