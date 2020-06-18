package me.pugabyte.bncore.features.holidays.bearfair20.islands;

import lombok.Getter;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Island.NPCClass;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Island.Region;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.MainIsland.MainNPCs;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.npcs.Talkers.TalkingNPC;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Region("main")
@NPCClass(MainNPCs.class)
public class MainIsland implements Listener, Island {

	public enum MainNPCs implements TalkingNPC {
		NPC1(9999, Collections.singletonList("Something here"));

		@Getter
		private final int npcId;
		@Getter
		private final List<String> script;

		MainNPCs(int npcId) {
			this.npcId = npcId;
			this.script = new ArrayList<>();
		}

		MainNPCs(int npcId, List<String> script) {
			this.npcId = npcId;
			this.script = script;
		}
	}

}
