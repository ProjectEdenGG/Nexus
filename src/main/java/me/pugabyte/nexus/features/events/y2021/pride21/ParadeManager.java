package me.pugabyte.nexus.features.events.y2021.pride21;

import lombok.Getter;
import me.pugabyte.nexus.features.events.models.Talker;
import me.pugabyte.nexus.models.pride21.Pride21UserService;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ParadeManager implements Talker.TalkingNPC {
	@Getter
	private final List<String> script = List.of(
			"Oh, what am I gonna do... what am I gonna- er, oh, hello there!",
			"wait 80",
			"<self> Hello! Is this where the Pride Parade is going to be this year?",
			"wait 80",
			"Yes! It seems you've arrived earlier than expected, <player>. I mean, well, this is where the Pride Parade was supposed to be...",
			"wait 100",
			"<self> \"Supposed to be\"? What happened to it?",
			"wait 60",
			"Well, you see, I may have... well I kind of- ugh, I lost the parade decorations, okay? I was supposed to be watching them to distribute to the floats but... they ended up scattered around the city.",
			"wait 110",
			"<self> How do bags of parade decorations just up and disappear?",
			"wait 80",
			"Eh? That's not the point right now! If we don't get these decorations back then the event will be cancelled...",
			"wait 80",
			"<self> Sorry, \"we\"?",
			"wait 60",
			"Yes, exactly! \"We\"! Will you help me find the decorations before the parade starts?",
			"wait 60",
			"<self> I don't really think I have much of a choice...",
			"wait 70",
			"That's the spirit! Now you'll be looking for 6 bags of rainbow decorations, when you find them I'll trade you the decorations for one fact per bag! Now that's a deal, be on your way now and good luck!",
			"wait 110",
			"<self> Thank you? I'll make sure to find all the decorations."
	);
	@Getter
	private final List<String> completionScript = List.of(
			"Oh! Have you found the last bag?",
			"wait 60",
			"<self> Yup! With this, all the decorations are returned.",
			"wait 80",
			"Awesome! We did it with just enough time left to prepare for the celebration.",
			"wait 80",
			"<self> ...we?",
			"wait 60",
			"Er- ehehe, thank you for the help <player>! I have no idea what I'd do if the parade had to be cancelled because of a mistake I made...",
			"wait 80",
			"<self> Well there's no way I was going to miss out on Pride this year, especially if I could do something about it.",
			"wait 80",
			"Well I'm happy to know that you're excited! I promise to make this a parade to remember with the decorations you recovered.",
			"wait 80",
			"As a thank you for helping me out, please stop by my stall at the parade, I'll give you two free flags or bunting of your choice. Past that, you'll be able to buy them with those event tokens.",
			"wait 110"
	);

	private static final Pride21UserService service = new Pride21UserService();

	@Override
	public String getName() {
		return "Parade Manager";
	}

	@Override
	public int getNpcId() {
		return 3850;
	}

	@Override
	public List<String> getScript(Player player) {
		if (!Pride21.QUESTS_ENABLED())
			return new ArrayList<>();
		return service.get(player).isComplete() ? getCompletionScript() : getScript();
	}
}
