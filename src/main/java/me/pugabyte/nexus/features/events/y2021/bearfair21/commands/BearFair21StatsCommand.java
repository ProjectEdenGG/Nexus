package me.pugabyte.nexus.features.events.y2021.bearfair21.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.features.events.models.QuestStage;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21.BF21PointSource;
import me.pugabyte.nexus.features.events.y2021.bearfair21.commands.BearFair21Command.BearFair21UserQuestStageHelper;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.bearfair21.BearFair21User;
import me.pugabyte.nexus.models.bearfair21.BearFair21UserService;
import me.pugabyte.nexus.models.eventuser.EventUser;
import me.pugabyte.nexus.models.eventuser.EventUserService;
import me.pugabyte.nexus.utils.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aliases("bf21stats")
public class BearFair21StatsCommand extends CustomCommand {
	BearFair21UserService userService = new BearFair21UserService();

	public BearFair21StatsCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void stats() {
		List<BearFair21User> users = userService.getAll();
		EventUserService eventUserService = new EventUserService();
		List<EventUser> eventUsers = eventUserService.getAll();

		// Total time played
		// % of time spent at bf vs other worlds
		// % of people who logged in that visited bf
		// % of playtime per world during bf

		send("Unique visitors: " + users.stream().filter(bearFair21User -> !bearFair21User.isFirstVisit()).toList().size());

		send("Event Participation:");
		for (String event : eventParticipation.keySet())
			send(" - " + event + ":" + eventParticipation.get(event).size());

		send("Daily Points:");
		send(json(" - [Day 1]").hover(getCompletedSources(eventUsers, Day.MON)));
		send(json(" - [Day 2]").hover(getCompletedSources(eventUsers, Day.TUE)));
		send(json(" - [Day 3]").hover(getCompletedSources(eventUsers, Day.WED)));
		send(json(" - [Day 4]").hover(getCompletedSources(eventUsers, Day.THU)));
		send(json(" - [Day 5]").hover(getCompletedSources(eventUsers, Day.FRI)));
		send(json(" - [Day 6]").hover(getCompletedSources(eventUsers, Day.SAT)));
		send(json(" - [Day 7]").hover(getCompletedSources(eventUsers, Day.SUN)));

		send("Quest Stages:");
		send(json("- [Main]").hover(getQuestStages(users, BearFair21UserQuestStageHelper.MAIN)));
		send(json("  - [LumberJack]").hover(getQuestStages(users, BearFair21UserQuestStageHelper.LUMBERJACK)));
		send(json("  - [BeeKeeper]").hover(getQuestStages(users, BearFair21UserQuestStageHelper.BEEKEEPER)));
		send(json("  - [Recycler]").hover(getQuestStages(users, BearFair21UserQuestStageHelper.RECYCLER)));
		send(json("- [Minigame Night]").hover(getQuestStages(users, BearFair21UserQuestStageHelper.MINIGAME_NIGHT)));
		send(json("- [Pugmas]").hover(getQuestStages(users, BearFair21UserQuestStageHelper.PUGMAS)));
		send(json("- [Halloween]").hover(getQuestStages(users, BearFair21UserQuestStageHelper.HALLOWEEN)));
		send(json("- [Summer Down Under]").hover(getQuestStages(users, BearFair21UserQuestStageHelper.SUMMER_DOWN_UNDER)));
	}

	private List<String> getQuestStages(List<BearFair21User> users, BearFair21UserQuestStageHelper quest) {
		Map<QuestStage, Integer> questStageMap = new HashMap<>();
		for (QuestStage questStage : QuestStage.values()) {
			for (BearFair21User user : users) {
				if (quest.getter().apply(user).equals(questStage)) {
					int count = questStageMap.getOrDefault(questStage, 0);
					questStageMap.put(questStage, ++count);
				}
			}
		}

		List<String> lines = new ArrayList<>();
		questStageMap.keySet().stream().sorted(Comparator.comparing(Enum::ordinal)).forEach(questStage -> {
			int count = questStageMap.get(questStage);
			if (count > 0)
				lines.add(StringUtils.camelCase(questStage) + ": " + count);
		});

		return lines;
	}

	private List<String> getCompletedSources(List<EventUser> users, Day day) {
		Map<BF21PointSource, Integer> sourceMap = new HashMap<>();
		for (EventUser user : users) {
			for (BF21PointSource source : BF21PointSource.values()) {
				int userTokens = user.getTokensRecieved(source.getId(), day.getLocalDate());
				int maxSourceTokens = BearFair21.getTokenMax(source);
				int count = sourceMap.getOrDefault(source, 0);
				if (userTokens == maxSourceTokens) {
					++count;
					sourceMap.put(source, count);
				}
			}
		}

		List<String> lines = new ArrayList<>();
		sourceMap.keySet().stream().sorted(Comparator.comparing(Enum::ordinal)).forEach(source -> {
			int count = sourceMap.get(source);
			lines.add(StringUtils.camelCase(source) + ": " + count);
		});

		return lines;
	}

	@AllArgsConstructor
	private enum Day {
		MON(LocalDate.of(2021, 6, 28)),
		TUE(LocalDate.of(2021, 6, 29)),
		WED(LocalDate.of(2021, 6, 30)),
		THU(LocalDate.of(2021, 7, 1)),
		FRI(LocalDate.of(2021, 7, 2)),
		SAT(LocalDate.of(2021, 7, 3)),
		SUN(LocalDate.of(2021, 7, 4));

		@Getter
		LocalDate localDate;
	}

	private static final Map<String, List<String>> eventParticipation = new HashMap<>() {{
		put("PixelDrop", List.of(
			"ichompbrownies",
			"Rose",
			"The_ChamPeon",
			"Lexi",
			"Diaphoni",
			"forrestbman",
			"Wire",
			"Boffo"
		));
		put("Builder Bash", List.of(
			"x_jay",
			"Phoebe",
			"ichomp",
			"honey_fun",
			"mystery_ele",
			"Diaphoni",
			"kiri",
			"jj",
			"rangedsp",
			"cynshi"
		));
		put("UHC", List.of(
			"banananue",
			"Cal_The_CHair",
			"Coal",
			"cream",
			"Diaphoni",
			"Elisa_Fox",
			"elpidaa",
			"Griffin",
			"Honey_fungus",
			"ichompbrownies",
			"Kaamos",
			"Kiri",
			"Lexi",
			"Marshy",
			"Pandanakinz",
			"Puppenjunge",
			"Rohan",
			"The_ChamPeon",
			"Wakka",
			"Willhelm",
			"Wire",
			"x_jayfiz"
		));
		put("Bingo", List.of(
			"The_ChamPeon",
			"Lexi",
			"Filid",
			"Kiri",
			"Cynshiii",
			"MaxAlex2000",
			"Midnight9746",
			"Phoebe",
			"Dia",
			"BeN0tAfraid",
			"kalibytes",
			"USAFog",
			"Kaamos",
			"elpidaa",
			"ichompbrownies"
		));
		put("Uncivil Engineers", List.of(
			"Willhelm",
			"The_ChamPeon",
			"forrestbman",
			"Blast",
			"Boffo",
			"cream",
			"Diaphoni",
			"Draki",
			"Elisa_Fox",
			"ichompbrownies",
			"Lexi",
			"Naqte",
			"Puppenjunge",
			"Rose",
			"Silver4Scythe",
			"Smaug",
			"Wire"
		));
		put("Jigsaw Jam", List.of(
			"jj",
			"sorat",
			"knack",
			"forrestbman",
			"The_ChamPeon",
			"WakkaFlocka",
			"SirBoffo",
			"PonyoBear",
			"ichompbrownies",
			"banananue",
			"Cynshiii",
			"Midnight9746",
			"Lupinez",
			"gamerflame",
			"Silver4Scythe",
			"Drakimau",
			"lexikiq",
			"Filid",
			"Honey_fungus",
			"hotwire2",
			"Pandanakinz"
		));
		put("Battleship", List.of(
			"The_ChamPeon",
			"Lexi",
			"Wakka",
			"Boffo",
			"Jelly",
			"Kiri",
			"Puppenjunge",
			"Cyn",
			"datimagination",
			"Diaphoni",
			"Hatterrr",
			"Knack",
			"Marshy",
			"MoonStar1232",
			"Coal",
			"ichompbrownies",
			"Lindyy",
			"MaxAlex2000"
		));
	}};
}
