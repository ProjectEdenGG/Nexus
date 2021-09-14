package gg.projecteden.nexus.features.events.y2021.pugmas21;

import gg.projecteden.nexus.features.events.models.InteractableNPC;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import lombok.Getter;

import static gg.projecteden.nexus.utils.PlayerUtils.runCommandAsConsole;
import static org.bukkit.Sound.BLOCK_NOTE_BLOCK_BELL;

@Getter
public enum Pugmas21InteractableNPC implements InteractableNPC {
	JOE("Joe", 1234) {
		@Override
		public void dialogue() {
			dialogue
				.npc("You're a nerd")
				.player("No you!")
				.thenRun(player -> {
					new SoundBuilder(BLOCK_NOTE_BLOCK_BELL).receiver(player).play();
					PlayerUtils.send(player, "boop");
				})
				.wait(50)
				.thenRun(player -> runCommandAsConsole("slap " + player.getName()))
				.npc("testing 123");
		}
	},
	BOBBY("Bobby", 4321) {
		@Override
		public void dialogue() {
			dialogue
				.npc("You're still a nerd")
				.player("No you!");
		}
	},
	;

	protected final String name;
	protected final int npcId;
	protected Dialogue dialogue;

	Pugmas21InteractableNPC(String name, int npcId) {
		this.name = name;
		this.npcId = npcId;
		this.dialogue = Dialogue.from(this);
		dialogue();
	}

	void dialogue() {
		this.dialogue = null;
	}
}
