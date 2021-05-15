package me.pugabyte.nexus.features.events.y2021.bearfair21.islands;

import lombok.Getter;
import me.pugabyte.nexus.features.events.annotations.Region;
import me.pugabyte.nexus.features.events.models.BearFairIsland.NPCClass;
import me.pugabyte.nexus.features.events.y2021.bearfair21.islands.SummerDownUnderIsland.SummerDownUnderNPCs;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.BearFair21TalkingNPC;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

// TODO BF21: Quest + Dialog
@Region("summerdownunder")
@NPCClass(SummerDownUnderNPCs.class)
public class SummerDownUnderIsland implements Listener, BearFair21Island {

	public enum SummerDownUnderNPCs implements BearFair21TalkingNPC {
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

		SummerDownUnderNPCs(String name, int npcId) {
			this.name = name;
			this.npcId = npcId;
			this.script = new ArrayList<>();
		}
	}
}
