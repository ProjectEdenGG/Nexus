package me.pugabyte.bncore.features.holidays.halloween20;

import me.pugabyte.bncore.features.holidays.halloween20.models.ComboLockNumber;
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

import java.util.ArrayList;
import java.util.Arrays;

public class Halloween20Command extends CustomCommand {

	public Halloween20Command(CommandEvent event) {
		super(event);
	}

	@Path("reset [player]")
	@Permission("group.admin")
	void reset(@Arg("self") OfflinePlayer player) {
		Halloween20Service service = new Halloween20Service();
		Halloween20User user = service.get(player);
		user.setLostPumpkinsStage(QuestStage.LostPumpkins.NOT_STARTED);
		user.setFoundPumpkins(new ArrayList<>());
		user.setCombinationStage(QuestStage.Combination.NOT_STARTED);
		user.setFoundComboLockNumbers(new ArrayList<>());
		service.save(user);
	}

	@Path("number <number>")
	@Permission("group.admin")
	void number(ComboLockNumber number) {
		player().teleport(number.getLoc());
	}

	@Path("picture")
	@Permission("group.admin")
	void picture() {
		Halloween20Menus.openPicturePuzzle(player(), null);
	}

	@Path("flashCard")
	@Permission("group.admin")
	void flash() {
		Halloween20Menus.openFlashCardPuzzle(player(), null);
	}

	@Path("gate open")
	@Permission("group.admin")
	void openGate() {
		new Gate(player()).open();
	}

	@Path("gate close")
	@Permission("group.admin")
	void closeGate() {
		new Gate(player()).close();
	}

	@Path("test combo")
	@Permission("group.admin")
	void testCombo() {
		Halloween20Service service = new Halloween20Service();
		Halloween20User user = service.get(player());
		user.getFoundComboLockNumbers().clear();
		user.getFoundComboLockNumbers().addAll(Arrays.asList(ComboLockNumber.values()));
		service.save(user);
		Halloween20Menus.openComboLock(player());
	}

	@Path
	void tp() {
		Halloween20User user = new Halloween20Service().get(player());
		if (user.getCombinationStage() == QuestStage.Combination.NOT_STARTED)
			Halloween20.start(player());
		else
			new Gate(player()).teleportIn();
	}

}
