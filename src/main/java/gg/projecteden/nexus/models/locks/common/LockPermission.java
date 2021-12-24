package gg.projecteden.nexus.models.locks.common;

public enum LockPermission {
	ADMIN {
		@Override
		public boolean canEdit(LockType lockType) {
			return true;
		}
	},
	MEMBER {
		@Override
		public boolean canEdit(LockType lockType) {
			return switch (lockType) {
				case DONATION, DISPLAY -> false;
				default -> true;
			};
		}
	},
	;

	public abstract boolean canEdit(LockType lockType);
}
