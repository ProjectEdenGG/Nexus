package me.pugabyte.bncore.features.holidays.bearfair20.islands;

import me.pugabyte.bncore.BNCore;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.List;

public class SummerDownUnder implements Listener {

	public SummerDownUnder() {
		BNCore.registerListener(this);
	}

	// Clutter NPCS
	static public List<String> SCRIPT_DYLAN = Collections.singletonList("These snags are lookin’ real hot boys hope you’re ready");
	static public List<String> SCRIPT_MATT = Collections.singletonList("OOOOOOH BOOOY IM READY FOR SOME SNAAAAAAAAAAGSSSS");
	static public List<String> SCRIPT_MAX = Collections.singletonList("Alright gents, if the snags are ready let’s get Lach and the boys from the pub here ASAP!");
	static public List<String> SCRIPT_TALITHA = Collections.singletonList("I’ll get the plates!");
	static public List<String> SCRIPT_DECLAN = Collections.singletonList("Oiiii Lachlan ch..uck as anofer beer ya dawwgg!");
	static public List<String> SCRIPT_CAMERON = Collections.singletonList("Shut Up Declan we need to meet the boys at the Ablett’s!");
	static public List<String> SCRIPT_JOSH = Collections.singletonList("Uh, yeah, I’m the deso tonight so just some Soft Drink if you got any mate.");
	static public List<String> SCRIPT_NIKKI = Collections.singletonList("This view is amazing.");
	static public List<String> SCRIPT_NICOLE = Collections.singletonList("I know right… I love farming.");
	static public List<String> SCRIPT_GRIFFIN = Collections.singletonList("Lest we Forget.");
	static public List<String> SCRIPT_TRINITY = Collections.singletonList("We Will Remember Them.");
	static public List<String> SCRIPT_RYAN = Collections.singletonList("Hey mate, wanna have a go in the ‘Lux?");
	static public List<String> SCRIPT_FOREMAN = Collections.singletonList("Who the bloody hell are ya? I have work to do mate, get lost!");
	static public List<String> SCRIPT_DRIVER = Collections.singletonList("Oh man… Lachlan’s gonna kill me. Where is that damn case?");
	static public List<String> SCRIPT_TALISHA = Collections.singletonList("Can you have a squiz at the drinks and suggest anything good? I’m new in town!");
	static public List<String> SCRIPT_TAYLOR = Collections.singletonList("Hey! You must be new here, hope you’re having a ball!");
	static public List<String> SCRIPT_LUCY = Collections.singletonList("We should probably get going soon, I think the party’s almost started. Wanna tag along?");
	static public List<String> SCRIPT_CHRIS = Collections.singletonList("MmmmMmm! Just as good as I remember. Hope I’m not late for the gatho!");

}
