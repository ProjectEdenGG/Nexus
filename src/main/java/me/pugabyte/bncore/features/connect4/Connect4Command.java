package me.pugabyte.bncore.features.connect4;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.connect4.models.Connect4Game;
import me.pugabyte.bncore.features.connect4.models.Connect4Team;
import me.pugabyte.bncore.models.exceptions.InvalidInputException;
import me.pugabyte.bncore.models.exceptions.NoPermissionException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.pugabyte.bncore.features.connect4.Connect4.PREFIX;

public class Connect4Command implements CommandExecutor {
	private Connect4Game game = new Connect4Game();

	Connect4Command() {
		BNCore.registerCommand("connect4", this);
	}

	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		try {
			checkSender(sender);
			if (args.length > 0) {
				switch (args[0]) {
					case "reload":
						BNCore.getInstance().reloadConfig();
						sender.sendMessage(PREFIX + "Config reloaded");
						return true;
					case "clear":
					case "reset":
						game.reset();
						return true;
					case "place":
						if (args.length == 3) {
							if (!validate(args[2])) {
								throw new InvalidInputException("Incorrect arguments");
							}
							place(Connect4Team.valueOf(args[1].toUpperCase()), Integer.parseInt(args[2]));
							return true;
						}
						throw new InvalidInputException("Not enough arguments");
				}
			}
			throw new InvalidInputException("");
		} catch (InvalidInputException | NoPermissionException ex) {
			Bukkit.getConsoleSender().sendMessage(ex.getMessage());
			sender.sendMessage(PREFIX + ex.getMessage());
			return true;
		}
	}

	private void checkSender(CommandSender sender) throws NoPermissionException {
		if (sender instanceof Player) {
			if (sender.hasPermission("connect4.admin")) {
				return;
			}
		} else {
			return;
		}
		throw new NoPermissionException();
	}

	private boolean validate(String col) {
		int column = Integer.parseInt(col);
		return !(column < 0 || column > 7);
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
