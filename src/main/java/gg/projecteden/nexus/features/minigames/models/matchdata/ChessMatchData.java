package gg.projecteden.nexus.features.minigames.models.matchdata;

import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.mechanics.Chess;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.MatchData;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchDataFor;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@MatchDataFor(Chess.class)
public class ChessMatchData extends MatchData {

	private ChessPiece[][] board;
	private Map<Entity, ChessMove> renderBlocks = new HashMap<>();
	private int renderTask = -1;
	private Location zeroLoc;
	private int move = 1;
	private ChessPiece selectedPiece;
	private List<ArmorStand> exclamationPoints = new ArrayList<>();

	public ChessMatchData(Match match) {
		super(match);
	}

	private static boolean isWhiteTeam(Team team, Match match) {
		return match.getArena().getTeams().indexOf(team) == 0;
	}

	public Player getWhiteTeam() {
		return this.getMatch().getArena().getTeams().get(0).getMinigamers(this.getMatch()).getFirst().getPlayer();
	}

	public Player getBlackTeam() {
		return this.getMatch().getArena().getTeams().get(1).getMinigamers(this.getMatch()).getFirst().getPlayer();
	}

	public void spawn() {
		for (int row = 0; row < 8; row++) {
			for (int  col = 0; col < 8; col++) {
				ChessPiece piece = board[row][col];
				if (piece != null) {
					piece.spawn(match);
				}
			}
		}
	}

	public void exclamationPoint(int row, int col, ColorType color) {
		double offset = 0;
		if (board[row][col] != null)
			offset = board[row][col].getType().getExclamationPointOffset();

		Location loc = ((ChessMatchData) match.getMatchData()).getZeroLoc().clone().toCenterLocation().add(row, 0, col);
		loc.setY(Math.floor(loc.getY()) + offset);

		ItemStack item = new ItemBuilder(ItemModelType.EXCLAMATION).dyeColor(color).build();

		ArmorStand stand = loc.getWorld().spawn(loc, ArmorStand.class, armorStand -> {
			armorStand.setInvulnerable(true);
			armorStand.setVisible(false);
			armorStand.setGravity(false);
			armorStand.setHeadPose(EulerAngle.ZERO);
			armorStand.setSmall(true);
			armorStand.getEquipment().setHelmet(item);

			for (EquipmentSlot slot : EquipmentSlot.values()) {
				armorStand.addEquipmentLock(slot, ArmorStand.LockType.ADDING_OR_CHANGING);
				armorStand.addEquipmentLock(slot, ArmorStand.LockType.REMOVING_OR_CHANGING);
			}
		});
		exclamationPoints.add(stand);
	}

	public void clearExclamationPoints() {
		this.exclamationPoints.forEach(Entity::remove);
		this.exclamationPoints.clear();
	}

	@Data
	public static class ChessMove {
		int row, col;
		ChessPiece piece;
		List<ChessMove> additionalMoves;

		public ChessMove(int row, int col, ChessPiece piece) {
			this.row = row;
			this.col = col;
			this.piece = piece;
		}

		public ChessPiece execute(ChessPiece[][] board, Match match, boolean copy) {
			ChessPiece capturedPiece = null;

			// Clear en passant flags from all pawns
			for (int r = 0; r < 8; r++) {
				for (int c = 0; c < 8; c++) {
					ChessPiece p = board[r][c];
					if (p instanceof Pawn pawn) {
						pawn.enPassantVulnerable = false;
					}
				}
			}

			// Handle 2-square pawn move: set en passant vulnerability
			if (piece instanceof Pawn pawn && Math.abs(piece.row - row) == 2) {
				pawn.enPassantVulnerable = true;
			}

			// Handle primary capture (if any)
			if (board[row][col] != null && board[row][col].team != piece.team) {
				capturedPiece = board[row][col];
				board[row][col] = null;
			}

			// Clear old position
			board[piece.row][piece.col] = null;

			// Place the piece at its new location
			board[row][col] = piece;
			piece.row = (byte) row;
			piece.col = (byte) col;

			// Apply additional moves (e.g. en passant, castling rook)
			if (additionalMoves != null) {
				Minigames.debug("AdditionalMoves is not null, copy: " + copy);
				Minigames.debug("Additional moves: " + additionalMoves);
				for (ChessMove m : additionalMoves) {
					if (m.piece != null) {
						Minigames.debug("Additional move piece is not null: " + m.piece.getClass().getSimpleName());
						// If it's a move (e.g., castling rook)
						m.execute(board, match, copy);
					} else {
						Minigames.debug("Additional move piece is null");
						// If it's a capture (e.g., en passant victim)
						if (board[m.row][m.col] != null && board[m.row][m.col].team != piece.team) {
							capturedPiece = board[m.row][m.col]; // overwrite previous capture
							board[m.row][m.col] = null;
						}
					}
				}
			}
			else {
				Minigames.debug("AdditionalMoves is null, copy: " + copy);
			}

			// Promotion
			if (piece instanceof Pawn pawn) {
				if ((isWhiteTeam(pawn.team, match) && row == 0) || (!isWhiteTeam(pawn.team, match) && row == 7)) {
					pawn.promoted = true;
				}
			}

			// Track move history if needed
			if (piece instanceof King k) k.hasMoved = true;
			if (piece instanceof Rook r) r.hasMoved = true;

			if (piece.getArmorStand() != null && !copy)
				piece.getArmorStand().teleport(getPiece().getPieceLocation(match));

			return capturedPiece;
		}

		public void render(Location zeroLoc, ChessMatchData matchData, boolean isAdditional) {
			Location spawnLoc = zeroLoc.clone().toCenterLocation();
			spawnLoc.setY(Math.floor(spawnLoc.getY()));
			spawnLoc.add(this.row, (-1 / 32d), this.col);

			matchData.renderBlocks.put(spawnLoc.getWorld().spawn(spawnLoc.clone().subtract(0, 2, 0), ArmorStand.class, armorStand -> {
				armorStand.setVisible(false);
				armorStand.setGravity(false);
				armorStand.setInvulnerable(true);

				FallingBlock fallingBlock = spawnLoc.getWorld().spawnFallingBlock(spawnLoc, Material.BLACK_CONCRETE.createBlockData());
				fallingBlock.setGlowing(true);
				fallingBlock.setGravity(false);
				fallingBlock.setInvulnerable(true);
				fallingBlock.setHurtEntities(false);
				fallingBlock.setDropItem(false);
				fallingBlock.teleport(spawnLoc);

				matchData.renderBlocks.put(fallingBlock, this);
				armorStand.addPassenger(fallingBlock);

				if (matchData.board[row][col] != null && matchData.board[row][col].team != piece.team) {
					Chess.RED.addEntities(fallingBlock);
					matchData.exclamationPoint(row, col, ColorType.RED);
				}

				if (getAdditionalMoves() != null && !getAdditionalMoves().isEmpty()) {
					if (getAdditionalMoves().getFirst().getPiece() == null) {
						Chess.RED.addEntities(fallingBlock);
						matchData.exclamationPoint(getAdditionalMoves().getFirst().row, getAdditionalMoves().getFirst().col, ColorType.RED);
					}
					else {
						getAdditionalMoves().getFirst().render(zeroLoc, matchData, true);
						matchData.exclamationPoint(row, col, ColorType.YELLOW);
					}
				}

				if (isAdditional && piece != null) {
					Chess.YELLOW.addEntities(fallingBlock);

					Location spawnLoc2 = zeroLoc.clone().toCenterLocation();
					spawnLoc2.setY(Math.floor(spawnLoc2.getY()));
					spawnLoc2.add(piece.row, (-1 / 32d), piece.col);

					matchData.renderBlocks.put(spawnLoc2.getWorld().spawn(spawnLoc2.clone().subtract(0, 2, 0), ArmorStand.class, armorStand2 -> {
						armorStand2.setVisible(false);
						armorStand2.setGravity(false);
						armorStand2.setInvulnerable(true);

						FallingBlock fallingBlock2 = spawnLoc2.getWorld().spawnFallingBlock(spawnLoc2, Material.BLACK_CONCRETE.createBlockData());
						fallingBlock2.setGlowing(true);
						fallingBlock2.setGravity(false);
						fallingBlock2.setInvulnerable(true);
						fallingBlock2.setHurtEntities(false);
						fallingBlock2.setDropItem(false);
						fallingBlock2.teleport(spawnLoc);

						Chess.YELLOW.addEntities(fallingBlock2);

						matchData.renderBlocks.put(fallingBlock2, this);
						armorStand2.addPassenger(fallingBlock2);
					}), this);
				}
			}), this);

			if (matchData.renderTask == -1)
				matchData.renderTask = matchData.getMatch().getTasks().repeat(20, 20, () -> matchData.renderBlocks.keySet().forEach(e -> e.setTicksLived(10)));
		}

		public void unrender(ChessMatchData matchData) {
			for (Entity renderBlock : matchData.renderBlocks.keySet())
				renderBlock.remove();
			matchData.renderBlocks.clear();

			if (matchData.renderTask != -1) {
				Tasks.cancel(matchData.renderTask);
				matchData.renderTask = -1;
			}
		}

	}

	@Getter
	public static abstract class ChessPiece {
		Team team;
		byte row;
		byte col;
		@Setter
		ArmorStand armorStand;
		List<ChessMove> moves = new ArrayList<>();

		public ChessPiece(Team team, byte row, byte col) {
			this.team = team;
			this.row = row;
			this.col = col;
		}

		public void destroy() {
			armorStand.remove();
		}

		public Location getPieceLocation(Match match) {
			Location loc = ((ChessMatchData) match.getMatchData()).getZeroLoc().clone().toCenterLocation().add(row, 0, col);
			loc.setY(Math.floor(loc.getY()));
			loc.setYaw(team.getSpawnpoints().get(0).getYaw());
			return loc;
		}

		protected List<ChessMove> slideMoves(ChessPiece[][] board, int[][] directions) {
			List<ChessMove> moves = new ArrayList<>();

			for (int[] dir : directions) {
				int r = row + dir[0], c = col + dir[1];
				while (inBounds(r, c)) {
					if (board[r][c] == null) {
						moves.add(new ChessMove(r, c, this));
					} else {
						if (board[r][c].team != this.team) {
							moves.add(new ChessMove(r, c, this));
						}
						break;
					}
					r += dir[0];
					c += dir[1];
				}
			}

			return moves;
		}

		public void select(ChessMatchData matchData) {
			if (matchData.selectedPiece != null)
				matchData.selectedPiece.deselect(matchData);

			moves = getLegalMoves(matchData.board, matchData.match);
			moves.forEach(move -> move.render(matchData.zeroLoc, matchData, false));
			matchData.selectedPiece = this;
		}

		public void deselect(ChessMatchData matchData) {
			moves.forEach(move -> move.unrender(matchData));
			matchData.selectedPiece = null;

			matchData.clearExclamationPoints();

			Team other = matchData.getArena().getTeams().get(matchData.getMove() % 2);
			if (Chess.isInCheck(other, matchData.getBoard(), matchData.match)) {
				King king = Chess.getKing(other, matchData.getBoard());
				if (king != null)
					matchData.exclamationPoint(king.getRow(), king.getCol(), ColorType.YELLOW);
			}

			Team current = matchData.getArena().getTeams().get(1 - (matchData.getMove() % 2));
			if (Chess.isInCheck(current, matchData.getBoard(), matchData.match)) {
				King king = Chess.getKing(current, matchData.getBoard());
				if (king != null)
					matchData.exclamationPoint(king.getRow(), king.getCol(), ColorType.YELLOW);
			}
		}

		public List<ChessMove> getLegalMoves(ChessPiece[][] board, Match match) {
			List<ChessMove> legalMoves = new ArrayList<>();

			List<ChessMove> pseudoMoves = getAvailableMoves(board, match, false); // includes all moves, even ones that leave king in check
			for (ChessMove move : pseudoMoves) {
				// Simulate the move
				ChessPiece[][] copy = Chess.deepCopyBoard(board);

				// Replace piece reference with the one in the copied board
				ChessPiece simulatedPiece = copy[row][col];

				ChessMove simulatedMove = new ChessMove(move.getRow(), move.getCol(), simulatedPiece);

				if (move.getAdditionalMoves() != null && !move.getAdditionalMoves().isEmpty()) {
					simulatedMove.setAdditionalMoves(new ArrayList<>());
					for (ChessMove additionalMove : move.getAdditionalMoves())
						simulatedMove.getAdditionalMoves().add(new  ChessMove(additionalMove.getRow(), additionalMove.getCol(),
							additionalMove.getPiece() == null ? null : copy[additionalMove.getPiece().row][additionalMove.getPiece().col]));
				}

				simulatedMove.execute(copy, match, true);

				// Check if this move leaves the player in check
				if (!Chess.isInCheck(team, copy, match)) {
					legalMoves.add(move);
				}
			}

			return legalMoves;
		}

		private boolean inBounds(int r, int c) {
			return r >= 0 && r < 8 && c >= 0 && c < 8;
		}

		public abstract ChessPieceType getType();

		public abstract List<ChessMove> getAvailableMoves(ChessPiece[][] board, Match match, boolean ignoreCheck);

		public ItemStack getItemModel() {
			return new ItemBuilder(ItemModelType.valueOf("CHESS_" + getType().name())).dyeColor(team.getHex().substring(1)).build();
		}

		public void spawn(Match match) {
			Location location = getPieceLocation(match);
			setArmorStand(location.getWorld().spawn(location, ArmorStand.class, armorStand -> {
				armorStand.setInvulnerable(true);
				armorStand.setVisible(false);
				armorStand.setGravity(false);
				armorStand.setHeadPose(EulerAngle.ZERO);
				armorStand.setSmall(true);
				armorStand.getEquipment().setHelmet(getItemModel());

				for (EquipmentSlot slot : EquipmentSlot.values()) {
					armorStand.addEquipmentLock(slot, ArmorStand.LockType.ADDING_OR_CHANGING);
					armorStand.addEquipmentLock(slot, ArmorStand.LockType.REMOVING_OR_CHANGING);
				}
			}));
		}

		@Getter
		@AllArgsConstructor
		public enum ChessPieceType {
			// The order of this is not random, it controls the menu for promotion
			QUEEN(0.85),
			KNIGHT(0.45),
			PAWN(0.4),
			ROOK(0.65),
			BISHOP(0.7),
			KING(0.9);

			final double exclamationPointOffset;
		}
	}

	public static class Pawn extends ChessPiece {

		public boolean enPassantVulnerable = false;
		@Getter
		public boolean promoted;

		public Pawn(Team team, byte row, byte col) {
			super(team, row, col);
		}

		@Override
		public ChessPieceType getType() {
			return ChessPieceType.PAWN;
		}

		@Override
		public List<ChessMove> getAvailableMoves(ChessPiece[][] board, Match match, boolean ignoreCheck) {
			List<ChessMove> moves = new ArrayList<>();
			int direction = isWhiteTeam(team, match) ? -1 : 1;
			int startRow = isWhiteTeam(team, match) ? 6 : 1;

			int nextRow = row + direction;

			// Forward 1 square
			if (inBounds(nextRow, col) && board[nextRow][col] == null) {
				moves.add(new ChessMove(nextRow, col, this));

				// Forward 2 squares
				int twoForward = row + 2 * direction;
				if (row == startRow && board[twoForward][col] == null) {
					moves.add(new ChessMove(twoForward, col, this));
				}
			}

			// Diagonal captures (and en passant)
			for (int dc = -1; dc <= 1; dc += 2) {
				int newCol = col + dc;
				if (inBounds(nextRow, newCol)) {
					ChessPiece target = board[nextRow][newCol];

					// Standard capture
					if (target != null && target.team != this.team) {
						moves.add(new ChessMove(nextRow, newCol, this));
					}

					// En Passant capture
					ChessPiece adjacent = board[row][newCol];
					if (adjacent instanceof Pawn enemyPawn &&
						enemyPawn.team != this.team &&
						enemyPawn.enPassantVulnerable) {

						ChessMove move = new ChessMove(nextRow, newCol, this);
						ChessMove captured = new ChessMove(row, newCol, null);
						move.additionalMoves = List.of(captured);
						moves.add(move);
					}
				}
			}

			return moves;
		}

		private boolean inBounds(int r, int c) {
			return r >= 0 && r < 8 && c >= 0 && c < 8;
		}
	}

	public static class Knight extends ChessPiece {

		public Knight(Team team, byte row, byte col) {
			super(team, row, col);
		}

		@Override
		public ChessPieceType getType() {
			return ChessPieceType.KNIGHT;
		}

		@Override
		public List<ChessMove> getAvailableMoves(ChessPiece[][] board, Match match, boolean ignoreCheck) {
			List<ChessMove> moves = new ArrayList<>();
			int[][] deltas = {
				{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
				{1, -2}, {1, 2}, {2, -1}, {2, 1}
			};

			for (int[] d : deltas) {
				int r = row + d[0];
				int c = col + d[1];
				if (inBounds(r, c) && (board[r][c] == null || board[r][c].team != this.team)) {
					moves.add(new ChessMove(r, c, this));
				}
			}

			return moves;
		}

		private boolean inBounds(int r, int c) {
			return r >= 0 && r < 8 && c >= 0 && c < 8;
		}
	}

	public static class Bishop extends ChessPiece {

		public Bishop(Team team, byte row, byte col) {
			super(team, row, col);
		}

		@Override
		public ChessPieceType getType() {
			return ChessPieceType.BISHOP;
		}

		@Override
		public List<ChessMove> getAvailableMoves(ChessPiece[][] board, Match match, boolean ignoreCheck) {
			return slideMoves(board, new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}});
		}
	}

	public static class Rook extends ChessPiece {

		public boolean hasMoved = false;

		public Rook(Team team, byte row, byte col) {
			super(team, row, col);
		}

		@Override
		public ChessPieceType getType() {
			return ChessPieceType.ROOK;
		}

		@Override
		public List<ChessMove> getAvailableMoves(ChessPiece[][] board, Match match, boolean ignoreCheck) {
			return slideMoves(board, new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}});
		}
	}

	public static class Queen extends ChessPiece {

		public Queen(Team team, byte row, byte col) {
			super(team, row, col);
		}

		@Override
		public ChessPieceType getType() {
			return ChessPieceType.QUEEN;
		}

		@Override
		public List<ChessMove> getAvailableMoves(ChessPiece[][] board, Match match, boolean ignoreCheck) {
			return slideMoves(board, new int[][]{
				{-1, -1}, {-1, 1}, {1, -1}, {1, 1},
				{-1, 0}, {1, 0}, {0, -1}, {0, 1}
			});
		}
	}

	public static class King extends ChessPiece {

		public boolean hasMoved = false;

		public King(Team team, byte row, byte col) {
			super(team, row, col);
		}

		@Override
		public ChessPieceType getType() {
			return ChessPieceType.KING;
		}

		@Override
		public List<ChessMove> getAvailableMoves(ChessPiece[][] board, Match match, boolean ignoreCheck) {
			List<ChessMove> moves = new ArrayList<>();

			for (int dr = -1; dr <= 1; dr++) {
				for (int dc = -1; dc <= 1; dc++) {
					if (dr == 0 && dc == 0) continue;
					int r = row + dr, c = col + dc;
					if (inBounds(r, c) && (board[r][c] == null || board[r][c].team != this.team)) {
						moves.add(new ChessMove(r, c, this));
					}
				}
			}

			// Castling
			if (!hasMoved && !ignoreCheck && !isInCheck(board, match)) {
				// Kingside castling
				if (canCastle(board, match, col + 1, col + 2)) {
					Minigames.debug("Can castle kingside");
					ChessMove move = new ChessMove(row, col + 2, this);

					// Rook from h -> f
					ChessPiece rook = board[row][7];
					if (rook instanceof Rook) {
						Minigames.debug("Rook is rook");
						move.additionalMoves = List.of(new ChessMove(row, col + 1, rook));
					}

					moves.add(move);
				}

				// Queenside castling
				if (canCastle(board, match,col - 1, col - 2, col - 3)) {
					Minigames.debug("Can castle queenside");
					ChessMove move = new ChessMove(row, col - 2, this);

					// Rook from a -> d
					ChessPiece rook = board[row][0];
					if (rook instanceof Rook) {
						Minigames.debug("Rook is rook");
						move.additionalMoves = List.of(new ChessMove(row, col - 1, rook));
					}

					moves.add(move);
				}
			}

			return moves;
		}

		private boolean canCastle(ChessPiece[][] board, Match match, int... cols) {
			for (int c : cols)
				if (!inBounds(row, c) || board[row][c] != null || squareIsThreatened(row, c, board, match))
					return false;

			int rookCol = cols[cols.length - 1] < col ? 0 : 7;
			ChessPiece rook = board[row][rookCol];
			return rook instanceof Rook && !((Rook) rook).hasMoved;
		}

		public boolean isInCheck(ChessPiece[][] board, Match match) {
			for (int r = 0; r < 8; r++) {
				for (int c = 0; c < 8; c++) {
					ChessPiece piece = board[r][c];
					if (piece != null && piece.team != this.team) {
						for (ChessMove move : piece.getAvailableMoves(board, match, true)) {
							if (move.row == this.row && move.col == this.col) {
								return true;
							}
						}
					}
				}
			}
			return false;
		}

		private boolean squareIsThreatened(int r, int c, ChessPiece[][] board, Match match) {
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					ChessPiece piece = board[i][j];
					if (piece != null && piece.team != this.team) {
						for (ChessMove move : piece.getAvailableMoves(board, match, true)) {
							if (move.row == r && move.col == c) {
								return true;
							}
						}
					}
				}
			}
			return false;
		}


		private boolean inBounds(int r, int c) {
			return r >= 0 && r < 8 && c >= 0 && c < 8;
		}
	}

}
