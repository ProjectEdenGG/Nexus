package gg.projecteden.nexus.features.minigames.mechanics;

import com.destroystokyo.paper.ParticleBuilder;
import com.sk89q.worldedit.math.BlockVector3;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.Team;
import gg.projecteden.nexus.features.minigames.models.annotations.Scoreboard;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchEndEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchInitializeEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchStartEvent;
import gg.projecteden.nexus.features.minigames.models.exceptions.MinigameException;
import gg.projecteden.nexus.features.minigames.models.matchdata.CheckersMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import gg.projecteden.nexus.features.minigames.models.scoreboards.MinigameScoreboard;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.*;
import gg.projecteden.parchment.entity.EntityData;
import gg.projecteden.parchment.entity.EntityDataFragment;
import gg.projecteden.parchment.entity.EntityDataKey;
import lombok.Data;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;
import tech.blastmc.holograms.api.HologramsAPI;
import tech.blastmc.holograms.api.models.Hologram;

import java.util.*;
import java.util.stream.Collectors;

@Scoreboard(sidebarType = MinigameScoreboard.Type.MINIGAMER)
public class Checkers extends TeamMechanic {

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemBuilder(CustomMaterial.CHECKERS_KING).dyeColor(ColorType.RED).build();
	}

	@Override
	public @NotNull String getName() {
		return "Checkers";
	}

	@Override
	public @NotNull String getDescription() {
		return "Compete against another player in a classic game of checkers";
	}

	@Override
	public void announceWinners(@NotNull Match match) {
		CheckersMatchData matchData = match.getMatchData();

		if (matchData.getWinnerTeam() == null) {
			Minigames.broadcast("&eCheckers &3has ended in a stalemate");
			return;
		}

		final Minigamer winner = matchData.getWinnerTeam().getMinigamers(match).get(0);
		Minigames.broadcast(winner.getColoredName() + " &3has won &eCheckers");
	}

	@Override
	public @NotNull LinkedHashMap<String, Integer> getScoreboardLines(@NotNull Minigamer minigamer) {
		LinkedHashMap<String, Integer> lines = new LinkedHashMap<>();
		Match match = minigamer.getMatch();

		if (match.isStarted()) {
			lines.put("", Integer.MIN_VALUE);
			lines.put("&3&lPieces Remaining:", Integer.MIN_VALUE);

			match.getMinigamers().stream().sorted(Comparator.comparingInt(mg -> ((Minigamer) mg).getTeam().getScore(match)).reversed())
				.forEachOrdered(mg -> lines.put(mg.getVanillaColoredName(), mg.getTeam().getScore(match)));
		}
		else
			match.getMinigamers().forEach(mg -> lines.put(mg.getNickname(), Integer.MIN_VALUE));

		return lines;
	}

	@Override
	public void onInitialize(@NotNull MatchInitializeEvent event) {
		super.onInitialize(event);
		placePieces(event.getMatch());
	}


	@Override
	public void onStart(@NotNull MatchStartEvent event) {
		event.getMatch().getArena().getTeams().forEach(team -> {
			event.getMatch().setScore(team, 12);
		});

		super.onStart(event);

		event.getMatch().getTasks().wait(1, () -> {
			if (event.getMatch().isStarted() && !event.getMatch().isEnded())
				event.getMatch().getTasks().repeat(1, 1, () -> sendActionBars(event.getMatch()));
		});

		event.getMatch().getMinigamers().forEach(mg -> {
			mg.getPlayer().showTitle(Title.title(new JsonBuilder("&e&lCheckers").asComponent(), new JsonBuilder("&3You are on team ").next(mg.getTeam().asComponent()).asComponent()));
		});
	}

	@Override
	public boolean shouldBeOver(@NotNull Match match) {
		if (!match.isStarted())
			return false;

		CheckersMatchData matchData = match.getMatchData();
		if (matchData.getMove() > 1)
			for (Team team : match.getArena().getTeams())
				if (team.getScore(match) == 0) {
					Minigames.debug("Checkers: Team " + team.getName() + " has no score");
					match.getMatchData().setWinnerTeam(getOtherTeam(team, match));
					return true;
				}

		return false;
	}

	private Team getTeam(int team, Match match) {
		return match.getArena().getTeams().get(team % 2);
	}

	public Team getOtherTeam(Team team, Match match) {
		return match.getArena().getTeams().stream()
			.filter(_team -> !team.getName().equals(_team.getName()))
			.findFirst()
			.orElseThrow(() -> new MinigameException("Could not find opposite team of " + team.getName() + " in " + getName()));
	}

	private void placePieces(Match match) {
		CheckersMatchData matchData = match.getMatchData();

		BlockVector3 vector3 = match.getArena().getRegion("board").getMinimumPoint();
		matchData.setZeroLoc(new Location(Minigames.getWorld(), vector3.getX(), vector3.getY(), vector3.getZ()));
		matchData.getZeroLoc().setYaw(0);

		match.worldguard().getEntitiesInRegion(match.getArena().getProtectedRegion("board")).forEach(entity -> {
			if (entity instanceof ArmorStand armorStand)
				armorStand.remove();
		});

		for (int row = 0; row < matchData.getBoard().length; row++)
			for (int col = 0; col < matchData.getBoard()[row].length; col++)
				if (col % 2 == ((row + 1) % 2))
					if (row < 3)
						spawnPiece(new CheckersPiece(1, row, col, match));
					else if (row > 4)
						spawnPiece(new CheckersPiece(2, row, col, match));
	}

	private void spawnPiece(CheckersPiece piece) {
		CheckersMatchData matchData = piece.getMatch().getMatchData();

		Location location = getPieceLocation(piece.getRow(), piece.getColumn(), piece.getMatch());
		piece.setArmorStand(location.getWorld().spawn(location, ArmorStand.class, armorStand -> {
			armorStand.setInvulnerable(true);
			armorStand.setVisible(false);
			armorStand.setGravity(false);
			armorStand.setHeadPose(EulerAngle.ZERO);
			armorStand.setSmall(true);
			armorStand.getEquipment().setHelmet(getPiece(false, piece.getMatch().getArena().getTeams().get(piece.getTeam() % 2)));

			for (EquipmentSlot slot : EquipmentSlot.values()) {
				armorStand.addEquipmentLock(slot, ArmorStand.LockType.ADDING_OR_CHANGING);
				armorStand.addEquipmentLock(slot, ArmorStand.LockType.REMOVING_OR_CHANGING);
			}

			CheckersFragment.get(armorStand).setPiece(piece);
		}));
		matchData.getBoard()[piece.getRow()][piece.getColumn()] = piece;
	}

	public static ItemStack getPiece(boolean king, Team team) {
		return new ItemBuilder(king ? CustomMaterial.CHECKERS_KING : CustomMaterial.CHECKERS_NORMAL).dyeColor(team.getHex().substring(1)).build();
	}

	@Override
	public void onEnd(@NotNull MatchEndEvent event) {
		super.onEnd(event);
		CheckersMatchData matchData = event.getMatch().getMatchData();

		for (CheckersPiece[] row : matchData.getBoard()) {
			for (CheckersPiece piece : row) {
				if (piece != null) {
					piece.unrender();
					piece.remove();
				}
			}
		}
		placePieces(event.getMatch());
	}

	public void sendActionBars(Match match) {
		CheckersMatchData matchData = match.getMatchData();

		if (matchData.getMove() % 2 != 0) {
			if (matchData.isValidMove())
				matchData.getOwner().sendActionBar("§aYour turn");
			else
				matchData.getOwner().sendActionBar("§c" + matchData.getActionBarMessage());
			matchData.getOpponent().sendActionBar("§cOpponent's Turn");
		} else {
			if (matchData.isValidMove())
				matchData.getOpponent().sendActionBar("§aYour turn");
			else
				matchData.getOpponent().sendActionBar("§c" + matchData.getActionBarMessage());
			matchData.getOwner().sendActionBar("§cOpponent's Turn");
		}
	}

	public Location getPieceLocation(int row, int column, Match match) {
		Location loc = ((CheckersMatchData) match.getMatchData()).getZeroLoc().clone().toCenterLocation().add(row, 0, column);
		loc.setY(Math.floor(loc.getY()) + .55);
		return loc;
	}

	public void selectPiece(CheckersPiece piece) {
		Match match = piece.getMatch();
		CheckersMatchData matchData = match.getMatchData();
		
		if (matchData.getSelectedPiece() != null) {
			matchData.getSelectedPiece().unrender();
		}
		matchData.setSelectedPiece(piece);
		
		List<CheckersPiece> jumpedPieces = new ArrayList<>();
		List<CheckersMove> moves = new ArrayList<>(getMoves(match, piece.getRow(), piece.getColumn(), piece.getTeam(), piece.isKing(), false, jumpedPieces));
		
		if (matchData.getActionBarTask() != -1)
			Tasks.cancel(matchData.getActionBarTask());
		
		try {
			this.validateMove(moves, piece.getTeam());
		} catch (InvalidInputException ex) {
			matchData.setActionBarMessage(ex.getMessage());
			matchData.setValidMove(false);
			matchData.setActionBarTask(match.getTasks().wait(40, () -> matchData.setValidMove(true)));
			return;
		}
		matchData.setValidMove(true);
		matchData.getSelectedPiece().setMoves(moves);
		matchData.getSelectedPiece().render(matchData.getZeroLoc());
	}

	public void validateMove(List<CheckersMove> moves, int team) throws InvalidInputException {
		if (moves == null || moves.isEmpty())
			throw new InvalidInputException("There are no moves for that piece");

		if (moves.stream().anyMatch(CheckersMove::isDidJump)) return;

		CheckersMatchData matchData = moves.get(0).getMatch().getMatchData();

		if (!matchData.isForceJumps()) return;

		for (int row = 0; row < matchData.getBoard().length; row++) {
			for (int col = 0; col < matchData.getBoard()[row].length; col++) {
				CheckersPiece piece = matchData.getBoard()[row][col];
				if (piece == null) continue;
				if (piece.getTeam() != team) continue;

				List<CheckersPiece> pieces = new ArrayList<>();
				if (getMoves(matchData.getMatch(), row, col, team, piece.isKing(), false, pieces).stream().anyMatch(CheckersMove::isDidJump)) {
					throw new InvalidInputException("There is a jump that can be made");
				}
			}
		}
	}

	public void makeMove(CheckersMove checkersMove) {
		Match match = checkersMove.getMatch();
		CheckersMatchData matchData = match.getMatchData();
		
		int wait = 1;
		matchData.setProcessing(true);
		matchData.getSelectedPiece().unrender();
		if (checkersMove.isDidJump()) {
			Location jumpLoc = checkersMove.getJumpedPiece().getArmorStand().getLocation().clone().add(0, 1, 0);

			matchData.getSelectedPiece().getArmorStand().teleport(jumpLoc);

			CheckersPiece jumpedPiece = checkersMove.getJumpedPiece();

			BlockData particleData = jumpedPiece.getTeam() == 1 ? Material.RED_WOOL.createBlockData() : Material.BLACK_CONCRETE_POWDER.createBlockData();
			new ParticleBuilder(Particle.BLOCK_CRACK)
				.location(jumpedPiece.getArmorStand().getLocation().clone().add(0, 2, 0))
				.allPlayers()
				.count(50)
				.data(particleData)
				.spawn();

			jumpLoc.getWorld().playSound(jumpLoc, Sound.ENTITY_ITEM_PICKUP, SoundCategory.MASTER, 1f, .1f);
			jumpedPiece.remove();

			matchData.getBoard()[jumpedPiece.getRow()][jumpedPiece.getColumn()] = null;
			match.scored(getOtherTeam(getTeam(matchData.getSelectedPiece().getTeam(), match), match), -1);

			wait += 5;
		}
		match.getTasks().wait(wait, () -> {
			matchData.getBoard()[matchData.getSelectedPiece().getRow()][matchData.getSelectedPiece().getColumn()] = null;
			matchData.getSelectedPiece().setRow(checkersMove.getNewRow());
			matchData.getSelectedPiece().setColumn(checkersMove.getNewColumn());
			matchData.getSelectedPiece().getArmorStand().teleport(this.getPieceLocation(checkersMove.getNewRow(), checkersMove.getNewColumn(), match));
			matchData.getBoard()[matchData.getSelectedPiece().getRow()][matchData.getSelectedPiece().getColumn()] = matchData.getSelectedPiece();
			if (((matchData.getSelectedPiece().getTeam() == 1 && matchData.getSelectedPiece().getRow() == 7) ||
				(matchData.getSelectedPiece().getTeam() == 2 && matchData.getSelectedPiece().getRow() == 0)) && !matchData.getSelectedPiece().isKing()) {
				match.getTasks().wait(3, () -> matchData.getSelectedPiece().setKing(true));
			}
			if (checkersMove.getSubMoves() == null || checkersMove.getSubMoves().isEmpty()) {
				endTurn(match);
			} else {
				if (checkersMove.getSubMoves().size() == 1) {
					match.getTasks().wait(5, () -> this.makeMove(checkersMove.getSubMoves().get(0)));
				} else {
					matchData.setProcessing(false);
					matchData.getSelectedPiece().setMoves(checkersMove.getSubMoves());
					matchData.getSelectedPiece().render(matchData.getZeroLoc());
				}
			}
		});
	}

	public List<Checkers.CheckersMove> getMoves(Match match, int row, int col, int team, boolean king, boolean jumped, List<Checkers.CheckersPiece> jumpedPieces) {
		CheckersMatchData matchData = match.getMatchData();
		
		List<Checkers.CheckersMove> moves = new ArrayList<>();
		List<Integer> rows = new ArrayList<>();
		int[] cols = {-1, 1};
		rows.add(team == 1 ? 1 : -1);
		if (king) {
			rows.add(team == 1 ? -1 : 1);
		}
		for (int i = 0; i < rows.size(); i++) {
			for (int colDelta : cols) {
				int newRow = row + rows.get(i);
				int newCol = col + colDelta;
				if (this.isInRange(newRow) && this.isInRange(newCol)) {
					if (matchData.getBoard()[newRow][newCol] == null && !jumped) {
						moves.add(new Checkers.CheckersMove(newRow, newCol, match));
					} else {
						int jumpRow = row + (rows.get(i) * 2);
						int jumpCol = col + (colDelta * 2);
						if (matchData.getBoard()[newRow][newCol] != null && matchData.getBoard()[newRow][newCol].getTeam() != team) {
							if (this.isInRange(jumpRow) && this.isInRange(jumpCol) && matchData.getBoard()[jumpRow][jumpCol] == null && !jumpedPieces.contains(matchData.getBoard()[newRow][newCol])) {
								Checkers.CheckersMove move = new Checkers.CheckersMove(jumpRow, jumpCol, match);
								move.setDidJump(true);
								move.setJumpedPiece(matchData.getBoard()[newRow][newCol]);
								jumpedPieces.add(matchData.getBoard()[newRow][newCol]);
								move.getSubMoves().addAll(this.getMoves(match, jumpRow, jumpCol, team, king, true, jumpedPieces));
								moves.add(move);
							}
						}
					}
				}
			}
		}
		if (moves.stream().anyMatch(Checkers.CheckersMove::isDidJump) && matchData.isForceJumps()) {
			return moves.stream().filter(Checkers.CheckersMove::isDidJump).collect(Collectors.toList());
		}
		return moves;
	}

	public boolean isInRange(int index) {
		return index >= 0 && index < 8;
	}

	public void endTurn(Match match) {
		CheckersMatchData matchData = match.getMatchData();

		matchData.setMove(matchData.getMove() + 1);
		matchData.setProcessing(false);

		this.checkStalemate(match);
	}

	public void checkStalemate(Match match) {
		CheckersMatchData matchData = match.getMatchData();
		
		for (int row = 0; row < matchData.getBoard().length; row++) {
			for (int col = 0; col < matchData.getBoard()[row].length; col++) {
				if (matchData.getBoard()[row][col] == null) continue;

				int team = matchData.getMove() % 2 == 1 ? 1 : 2;
				if (matchData.getBoard()[row][col].getTeam() == team) {
					if (!getMoves(match, row, col, team, matchData.getBoard()[row][col].isKing(), false, new ArrayList<>()).isEmpty())
						return;
				}
			}
		}

		match.end();
	}

	private void processMove(Minigamer minigamer, Entity entity) {
		if (!minigamer.isPlaying(this)) return;
		if (!(entity instanceof ArmorStand armorStand)) return;

		Match match = minigamer.getMatch();
		CheckersMatchData matchData = match.getMatchData();

		if (matchData.isProcessing()) return;

		CheckersPiece piece = CheckersFragment.get(armorStand).getPiece();
		if (piece == null) return;

		int team = piece.getTeam();
		if (team == 0) return;

		if (matchData.getMove() % 2 == 1 && minigamer.getPlayer() == matchData.getOwner() && team == 1)
			this.selectPiece(piece);
		else if (matchData.getMove() % 2 == 0 && minigamer.getPlayer() == matchData.getOpponent() && team == 2)
			this.selectPiece(piece);

		if (matchData.getSelectedPiece() == null || matchData.getSelectedPiece().getMoves() == null) return;

		CheckersMove checkersMove = CheckersFragment.get(armorStand).getMove();
		if (checkersMove == null) return;

		if (matchData.getMove() % 2 == 1 && minigamer.getPlayer() == matchData.getOwner() && team == 2)
			this.makeMove(checkersMove);
		else if (matchData.getMove() % 2 == 0 && minigamer.getPlayer() == matchData.getOpponent() && team == 1)
			this.makeMove(checkersMove);
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

		Match match = minigamer.getMatch();
		CheckersMatchData matchData = match.getMatchData();

		if (matchData.isProcessing()) return;

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
		if (CheckersFragment.get(armorStand).getPiece() != null)
			event.setCancelled(true);
	}

	@EventHandler
	public void onClickMovePiece(PlayerInteractEvent event) {
		Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;
		
		if (event.getClickedBlock() == null) return;

		Match match = minigamer.getMatch();
		CheckersMatchData matchData = match.getMatchData();
		
		if (matchData.isProcessing()) return;
		
		FallingBlock fallingBlock = event.getClickedBlock().getLocation().clone().toCenterLocation()
			.getNearbyEntitiesByType(FallingBlock.class, .2).stream().findFirst().orElse(null);
		
		if (fallingBlock == null) return;
		
		CheckersFragment fragment = CheckersFragment.get(fallingBlock);
		if (fragment.getMove() == null) return;
		
		if (matchData.getMove() % 2 == 1 && minigamer.getPlayer() == matchData.getOwner())
			this.makeMove(fragment.getMove());
		else if (matchData.getMove() % 2 == 0 && minigamer.getPlayer() == matchData.getOpponent())
			this.makeMove(fragment.getMove());
	}
	
	@Data
	public static class CheckersFragment extends EntityDataFragment<Entity> {

		public static EntityDataKey<CheckersFragment, Entity> DATA_KEY = EntityData.createKey(CheckersFragment::new, Entity.class);

		public static CheckersFragment get(Entity armorStand) {
			return armorStand.getStoredEntityData().get(CheckersFragment.DATA_KEY);
		}

		private CheckersPiece piece;
		private CheckersMove move;
	}

	@Data
	public static class CheckersMove {
		
		private final int newRow;
		private final int newColumn;
		private final Match match;
		private boolean didJump;
		private CheckersPiece jumpedPiece;
		private final List<CheckersMove> subMoves = new ArrayList<>();

		public CheckersMove(int newRow, int newColumn, Match match) {
			this.newRow = newRow;
			this.newColumn = newColumn;
			this.match = match;
		}

	}

	@Data
	public static class CheckersPiece {

		private final int team;
		private int row;
		private int column;
		private Match match;
		private boolean king;
		private ArmorStand armorStand;
		private List<CheckersMove> moves;
		private final List<Entity> renderBlocks = new ArrayList<>();
		private final List<Hologram> holograms = new ArrayList<>();
		int renderTask = -1;

		public CheckersPiece(int team, int row, int column, Match match) {
			this.team = team;
			this.row = row;
			this.column = column;
			this.match = match;
		}

		public void setKing(boolean king) {
			ItemStack item = getPiece(true, match.getArena().getTeams().get(team % 2));

			this.king = king;
			this.armorStand.getEquipment().setHelmet(item);
			this.armorStand.getLocation().getWorld().playSound(this.armorStand.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);

			int r = this.getTeam() == 1 ? 203 : 63;
			int g = this.getTeam() == 1 ? 95 : 56;
			int b = this.getTeam() == 1 ? 101 : 73;
			Particle.DustOptions options = new Particle.DustOptions(Color.fromRGB(r, g, b), 1);

			for (int i = 0; i < 50; i++) {
				double x = RandomUtils.randomDouble(-.3, .3);
				double y = RandomUtils.randomDouble(-.3, .3);
				double z = RandomUtils.randomDouble(-.3, .3);
				new ParticleBuilder(Particle.REDSTONE)
					.location(this.armorStand.getLocation().clone().add(x, 2 + y, z))
					.allPlayers()
					.data(options)
					.spawn();
			}
		}

		public void remove() {
			if (this.armorStand != null)
				this.armorStand.remove();
		}

		public void render(Location zeroLoc) {
			if (this.getMoves() == null) return;
			
			for (CheckersMove move : this.getMoves()) {
				if (move.isDidJump()) {
					Hologram hologram = HologramsAPI.builder()
						.id(UUID.randomUUID().toString())
						.lines("§a§lJump")
						.location(move.getJumpedPiece().getArmorStand().getLocation().clone().add(0, 2, 0))
						.build();
					hologram.spawn();
					this.holograms.add(hologram);
					CheckersFragment.get(move.getJumpedPiece().getArmorStand()).setMove(move);
				} else {
					Location spawnLoc = zeroLoc.clone().toCenterLocation();
					spawnLoc.setY(Math.floor(spawnLoc.getY()));
					spawnLoc.add(move.getNewRow(), (-1 / 32d), move.getNewColumn());

					this.renderBlocks.add(spawnLoc.getWorld().spawn(spawnLoc.clone().subtract(0, 2, 0), ArmorStand.class, armorStand -> {
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
						CheckersFragment.get(fallingBlock).setMove(move);
						this.renderBlocks.add(fallingBlock);

						armorStand.addPassenger(fallingBlock);
					}));

					Hologram hologram = HologramsAPI.builder()
						.id(UUID.randomUUID().toString())
						.lines("§a§lMove")
						.location(spawnLoc.clone().add(0, 1.2, 0))
						.build();
					hologram.spawn();
					this.holograms.add(hologram);
					
					this.renderTask = match.getTasks().repeat(20, 20, () -> this.renderBlocks.forEach(e -> e.setTicksLived(10)));
				}
			}
		}

		public void unrender() {
			if (this.getMoves() == null) return;
			
			for (CheckersMove move : this.getMoves())
				if (move.isDidJump())
					CheckersFragment.get(move.getJumpedPiece().getArmorStand()).setMove(null);
			
			for (Entity renderBlock : this.renderBlocks)
				renderBlock.remove();
			this.renderBlocks.clear();
			
			for (Hologram hologram : this.holograms) 
				hologram.remove();
			this.holograms.clear();
			
			if (this.renderTask != -1)
				Tasks.cancel(this.renderTask);
		}
	}

}
