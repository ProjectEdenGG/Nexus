package me.pugabyte.nexus.features.events.y2021.bearfair21.islands;

import me.pugabyte.nexus.features.events.annotations.Region;
import me.pugabyte.nexus.features.events.models.BearFairIsland.NPCClass;
import me.pugabyte.nexus.features.events.models.Talker.TalkingNPC;
import me.pugabyte.nexus.features.events.y2021.bearfair21.islands.SummerDownUnderIsland.SummerDownUnderNPCs;
import org.bukkit.event.Listener;

import java.util.List;

@Region("summerdownunder")
@NPCClass(SummerDownUnderNPCs.class)
public class SummerDownUnderIsland implements Listener, BearFair21Island {

	public enum SummerDownUnderNPCs implements TalkingNPC {
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
