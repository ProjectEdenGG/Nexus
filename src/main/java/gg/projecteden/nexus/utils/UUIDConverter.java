package gg.projecteden.nexus.utils;

import java.util.UUID;

public class UUIDConverter {
	public static int[] toIntArray(UUID uuid) {
		return toIntArray(uuid.toString());
	}

	public static int[] toIntArray(String uuid) {
		uuid = uuid.replace("-", "");
		int loop = 0;
		int[] intArray = new int[4];
		for (int i = 0; i < 4; i++) {
			loop = loop + 8;
			intArray[i] = (int) Long.parseLong(uuid.substring(loop - 8, loop), 16);
		}
		return intArray;
	}

	public static UUID toUUID(int[] intArray) {
		return UUID.fromString(toString(intArray));
	}

	public static String toString(int[] intArray) {
		StringBuilder uuid = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			uuid.append(Integer.toHexString(intArray[i]));
		}
		uuid.insert(8, "-");
		uuid.insert(13, "-");
		uuid.insert(18, "-");
		uuid.insert(23, "-");
		return uuid.toString();
	}
}
