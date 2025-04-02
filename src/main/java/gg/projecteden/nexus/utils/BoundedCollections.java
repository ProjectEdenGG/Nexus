package gg.projecteden.nexus.utils;

import lombok.AllArgsConstructor;

import java.util.ArrayList;

public class BoundedCollections {

	@AllArgsConstructor
	public static class BoundedList<Element> extends ArrayList<Element> {
		private final int maxEntries;

		@Override
		public boolean add(Element element) {
			if (size() > maxEntries) {
				removeFirst();
			}

			return super.add(element);
		}
	}
}
