package me.pugabyte.nexus.features.events.y2021.bearfair21.islands;

import lombok.Getter;
import me.pugabyte.nexus.features.events.annotations.Region;
import me.pugabyte.nexus.features.events.models.BearFairIsland.NPCClass;
import me.pugabyte.nexus.features.events.y2021.bearfair21.islands.HalloweenIsland.HalloweenNPCs;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.BearFair21TalkingNPC;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

// TODO BF21: Quest + Dialog
@Region("halloween")
@NPCClass(HalloweenNPCs.class)
public class HalloweenIsland implements Listener, BearFair21Island {

	public enum HalloweenNPCs implements BearFair21TalkingNPC {
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

		HalloweenNPCs(String name, int npcId) {
			this.name = name;
			this.npcId = npcId;
			this.script = new ArrayList<>();
		}
	}


}
