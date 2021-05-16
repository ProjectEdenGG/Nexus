package me.pugabyte.nexus.features.events.y2021.bearfair21.islands;

import me.pugabyte.nexus.features.events.annotations.Region;
import me.pugabyte.nexus.features.events.models.BearFairIsland.NPCClass;
import me.pugabyte.nexus.features.events.y2021.bearfair21.islands.MainIsland.MainNPCs;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.BearFair21TalkingNPC;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC;
import me.pugabyte.nexus.models.bearfair21.BearFair21User;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

// TODO BF21: Quest + Dialog
@Region("main")
@NPCClass(MainNPCs.class)
public class MainIsland implements Listener, BearFair21Island {

	public enum MainNPCs implements BearFair21TalkingNPC {
		WakkaFlocka(BearFair21NPC.WAKKAFLOCKA) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				script.add("TODO");
				return script;
			}
		},
		Captain(BearFair21NPC.CAPTAIN) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				script.add("TODO");
				return script;
			}
		},
		// Side Quests
		BeeKeeper(BearFair21NPC.BEEKEEPER) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				script.add("TODO");
				return script;
			}
		},
		Fisherman2(BearFair21NPC.FISHERMAN2) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				script.add("TODO");
				script.add("wait 20");
				script.add("You can get useful materials from recycling");
				script.add("wait 20");
				script.add("The more trash you recycle, the less trash you will catch");
				script.add("wait 20");
				script.add("You've recycled: " + user.getRecycledItems() + " trash");
				return script;
			}
		},
		Lumberjack(BearFair21NPC.LUMBERJACK) {
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				script.add("TODO");
				return script;
			}
		},
		// Main Quest
		Mayor(BearFair21NPC.MAYOR) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				script.add("TODO");
				return script;
			}

		},
		;

		private final BearFair21NPC npc;
		private final List<String> script;

		@Override
		public List<String> getScript(BearFair21User user) {
			return this.script;
		}

		@Override
		public String getName() {
			return this.npc.getName();
		}

		@Override
		public int getNpcId() {
			return this.npc.getId();
		}

		MainNPCs(BearFair21NPC npc) {
			this.npc = npc;
			this.script = new ArrayList<>();
		}
	}
}
