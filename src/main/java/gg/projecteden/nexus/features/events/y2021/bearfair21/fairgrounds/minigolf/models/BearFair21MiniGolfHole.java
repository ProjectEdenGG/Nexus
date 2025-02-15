package gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models;

import gg.projecteden.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.BearFair21MiniGolf;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public enum BearFair21MiniGolfHole {
	PRACTICE(0, 1),
	ONE(1, 1),
	TWO(2, 1),
	THREE(3, 2),
	FOUR(4, 2),
	FIVE(5, 3),
	SIX(6, 3),
	SEVEN(7, 2),
	EIGHT(8, 2),
	NINE(9, 3),
	TEN(10, 2),
	ELEVEN(11, 3),
	TWELVE(12, 2),
	THIRTEEN(13, 3),
	FOURTEEN(14, 3),
	FIFTEEN(15, 5),
	SIXTEEN(16, 4),
	SEVENTEEN(17, 3),
	EIGHTEEN(18, 2);

	@Getter
	private final int hole;
	@Getter
	private final int par;

	public String getRegionId() {
		return BearFair21MiniGolf.getRegionHole() + this.hole;
	}

	public static List<BearFair21MiniGolfHole> getHoles() {
		List<BearFair21MiniGolfHole> holes = new ArrayList<>();
		for (BearFair21MiniGolfHole hole : values()) {
			if (!hole.equals(PRACTICE))
				holes.add(hole);
		}
		return holes;
	}
}
