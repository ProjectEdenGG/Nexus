package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.features.wiki._WikiSearchCommand.WikiType;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.rule.HasReadRules.RulesSection;
import gg.projecteden.nexus.models.rule.HasReadRules.RulesType;
import gg.projecteden.nexus.utils.JsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class RulesCommand extends CustomCommand {

	public RulesCommand(CommandEvent event) {
		super(event);
	}

	@Path("search <string...>")
	@Description("Search for a specific rule on the server")
	void search(String filter) {
		List<JsonBuilder> searched = new ArrayList<>();
		for (RulesType rulesType : RulesType.values())
			searched.addAll(search(rulesType, filter));

		if (searched.size() == 0)
			error("No results found");

		line();
		for (JsonBuilder message : searched)
			send(message);
	}

	private List<JsonBuilder> search(RulesType rulesType, String filter) {
		List<JsonBuilder> results = new ArrayList<>();
		for (RulesSection page : rulesType.getPages())
			for (JsonBuilder message : page.getRules())
				if (message.toString().toLowerCase().contains(filter.toLowerCase()))
					results.add(message);

		if (results.isEmpty())
			return new ArrayList<>();

		return new ArrayList<>() {{
			add(json("&3[+] &e" + camelCase(rulesType) + " Rules").command("/rules " + rulesType.name().toLowerCase()));
			add(json(" "));
			addAll(results);
			add(json(" "));
		}};
	}

	@Path
	@Description("View the server's rules")
	void menu() {
		send("&3Project Eden's rules are divided into categories; &e&lclick on the lines below&3 to read the rules for each category.");
		line();

		for (RulesType rulesType : RulesType.values())
			if (!rulesType.isHide())
				send(json("&3[+] &e" + camelCase(rulesType) + " Rules").command("/rules " + rulesType.name().toLowerCase()));

		send(json("&3[+] &eOther Rules").url(WikiType.SERVER.getBasePath() + "Main_Page#Discord_rules"));
		line();

		RulesSection.MAIN.markRead(player());
	}

	@Path("[section] [page]")
	@HideFromWiki
	void menu(RulesType rulesType, @Arg("1") int page) {
		try {
			rulesType.getPages().get(page - 1).show(player());
		} catch (IndexOutOfBoundsException ex) {
			error("Page &e" + page + " &cof &e" + camelCase(rulesType) + " &cnot found");
		}
	}
}
