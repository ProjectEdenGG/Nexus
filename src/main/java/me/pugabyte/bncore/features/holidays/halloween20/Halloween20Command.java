package me.pugabyte.bncore.features.holidays.halloween20;

import me.pugabyte.bncore.features.holidays.halloween20.models.QuestStage;
import me.pugabyte.bncore.features.holidays.halloween20.quest.Gate;
import me.pugabyte.bncore.features.holidays.halloween20.quest.menus.Halloween20Menus;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.halloween20.Halloween20Service;
import me.pugabyte.bncore.models.halloween20.Halloween20User;
import org.bukkit.OfflinePlayer;

@Permission("group.staff")
public class Halloween20Command extends CustomCommand {

	public Halloween20Command(CommandEvent event) {
		super(event);
	}

	@Path("reset [player]")
	void reset(@Arg("self") OfflinePlayer player) {
		Halloween20Service service = new Halloween20Service();
		Halloween20User user = service.get(player);
		user.setLostPumpkinsStage(QuestStage.LostPumpkins.NOT_STARTED);
		user.setCombinationStage(QuestStage.Combination.NOT_STARTED);
		service.save(user);
	}

	@Path("picture")
	void picture() {
		Halloween20Menus.openPicturePuzzle(player(), null);
	}

	@Path("flashCard")
	void flash() {
		Halloween20Menus.openFlashCardPuzzle(player(), null);
	}

	@Path("gate open")
	void openGate() {
		new Gate(player()).open();
	}

	@Path("gate close")
	void closeGate() {
		new Gate(player()).close();
	}

}
