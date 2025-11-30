package gg.projecteden.nexus.features.minigolf.menus;

import gg.projecteden.nexus.features.menus.BookBuilder.WrittenBookMenu;
import gg.projecteden.nexus.models.minigolf.MiniGolfConfig.MiniGolfCourse;
import gg.projecteden.nexus.models.minigolf.MiniGolfUser;
import gg.projecteden.nexus.utils.JsonBuilder;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class ScorecardBookMenu {
	private final MiniGolfUser user;
	private final MiniGolfCourse course;
	private final int page;
	private final Map<Integer, Integer> scorecard;

	public void open() {
		var builder = new WrittenBookMenu();
		var json = new JsonBuilder();

		json.next(" ## |  Par | Strokes").newline();
		json.next("------------------").newline();

		int count = 0;
		int start = page == 1 ? 1 : 10;

		for (var hole : course.getHoles()) {
			if (hole.getId() < start)
				continue;

			String holeNumber = String.valueOf(hole.getId());
			if (hole.getId() < 10)
				holeNumber = "0" + hole.getId();

			String strokes = " ?";
			if (scorecard.containsKey(hole.getId())) {
				int strokeCount = scorecard.get(hole.getId());
				String space = " ";
				if (strokeCount > 9)
					space = "";

				strokes = space + strokeCount;
			}

			json.next(" " + holeNumber + " |   " + hole.getPar() + "   |   " + strokes).newline();

			if (++count >= 9)
				break;
		}

		json.newline().group();

		if (page == 1)
			json.next("      &3----> ").hover("&eNext Page").command("/minigolf scorecard " + course.getName() + " 2");
		else
			json.next("      &3<---- ").hover("&ePrevious Page").command("/minigolf scorecard " + course.getName() + " 1");

		builder.addPage(json).open(user.getOnlinePlayer());
	}
}
