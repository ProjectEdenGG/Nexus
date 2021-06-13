package me.pugabyte.nexus.features.events.y2021.bearfair21.islands;

import eden.utils.RandomUtils;
import me.pugabyte.nexus.features.events.annotations.Region;
import me.pugabyte.nexus.features.events.models.BearFairIsland.NPCClass;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.features.events.y2021.bearfair21.islands.PugmasIsland.PugmasNPCs;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.BearFair21TalkingNPC;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC;
import me.pugabyte.nexus.models.bearfair21.BearFair21User;
import me.pugabyte.nexus.models.bearfair21.BearFair21UserService;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO BF21: Quest + Dialog
@Region("pugmas")
@NPCClass(PugmasNPCs.class)
public class PugmasIsland implements Listener, BearFair21Island {
	static BearFair21UserService userService = new BearFair21UserService();

	private static final List<Location> presets = Arrays.asList(loc(-89, 123, -295), loc(-88, 123, -295),
			loc(-87, 123, -295), loc(-86, 123, -295), loc(-85, 123, -295));

	public enum PugmasNPCs implements BearFair21TalkingNPC {
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

		PugmasNPCs(BearFair21NPC npc) {
			this.npc = npc;
			this.script = new ArrayList<>();
		}
	}

	private static Location loc(int x, int y, int z){
		return new Location(BearFair21.getWorld(), x, y, z);
	}

	public static Block getPresentBlock(){
		return RandomUtils.randomElement(presets).getBlock();
	}
}
