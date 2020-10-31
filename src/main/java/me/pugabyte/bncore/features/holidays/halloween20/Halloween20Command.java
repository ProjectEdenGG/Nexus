package me.pugabyte.bncore.features.holidays.halloween20;

import me.pugabyte.bncore.features.holidays.halloween20.models.ComboLockNumber;
import me.pugabyte.bncore.features.holidays.halloween20.models.Pumpkin;
import me.pugabyte.bncore.features.holidays.halloween20.models.QuestStage;
import me.pugabyte.bncore.features.holidays.halloween20.models.SoundButton;
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
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.ArrayList;
import java.util.Arrays;

public class Halloween20Command extends CustomCommand {

	public Halloween20Command(CommandEvent event) {
		super(event);
	}

	@Path
	void tp() {
		player().resetPlayerTime();
		Halloween20User user = new Halloween20Service().get(player());
		if (user.getCombinationStage() == QuestStage.Combination.NOT_STARTED)
			Halloween20.start(player());
		else
			new Gate(player()).teleportIn();
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

	@Path("tp pumpkin original <number>")
	@Permission("group.admin")
	void teleportPumpkinOriginal(Pumpkin pumpkin) {
		player().teleport(pumpkin.getOriginal(), TeleportCause.COMMAND);
	}

	@Path("tp pumpkin end <number>")
	@Permission("group.admin")
	void teleportPumpkinEnd(Pumpkin pumpkin) {
		player().teleport(pumpkin.getEnd(), TeleportCause.COMMAND);
	}

	@Path("tp comboLockNumber <number>")
	@Permission("group.admin")
	void teleportComboLockNumber(ComboLockNumber number) {
		player().teleport(number.getLoc(), TeleportCause.COMMAND);
	}

	@Path("tp button <number>")
	@Permission("group.admin")
	void teleportButton(SoundButton number) {
		player().teleport(number.getLocation(), TeleportCause.COMMAND);
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

}
