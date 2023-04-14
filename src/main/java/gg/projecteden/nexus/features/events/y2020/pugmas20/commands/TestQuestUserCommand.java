package gg.projecteden.nexus.features.events.y2020.pugmas20.commands;

import gg.projecteden.nexus.features.events.models.QuestStage;
import gg.projecteden.nexus.features.events.y2020.pugmas20.quests.Quests.Pugmas20Quest;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.testquestuser.TestQuestUser;
import gg.projecteden.nexus.models.testquestuser.TestQuestUserService;
import lombok.NonNull;

@HideFromWiki
@Permission(Group.ADMIN)
public class TestQuestUserCommand extends CustomCommand {
	private TestQuestUserService service = new TestQuestUserService();
	private TestQuestUser user;

	public TestQuestUserCommand(@NonNull CommandEvent event) {
		super(event);
		user = service.get(player());
	}

	@Path("getStage <quest>")
	void getStage(Pugmas20Quest quest) {
		send(PREFIX + "Stage for quest " + camelCase(quest) + ": " + camelCase(user.getQuestProgress(quest).getQuestStage()));
	}

	@Path("setStage <quest> <stage>")
	void setStage(Pugmas20Quest quest, QuestStage stage) {
		user.getQuestProgress(quest).setQuestStage(stage);
		service.save(user);
		send(PREFIX + "Set stage to " + camelCase(stage) + " for quest " + camelCase(quest));
	}

}
