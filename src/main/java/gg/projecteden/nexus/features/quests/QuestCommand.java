package gg.projecteden.nexus.features.quests;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.models.quests.QuesterService;

import java.util.List;

public class QuestCommand extends CustomCommand {
	private final QuesterService questerService = new QuesterService();
	private Quester quester;

	public QuestCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			quester = questerService.get(player());
	}

	@Permission(Group.ADMIN)
	@Path("item <item> [amount]")
	@Description("Spawn a quest item")
	void quest_item(CommonQuestItem item, @Arg("1") int amount) {
		giveItems(item.get(), amount);
	}

	@TabCompleterFor(CommonQuestItem.class)
	List<String> tabCompleteCommonQuestItem(String filter) {
		return tabCompleteEnum(filter, CommonQuestItem.class);
	}

	@ConverterFor(CommonQuestItem.class)
	CommonQuestItem convertToCommonQuestItem(String value) {
		return (CommonQuestItem) convertToEnum(value, CommonQuestItem.class);
	}
}
