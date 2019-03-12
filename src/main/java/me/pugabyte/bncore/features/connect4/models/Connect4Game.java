package me.pugabyte.bncore.features.connect4.models;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static me.pugabyte.bncore.features.connect4.Connect4.PREFIX;

public class Connect4Game {
	public boolean won = false;
	int[][] board = newGame();

	public boolean hasBeenWon() {
		return won;
	}

	public void win(Connect4Team team) {
		won = true;
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getWorld().getName().equals("gameworld")) {
				String teamName = team.getColor() + "" + team.name().charAt(0) + team.name().substring(1).toLowerCase() + " Team";
				player.sendMessage(PREFIX + teamName + ChatColor.WHITE + " has won Connect4!");
			}
		}
	}

	public int[][] newGame() {
		int[][] newGame = new int[6][7];
		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 7; col++) {
				newGame[row][col] = 8;
			}
		}
		return newGame;
	}

	public void reset() {
		won = false;
		board = newGame();
	}

	// Designed By: https://codereview.stackexchange.com/users/104089/4castle
	public boolean checkWin() {
		final int HEIGHT = board.length;
		final int WIDTH = board[0].length;
		final int EMPTY_SLOT = 8;
		for (int r = 0; r < HEIGHT; r++) { // iterate rows, bottom to top
			for (int c = 0; c < WIDTH; c++) { // iterate columns, left to right
				int color = board[r][c];
				if (color == EMPTY_SLOT)
					continue; // don't check empty slots

				if (c + 3 < WIDTH &&
						color == board[r][c + 1] && // look right
						color == board[r][c + 2] &&
						color == board[r][c + 3])
					return true;
				if (r + 3 < HEIGHT) {
					if (color == board[r + 1][c] && // look up
							color == board[r + 2][c] &&
							color == board[r + 3][c])
						return true;
					if (c + 3 < WIDTH &&
							color == board[r + 1][c + 1] && // look up & right
							color == board[r + 2][c + 2] &&
							color == board[r + 3][c + 3])
						return true;
					if (c - 3 >= 0 &&
							color == board[r + 1][c - 1] && // look up & left
							color == board[r + 2][c - 2] &&
							color == board[r + 3][c - 3])
						return true;
				}
			}
		}
		return false;
	}

	public int[][] getBoard() {
		return board;
	}

}
