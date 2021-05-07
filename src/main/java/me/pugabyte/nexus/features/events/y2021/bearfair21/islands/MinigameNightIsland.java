package me.pugabyte.nexus.features.events.y2021.bearfair21.islands;

import me.pugabyte.nexus.features.events.annotations.Region;
import me.pugabyte.nexus.features.events.models.BearFairIsland;
import me.pugabyte.nexus.features.events.models.BearFairIsland.NPCClass;
import me.pugabyte.nexus.features.events.models.Talker.TalkingNPC;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.features.events.y2021.bearfair21.islands.MinigameNightIsland.MinigameNightNPCs;
import org.bukkit.event.Listener;

import java.util.List;

@Region("minigamenight")
@NPCClass(MinigameNightNPCs.class)
public class MinigameNightIsland implements Listener, BearFairIsland {
	@Override
	public String getEventRegion() {
		return BearFair21.getRegion();
	}

	public enum MinigameNightNPCs implements TalkingNPC {
		;

		@Override
		public int getNpcId() {
			return 0;
		}

		@Override
		public List<String> getScript() {
			return null;
		}
	}
}
