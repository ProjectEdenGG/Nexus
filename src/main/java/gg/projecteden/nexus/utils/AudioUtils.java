package gg.projecteden.nexus.utils;

import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class AudioUtils {

	@SneakyThrows
	public static double getVorbisDuration(File file) {
		return getVorbisDuration(new FileInputStream(file));
	}

	private static final Function<byte[], Integer> get = array -> ByteBuffer.wrap(array).order(ByteOrder.LITTLE_ENDIAN).getInt(0);

	public static double getVorbisDuration(FileInputStream stream) throws IOException {

		final int size = (int) stream.getChannel().size();
		byte[] data = new byte[size];

		stream.read(data);

		final double duration = getVorbisDuration(data);

		stream.close();

		return duration;
	}

	public static double getVorbisDuration(byte[] data) {
		int rate = -1;
		int length = -1;

		BiPredicate<Integer, String> search = (start, string) -> {
			int index = 0;
			for (byte character : string.getBytes())
				if (data[start + index++] != character)
					return false;

			return true;
		};

		BiFunction<Integer, Integer, Integer> read = (start, amount) -> {
			byte[] array = new byte[amount + 1];
			int index = 0;
			for (int offset = 0; offset <= amount; offset++)
				array[index++] = data[start + offset];
			return get.apply(array);
		};

		for (int i = data.length - 15; i >= 0 && length < 0; i--)
			if (search.test(i, "OggS"))
				length = read.apply(i + 6, 8);

		for (int i = 0; i < data.length - 14 && rate < 0; i++)
			if (search.test(i, "vorbis"))
				rate = read.apply(i + 11, 4);

		return (double) (length * 1000) / (double) rate;
	}

}
