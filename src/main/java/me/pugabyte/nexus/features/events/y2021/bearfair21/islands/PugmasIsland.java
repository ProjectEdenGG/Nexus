package me.pugabyte.nexus.features.events.y2021.bearfair21.islands;

import lombok.Getter;
import me.pugabyte.nexus.features.events.annotations.Region;
import me.pugabyte.nexus.features.events.models.BearFairIsland.NPCClass;
import me.pugabyte.nexus.features.events.y2021.bearfair21.islands.PugmasIsland.PugmasNPCs;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.BearFair21TalkingNPC;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

// TODO BF21: Quest + Dialog
@Region("pugmas")
@NPCClass(PugmasNPCs.class)
public class PugmasIsland implements Listener, BearFair21Island {

	public enum PugmasNPCs implements BearFair21TalkingNPC {
		;

		@Getter
		private final int npcId;
		private final String name;
		@Getter
		private final List<String> script;

		@Override
		public String getName() {
			return this.name;
		}

		PugmasNPCs(String name, int npcId) {
			this.name = name;
			this.npcId = npcId;
			this.script = new ArrayList<>();
		}
	}
}
