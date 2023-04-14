package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.features.wiki._WikiSearchCommand.WikiType;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Vararg;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.rule.HasReadRules.RulesSection;
import gg.projecteden.nexus.models.rule.HasReadRules.RulesType;
import gg.projecteden.nexus.utils.JsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class RulesCommand extends CustomCommand {

	public RulesCommand(CommandEvent event) {
		super(event);
	}

	@Description("Search for a specific rule on the server")
	void search(@Vararg String filter) {
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

	@NoLiterals
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

	@NoLiterals
	@HideFromWiki
	void menu(@Optional RulesType section, @Optional("1") int page) {
		try {
			section.getPages().get(page - 1).show(player());
		} catch (IndexOutOfBoundsException ex) {
			error("Page &e" + page + " &cof &e" + camelCase(section) + " &cnot found");
		}
	}
}
