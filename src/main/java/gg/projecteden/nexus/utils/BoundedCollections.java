package gg.projecteden.nexus.utils;

import lombok.AllArgsConstructor;

import java.util.ArrayList;

public class BoundedCollections {

	@AllArgsConstructor
	public static class BoundedList<Type> extends ArrayList<Type> {
		private final int maxEntries;

		@Override
		public boolean add(Type type) {
			if (size() > maxEntries) {
				removeFirst();
			}

			return super.add(type);
		}
	}
}
