package me.pugabyte.nexus.features.events.y2021.pride21;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.pride21.Pride21User;
import me.pugabyte.nexus.models.pride21.Pride21UserService;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

import static eden.utils.StringUtils.plural;

public class Quests implements Listener {
	public Quests() {
		Nexus.registerListener(this);
		PlayerUtils.Dev.LEXI.send("hi");
	}

	private static final Pride21UserService service = new Pride21UserService();
	// Pride Related Facts, for display after
	private static final List<String> FACTS = List.of(
			"In 1918, poets and writers Elsa Gidlow and Roswell George Mills launched \"Les Mouches fantastiques\" in Montreal. It is regarded as the first LGBTQ+ publication in Canada, and North America.",
			"In 1967, the Oscar Wilde memorial bookshop opened in New York City. It was the first gay bookshop in the world.",
			"It is believed that the term 'lesbian' comes from the Greek island Lesbos. Sappho, a Greek poetess known for her poetry about the beauty of other women, and her love for them, was from the island.",
			"Amsterdam celebrates pride a little differently, their floats actually 'float'! The pride celebration takes place on 100 decorated boats that sail through the city on Prinsengracht and Amstel Rivers.",
			"The original pride flag featured 8 colors, each with a distinct meaning assigned. Hot pink (Sex), Red (Life), Orange (Healing), Yellow (Sunlight), Green (Nature), Turquoise (Magic and Art), Indigo (Serenity), and Violet (Spirit).",
			"Gilbert Baker was an American artist, gay rights activist, and the original designer of the rainbow flag (1978). This flag design was originally changed due to the fact that Hot Pink wasn't available, because of this they dropped two of the colours (Hot pink and Turquoise) so there was an even number of colours to line the streets with for the Pride Parade."
	);

	@EventHandler
	public void onClickDecoration(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getClickedBlock() == null) return;
		Decorations decoration = Decorations.getByLocation(event.getClickedBlock());
		if (decoration == null) return;
		Pride21User user = service.get(event.getPlayer());
		JsonBuilder json = JsonBuilder.fromPrefix("Pride");
		if (!Pride21.QUESTS_ENABLED()) {
			user.sendMessage(json.next("You've found a bag of rainbow decorations from the &e2021 Pride event&3!"));
			return;
		}


		boolean sendFact = !user.getDecorationsCollected().contains(decoration);
		if (!sendFact) {
			if (user.getDecorationsCollected().size() == Decorations.values().length)
				json.next("You've already found all the decorations! You should take them back to the &eParade Manager&3.");
			else
				addLeftToFind(json.next("You've already found this decoration!"), user);
		} else {
			user.getDecorationsCollected().add(decoration);
			service.save(user);
			if (user.getDecorationsCollected().size() == Decorations.values().length)
				json.next("You've found the last bag! You should take them back to the &eParade Manager&3.");
			else
				addLeftToFind(json.next("You've found a bag of decorations!"), user);
		}
		user.sendMessage(json);
		user.sendMessage(new JsonBuilder(NamedTextColor.DARK_AQUA).next("&8&l[").next(StringUtils.Rainbow.apply("Fun Fact")).next("&8&l]").next(" " + FACTS.get(decoration.ordinal())));
	}

	private static JsonBuilder addLeftToFind(JsonBuilder json, Pride21User user) {
		int decorationsLeft = Decorations.values().length - user.getDecorationsCollected().size();
		return json.next(" You have").next(plural("&e " + decorationsLeft + " bag", decorationsLeft) + "&3 left to find.");
	}
}
