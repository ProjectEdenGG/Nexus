package gg.projecteden.nexus.features.commands.staff.admin;

import com.sk89q.worldedit.regions.Region;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ColorUtils;
import gg.projecteden.nexus.utils.LocationUtils.CardinalDirection;
import gg.projecteden.nexus.utils.MaterialTag;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Permission(Group.ADMIN)
public class PixelartCommand extends CustomCommand {
	private static final MaterialTag SURVIVAL_IGNORE = new MaterialTag(MaterialTag.UNOBTAINABLE)
		.append(Material.NETHERITE_BLOCK, Material.DEEPSLATE_EMERALD_ORE);

	public PixelartCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("draw <url> [--survival] [--exclude] [--excludeTags] [--include] [--includeTags]")
	@Description("Draw a image to your current selection")
	@SuppressWarnings("SuspiciousNameCombination")
	void blockParty_drawImage(
		String url,
		@Switch boolean survival,
		@Switch @Arg(type = Material.class) List<Material> exclude,
		@Switch @Arg(type = Tag.class) List<Tag<Material>> excludeTags,
		@Switch @Arg(type = Material.class) List<Material> include,
		@Switch @Arg(type = Tag.class) List<Tag<Material>> includeTags
	) {
		try {
			MaterialTag exclusions = new MaterialTag().append(exclude);
			if (excludeTags != null)
				for (Tag<Material> tag : excludeTags)
					exclusions.append(tag);

			MaterialTag inclusions = new MaterialTag().append(include);
			if (includeTags != null)
				for (Tag<Material> tag : includeTags)
					inclusions.append(tag);

			if (survival)
				exclusions.append(SURVIVAL_IGNORE);

			Region selection = null;

			try {
				selection = worldedit().getPlayerSelection(player());
			} catch (Exception ex) {
				error("Error getting WorldEdit selection: " + ex.getMessage());
			}

			if (selection == null)
				error("You must have a WorldEdit selection");

			List<Block> blocks = worldedit().getBlocks(selection);
			if (blocks.stream().map(Block::getType).collect(Collectors.toSet()).size() != 1)
				error("Your WorldEdit selection must contain only one type of block (to prevent drawing over previous image)");

			URI imageUrl = URI.create(url);
			URLConnection connection = imageUrl.toURL().openConnection();
			connection.connect();

			String contentType = connection.getContentType();
			if (contentType == null || !contentType.startsWith("image/")) {
				error("The provided URL does not point to an image. Content type: " + (contentType == null ? "unknown" : contentType));
				return;
			}

			BufferedImage image = ImageIO.read(connection.getInputStream());
			if (image == null) {
				error("Failed to read image from the provided URL");
				return;
			}

			if (image.getWidth() != selection.getWidth() || image.getHeight() != selection.getLength()) {
				error("The image dimensions do not match the WorldEdit selection (%d, %d vs %d, %d)".formatted(image.getWidth(), image.getHeight(), selection.getWidth(), selection.getWidth()));
				return;
			}

			var facing = CardinalDirection.of(player());
			blocks.sort(Comparator.comparing(block -> {
				var pos = block.getLocation().toVector().subtract(facing.toVector());

				int x = pos.getBlockX();
				int z = pos.getBlockZ();

				int relativeX = 0, relativeY = 0;
				switch (facing) {
					case NORTH -> { relativeX = x; relativeY = z; }
					case EAST -> { relativeX = z; relativeY = -x; }
					case SOUTH -> { relativeX = -x; relativeY = -z; }
					case WEST -> { relativeX = -z; relativeY = x; }
				}

				return relativeY + (relativeX * 1000);
			}));

			var blockIterator = blocks.iterator();
			// TODO WorldEdit?
			for (int x = 0; x < image.getWidth(); x++) {
				for (int y = 0; y < image.getHeight(); y++ ) {
					var pixel = new Color(image.getRGB(x, y));
					blockIterator.next().setType(ColorUtils.closestBlock(pixel, exclusions, inclusions));
				}
			}

			send(PREFIX + "Image drawn successfully");
		} catch (MalformedURLException e) {
			error("Invalid URL format: " + e.getMessage());
		} catch (IOException e) {
			error("Failed to fetch image: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@SneakyThrows
	@Path("palette")
	void palette() {
		for (Material material : ColorUtils.BLOCK_PALETTE.keySet()) {
			try {
				send(PREFIX + ChatColor.of(ColorUtils.BLOCK_PALETTE.get(material)) + material);
			} catch (Exception ex) {
				send(material + ": " + ex.getMessage());
			}
		}
	}
}
