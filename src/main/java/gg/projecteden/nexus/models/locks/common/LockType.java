package gg.projecteden.nexus.models.locks.common;

public enum LockType {
	PRIVATE,
	PASSWORD,
	PUBLIC,
	DONATION,
	DISPLAY,
	;

	public boolean canOpen() {
		return switch (this) {
			case PRIVATE, PASSWORD -> false;
			default -> true;
		};
	}
}
