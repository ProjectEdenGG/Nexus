package me.pugabyte.bncore.features.connect4;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.connect4.models.Connect4Game;
import me.pugabyte.bncore.features.connect4.models.Connect4Team;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;

@Aliases("c4")
@Permission("connect4.admin")
public class Connect4Command extends CustomCommand {
	private Connect4Game game = BNCore.connect4.game;

	Connect4Command(CommandEvent event) {
		super(event);
	}

	@Path("reload")
	void reload() {
		BNCore.getInstance().reloadConfig();
		reply(PREFIX + "Config reloaded");
	}

	@Path("(clear|reset)")
	void reset() {
		game.reset();
	}

	@Path("place {string} {int}")
	void place(@Arg String team, @Arg int column) {
		place(Connect4Team.valueOf(team.toUpperCase()), validate(column));
	}

	private int validate(int column) {
		if (!(column < 0 || column > 7))
			throw new InvalidInputException("Incorrect arguments");
		return column;
	}

	private void place(Connect4Team team, int col) {
		if (!game.hasBeenWon()) {
			int[][] board = game.getBoard();
			int row = 0;
			while (board[row][col] == 8 && row <= 4) {
				row++;
			}
			if (board[row][col] != 8) {
				row--;
			}

			board[row][col] = team.getId();

			if (game.checkWin()) {
				game.win(team);
			}
		}
	}
}
