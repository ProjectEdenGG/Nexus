package me.pugabyte.nexus.features.events.y2020.pugmas20.models;

import lombok.Getter;
import me.pugabyte.nexus.features.events.models.QuestStage;
import me.pugabyte.nexus.features.events.models.Script;
import me.pugabyte.nexus.features.events.y2020.pugmas20.models.Merchants.MerchantNPC;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.GiftGiver;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.LightTheTree;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.Ores;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.OrnamentVendor;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.OrnamentVendor.Ornament;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.Quests;
import me.pugabyte.nexus.models.pugmas20.Pugmas20Service;
import me.pugabyte.nexus.models.pugmas20.Pugmas20User;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
					return Arrays.asList(
							Script.wait(0, "todo - reminder")
					);
				case STEP_TWO:
					if (!hasItem(player, LightTheTree.lighter_broken)) {
						return Arrays.asList(
								Script.wait(0, "todo - <did you find the ceremonial lighter?>")
						);
					}

					return Arrays.asList(
							Script.wait(0, "The mechanism is broken! How could I have been so careless."),

							Script.wait(0, "The ceremony is supposed to start soon, but there might be enough time- " +
									"hurry to the tree and find " + ELF2.getName() + " they will know how to fix this.")
					);

				case STEPS_DONE:
					return Arrays.asList(
							Script.wait(0, "You have it! Just in the nick of time. The ceremony shall now begin."),

							Script.wait(0, "Light all the torches around Santa’s Workshop leading up to the tree using the ceremonial lighter, " +
									"don’t forget the one at the base of the tree! You will be timed, so hurry!")
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
					user.getNextStepNPCs().add(ELF1.getId());
					service.save(user);

					return Arrays.asList(
							Script.wait(0, "It's time for our annual tree lighting ceremony, but " + ELF1.getName() +
									" still hasn’t returned with the special lighter!"),

							Script.wait(0, "todo - <ask the player to find elf 1 in the workshop>")
					);
				case STARTED:
					return Arrays.asList(
							Script.wait(0, "todo - <ask the player to find elf 1 in the workshop>")
					);

				case STEP_TWO:
					if (!hasItem(player, LightTheTree.lighter_broken)) {
						return Arrays.asList(
								Script.wait(0, "todo - <have you found " + ELF1.getName() + " and that ceremonial lighter?>")
						);
					}

					user.setLightTreeStage(QuestStage.STEP_THREE);
					user.getNextStepNPCs().add(SUPERVISOR.getId());
					service.save(user);

					return Arrays.asList(
							Script.wait(0, "Dangit " + ELF1.getName() + ", I *told* him to be careful with this."),

							Script.wait(0, "Hmm, yes, it is fixable, just needs a new flint wheel and a steel striker."),

							Script.wait(0, "If you get me a piece of Flint and a steel nugget I can make both fast."),

							Script.wait(0, "Head to the coal mine and you should be able to get both- ask the mine supervisor for help")
					);
				case STEP_THREE:
					ItemStack lighter = getItem(player, LightTheTree.lighter_broken);
					ItemStack steelNugget = getItem(player, LightTheTree.steel_nugget);
					ItemStack flint = getItem(player, Ores.getFlint());
					if (lighter == null || steelNugget == null || flint == null) {
						return Arrays.asList(
								Script.wait(0, "todo - <you seem to be missing a few ingredients>")
						);
					}

					player.getInventory().removeItem(lighter, steelNugget, flint);
					ItemUtils.giveItem(player, LightTheTree.lighter);

					user.setLightTreeStage(QuestStage.STEPS_DONE);
					service.save(user);

					return Arrays.asList(
							Script.wait(0, "There you go, right as rain. Now give this back to " +
									ELF1.getName() + " and tell them to be careful with it this time!")
					);
				case STEPS_DONE:
					return Arrays.asList(
							Script.wait(0, "todo - <reminder>")
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

							Script.wait(0, "Sorry, we don’t have any flint or steel available right now but " +
									"you can certainly go grab your own."),

							Script.wait(0, "Go into the equipment room and grab a sieve and a pick- you will need " +
									"to sift the gavel piles for flint."),

							Script.wait(0, "For the steel you will need <how to obtain steel>")
					);
			}

			if (user.getLightTreeStage().equals(QuestStage.COMPLETE)) {
				switch (user.getMinesStage()) {
					case NOT_STARTED:
						return Arrays.asList(
								Script.wait(0, "Since you’re already cleared for the mine, wanna do me a favor?"),

								Script.wait(0, "There's always a rush of last minute demands for materials by the workshop- " +
										"things that need to be fixed, production that came up a little short."),

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
							Script.wait(0, "Hey kid, I need a favor! After last year’s debacle with the sled, " +
									"half of the Quality Assurance team was fired, and the other half have spent all year " +
									"on making sure that the Sled won’t fall apart again."),

							Script.wait(0, "But that's left just me to try and keep up with testing all the toys " +
									"that come off the line. I’m way, way behind and Pugmas is coming fast."),

							Script.wait(0, "Think you can help an elf out? You might need to find a friend to help you, " +
									"all these games need to be tested before they can be added to the present piles."),

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

					return Arrays.asList(
							Script.wait(0, "todo - " + String.join(", ", leftover))
					);
				case STEPS_DONE:
					user.setToyTestingStage(QuestStage.COMPLETE);
					user.getNextStepNPCs().remove(getId());
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
					user.getNextStepNPCs().add(MerchantNPC.ORNAMENT_VENDOR.getNpcId());
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
					List<ItemStack> ornaments = OrnamentVendor.getOrnaments(player);

					if (ornaments.size() != Ornament.values().length) {
						return Arrays.asList(
								Script.wait(0, "todo - reminder")
						);
					}

					user.setOrnamentVendorStage(QuestStage.COMPLETE);
					user.getNextStepNPCs().remove(getId());
					service.save(user);

					for (ItemStack ornament : ornaments)
						player.getInventory().removeItem(ornament);

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
	},
	GIFT_GIVER(3110) {
		@Override
		public List<Script> getScript(Player player) {
			Pugmas20Service service = new Pugmas20Service();
			Pugmas20User user = service.get(player);

			if (user.getGiftGiverStage() == QuestStage.NOT_STARTED) {
				user.setGiftGiverStage(QuestStage.COMPLETE);
				user.getNextStepNPCs().remove(getId());
				service.save(user);

				GiftGiver.giveGift(player);

				return Arrays.asList(
						Script.wait(0, "Spread some cheer and give this gift to another player!")
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

	public static void startScript(Player player, int id) {
		QuestNPC npc = QuestNPC.getById(id);
		if (npc != null)
			npc.sendScript(player);
	}

	public void sendScript(Player player) {
		List<Script> scripts = getScript(player);
		if (scripts == null || scripts.isEmpty()) return;
		AtomicReference<String> npcName = new AtomicReference<>("");

		AtomicInteger wait = new AtomicInteger(0);
		getName(npcName);

		scripts.forEach(script -> {
			wait.getAndAdd(script.getDelay());

			script.getLines().forEach(line -> {
				line = line.replaceAll("<player>", player.getName());
				if (line.contains("<self>")) {
					npcName.set("&b&lYOU&f");
					line = line.replaceAll("<self> ", "");
				}

				String message = "&3" + npcName.get() + " &7> &f" + line;
				Tasks.wait(wait.get(), () -> {
					Utils.send(player, message);
					Quests.sound_npcAlert(player);
				});
			});
		});
	}

	public abstract List<Script> getScript(Player player);

	private String getName() {
		return getName(new AtomicReference<>(""));
	}

	private String getName(AtomicReference<String> npcName) {
		if (npcName == null)
			npcName = new AtomicReference<>("");

		if (!npcName.get().isEmpty() && npcName.get().equalsIgnoreCase(QA_ELF.name()))
			npcName.set("Q.A. Elf");
		else
			npcName.set(camelCase(name()));
		npcName.set(npcName.get().replaceAll("[0-9]+", ""));

		return npcName.get();
	}

	public boolean hasItem(Player player, ItemStack item) {
		return player.getInventory().containsAtLeast(item, 1);
	}

	public ItemStack getItem(Player player, ItemStack item) {
		for (ItemStack content : player.getInventory().getContents()) {
			if (ItemUtils.isNullOrAir(content))
				continue;

			if (ItemUtils.isFuzzyMatch(item, content))
				return content;
		}
		return null;
	}
}
