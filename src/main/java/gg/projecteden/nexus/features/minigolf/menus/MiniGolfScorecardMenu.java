package gg.projecteden.nexus.features.minigolf.menus;

import gg.projecteden.nexus.models.minigolf.MiniGolfConfig.MiniGolfCourse;
import gg.projecteden.nexus.models.minigolf.MiniGolfUser;
import gg.projecteden.nexus.models.minigolf.MiniGolfUserService;
import gg.projecteden.nexus.utils.DialogUtils.DialogBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

import static gg.projecteden.nexus.utils.DialogUtils.DialogBuilder.close;

@SuppressWarnings("FieldCanBeLocal")
public class MiniGolfScorecardMenu {
	private final MiniGolfUser user;
	private final MiniGolfCourse course;
	private final Map<String, Double> spaces;
	private final String pipe = " &8|&f ";
	private final String background = "艋";

	public MiniGolfScorecardMenu(MiniGolfUser user, MiniGolfCourse course) {
		this(user, course, Map.of());
	}

	public MiniGolfScorecardMenu(MiniGolfUser user, MiniGolfCourse course, Map<String, Double> spaces) {
		this.user = user;
		this.course = course;

		this.spaces = new HashMap<>() {{
			put("BEFORE_HEADER", 8d);

			put("BEFORE_SEPARATORS", 8d);
			put("SEPARATORS", 40d);

			put("BEFORE_HOLE", 10d);
			put("AFTER_HOLE", 1d);

			put("BEFORE_PAR", 2d);
			put("AFTER_PAR", 1d);

			put("BEFORE_CURRENT", 3d);
			put("AFTER_CURRENT", 4d);

			put("BEFORE_BEST", 1d);
			put("AFTER_BEST", 2d);

			put("NEWLINES_AFTER_BACKGROUND", 2d);
			put("NEWLINES_BEFORE_BACKGROUND", 1d);
			put("NEWLINES_END", 5d);
		}};

		if (spaces != null && !spaces.isEmpty())
			this.spaces.putAll(spaces);
	}

	public int spaces(String key) {
		return spaces.getOrDefault(key, 0d).intValue();
	}

	public void open() {
		var dialog = new DialogBuilder()
			.title(course.getName() + " MiniGolf Scorecard");
		var json = new JsonBuilder();

		for (int i = 0; i < spaces("NEWLINES_BEFORE_BACKGROUND"); i++)
			json.newline();

		json.next(background);

		for (int i = 0; i < spaces("NEWLINES_AFTER_BACKGROUND"); i++)
			json.newline();

		String pipe = " " + this.pipe + " ";
		json.next(
			" ".repeat(spaces("BEFORE_HEADER")) +
			"Hole" + pipe + "Par" + pipe + "Current" + pipe + "Best" +
			" ".repeat(spaces("AFTER_HEADER"))
		).newline();

		json.next(" ".repeat(spaces("BEFORE_SEPARATORS")) + "&8&m ".repeat(spaces("SEPARATORS"))).newline();

		for (var hole : course.getHoles()) {
			String holeNumber = String.valueOf(hole.getId());
			if (hole.getId() < 10)
				holeNumber = "0" + hole.getId();

			int current = 0;
			var currentScorecard = user.getCurrentScorecard(course);
			if (currentScorecard.containsKey(hole.getId()))
				current = currentScorecard.get(hole.getId());

			int best = 0;
			var bestScorecard = user.getBestScorecard(course);
			if (bestScorecard.containsKey(hole.getId()))
				best = bestScorecard.get(hole.getId());

			String currentString = current == 0 ? "??" : current < 10 ? "0" + current : "" + current;
			String bestString = best == 0 ? "??" : best < 10 ? (best == 1 ? "&a" : "") + "0" + best : "" + best;

			json.next(
				" ".repeat(spaces("BEFORE_HOLE")) +
				holeNumber +
				" ".repeat(spaces("AFTER_HOLE")) +
				pipe +
				" ".repeat(spaces("BEFORE_PAR")) +
				hole.getPar() +
				" ".repeat(spaces("AFTER_PAR")) +
				pipe +
				" ".repeat(spaces("BEFORE_CURRENT")) +
				currentString +
				" ".repeat(spaces("AFTER_CURRENT")) +
				pipe +
				" ".repeat(spaces("BEFORE_BEST")) +
				bestString +
				" ".repeat(spaces("AFTER_BEST"))
			).newline();
		}

		for (int i = 0; i < spaces("NEWLINES_END"); i++)
			json.newline();

		Player player = user.getOnlinePlayer();
		dialog
			.bodyText(json)
			.multiAction()
			.button("Close", action -> close(player))
			.button("&cReset", action -> {
				// TODO Confirm?
				user.getCurrentScorecard(course).clear();
				new MiniGolfUserService().save(user);
				open();
			})
			.open(player);
	}
}
