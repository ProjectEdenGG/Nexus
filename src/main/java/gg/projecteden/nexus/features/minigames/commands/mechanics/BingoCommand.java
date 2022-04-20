package gg.projecteden.nexus.features.minigames.commands.mechanics;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.managers.PlayerManager;
import gg.projecteden.nexus.features.minigames.mechanics.Bingo;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.matchdata.BingoMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.Challenge;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IChallengeProgress;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.NonNull;

import java.util.stream.Collectors;

@SuppressWarnings("FieldCanBeLocal")
public class BingoCommand extends CustomCommand {

	private Minigamer minigamer;

	private Match match;
	private Bingo mechanic;
	private BingoMatchData matchData;

	public BingoCommand(@NonNull CommandEvent event) {
		super(event);

		if (isPlayerCommandEvent()) {
			minigamer = PlayerManager.get(player());
			if (!minigamer.isIn(Bingo.class))
				error("You must be playing Bingo to use this command");

			match = minigamer.getMatch();
			mechanic = minigamer.getMatch().getMechanic();
			matchData = minigamer.getMatch().getMatchData();
		}
	}

	@Path
	void menu() {
		matchData.check(minigamer);
		new BingoMenu(minigamer).open(player());
	}

	@Confirm
	@Permission(Group.ADMIN)
	@Path("challenge complete <challenge> [player]")
	void complete(Challenge challenge, @Arg("self") Minigamer minigamer) {
		matchData.getData(minigamer).setCompleted(challenge, true);
	}

	@Confirm
	@Permission(Group.ADMIN)
	@Path("challenge reset <challenge> [player]")
	void reset(Challenge challenge, @Arg("self") Minigamer minigamer) {
		matchData.getData(minigamer).setCompleted(challenge, false);
	}

	@Title("Bingo")
	private static class BingoMenu extends InventoryProvider {
		private final Minigamer minigamer;
		private final BingoMatchData matchData;

		public BingoMenu(Minigamer minigamer) {
			this.minigamer = minigamer;
			if (!minigamer.isIn(Bingo.class))
				throw new InvalidInputException("You must be playing Bingo to use this command");

			matchData = minigamer.getMatch().getMatchData();
		}

		@Override
		public void init() {
			addCloseItem();

			int row = 1;
			int column = 2;

			for (Challenge[] array : matchData.getChallenges()) {
				for (Challenge challenge : array) {
					final ItemBuilder builder = challenge.getDisplayItem();
					final IChallengeProgress progress = matchData.getProgress(minigamer, challenge);
					if (progress.isCompleted(challenge))
						builder.glow().lore("&aCompleted");
					else
						builder.lore("&cRemaining Tasks").lore(progress.getRemainingTasks(challenge).stream().map(task -> "&7☐ " + task).collect(Collectors.toSet()));

					contents.set(row, column, ClickableItem.empty(builder.build()));
					++column;
				}

				++row;
				column = 2;
			}
		}

	}

}
