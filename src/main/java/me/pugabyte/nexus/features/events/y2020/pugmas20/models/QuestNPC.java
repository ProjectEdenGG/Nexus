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

import java.util.ArrayList;
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
				case STARTED:
					user.setLightTreeStage(QuestStage.STEP_ONE);
					service.save(user);

					return Arrays.asList(
							Script.wait(0, "I can’t find the special lighter… Ooooh Santa is gonna be SO mad at me if I’ve lost it."),
							Script.wait(0, "We haven't used it since last year. Could you search in the basement to help me find it?")
					);
				case STEP_ONE:
					// TODO: if user doesn't have special lighter, send reminder
					user.setLightTreeStage(QuestStage.STEP_TWO);
					service.save(user);

					return Arrays.asList(
							Script.wait(0, "The mechanism is broken! How could I have been so careless."),
							Script.wait(0, "The ceremony is supposed to start soon, but there might be enough time- " +
									"hurry to the tree and find <Elf2> they will know how to fix this.")
					);
				case STEP_TWO:
					return Arrays.asList(
							Script.wait(0, "todo - reminder")
					);


				case STEPS_DONE:
					// todo: start challenge for player
					return Arrays.asList(
							Script.wait(0, "You have it! Just in the nick of time. The ceremony has begun."),
							Script.wait(0, "Light all the torches around Santa’s Workshop leading up to the tree, " +
									"don’t forget the one at the base of the tree! You have <time limit>")
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
	},
	ELF2(3079) {
		@Override
		public List<Script> getScript(Player player) {
			Pugmas20Service service = new Pugmas20Service();
			Pugmas20User user = service.get(player);

			switch (user.getLightTreeStage()) {
				case NOT_STARTED:
					user.setLightTreeStage(QuestStage.STARTED);
					service.save(user);

					return Arrays.asList(
							Script.wait(0, "It's time for our annual tree lighting ceremony, but <Elf1> still hasn’t returned with the special lighter!"),
							Script.wait(0, "<ask the player to find elf 1 in the workshop>")
					);
				case STARTED:
					return Arrays.asList(
							Script.wait(0, "<ask the player to find elf 1 in the workshop>")
					);

				case STEP_TWO:
					user.setLightTreeStage(QuestStage.STEP_THREE);
					service.save(user);

					return Arrays.asList(
							Script.wait(0, "Dangit <Elf1>, I *told* him to be careful with this."),
							Script.wait(0, "Hmm, yes, it is fixable, just needs a new flint wheel and a steel striker."),
							Script.wait(0, "If you get me a piece of Flint and a steel nugget I can make both fast."),
							Script.wait(0, "Head to the coal mine and you should be able to get both- ask the mine supervisor for help")
					);
				case STEP_THREE:
					// todo: if player does not have required materials, send reminder
					user.setLightTreeStage(QuestStage.STEPS_DONE);
					service.save(user);

					return Arrays.asList(
							Script.wait(0, "There you go, right as rain. Now tell <Elf 1> to be careful with it this time!")
					);
				case STEPS_DONE:
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
				case STEP_THREE:
					return Arrays.asList(
							Script.wait(0, "Eh? What? Oh, right, lemme take the ear plugs out."),
							Script.wait(0, "Sorry, we don’t have any flint or steel available right now but you can certainly go grab your own."),
							Script.wait(0, "Go into the equipment room and grab a sieve and a pick- you will need to sift the gavel piles for flint."),
							Script.wait(0, "For the steel you will need <how to obtain steel>")
					);
			}

			if (user.getLightTreeStage().equals(QuestStage.COMPLETE)) {
				switch (user.getMinesStage()) {
					case NOT_STARTED:
						return Arrays.asList(
								Script.wait(0, "Since you’re already cleared for the mine, wanna do me a favor?"),
								Script.wait(0, "There's always a rush of last minute demands for materials by the workshop- things that need to be fixed, production that came up a little short."),
								Script.wait(0, "And almost all my mine-elfs have been sent to help in the wrapping and sled loading."),
								Script.wait(0, "If you bring me materials, put them in this chest here, I’ll see you get paid")
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
					user.setToyTestingStage(QuestStage.STARTED);
					service.save(user);

					return Arrays.asList(
							Script.wait(0, "Hey kid, I need a favor! After last year’s debacle with the sled, half of the Quality Assurance team was fired, and the other half have spent all year on making sure that the Sled won’t fall apart again."),
							Script.wait(0, "But that's left just me to try and keep up with testing all the toys that come off the line. I’m way, way behind and Pugmas is coming fast."),
							Script.wait(0, "Think you can help an elf out? You might need to find a friend to help you, all these games need to be tested before they can be added to the present piles."),
							Script.wait(0, "If you could just play a round or two of each, that would be perfect")
					);
				case STARTED:
					List<String> leftover = new ArrayList<>();
					if (!user.isMasterMind())
						leftover.add("MasterMind");
					if (!user.isBattleship())
						leftover.add("Battleship");
					if (!user.isConnectFour())
						leftover.add("Connect4");
					if (!user.isTicTacToe())
						leftover.add("TicTacToe");

					if (Utils.isNullOrEmpty(leftover)) {
						user.setToyTestingStage(QuestStage.STEPS_DONE);
						service.save(user);
						getScript(player);
					}

					String reminder = String.join(",", leftover);

					return Arrays.asList(
							Script.wait(0, "todo - " + reminder)
					);
				case STEPS_DONE:
					user.setToyTestingStage(QuestStage.COMPLETE);
					service.save(user);

					return Arrays.asList(
							Script.wait(0, "They all work! Excellent! You have the thanks of many children and one overworked elf."),
							Script.wait(0, "Have this too <prize>")
					);
				case COMPLETE:
					return Arrays.asList(
							Script.wait(0, "todo - thx 4 help")
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
					user.setOrnamentVendorStage(QuestStage.STARTED);
					service.save(user);

					return Arrays.asList(
							Script.wait(0, "This tree is so big it takes a lot of ornaments to fill, " +
									"and I may have uh, lost some of them from last year."),
							Script.wait(0, "Don’t tell Santa! Just help me out. Here in town is an ornament vendor, " +
									"he trades players different wood types that we need for the factory for spare pugmas ornaments."),
							Script.wait(0, "I’d just ask him for some extra myself, but he’s mean and would tell Santa I " +
									"lost the town’s ornaments. If you bring me one of each of the 10 ornaments, I'll reward you")
					);
				case STARTED:
					// TODO: if player has all ornaments, set to steps done and call getscript,
					//  otherwise tell the player which ornament they are missing
					return Arrays.asList(
							Script.wait(0, "todo - reminder")
					);
				case STEPS_DONE:
					return Arrays.asList(
							Script.wait(0, "todo - steps done")
					);

				case COMPLETE:
					return Arrays.asList(
							Script.wait(0, "todo - already complete")
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
