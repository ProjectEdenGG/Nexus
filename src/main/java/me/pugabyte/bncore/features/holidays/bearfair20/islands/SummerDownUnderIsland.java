package me.pugabyte.bncore.features.holidays.bearfair20.islands;

import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Island.NPCClass;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Island.Region;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.SummerDownUnderIsland.SummerDownUnderNPCs;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.npcs.Talkers.TalkingNPC;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Region("summerdownunder")
@NPCClass(SummerDownUnderNPCs.class)
public class SummerDownUnderIsland implements Listener, Island {

	public SummerDownUnderIsland() {
		BNCore.registerListener(this);
	}

	public enum SummerDownUnderNPCs implements TalkingNPC {
		DYLAN(2915, Collections.singletonList("These snags are lookin’ real hot boys hope you’re ready")),
		MATT(2916, Collections.singletonList("OOOOOOH BOOOY IM READY FOR SOME SNAAAAAAAAAAGSSSS")),
		MAX(2917, Collections.singletonList("Alright gents, if the snags are ready let’s get Lach and the boys from the pub here ASAP!")),
		TALITHA(2933, Collections.singletonList("I’ll get the plates!")),
		DECLAN(2922, Collections.singletonList("Oiiii Lachlan ch..uck as anofer beer ya dawwgg!")),
		CAMERON(2921, Collections.singletonList("Shut Up Declan we need to meet the boys at the Ablett’s!")),
		JOSH(2918, Collections.singletonList("Uh, yeah, I’m the deso tonight so just some Soft Drink if you got any mate.")),
		NIKKI(2944, Collections.singletonList("This view is amazing.")),
		NICOLE(2945, Collections.singletonList("I know right… I love farming.")),
		GRIFFIN(2931, Collections.singletonList("Lest we Forget.")),
		TRINITY(2932, Collections.singletonList("We Will Remember Them.")),
		RYAN(2923, Collections.singletonList("Hey mate, wanna have a go in the ‘Lux?")),
		FOREMAN(2927, Collections.singletonList("Who the bloody hell are ya? I have work to do mate, get lost!")),
		DRIVER(2930, Collections.singletonList("Oh man… Lachlan’s gonna kill me. Where is that damn case?")),
		TALISHA(2939, Collections.singletonList("Can you have a squiz at the drinks and suggest anything good? I’m new in town!")),
		TAYLOR(2940, Collections.singletonList("Hey! You must be new here, hope you’re having a ball!")),
		LUCY(2941, Collections.singletonList("We should probably get going soon, I think the party’s almost started. Wanna tag along?")),
		CHRIS(2942, Collections.singletonList("MmmmMmm! Just as good as I remember. Hope I’m not late for the gatho!"));

		@Getter
		private final int npcId;
		@Getter
		private final List<String> script;

		SummerDownUnderNPCs(int npcId) {
			this.npcId = npcId;
			this.script = new ArrayList<>();
		}

		SummerDownUnderNPCs(int npcId, List<String> script) {
			this.npcId = npcId;
			this.script = script;
		}
	}

}
