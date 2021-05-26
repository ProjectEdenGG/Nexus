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
		WAKKAFLOCKA(BearFair21NPC.ORGANIZER) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				if (!user.hasMet(this.getNpcId())) {
					script.add("Welcome to Bear Fair, Project Eden's anniversary event!");
					script.add("wait 80");
					script.add("This year features several islands to explore, find easter eggs, and do quests!");
					script.add("wait 80");
					script.add("At the carnival, you can play daily minigames in which you can play to gain Event Points.");
					script.add("wait 80");
					script.add("At any point you can buy unique prizes and perks using those points.");
					script.add("wait 80");
					script.add("There are many ways to acquire the currency here, you should talk to the local merchants.");
					script.add("wait 80");
					script.add("And if you need help figuring out where you are, check out this map to my side.");
				}

				return script;
			}
		},
		ADMIRAL(BearFair21NPC.ADMIRAL) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
				} else {
					script.add("TODO");
				}

				return script;
			}
		},
		// Side Quests
		BEEKEEPER(BearFair21NPC.BEEKEEPER) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
				} else {
					script.add("TODO");
				}

				return script;
			}
		},
		FISHERMAN2(BearFair21NPC.FISHERMAN2) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
				} else {
					script.add("TODO");
					script.add("wait 20");
					script.add("You can get useful materials from recycling");
					script.add("wait 20");
					script.add("The more trash you recycle, the less trash you will catch");
					script.add("wait 20");
					script.add("You've recycled: " + user.getRecycledItems() + " trash");
				}


				return script;
			}
		},
		LUMBERJACK(BearFair21NPC.LUMBERJACK) {
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
				} else {
					script.add("TODO");
				}

				return script;
			}
		},
		// Main Quest
		MAYOR(BearFair21NPC.MAYOR) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
				} else {
					script.add("TODO");
				}

				return script;
			}

		},
		// Merchants
		ARTIST(BearFair21NPC.ARTIST) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
					script.add("wait 20");
				}

				return script;
			}
		},
		BAKER(BearFair21NPC.BAKER) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
					script.add("wait 20");
				}

				return script;
			}
		},
		BARTENDER(BearFair21NPC.BARTENDER) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
					script.add("wait 20");
				}

				return script;
			}
		},
		BLACKSMITH(BearFair21NPC.BLACKSMITH) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
					script.add("wait 20");
				}

				return script;
			}
		},
		BOTANIST(BearFair21NPC.BOTANIST) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
					script.add("wait 20");
				}

				return script;
			}
		},
		BREWER(BearFair21NPC.BREWER) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
					script.add("wait 20");
				}

				return script;
			}
		},
		COLLECTOR(BearFair21NPC.COLLECTOR) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
					script.add("wait 20");
				}

				return script;
			}
		},
		FISHERMAN1(BearFair21NPC.FISHERMAN1) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
					script.add("wait 20");
				}

				return script;
			}
		},
		INVENTOR(BearFair21NPC.INVENTOR) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
					script.add("wait 20");
				}

				return script;
			}
		},
		PASTRY_CHEF(BearFair21NPC.PASTRY_CHEF) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
					script.add("wait 20");
				}

				return script;
			}
		},
		SORCERER(BearFair21NPC.SORCERER) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				if (!user.hasMet(this.getNpcId())) {
					script.add("TODO - Greeting");
					script.add("wait 20");
				}

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
