package gg.projecteden.nexus.features.commands;

import de.sfuhrm.sudoku.Creator;
import de.sfuhrm.sudoku.GameMatrix;
import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.features.commands.SudokuCommand.SudokuGame.Difficulty;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.nms.packet.CustomMapPacket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.With;
import lombok.experimental.Accessors;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.MapUtils.getMapPixelHit;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@NoArgsConstructor
@Environments({Env.TEST, Env.UPDATE})
public class SudokuCommand extends CustomCommand implements Listener {
	private static SudokuGame game;

	public SudokuCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("start <difficulty>")
	void setup(Difficulty difficulty) {
		game = new SudokuGame(difficulty, get3x3ItemFrameGrid());
		game.sendMapImagePackets(player());
	}

	@EventHandler
	public void on(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player player))
			return;
		if (!(event.getEntity() instanceof ItemFrame itemFrame))
			return;
		if (game == null)
			return;
		if (!game.getItemFrames().contains(itemFrame))
			return;

		event.setCancelled(true);

		var result = player.rayTraceEntities(10);
		if (result == null)
			return;

		var pixelCoords = getMapPixelHit(itemFrame, result.getHitPosition());
		var map = game.getItemFrames().indexOf(itemFrame);
		var highlight = game.clickedCoordinateToGameCoordinate(map, pixelCoords);

		if (game.getCellAnswer(highlight) == 0) {
			var slot = player.getInventory().getHeldItemSlot() + 1;
			if (player.isSneaking())
				if (game.getCandidates(highlight).contains(slot))
					game.removeCandidate(highlight, slot);
				else
					game.addCandidate(highlight, slot);
			else
				game.setCellAnswer(highlight, slot);

			game.render().sendMapImagePackets(player);
		} else {
			if (player.isSneaking())
				game.setCellAnswer(highlight, 0);

			game.render(settings -> settings.setHighlight(highlight)).sendMapImagePackets(player);
		}
	}

	@Path("target")
	void target() {
		var itemFrame = getTargetEntityRequired(ItemFrame.class);
		var item = itemFrame.getItem();
		if (isNullOrAir(item))
			error("Item is null");
		if (!(item.getItemMeta() instanceof MapMeta mapMeta))
			throw new InvalidInputException("Item is not a map");

		var result = player().rayTraceEntities(10);
		if (result == null)
			error("Ray trace failed");

		Vector hitPosition = result.getHitPosition();
		send("Hit Position: " + hitPosition);

		// Convert hit position to pixel coordinates
		var pixelCoords = getMapPixelHit(itemFrame, hitPosition);
		var highlight = game.clickedCoordinateToGameCoordinate(game.getItemFrames().indexOf(itemFrame), pixelCoords);
		send("Highlight: " + highlight);
		send("Answer at highlight: " + game.getCellAnswer(highlight));
		game.render(settings -> settings.setHighlight(highlight)).sendMapImagePackets(player());
	}
	
	/**
	 * Gets a 3x3 grid of item frames with the target frame at the center
	 * @return List of 9 item frames sorted left to right, top to bottom from player's perspective
	 */
	public List<ItemFrame> get3x3ItemFrameGrid() {
		Player player = player();
		
		// Get target item frame from player's line of sight
		RayTraceResult rayTrace = player.rayTraceEntities(5, false);
		if (rayTrace == null || !(rayTrace.getHitEntity() instanceof ItemFrame))
			error("You must be looking at an item frame");
		
		ItemFrame targetFrame = (ItemFrame) rayTrace.getHitEntity();
		Location centerLocation = targetFrame.getLocation();
		BlockFace attachedFace = targetFrame.getAttachedFace();
		
		// Find all nearby item frames
		List<ItemFrame> nearbyFrames = centerLocation.getWorld().getNearbyEntitiesByType(
			ItemFrame.class, 
			centerLocation, 
			3.0, // Search radius large enough to find frames
			frame -> frame.getAttachedFace().equals(attachedFace) // Only include frames attached to the same side
		).stream().collect(Collectors.toList());
		
		if (nearbyFrames.size() < 9)
			error("Could not find 9 item frames in a grid pattern");
		
		// Sort frames by distance
		nearbyFrames.sort(Comparator.comparingDouble(frame -> frame.getLocation().distanceSquared(centerLocation)));
		
		// Keep only the 9 closest frames
		if (nearbyFrames.size() > 9)
			nearbyFrames = nearbyFrames.subList(0, 9);
		
		// Sort frames from player perspective (left to right, top to bottom)
		final BlockFace finalAttachedFace = attachedFace;
		nearbyFrames.sort((frame1, frame2) -> {
			Location loc1 = frame1.getLocation();
			Location loc2 = frame2.getLocation();
			
			// Determine the local "up" vector based on the attached face
			double yDiff;
			double xDiff;
			
			switch (finalAttachedFace) {
				case NORTH:
				case SOUTH:
					yDiff = loc1.getY() - loc2.getY();
					xDiff = loc1.getX() - loc2.getX();
					// Invert X axis if facing south
					if (finalAttachedFace == BlockFace.SOUTH)
						xDiff = -xDiff;
					break;
				case EAST:
				case WEST:
					yDiff = loc1.getY() - loc2.getY();
					xDiff = loc1.getZ() - loc2.getZ();
					// Invert X axis if facing east
					if (finalAttachedFace == BlockFace.EAST)
						xDiff = -xDiff;
					break;
				case UP:
				case DOWN:
					// Get player's facing direction to determine orientation
					double yaw = player.getLocation().getYaw() % 360;
					if (yaw < 0) yaw += 360;
					
					if (yaw >= 315 || yaw < 45) { // Facing SOUTH
						xDiff = loc1.getX() - loc2.getX();
						yDiff = loc1.getZ() - loc2.getZ();
						if (finalAttachedFace == BlockFace.DOWN)
							yDiff = -yDiff;
					} else if (yaw >= 45 && yaw < 135) { // Facing WEST
						xDiff = loc1.getZ() - loc2.getZ();
						yDiff = -(loc1.getX() - loc2.getX());
						if (finalAttachedFace == BlockFace.DOWN)
							yDiff = -yDiff;
					} else if (yaw >= 135 && yaw < 225) { // Facing NORTH
						xDiff = -(loc1.getX() - loc2.getX());
						yDiff = -(loc1.getZ() - loc2.getZ());
						if (finalAttachedFace == BlockFace.DOWN)
							yDiff = -yDiff;
					} else { // Facing EAST
						xDiff = -(loc1.getZ() - loc2.getZ());
						yDiff = loc1.getX() - loc2.getX();
						if (finalAttachedFace == BlockFace.DOWN)
							yDiff = -yDiff;
					}
					break;
				default:
					return 0;
			}
			
			// Sort by Y first (top to bottom), then X (left to right)
			if (Math.abs(yDiff) > 0.1) // Small threshold to handle floating point comparison
				return yDiff > 0 ? -1 : 1; // Negative Y is higher (top)
				
			return xDiff > 0 ? 1 : -1; // Positive X is right
		});
		
		return nearbyFrames;
	}

	@Getter
	public static class SudokuGame {
		private final GameMatrix riddle;
		private final Difficulty difficulty;
		private final List<ItemFrame> itemFrames;
		private final RenderSettings renderSettings = RenderSettings.builder().build();
		private final Map<Integer, Map<Integer, Set<Integer>>> candidates = new HashMap<>();
		private BufferedImage image;

		public SudokuGame(Difficulty difficulty, List<ItemFrame> itemFrames) {
			this.difficulty = difficulty;
			this.itemFrames = itemFrames;
			this.riddle = Creator.createRiddle(Creator.createFull(), difficulty.getDifficulty());

			render();
		}

		@Data
		public static class Coordinate {
			public final int row;
			public final int col;
		}

		public Integer getCellAnswer(Coordinate coordinate) {
			if (coordinate == null)
				return 0;
			return (int) this.riddle.get(coordinate.row, coordinate.col);
		}

		public SudokuGame setCellAnswer(Coordinate coordinate, int answer) {
			this.riddle.set(coordinate.row, coordinate.col, (byte) answer);
			removeAnswerFromCandidates(coordinate, answer);
			return this;
		}

		private void removeAnswerFromCandidates(Coordinate coordinate, int answer) {
			// Remove the answer from candidates in the same row
			for (int c = 0; c < 9; c++) {
				removeCandidate(new Coordinate(coordinate.row, c), answer);
			}

			// Remove the answer from candidates in the same column
			for (int r = 0; r < 9; r++) {
				removeCandidate(new Coordinate(r, coordinate.col), answer);
			}

			// Remove the answer from candidates in the same 3x3 box
			int boxStartRow = (coordinate.row / 3) * 3;
			int boxStartCol = (coordinate.col / 3) * 3;
			for (int r = boxStartRow; r < boxStartRow + 3; r++) {
				for (int c = boxStartCol; c < boxStartCol + 3; c++) {
					removeCandidate(new Coordinate(r, c), answer);
				}
			}
		}

		public SudokuGame addCandidate(Coordinate coordinate, int candidate) {
			this.candidates
				.computeIfAbsent(coordinate.row, $ -> new HashMap<>())
				.computeIfAbsent(coordinate.col, $ -> new HashSet<>())
				.add(candidate);
			return this;
		}

		public SudokuGame removeCandidate(Coordinate coordinate, int candidate) {
			this.candidates
				.computeIfAbsent(coordinate.row, $ -> new HashMap<>())
				.computeIfAbsent(coordinate.col, $ -> new HashSet<>())
				.remove(candidate);
			return this;
		}

		public SudokuGame setCandidates(Coordinate coordinate, Set<Integer> candidates) {
			this.candidates
				.computeIfAbsent(coordinate.row, $ -> new HashMap<>())
				.put(coordinate.col, new HashSet<>(candidates));
			return this;
		}

		public Set<Integer> getCandidates(Coordinate coordinate) {
			return this.candidates
				.getOrDefault(coordinate.row, Collections.emptyMap())
				.getOrDefault(coordinate.col, Collections.emptySet());
		}

		public Map<Integer, Map<Integer, Integer>> getCellsWithAnswers() {
			Map<Integer, Map<Integer, Integer>> cellsWithAnswers = new HashMap<>();

			for (int col = 0; col < 9; col++) {
				for (int row = 0; row < 9; row++) {
					int answer = riddle.get(row, col);
					if (answer != 0) {
						cellsWithAnswers
							.computeIfAbsent(row, $ -> new HashMap<>())
							.computeIfAbsent(col, $ -> answer);
					}
				}
			}

			return cellsWithAnswers;
		}

		public boolean isComplete() {
			byte[][] array = riddle.getArray();
			for (int i = 0; i < array.length; i++)
				for (int j = 0; j < array[i].length; j++)
					if (array[i][j] == 0)
						return false;
			return true;
		}

		public int getAnswers() {
			int answers = 0;
			byte[][] array = riddle.getArray();
			for (int i = 0; i < array.length; i++)
				for (int j = 0; j < array[i].length; j++)
					if (array[i][j] != 0)
						++answers;
			return answers;
		}

		public List<Coordinate> getAllCoordinates() {
			List<Coordinate> coordinates = new ArrayList<>();
			for (int row = 0; row < 9; row++)
				for (int col = 0; col < 9; col++)
					coordinates.add(new Coordinate(row, col));
			return coordinates;
		}

		public Set<Integer> computeCandidates(Coordinate coordinate) {
			// If the cell already has a value, it has no candidates
			if (riddle.get(coordinate.row, coordinate.col) != 0) {
				return Collections.emptySet();
			}

			// Start with all numbers 1-9 as candidates
			Set<Integer> candidates = new HashSet<>();
			for (int i = 1; i <= 9; i++) {
				candidates.add(i);
			}

			// Remove numbers that already exist in the same row
			for (int c = 0; c < 9; c++) {
				candidates.remove(Byte.valueOf(riddle.get(coordinate.row, c)).intValue());
			}

			// Remove numbers that already exist in the same column
			for (int r = 0; r < 9; r++) {
				candidates.remove(Byte.valueOf(riddle.get(r, coordinate.col)).intValue());
			}

			// Remove numbers that already exist in the same 3x3 box
			int boxStartRow = (coordinate.row / 3) * 3;
			int boxStartCol = (coordinate.col / 3) * 3;

			for (int r = boxStartRow; r < boxStartRow + 3; r++) {
				for (int c = boxStartCol; c < boxStartCol + 3; c++) {
					candidates.remove(Byte.valueOf(riddle.get(r, c)).intValue());
				}
			}

			return candidates;
		}

		public boolean isCorrect() {
			return this.riddle.isValid();
		}

		public SudokuGame render() {
			return render(this.renderSettings);
		}

		public SudokuGame render(Function<RenderSettings, RenderSettings> consumer) {
			return render(consumer.apply(this.renderSettings));
		}

		public SudokuGame render(RenderSettings renderSettings) {
			GameMatrix matrix = riddle;
			int imageSize = renderSettings.getImageSize();

			// Create a BufferedImage with the specified dimensions
			BufferedImage image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = image.createGraphics();

			// Fill the background
			g2d.setColor(renderSettings.getBackgroundColor());
			g2d.fillRect(0, 0, imageSize, imageSize);

			// Set properties for drawing
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			// Calculate positions for each line to distribute evenly
			int[] positions = new int[10];
			positions[0] = 0;
			positions[9] = imageSize;

			// Calculate positions for the internal lines (1-8)
			for (int i = 1; i < 9; i++) {
				positions[i] = (i * imageSize) / 9;
			}

			// Calculate cell size
			int cellSize = imageSize / 9;

			// Set up fonts
			Font numberFont = new Font(renderSettings.getFontName(), renderSettings.getFontStyle(), cellSize / 2);
			Font candidateFont = new Font(renderSettings.getFontName(), renderSettings.getCandidateFontStyle(), cellSize / 3);

			// Set up for drawing text
			g2d.setFont(numberFont);
			g2d.setColor(renderSettings.getTextColor());

			// Get FontMetrics for centering text
			FontMetrics metrics = g2d.getFontMetrics();

			// Find the first cell with the highlightNumber to identify siblings
			int highlightRow = -1;
			int highlightCol = -1;
			boolean[][] siblingCells = new boolean[9][9];

			var highlight = renderSettings.getHighlight();
			var highlightNumber = getCellAnswer(highlight);

			// First pass to find the first cell with highlightNumber value
			if (renderSettings.isHighlightSiblings() && highlightNumber != 0) {
				var array = matrix.getArray();
				for (int i = 0; i < array.length; i++) {
					for (int j = 0; j < array[i].length; j++) {
						if (highlight.row == i && highlight.col == j) {
							highlightRow = i;
							highlightCol = j;
							break;
						}
					}
					if (highlightRow != -1) break;
				}

				// Identify sibling cells
				if (highlightRow != -1) {
					// Mark cells in the same row
					for (int j = 0; j < 9; j++) {
						siblingCells[highlightRow][j] = true;
					}

					// Mark cells in the same column
					for (int i = 0; i < 9; i++) {
						siblingCells[i][highlightCol] = true;
					}

					// Mark cells in the same 3x3 box
					int boxStartRow = (highlightRow / 3) * 3;
					int boxStartCol = (highlightCol / 3) * 3;
					for (int i = boxStartRow; i < boxStartRow + 3; i++) {
						for (int j = boxStartCol; j < boxStartCol + 3; j++) {
							siblingCells[i][j] = true;
						}
					}
				}
			}

			// Draw backgrounds first
			var array = matrix.getArray();
			for (int i = 0; i < array.length; i++) {
				for (int j = 0; j < array[i].length; j++) {
					byte b = array[i][j];

					// Calculate cell boundaries
					int cellStartX = positions[j];
					int cellEndX = positions[j + 1];
					int cellStartY = positions[i];
					int cellEndY = positions[i + 1];
					int cellWidth = cellEndX - cellStartX;
					int cellHeight = cellEndY - cellStartY;

					if (highlightNumber != 0) {
						// Apply background highlighting based on settings
						if (renderSettings.isHighlightSolvedCells() && b == highlightNumber) {
							// Highlight cells with the number
							g2d.setColor(renderSettings.getSiblingHighlightColor());
							g2d.fillRect(cellStartX + 1, cellStartY + 1, cellWidth - 1, cellHeight - 1);
						} else if (renderSettings.isHighlightSiblings() && siblingCells[i][j] && b != highlightNumber) {
							// Highlight sibling cells with a lighter color
							g2d.setColor(renderSettings.getAffectedCellHighlightColor());
							g2d.fillRect(cellStartX + 1, cellStartY + 1, cellWidth - 1, cellHeight - 1);
						}
					}
				}
			}

			// Add numbers to the grid
			for (int i = 0; i < array.length; i++) {
				for (int j = 0; j < array[i].length; j++) {
					byte b = array[i][j];

					// Calculate cell boundaries
					int cellStartX = positions[j];
					int cellEndX = positions[j + 1];
					int cellStartY = positions[i];
					int cellEndY = positions[i + 1];
					int cellWidth = cellEndX - cellStartX;
					int cellHeight = cellEndY - cellStartY;

					if (b != 0) {
						// Draw the given number
						String numberStr = String.valueOf(b);
						g2d.setColor(renderSettings.getTextColor());

						// Calculate the position to center the text in the cell
						int x = cellStartX + (cellWidth - metrics.stringWidth(numberStr)) / 2;
						int y = cellStartY + ((cellHeight - metrics.getHeight()) / 2) + metrics.getAscent();

						g2d.drawString(numberStr, x, y);
					} else if (renderSettings.isShowCandidates()) {
						// Draw candidates
						g2d.setFont(candidateFont);
						FontMetrics candidateMetrics = g2d.getFontMetrics();

						// Add padding around the edges
						int padding = renderSettings.getCandidatePadding();

						// Calculate the usable cell area after padding
						int usableCellStartX = cellStartX + padding;
						int usableCellStartY = cellStartY + padding;
						int usableCellWidth = cellWidth - (2 * padding);
						int usableCellHeight = cellHeight - (2 * padding);

						// Calculate the size of each mini-cell (3x3 grid within the cell)
						int miniCellWidth = usableCellWidth / 3;
						int miniCellHeight = usableCellHeight / 3;

						// Draw all 9 numbers as candidates
						getCandidates(new Coordinate(i, j)).forEach(num -> {
							String candidateStr = String.valueOf(num);

							// Calculate which mini-cell this number belongs in (0-indexed)
							int miniRow = (num - 1) / 3;  // 0, 0, 0, 1, 1, 1, 2, 2, 2
							int miniCol = (num - 1) % 3;  // 0, 1, 2, 0, 1, 2, 0, 1, 2

							// Calculate center position of the mini-cell
							int miniCellCenterX = usableCellStartX + (miniCol * miniCellWidth) + (miniCellWidth / 2);
							int miniCellCenterY = usableCellStartY + (miniRow * miniCellHeight) + (miniCellHeight / 2);

							// Calculate position for the text to be centered in the mini-cell
							int x = miniCellCenterX - (candidateMetrics.stringWidth(candidateStr) / 2);
							int y = miniCellCenterY + (candidateMetrics.getAscent() / 2);

							// Use highlight color for candidate number based on settings
							if (renderSettings.isHighlightCandidates() && num == highlightNumber) {
								g2d.setColor(renderSettings.getCandidateHighlightColor());
							} else {
								g2d.setColor(renderSettings.getCandidateTextColor());
							}

							g2d.drawString(candidateStr, x, y);
						});

						// Restore original font
						g2d.setFont(numberFont);
					}
				}
			}

			// Draw grid lines on top if enabled
			if (renderSettings.isShowGrid()) {
				g2d.setColor(renderSettings.getGridColor());

				// Draw the thin lines
				g2d.setStroke(new BasicStroke(renderSettings.getThinLineWidth()));
				for (int i = 0; i <= 9; i++) {
					// Skip the thick lines (we'll draw them later)
					if (i % 3 != 0) {
						int position = positions[i];

						// Draw horizontal lines
						g2d.drawLine(0, position, imageSize, position);

						// Draw vertical lines
						g2d.drawLine(position, 0, position, imageSize);
					}
				}

				// Draw the thick lines (for 3x3 box boundaries)
				g2d.setStroke(new BasicStroke(renderSettings.getThickLineWidth()));
				for (int i = 0; i <= 9; i += 3) {
					int position = positions[i];

					// Draw horizontal lines
					g2d.drawLine(0, position, imageSize, position);

					// Draw vertical lines
					g2d.drawLine(position, 0, position, imageSize);
				}
			}

			g2d.dispose(); // Clean up resources

			this.image = image;
			return this;
		}

		@SneakyThrows
		public SudokuGame saveImage(String filePath) {
			ImageIO.write(image, "PNG", new File(filePath));
			return this;
		}

		@SneakyThrows
		public SudokuGame saveMapImages(String filePath) {
			int index = 0;
			for (BufferedImage bufferedImage : getMapImages())
				ImageIO.write(bufferedImage, "PNG", new File(filePath.replace(".png", "_" + index++ + ".png")));
			return this;
		}

		public List<CustomMapPacket> getMapImagePackets() {
			var frames = itemFrames.iterator();
			var images = getMapImages().iterator();
			List<CustomMapPacket> packets = new ArrayList<>();

			while (frames.hasNext() && images.hasNext()) {
				var frame = frames.next();
				var image = images.next();
				if (frame == null)
					throw new InvalidInputException("Frame is null");
				if (image == null)
					throw new InvalidInputException("Image is null");
				if (isNullOrAir(frame.getItem()))
					throw new InvalidInputException("Frame item is null");
				if (!(frame.getItem().getItemMeta() instanceof MapMeta mapMeta))
					throw new InvalidInputException("Frame item is not a map");

				var id = mapMeta.getMapId();
				packets.add(CustomMapPacket.of(id, image));
			}

			return packets;
		}

		public void sendMapImagePackets(Player player) {
			for (CustomMapPacket packet : getMapImagePackets())
				packet.send(player);
		}

		public List<BufferedImage> getMapImages() {
			List<BufferedImage> images = new ArrayList<>();
			int mapSize = 128;

			for (int y = 0; y < image.getHeight(); y += mapSize) {
				for (int x = 0; x < image.getWidth(); x += mapSize) {
					int width = Math.min(mapSize, image.getWidth() - x);
					int height = Math.min(mapSize, image.getHeight() - y);

					BufferedImage subImage = new BufferedImage(mapSize, mapSize, BufferedImage.TYPE_INT_RGB);
					Graphics2D g2d = subImage.createGraphics();
					g2d.setColor(Color.WHITE);
					g2d.fillRect(0, 0, mapSize, mapSize);
					g2d.drawImage(image, 0, 0, width, height, x, y, x + width, y + height, null);
					g2d.dispose();

					images.add(subImage);
				}
			}

			return images;
		}

		public Coordinate clickedCoordinateToGameCoordinate(int map, Coordinate clicked) {
			int baseRow = map / 3 * 3;
			int baseCol = map % 3 * 3;

			double cellSize = (double) 128 / 3;
			int cellRow = (int) (clicked.col / cellSize);
			int cellCol = (int) (clicked.row / cellSize);

			return new Coordinate(baseRow + cellRow, baseCol + cellCol);
		}


		@Data
		@Builder
		@Accessors(chain = true)
		@NoArgsConstructor
		@AllArgsConstructor
		@With
		public static class RenderSettings {
			// The puzzle to render
			private SudokuGame game;

			// The coordinate to highlight and show siblings for
			private Coordinate highlight;

			// Image dimensions
			@Builder.Default
			private int imageSize = 384;

			// Colors for different elements
			@Builder.Default
			private Color backgroundColor = Color.WHITE;
			@Builder.Default
			private Color gridColor = Color.BLACK;
			@Builder.Default
			private Color textColor = Color.BLACK;
			@Builder.Default
			private Color candidateTextColor = Color.GRAY;
			@Builder.Default
			private Color siblingHighlightColor = new Color(102, 153, 216); // Light Blue
			@Builder.Default
			private Color affectedCellHighlightColor = new Color(92, 219, 213);   // Very Light Blue
			@Builder.Default
			private Color candidateHighlightColor = new Color(51, 76, 178); // Royal Blue

			// Font settings
			@Builder.Default
			private String fontName = "Arial";
			@Builder.Default
			private int fontStyle = Font.BOLD;
			@Builder.Default
			private int candidateFontStyle = Font.PLAIN;

			// Line settings
			@Builder.Default
			private float thinLineWidth = 1.0f;
			@Builder.Default
			private float thickLineWidth = 3.0f;

			// Feature toggles
			@Builder.Default
			private boolean showGrid = true;
			@Builder.Default
			private boolean showCandidates = true;
			@Builder.Default
			private boolean highlightSolvedCells = true;
			@Builder.Default
			private boolean highlightSiblings = true;
			@Builder.Default
			private boolean highlightCandidates = true;

			// Padding for candidates
			@Builder.Default
			private int candidatePadding = 2;
		}

		@Getter
		@AllArgsConstructor
		public enum Difficulty {
			VERY_EASY(Creator.RIDDLE_9X9_EMPTY_FIELDS_VERY_EASY),
			EASY(Creator.RIDDLE_9X9_EMPTY_FIELDS_EASY),
			MEDIUM(Creator.RIDDLE_9X9_EMPTY_FIELDS_MEDIUM),
			HARD(Creator.RIDDLE_9X9_EMPTY_FIELDS_HARD),
			VERY_HARD(Creator.RIDDLE_9X9_EMPTY_FIELDS_VERY_HARD),
			HARDEST(81)
			;

			private final int difficulty;
		}
	}
}
