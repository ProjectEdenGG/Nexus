package me.pugabyte.bncore.features.holidays.bearfair20.islands;

import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Island.NPCClass;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Island.Region;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.PugmasIsland.PugmasNPCs;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.npcs.Talkers.TalkingNPC;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Region("pugmas")
@NPCClass(PugmasNPCs.class)
public class PugmasIsland implements Listener, Island {

	public PugmasIsland() {
		BNCore.registerListener(this);
	}

	public enum PugmasNPCs implements TalkingNPC {
		NPC1(9999, Collections.singletonList("Something here"));

		@Getter
		private final int npcId;
		@Getter
		private final List<String> script;

		PugmasNPCs(int npcId) {
			this.npcId = npcId;
			this.script = new ArrayList<>();
		}

		PugmasNPCs(int npcId, List<String> script) {
			this.npcId = npcId;
			this.script = script;
		}
	}
}
