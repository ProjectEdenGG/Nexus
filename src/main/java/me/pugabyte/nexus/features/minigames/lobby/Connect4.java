package me.pugabyte.nexus.features.minigames.lobby;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

@Aliases("c4")
@Permission("group.staff")
public class Connect4 extends CustomCommand {
	public final static String PREFIX = colorize("&f[&cConnect&94&f] ");
	static Connect4Game game = new Connect4Game();

	Connect4(CommandEvent event) {
		super(event);
	}

	@Path("reload")
	@Permission("group.admin")
	void reload() {
		Nexus.getInstance().reloadConfig();
		send(PREFIX + "Config reloaded");
	}

	@Path("(clear|reset)")
	@Permission("group.admin")
	void reset() {
		game.reset();
	}

	@Path("place <team> <column>")
	@Permission("group.admin")
	void place(String team, int column) {
		place(Connect4Team.valueOf(team.toUpperCase()), validate(column));
	}

	@Path("debug")
	@Permission("group.admin")
	void debug() {
		send();
		int[][] board = game.getBoard();
		for (int[] row : board) {
			send(Arrays.toString(row));
		}
		send();
	}

	private int validate(int column) {
		if (column >= 0 && column <= 7)
			return column;
		throw new InvalidInputException("Incorrect arguments");
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

	public enum Connect4Team {
		RED(0, ChatColor.RED),
		BLUE(1, ChatColor.BLUE);

		private int id;
		private ChatColor color;

		Connect4Team(int id, ChatColor color) {
			this.id = id;
			this.color = color;
		}

		public int getId() {
			return id;
		}

		public ChatColor getColor() {
			return color;
		}
	}

	public static class Connect4Game {
		public boolean won = false;
		int[][] board = newGame();

		public boolean hasBeenWon() {
			return won;
		}

		public void win(Connect4Team team) {
			won = true;
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.getWorld() == Minigames.getWorld()) {
					String teamName = team.getColor() + "" + team.name().charAt(0) + team.name().substring(1).toLowerCase() + " Team";
					PlayerUtils.send(player, PREFIX + teamName + ChatColor.WHITE + " has won Connect4!");
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

	// Tournament Command
	@Path("tourney buttons <number> <text>")
	public void boardPlaceButtons(int board, String type) {
		World gameworld = Bukkit.getWorld("gameworld");
		// board 1 vars
		Location place_1 = new Location(gameworld, 2286, 16, -136);
		Location remove_1 = new Location(gameworld, 2286, 16, -133);
		Location reset_1 = new Location(gameworld, 2275, 13, -132);
		// board 2 vars
		Location place_2 = new Location(gameworld, 2294, 16, -136);
		Location remove_2 = new Location(gameworld, 2294, 16, -133);
		Location reset_2 = new Location(gameworld, 2306, 13, -132);

		if (board == 1) {
			switch (type) {
				case "remove":
					triggerCommandBlock(remove_1);
					send(PREFIX + "Removed buttons from board 1");
					break;
				case "place":
					triggerCommandBlock(place_1);
					send(PREFIX + "Placed buttons on board 1");
					break;
				case "reset":
					triggerCommandBlock(reset_1);
					break;
			}
		} else if (board == 2) {
			switch (type) {
				case "remove":
					triggerCommandBlock(remove_2);
					send(PREFIX + "Removed buttons from board 2");
					break;
				case "place":
					triggerCommandBlock(place_2);
					send(PREFIX + "Placed buttons on board 2");
					break;
				case "reset":
					triggerCommandBlock(reset_2);
					break;

			}
		}
	}

	private void triggerCommandBlock(Location location) {
		location.getBlock().setType(Material.REDSTONE_BLOCK);
		Tasks.wait(Time.SECOND.x(1), () -> location.getBlock().setType(Material.AIR));
	}
}
