package gg.projecteden.nexus.features.minigames.models.perks;

public enum HideParticle {
	ALL {
		@Override
		public boolean showParticle(PerkCategory particleType) {
			return false;
		}
	},
	NONE {
		@Override
		public boolean showParticle(PerkCategory particleType) {
			return true;
		}

		@Override
		public String toString() {
			return "no";
		}
	},
	PLAYER {
		@Override
		public boolean showParticle(PerkCategory particleType) {
			return particleType != PerkCategory.PARTICLE;
		}
	},
	TRAIL {
		@Override
		public boolean showParticle(PerkCategory particleType) {
			return particleType != PerkCategory.ARROW_TRAIL;
		}
	},
	;

	public abstract boolean showParticle(PerkCategory particleType);
}
