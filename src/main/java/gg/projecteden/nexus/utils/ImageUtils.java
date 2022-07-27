package gg.projecteden.nexus.utils;

import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public class ImageUtils {

	@SneakyThrows
	public static BufferedImage read(File folder, String file) {
		return read(new File(folder, file));
	}

	@SneakyThrows
	public static BufferedImage read(File file) {
		final BufferedImage image = ImageIO.read(file);

//		final int type = image.getType();
//		if (type == 1 || type == 4 || type == 5)
//			System.out.println("[WARN] Transparency not supported on " + file.getName());

		return image;
	}

	private static final String PNG = "png";

	@SneakyThrows
	public static void write(final BufferedImage image, final File folder, final String file) {
		ImageIO.write(image, PNG, new File(folder, file));
	}

	@SneakyThrows
	public static void write(final BufferedImage image, final File file) {
		ImageIO.write(image, PNG, file);
	}

	public static BufferedImage newSquareImage(final int dimension) {
		return newImage(dimension, dimension);
	}

	public static BufferedImage newImage(final int width, final int height) {
		return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	public static BufferedImage combine(final BufferedImage... images) {
		final List<BufferedImage> list = Arrays.asList(images);
		int width = Collections.max(list, Comparator.comparing(BufferedImage::getWidth)).getWidth();
		int height = Collections.max(list, Comparator.comparing(BufferedImage::getHeight)).getHeight();

		BufferedImage combined = newImage(width, height);

		Graphics graphics = combined.getGraphics();
		for (BufferedImage image : images)
			graphics.drawImage(image, 0, 0, null);

		graphics.dispose();
		return combined;
	}

	public static BufferedImage replace(final BufferedImage image, final Color from, final Color to) {
		return replace(image, from, () -> to);
	}

	public static BufferedImage replace(final BufferedImage image, final Color from, final Supplier<Color> to) {
		for (int x = 0; x < image.getWidth(); x++)
			for (int y = 0; y < image.getHeight(); y++)
				if (from.equals(new Color(image.getRGB(x, y))))
					image.setRGB(x, y, to.get().getRGB());

		return image;
	}

}
