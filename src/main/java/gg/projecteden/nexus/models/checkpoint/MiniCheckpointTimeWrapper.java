package gg.projecteden.nexus.models.checkpoint;

import gg.projecteden.nexus.features.minigames.models.matchdata.CheckpointData;

import java.time.Duration;
import java.util.Comparator;

public record MiniCheckpointTimeWrapper(int checkpoint, Duration time) {
	public String getShortDisplayName() {
		return CheckpointData.formatShortCheckpointName(checkpoint);
	}

	public static Comparator<MiniCheckpointTimeWrapper> idComparator() {
		return IdComparator.INSTANCE;
	}

	public static Comparator<MiniCheckpointTimeWrapper> timeComparator() {
		return TimeComparator.INSTANCE;
	}

	private static final class IdComparator implements Comparator<MiniCheckpointTimeWrapper> {
		private static final IdComparator INSTANCE = new IdComparator();

		@Override
		public int compare(MiniCheckpointTimeWrapper o1, MiniCheckpointTimeWrapper o2) {
			int id1 = o1.checkpoint;
			if (id1 == -1) id1 = Integer.MAX_VALUE;
			int id2 = o2.checkpoint;
			if (id2 == -1) id2 = Integer.MAX_VALUE;
			return id1 - id2;
		}
	}

	private static final class TimeComparator implements Comparator<MiniCheckpointTimeWrapper> {
		private static final TimeComparator INSTANCE = new TimeComparator();

		@Override
		public int compare(MiniCheckpointTimeWrapper o1, MiniCheckpointTimeWrapper o2) {
			return o1.time.compareTo(o2.time);
		}
	}
}
