package me.pugabyte.nexus.features.events.y2020.pugmas20.models;

import lombok.Getter;
import me.pugabyte.nexus.features.events.models.QuestStage;
import me.pugabyte.nexus.features.events.models.Script;
import me.pugabyte.nexus.models.pugmas20.Pugmas20Service;
import me.pugabyte.nexus.models.pugmas20.Pugmas20User;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static me.pugabyte.nexus.utils.StringUtils.camelCase;

@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
public enum QuestNPC {
	ELF1(3078) {
		@Override
		public List<Script> getScript(Player player) {
			Pugmas20Service service = new Pugmas20Service();
			Pugmas20User user = service.get(player);

			switch (user.getLightTreeStage()) {
				case NOT_STARTED:
					return Arrays.asList(
							Script.wait(0, "It's time for our annual tree lighting ceremony, but I can’t find the special lighter... "
									+ "Ooooh Santa is gonna be SO mad at me if I’ve lost it. Can you find it? We haven't used it since last year, "
									+ "so check over near <location>")
					);
				case STARTED:
					return Arrays.asList(
							Script.wait(0, "The mechanism is broken! How could I have been so careless. "
									+ "The ceremony is supposed to start soon, but there might be enough time- hurry "
									+ "to the workshop and find <Elf2> they will know how to fix this.")
					);
				case STEP_THREE:
					return Arrays.asList(
							Script.wait(0, "You have it! Just in the nick of time. The ceremony has begun. Light all the torches around "
									+ "Santa’s Workshop leading up to the tree, don’t forget the one at the base of the tree! "
									+ "You have <time limit>")
					);
				case COMPLETE:
					return Arrays.asList(
							Script.wait(0, "todo - complete")
					);
				case STEP_ONE:
				case STEP_TWO:
					return Arrays.asList(
							Script.wait(0, "todo - reminder")
					);
			}

			return Arrays.asList(
					Script.wait(0, "todo - default")
			);
		}
	},
	ELF2(3079) {
		@Override
		public List<Script> getScript(Player player) {
			Pugmas20Service service = new Pugmas20Service();
			Pugmas20User user = service.get(player);

			switch (user.getLightTreeStage()) {
				case STEP_ONE:
					return Arrays.asList(
							Script.wait(0, "Dangit <Elf1>, I *told* him to be careful with this. Yes, it is fixable, "
									+ "just needs a new flint wheel and a steel striker. If you get me a piece of Flint and a steel nugget "
									+ "I can make both fast. Head to the coal mine and you should be able to get both- ask the mine supervisor for help")
					);
				case STEP_TWO:
					return Arrays.asList(
							Script.wait(0, "There you go, right as rain. Now tell <Elf 1> to be careful with it this time!")
					);
				case STEP_THREE:
					return Arrays.asList(
							Script.wait(0, "todo - reminder")
					);
			}

			return Arrays.asList(
					Script.wait(0, "todo - default")
			);
		}
	},
	SUPERVISOR(3080) {
		@Override
		public List<Script> getScript(Player player) {
			Pugmas20Service service = new Pugmas20Service();
			Pugmas20User user = service.get(player);

			switch (user.getLightTreeStage()) {
				case STEP_ONE:
					return Arrays.asList(
							Script.wait(0, "Eh? What? Oh, right, lemme take the ear plugs out. "
									+ "Sorry, we don’t have any flint or steel available right now but you can certainly "
									+ "go grab your own. Go into the equipment room and grab a seiv and a pick- you will "
									+ "need to sift the gavel piles for flint. For the steel you will need to mine 6 iron, and 3 coal. "
									+ "Give them to the blast furnace attendant and you will get your steel.")
					);
			}

			if (user.getLightTreeStage().equals(QuestStage.COMPLETE)) {
				switch (user.getMinesStage()) {
					case NOT_STARTED:
						return Arrays.asList(
								Script.wait(0, "Since you’re already cleared for the mine, wanna do me a favor? "
										+ "There's always a rush of last minute demands for materials by the workshop- things "
										+ "that need to be fixed, production that came up a little short. And almost all my mine-elfs "
										+ "have been sent to help in the wrapping and sled loading. If you bring me materials, put them in "
										+ "this chest here, I’ll see you get paid")
						);
					case STARTED:
						return Arrays.asList(
								Script.wait(0, "todo - reminder")
						);
					case COMPLETE:
						return Arrays.asList(
								Script.wait(0, "todo - complete")
						);
				}
			}

			return Arrays.asList(
					Script.wait(0, "todo - default")
			);
		}
	},
	QA_ELF(3081) {
		@Override
		public List<Script> getScript(Player player) {
			Pugmas20Service service = new Pugmas20Service();
			Pugmas20User user = service.get(player);

			switch (user.getToyTestingStage()) {
				case NOT_STARTED:
					return Arrays.asList(
							Script.wait(0, "Hey kid, I need a favor! After last year’s debacle with the sled, half of the Quality Assurance team was fired, and the other half have spent all year on making sure that the Sled won’t fall apart again."),
							Script.wait(0, "But that's left just me to try and keep up with testing all the toys that come off the line. I’m way, way behind and Pugmas is coming fast."),
							Script.wait(0, "Think you can help an elf out? You might need to find a friend to help you, all these games need to be tested before they can be added to the present piles."),
							Script.wait(0, "If you could just play a round or two of each, that would be perfect")
					);
				case STARTED:
					return Arrays.asList(
							Script.wait(0, "todo - reminder")
					);
				case COMPLETE:
					return Arrays.asList(
							Script.wait(0, "They all work! Excellent! You have the thanks of many children and one overworked elf. "
									+ "Have this too <prize>")
					);
			}

			return Arrays.asList(
					Script.wait(0, "todo - default")
			);
		}
	},
	ELF3(3082) {
		@Override
		public List<Script> getScript(Player player) {
			Pugmas20Service service = new Pugmas20Service();
			Pugmas20User user = service.get(player);

			switch (user.getOrnamentVendorStage()) {
				case NOT_STARTED:
					return Arrays.asList(
							Script.wait(0, "This tree is so big it takes a lot of ornaments to fill, and I may have uh, lost "
									+ "some of them from last year. Don’t tell Santa! Just help me out. Here in town is an ornament vendor, "
									+ "he trades players different wood types that we need for the factory for spare pugmas ornaments. "
									+ "I’d just ask him for some extra myself, but he’s mean and would tell Santa I lost the town’s ornaments. "
									+ "If you bring me one of each of the 10 ornaments, I'll reward you")
					);
				case STARTED:
					return Arrays.asList(
							Script.wait(0, "todo - reminder")
					);
				case COMPLETE:
					return Arrays.asList(
							Script.wait(0, "todo - complete")
					);
			}

			return Arrays.asList(
					Script.wait(0, "todo - default")
			);
		}
	};

	@Getter
	int id;

	QuestNPC(int id) {
		this.id = id;
	}

	public static QuestNPC getById(int id) {
		for (QuestNPC value : QuestNPC.values())
			if (value.id == id) return value;
		return null;
	}

	public void sendScript(Player player) {
		List<Script> scripts = getScript(player);
		if (scripts == null || scripts.isEmpty()) return;
		AtomicReference<String> npcName = new AtomicReference<>("");

		AtomicInteger wait = new AtomicInteger(0);
		scripts.forEach(script -> {
			wait.getAndAdd(script.getDelay());

			script.getLines().forEach(line -> {
				if(npcName.get().equalsIgnoreCase(QA_ELF.name()))
					npcName.set("Q.A. Elf");
				else
					npcName.set(camelCase(name()));
				npcName.set(npcName.get().replaceAll("[0-9]+", ""));

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
		});
	}

	public abstract List<Script> getScript(Player player);
}
