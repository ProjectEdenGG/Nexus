package gg.projecteden.nexus.features.minigames.mechanics;

import com.sk89q.worldedit.math.BlockVector3;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.annotations.Scoreboard;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.ChessMatchData;
import gg.projecteden.nexus.features.minigames.models.matchdata.ChessMatchData.Bishop;
import gg.projecteden.nexus.features.minigames.models.matchdata.ChessMatchData.ChessLogger;
import gg.projecteden.nexus.features.minigames.models.matchdata.ChessMatchData.ChessLogger.ChessMatchResult;
import gg.projecteden.nexus.features.minigames.models.matchdata.ChessMatchData.ChessMove;
import gg.projecteden.nexus.features.minigames.models.matchdata.ChessMatchData.ChessPiece;
import gg.projecteden.nexus.features.minigames.models.matchdata.ChessMatchData.ChessPiece.ChessPieceType;
import gg.projecteden.nexus.features.minigames.models.matchdata.ChessMatchData.King;
import gg.projecteden.nexus.features.minigames.models.matchdata.ChessMatchData.Knight;
import gg.projecteden.nexus.features.minigames.models.matchdata.ChessMatchData.Pawn;
import gg.projecteden.nexus.features.minigames.models.matchdata.ChessMatchData.Queen;
import gg.projecteden.nexus.features.minigames.models.matchdata.ChessMatchData.Rook;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import gg.projecteden.nexus.features.minigames.models.scoreboards.MinigameScoreboard.Type;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.resourcepack.models.font.InventoryTexture;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Scoreboard(sidebarType = Type.MINIGAMER)
public class Chess extends TeamMechanic {

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemBuilder(ItemModelType.CHESS_PAWN).dyeColor(ColorType.GRAY).build();
	}

	@Override
	public @NotNull String getName() {
		return "Chess";
	}

	@Override
	public @NotNull String getDescription() {
		return "This is the classic game of Chess built right into Minecraft";
	}

	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		placePieces(event.getMatch());

		event.getMatch().getArena().getTeams().forEach(team -> {
			event.getMatch().setScore(team, 12);
		});

		super.onStart(event);

		event.getMatch().getTasks().wait(1, () -> {
			if (event.getMatch().isStarted() && !event.getMatch().isEnded())
				event.getMatch().getTasks().repeat(1, 1, () -> sendActionBars(event.getMatch()));
		});

		event.getMatch().getMinigamers().forEach(mg -> {
			mg.getPlayer().showTitle(Title.title(new JsonBuilder("&e&lChess").asComponent(), new JsonBuilder("&3You are on team ").next(mg.getTeam().asComponent()).asComponent()));
		});
		((ChessMatchData) event.getMatch().getMatchData()).setLogger(new ChessLogger(event.getMatch()));
	}

	@Override
	public void end(@NotNull Match match) {
		announceChessWinners(match);
		Tasks.wait(TickTime.SECOND.x(5), () -> super.end(match));
	}

	@Override
	public void onEnd(@NotNull MatchEndEvent event) {
		ChessMatchData matchData = event.getMatch().getMatchData();
		String paste = StringUtils.paste(String.join("\n", matchData.getLogger().getFullPGNFile()));
		event.getMatch().getMinigamers().forEach(minigamer -> {
			minigamer.sendMessage(new JsonBuilder("&e&lClick here to view a replay of the game").hover("&3View the PGN log of the game").url(paste));
		});

		super.onEnd(event);
		if (matchData.getSelectedPiece() != null)
			matchData.getSelectedPiece().deselect(matchData);

		placePieces(event.getMatch());
	}

	@Override
	public void announceWinners(@NotNull Match match) {}

	public void announceChessWinners(@NotNull Match match) {
		ChessMatchData matchData = match.getMatchData();

		if (matchData.getWinnerTeam() == null) {
			Minigames.broadcast("&eChess &3has ended in a stalemate");
			return;
		}

		final Minigamer winner = matchData.getWinnerTeam().getMinigamers(match).get(0);
		match.getMatchStatistics().award(MatchStatistics.WINS, winner);
		Minigames.broadcast(winner.getColoredName() + " &3has won &eChess");
	}

	@Override
	public @NotNull LinkedHashMap<String, Integer> getScoreboardLines(@NotNull Minigamer minigamer) {
		LinkedHashMap<String, Integer> lines = new LinkedHashMap<>();
		Match match = minigamer.getMatch();
		ChessMatchData matchData = match.getMatchData();

		if (match.isStarted()) {
			lines.put("&e&lPlayers", Integer.MIN_VALUE);
			lines.put("&f" + Nickname.of(matchData.getWhiteTeam()), Integer.MIN_VALUE);
			lines.put("&8" + Nickname.of(matchData.getBlackTeam()), Integer.MIN_VALUE);
			lines.put("&f", Integer.MIN_VALUE);

			renderWinChance(lines, matchData.getWinChance());

			if (matchData.getLogger() != null) {
				lines.put("&f&f", Integer.MIN_VALUE);
				lines.put("&e&lMove History", Integer.MIN_VALUE);

				List<String> pgn = matchData.getLogger().toPGN();
				int size = pgn.size();
				int pad = Math.max(0, 5 - size);

				List<String> result = new ArrayList<>();

				for (int i = 0; i < pad; i++)
					result.add("-");

				for (int i = Math.max(0, size - 5); i < size; i++)
					result.add(pgn.get(i));

				for (int i = 0; i < result.size(); i++)
					lines.put(result.get(i), Integer.MIN_VALUE);
			}
		}
		else
			match.getMinigamers().forEach(mg -> lines.put(mg.getNickname(), Integer.MIN_VALUE));

		return lines;
	}

	private void renderWinChance(LinkedHashMap<String, Integer> lines, double percentage) {
		int totalBars = 16;
		int whiteBars = (int) Math.round((percentage / 100.0) * totalBars);
		int blackBars = totalBars - whiteBars;

		StringBuilder bar = new StringBuilder("&f");
		for (int i = 0; i < whiteBars; i++) bar.append("|");
		bar.append("&8");
		for (int i = 0; i < blackBars; i++) bar.append("|");

		lines.put("&e&lEvaluation", Integer.MIN_VALUE);
		lines.put(bar.toString(), Integer.MIN_VALUE);
	}

	private void placePieces(Match match) {
		match.worldguard().getEntitiesInRegion(match.getArena().getProtectedRegion("board")).forEach(entity -> {
			if (entity instanceof ArmorStand armorStand)
				armorStand.remove();
		});

		ChessMatchData matchData = match.getMatchData();

		BlockVector3 vector3 = match.getArena().getRegion("board").getMinimumPoint();
		matchData.setZeroLoc(new Location(Minigames.getWorld(), vector3.x(), vector3.y(), vector3.z()));
		matchData.getZeroLoc().setYaw(0);

		Team white = match.getArena().getTeams().get(0);
		Team black = match.getArena().getTeams().get(1);

		ChessPiece[][] board = new ChessPiece[8][8];

		// Black pieces (top side)
		board[0][0] = new Rook(white, (byte) 0, (byte) 0);
		board[0][1] = new Knight(white, (byte) 0, (byte) 1);
		board[0][2] = new Bishop(white, (byte) 0, (byte) 2);
		board[0][3] = new Queen(white, (byte) 0, (byte) 3);
		board[0][4] = new King(white, (byte) 0, (byte) 4);
		board[0][5] = new Bishop(white, (byte) 0, (byte) 5);
		board[0][6] = new Knight(white, (byte) 0, (byte) 6);
		board[0][7] = new Rook(white, (byte) 0, (byte) 7);
		for (int c = 0; c < 8; c++) {
			board[1][c] = new Pawn(white, (byte) 1, (byte) c);
		}

		// White pieces (bottom side)
		board[7][0] = new Rook(black, (byte) 7, (byte) 0);
		board[7][1] = new Knight(black, (byte) 7, (byte) 1);
		board[7][2] = new Bishop(black, (byte) 7, (byte) 2);
		board[7][3] = new Queen(black, (byte) 7, (byte) 3);
		board[7][4] = new King(black, (byte) 7, (byte) 4);
		board[7][5] = new Bishop(black, (byte) 7, (byte) 5);
		board[7][6] = new Knight(black, (byte) 7, (byte) 6);
		board[7][7] = new Rook(black, (byte) 7, (byte) 7);
		for (int c = 0; c < 8; c++) {
			board[6][c] = new Pawn(black, (byte) 6, (byte) c);
		}

		matchData.setBoard(board);
		matchData.spawn();
	}

	public void sendActionBars(Match match) {
		ChessMatchData matchData = match.getMatchData();

		matchData.getWhiteTeam().sendActionBar(matchData.getMove() % 2 != 0 ? "§aYour turn" : "§cOpponent's Turn");
		matchData.getBlackTeam().sendActionBar(matchData.getMove() % 2 != 0 ? "§cOpponent's Turn" : "§aYour turn");
	}

	public static boolean isCheckmate(Team team, ChessPiece[][] board, Match match) {
		return isInCheck(team, board, match) && !hasAnyLegalMoves(team, board, match);
	}

	public static boolean isStalemate(Team team, ChessPiece[][] board, Match match) {
		return (!isInCheck(team, board, match) && !hasAnyLegalMoves(team, board, match)) || !doesOtherThanKingExist(board);
	}

	public static boolean isInCheck(Team team, ChessPiece[][] board, Match match) {
		King king = getKing(team, board);
		if (king != null)
			return king.isInCheck(board, match);
		return false; // Shouldn't happen unless king is missing
	}

	public static King getKing(Team team, ChessPiece[][] board) {
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				ChessPiece piece = board[r][c];
				if (piece instanceof King king && piece.getTeam() == team) {
					return king;
				}
			}
		}
		return null;
	}

	public static boolean doesOtherThanKingExist(ChessPiece[][] board) {
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				ChessPiece piece = board[r][c];
				if (!(piece instanceof King)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean hasAnyLegalMoves(Team team, ChessPiece[][] board, Match match) {
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				ChessPiece piece = board[r][c];
				if (piece != null && piece.getTeam() == team) {
					List<ChessMove> moves = piece.getAvailableMoves(board, match, false);
					for (ChessMove move : moves) {
						// Simulate move
						ChessPiece[][] copy = deepCopyBoard(board);
						List<ChessMove> additionalMoves = move.getAdditionalMoves();
						move = new ChessMove(move.getRow(), move.getCol(), copy[piece.getRow()][piece.getCol()]);

						if (additionalMoves != null && !additionalMoves.isEmpty()) {
							move.setAdditionalMoves(new ArrayList<>());
							for (ChessMove additionalMove : additionalMoves)
								move.getAdditionalMoves().add(new  ChessMove(additionalMove.getRow(), additionalMove.getCol(), additionalMove.getPiece()));
						}

						move.execute(copy, match, true);

						if (!isInCheck(team, copy, match)) {
							return true; // Found a legal move
						}
					}
				}
			}
		}
		return false;
	}

	public static ChessPiece[][] deepCopyBoard(ChessPiece[][] board) {
		ChessPiece[][] copy = new ChessPiece[8][8];
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				ChessPiece piece = board[r][c];
				if (piece != null) {
					ChessPiece cloned = clonePiece(piece);
					copy[r][c] = cloned;
				}
			}
		}
		return copy;
	}

	public static ChessPiece clonePiece(ChessPiece piece) {
		return switch (piece.getType()) {
			case PAWN -> {
				Pawn p = new Pawn(piece.getTeam(), piece.getRow(), piece.getCol());
				p.enPassantVulnerable = ((Pawn) piece).enPassantVulnerable;
				yield p;
			}
			case ROOK -> {
				Rook r = new Rook(piece.getTeam(), piece.getRow(), piece.getCol());
				r.hasMoved = ((Rook) piece).hasMoved;
				yield r;
			}
			case KNIGHT -> new Knight(piece.getTeam(), piece.getRow(), piece.getCol());
			case BISHOP -> new Bishop(piece.getTeam(), piece.getRow(), piece.getCol());
			case QUEEN -> new Queen(piece.getTeam(), piece.getRow(), piece.getCol());
			case KING -> {
				King k = new King(piece.getTeam(), piece.getRow(), piece.getCol());
				k.hasMoved = ((King) piece).hasMoved;
				yield k;
			}
		};
	}

	// Entity Interactions
	public void selectPiece(ChessPiece piece, Match match) {
		ChessMatchData matchData = match.getMatchData();
		piece.select(matchData);
	}

	public void makeMove(ChessMove move, Match match) {
		ChessMatchData matchData = match.getMatchData();
		matchData.getSelectedPiece().deselect(matchData);
		ChessPiece captured = move.execute(matchData.getBoard(), match, false);
		if (captured != null)
			captured.destroy();

		Runnable continueLoop = () -> {
			matchData.getLogger().log(move, matchData.getBoard(), match);

			Team current = match.getArena().getTeams().get(1 - (matchData.getMove() % 2));
			Team other = match.getArena().getTeams().get(matchData.getMove() % 2);
			if (isCheckmate(other, matchData.getBoard(), match)) {
				matchData.setWinnerTeam(current);
				matchData.getLogger().setResult(current.getColorType() == ColorType.WHITE ? ChessMatchResult.WHITE : ChessMatchResult.BLACK);
				end(match);
				return;
			} else if (isStalemate(other, matchData.getBoard(), match)) {
				matchData.getLogger().setResult(ChessMatchResult.DRAW);
				end(match);
				return;
			}
			else
				matchData.setMove(matchData.getMove() + 1);

			matchData.clearExclamationPoints();
			matchData.updateWinChance();

			if (isInCheck(other, matchData.getBoard(), match)) {
				King king = getKing(other, matchData.getBoard());
				if (king != null)
					matchData.exclamationPoint(king.getRow(), king.getCol(), ColorType.YELLOW);
			}
		};

		if (move.getPiece() instanceof Pawn pawn && pawn.isPromoted()) {
			new PromoteMenu().select(matchData.getMove() % 2 == 1 ? matchData.getWhiteTeam() : matchData.getBlackTeam())
				.thenAccept(type -> {
					ChessPiece piece = switch (type) {
						case QUEEN -> new Queen(pawn.getTeam(), pawn.getRow(), pawn.getCol());
						case KNIGHT -> new Knight(pawn.getTeam(), pawn.getRow(), pawn.getCol());
						case PAWN -> new Pawn(pawn.getTeam(), pawn.getRow(), pawn.getCol());
						case ROOK -> new Rook(pawn.getTeam(), pawn.getRow(), pawn.getCol());
						case BISHOP -> new Bishop(pawn.getTeam(), pawn.getRow(), pawn.getCol());
						case KING -> new King(pawn.getTeam(), pawn.getRow(), pawn.getCol());
					};
					pawn.setPromotedType(type);
					pawn.destroy();
					piece.spawn(match);
					matchData.getBoard()[pawn.getRow()][pawn.getCol()] = piece;
					continueLoop.run();
				});
		}
		else {
			continueLoop.run();
		}
	}

	public ChessPiece getPieceFromEntity(ArmorStand stand, Match match) {
		ChessMatchData matchData = match.getMatchData();
		for (int row = 0; row < 8; row++) {
			for  (int col = 0; col < 8; col++) {
				ChessPiece piece = matchData.getBoard()[row][col];
				if (piece == null) continue;
				if (piece.getArmorStand() == stand)
					return piece;
			}
		}
		return null;
	}

	public ChessMove getMoveFromEntity(Entity entity, Match match) {
		ChessMatchData matchData = match.getMatchData();
		return matchData.getRenderBlocks().get(entity);
	}

	private void processMove(Minigamer minigamer, Entity entity) {
		if (!minigamer.isPlaying(this)) return;
		if (!(entity instanceof ArmorStand armorStand)) return;

		Match match = minigamer.getMatch();
		ChessMatchData matchData = match.getMatchData();

		ChessPiece piece = getPieceFromEntity(armorStand, match);
		if (piece == null) return;

		Team team = piece.getTeam();
		if (team == null) return;

		if (matchData.getMove() % 2 == 1 && minigamer.getPlayer() == matchData.getWhiteTeam() && team == match.getArena().getTeams().get(0)) {
			this.selectPiece(piece, match);
			return;
		}
		else if (matchData.getMove() % 2 == 0 && minigamer.getPlayer() == matchData.getBlackTeam() && team == match.getArena().getTeams().get(1)) {
			this.selectPiece(piece, match);
			return;
		}

		if (matchData.getSelectedPiece() == null || matchData.getSelectedPiece().getMoves() == null) return;

		ChessMove move = getMoveFromEntity(entity, match);
		if (move == null) return;

		if (matchData.getMove() % 2 == 1 && minigamer.getPlayer() == matchData.getWhiteTeam() && team ==match.getArena().getTeams().get(1))
			this.makeMove(move, match);
		else if (matchData.getMove() % 2 == 0 && minigamer.getPlayer() == matchData.getBlackTeam() && team == match.getArena().getTeams().get(0))
			this.makeMove(move, match);
	}

	@EventHandler
	public void on(PlayerToggleSneakEvent event) {
		if (!event.isSneaking()) return;

		Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		Match match = minigamer.getMatch();
		ChessMatchData matchData = match.getMatchData();

		if (matchData.getSelectedPiece() == null) return;
		Team team = matchData.getSelectedPiece().getTeam();

		if (!team.equals(minigamer.getTeam())) return;

		matchData.getSelectedPiece().deselect(matchData);
	}

	@EventHandler
	public void on(PlayerInteractAtEntityEvent event) {
		processMove(Minigamer.of(event.getPlayer()), event.getRightClicked());
	}

	@EventHandler
	public void on(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player player)) return;
		processMove(Minigamer.of(player), event.getEntity());
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		if (event.getClickedBlock() == null) return;

		ArmorStand armorStand = event.getClickedBlock().getLocation().clone().toCenterLocation()
			.getNearbyEntitiesByType(ArmorStand.class, .2).stream().findFirst().orElse(null);

		if (armorStand == null) return;
		processMove(minigamer, armorStand);
	}

	@EventHandler
	public void onInteractArmorStand(PlayerInteractEntityEvent event) {
		this.cancelEvent(event);
	}

	@EventHandler
	public void onInteractAtArmorStand(PlayerInteractAtEntityEvent event) {
		this.cancelEvent(event);
	}

	public void cancelEvent(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof ArmorStand armorStand)) return;
		if (!Minigamer.of(event.getPlayer()).isPlaying(this)) return;
		Match match = Minigamer.of(event.getPlayer()).getMatch();
		if (getPieceFromEntity(armorStand, match) == null) return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onClickMovePiece(PlayerInteractEvent event) {
		Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		if (event.getClickedBlock() == null) return;

		Match match = minigamer.getMatch();
		ChessMatchData matchData = match.getMatchData();

		FallingBlock fallingBlock = event.getClickedBlock().getLocation().clone().toCenterLocation()
			.getNearbyEntitiesByType(FallingBlock.class, .2).stream().findFirst().orElse(null);

		if (fallingBlock == null) return;

		ChessMove move = getMoveFromEntity(fallingBlock, match);
		if (move == null) return;

		if (matchData.getMove() % 2 == 1 && minigamer.getPlayer() == matchData.getWhiteTeam())
			this.makeMove(move, match);
		else if (matchData.getMove() % 2 == 0 && minigamer.getPlayer() == matchData.getBlackTeam())
			this.makeMove(move, match);
	}

	public static org.bukkit.scoreboard.Team  YELLOW;
	public static org.bukkit.scoreboard.Team RED;

	static {
		YELLOW = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("chess_yellow");
		if (YELLOW == null) {
			YELLOW = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("chess_yellow");
			YELLOW.color(NamedTextColor.YELLOW);
		}

		RED = Bukkit.getScoreboardManager().getMainScoreboard().getTeam("chess_red");
		if (RED == null) {
			RED = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("chess_red");
			RED.color(NamedTextColor.RED);
		}

	}

	@Rows(1)
	public static class PromoteMenu extends InventoryProvider {

		public CompletableFuture<ChessPieceType> future;

		public CompletableFuture<ChessPieceType> select(Player player) {
			CompletableFuture<ChessPieceType> future = new CompletableFuture<>();
			this.future = future;
			open(player);
			return future;
		}

		@Override
		public String getTitle() {
			return InventoryTexture.GUI_BLANK_ONE.getMenuTexture(1) + "&8Promote into...";
		}

		@Override
		public void init() {
			for (ChessPieceType type : ChessPieceType.values()) {
				if (type == ChessPieceType.PAWN || type == ChessPieceType.KING) continue;
				ItemBuilder builder = new ItemBuilder(ItemModelType.valueOf("CHESS_ITEM_" + type.name()))
					.name("&e" + StringUtils.camelCase(type));

				contents.set(2 + type.ordinal(), ClickableItem.of(builder.build(),
					e -> {
						e.getPlayer().closeInventory();
						future.complete(type);
					}
				));
			}
		}
	}

}
