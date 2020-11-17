package me.pugabyte.bncore.features.holidays.pugmas20.models;

import me.pugabyte.bncore.features.holidays.QuestStage;
import me.pugabyte.bncore.models.pugmas20.Pugmas20Service;
import me.pugabyte.bncore.models.pugmas20.Pugmas20User;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;

public enum QuestNPC {
	ELF1(3078) {
		@Override
		public LinkedHashMap<Integer, String> getScriptMap(Player player) {
			LinkedHashMap<Integer, String> scriptMap = new LinkedHashMap<>();

			Pugmas20Service service = new Pugmas20Service();
			Pugmas20User user = service.get(player);

			switch (user.getLightTreeStage()) {
				case NOT_STARTED:
					scriptMap.put(0, "It's time for our annual tree lighting ceremony, but I can’t find the special lighter... "
							+ "Ooooh Santa is gonna be SO mad at me if I’ve lost it. Can you find it? We haven't used it since last year, "
							+ "so check over near <location>");
					break;
				case STARTED:
					scriptMap.put(0, "The mechanism is broken! How could I have been so careless. The ceremony is supposed to start soon, "
							+ "but there might be enough time- hurry to the workshop and find <Elf2> they will know how to fix this.");
					break;
				case STEP_THREE:
					scriptMap.put(0, "You have it! Just in the nick of time. The ceremony has begun. Light all the torches around "
							+ "Santa’s Workshop leading up to the tree, don’t forget the one at the base of the tree! "
							+ "You have <time limit>");
					break;
				case COMPLETE:
					scriptMap.put(0, "todo - complete");
					break;
				case STEP_ONE:
				case STEP_TWO:
					scriptMap.put(0, "todo - reminder");
			}
			return scriptMap;
		}
	},
	ELF2(3079) {
		@Override
		public LinkedHashMap<Integer, String> getScriptMap(Player player) {
			LinkedHashMap<Integer, String> scriptMap = new LinkedHashMap<>();

			Pugmas20Service service = new Pugmas20Service();
			Pugmas20User user = service.get(player);

			switch (user.getLightTreeStage()) {
				case STEP_ONE:
					scriptMap.put(0, "Dangit <Elf1>, I *told* him to be careful with this. Yes, it is fixable, "
							+ "just needs a new flint wheel and a steel striker. If you get me a piece of Flint and a steel nugget "
							+ "I can make both fast. Head to the coal mine and you should be able to get both- ask the mine supervisor for help");
					break;
				case STEP_TWO:
					scriptMap.put(0, "There you go, right as rain. Now tell <Elf 1> to be careful with it this time!");
					break;
				case STEP_THREE:
					scriptMap.put(0, "todo - reminder");
					break;
			}

			scriptMap.put(0, "todo - default");
			return scriptMap;
		}
	},
	SUPERVISOR(3080) {
		@Override
		public LinkedHashMap<Integer, String> getScriptMap(Player player) {
			LinkedHashMap<Integer, String> scriptMap = new LinkedHashMap<>();

			Pugmas20Service service = new Pugmas20Service();
			Pugmas20User user = service.get(player);

			switch (user.getLightTreeStage()) {
				case STEP_ONE:
					scriptMap.put(0, "Eh? What? Oh, right, lemme take the ear plugs out. "
							+ "Sorry, we don’t have any flint or steel available right now but you can certainly "
							+ "go grab your own. Go into the equipment room and grab a seiv and a pick- you will "
							+ "need to sift the gavel piles for flint. For the steel you will need to mine 6 iron, and 3 coal. "
							+ "Give them to the blast furnace attendant and you will get your steel.");
					break;
			}

			if (user.getLightTreeStage().equals(QuestStage.COMPLETE)) {
				switch (user.getMinesStage()) {
					case NOT_STARTED:
						scriptMap.put(0, "Since you’re already cleared for the mine, wanna do me a favor? "
								+ "There's always a rush of last minute demands for materials by the workshop- things "
								+ "that need to be fixed, production that came up a little short. And almost all my mine-elfs "
								+ "have been sent to help in the wrapping and sled loading. If you bring me materials, put them in "
								+ "this chest here, I’ll see you get paid");
						break;
					case STARTED:
						scriptMap.put(0, "todo - reminder");
						break;
					case COMPLETE:
						scriptMap.put(0, "todo - complete");
						break;
				}
			}

			scriptMap.put(0, "todo - default");
			return scriptMap;
		}
	},
	QA_ELF(3081) {
		@Override
		public LinkedHashMap<Integer, String> getScriptMap(Player player) {
			LinkedHashMap<Integer, String> scriptMap = new LinkedHashMap<>();

			Pugmas20Service service = new Pugmas20Service();
			Pugmas20User user = service.get(player);

			switch (user.getToyTestingStage()) {
				case NOT_STARTED:
					scriptMap.put(0, "Hey kid, I need a favor! After last year’s debacle with the sled, "
							+ "half of the Quality Assurance team was fired, and the other half have spent all year on "
							+ "making sure that the Sled won’t fall apart again. But that's left just me to try and keep "
							+ "up with testing all the toys that come off the line. I’m way, way behind and Pugmas is coming fast. "
							+ "Think you can help an elf out? You might need to find a friend to help you, all these games need to be "
							+ "tested before they can be added to the present piles. If you could just play a round or two of each, "
							+ "that would be perfect");
					break;
				case STARTED:
					scriptMap.put(0, "todo - reminder");
					break;
				case COMPLETE:
					scriptMap.put(0, "They all work! Excellent! You have the thanks of many children and one overworked elf. "
							+ "Have this too <prize>");
					break;
			}
			scriptMap.put(0, "todo - default");
			return scriptMap;
		}
	},
	ELF3(3082) {
		@Override
		public LinkedHashMap<Integer, String> getScriptMap(Player player) {
			LinkedHashMap<Integer, String> scriptMap = new LinkedHashMap<>();

			Pugmas20Service service = new Pugmas20Service();
			Pugmas20User user = service.get(player);

			switch (user.getOrnamentVendorStage()) {
				case NOT_STARTED:
					scriptMap.put(0, "This tree is so big it takes a lot of ornaments to fill, and I may have uh, lost "
							+ "some of them from last year. Don’t tell Santa! Just help me out. Here in town is an ornament vendor, "
							+ "he trades players different wood types that we need for the factory for spare pugmas ornaments. "
							+ "I’d just ask him for some extra myself, but he’s mean and would tell Santa I lost the town’s ornaments. "
							+ "If you bring me one of each of the 10 ornaments, I'll reward you");
					break;
				case STARTED:
					scriptMap.put(0, "todo - reminder");
					break;
				case COMPLETE:
					scriptMap.put(0, "todo - complete");
					break;
			}

			scriptMap.put(0, "todo - default");
			return scriptMap;
		}
	};

	int npcId;

	QuestNPC(int id) {
		npcId = id;
	}

	public static QuestNPC getByID(int id) {
		for (QuestNPC value : QuestNPC.values())
			if (value.npcId == id) return value;
		return null;
	}

	public void sendScript(Player player) {
		LinkedHashMap<Integer, String> scriptMap = getScriptMap(player);
		if (scriptMap == null || scriptMap.isEmpty()) return;
		AtomicReference<String> npcName = new AtomicReference<>("");

		AtomicInteger wait = new AtomicInteger(0);
		scriptMap.forEach((delay, line) -> {
			npcName.set(camelCase(name().replaceAll("_", " ")));
			npcName.set(npcName.get().replaceAll("[0-9]+", ""));

			wait.getAndAdd(delay);

			line = line.replaceAll("<player>", player.getName());
			if (line.contains("<self>")) {
				npcName.set("&b&lYOU&f");
				line = line.replaceAll("<self> ", "");
			}

			String message = "&3" + npcName.get() + " &7> &f" + line;
			Tasks.wait(wait.get(), () -> {
				Utils.send(player, message);
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 1F);
			});
		});
	}

	public abstract LinkedHashMap<Integer, String> getScriptMap(Player player);
}
