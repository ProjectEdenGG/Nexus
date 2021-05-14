package me.pugabyte.nexus.features.events.y2021.bearfair21.islands;

import lombok.Getter;
import me.pugabyte.nexus.features.events.annotations.Region;
import me.pugabyte.nexus.features.events.models.BearFairIsland.NPCClass;
import me.pugabyte.nexus.features.events.y2021.bearfair21.islands.MainIsland.MainNPCs;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.BearFair21TalkingNPC;
import me.pugabyte.nexus.models.bearfair21.BearFair21User;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

// TODO BF21: Quest + Dialog
@Region("main")
@NPCClass(MainNPCs.class)
public class MainIsland implements Listener, BearFair21Island {

	public enum MainNPCs implements BearFair21TalkingNPC {
		WakkaFlocka("WakkaFlocka", 3798) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				script.add("TODO");
				return script;
			}
		},
		Captain("Captain", 3839) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				script.add("TODO");
				return script;
			}
		},
		// Side Quests
		BeeKeeper("Harold", 3844) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				script.add("TODO");
				return script;
			}
		},
		Fisherman1("Nate", 3841) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				script.add("TODO");
				script.add("You can get useful materials from recycling");
				script.add("The more trash you recycle, the less trash you will catch");
				script.add("You've recycled: " + user.getRecycledItems() + " trash");
				return script;
			}
		},
		Lumberjack("Flint", 3845) {
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				script.add("TODO");
				return script;
			}
		},
		// Main Quest
		Mayor("John", 3838) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				script.add("TODO");
				return script;
			}

		},
		;

		@Getter
		private final int npcId;
		private final String name;
		private final List<String> script;

		@Override
		public List<String> getScript(BearFair21User user) {
			return this.script;
		}

		@Override
		public String getName() {
			return this.name;
		}

		MainNPCs(String name, int npcId) {
			this.name = name;
			this.npcId = npcId;
			this.script = new ArrayList<>();
		}
	}
}
