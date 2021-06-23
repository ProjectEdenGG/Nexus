package me.pugabyte.nexus.features.events.y2021.bearfair21.islands;

import me.pugabyte.nexus.features.events.annotations.Region;
import me.pugabyte.nexus.features.events.models.BearFairIsland.NPCClass;
import me.pugabyte.nexus.features.events.y2021.bearfair21.islands.SummerDownUnderIsland.SummerDownUnderNPCs;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.BearFair21TalkingNPC;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC;
import me.pugabyte.nexus.models.bearfair21.BearFair21User;

import java.util.ArrayList;
import java.util.List;

// TODO BF21: Quest + Dialog
@Region("summerdownunder")
@NPCClass(SummerDownUnderNPCs.class)
public class SummerDownUnderIsland implements BearFair21Island {

	public enum SummerDownUnderNPCs implements BearFair21TalkingNPC {
		;

		private final BearFair21NPC npc;
		private final List<String> script;

		@Override
		public List<String> getScript(BearFair21User user) {
			return this.script;
		}

		@Override
		public String getName() {
			return this.npc.getNpcName();
		}

		@Override
		public int getNpcId() {
			return this.npc.getId();
		}

		SummerDownUnderNPCs(BearFair21NPC npc) {
			this.npc = npc;
			this.script = new ArrayList<>();
		}
	}
}
