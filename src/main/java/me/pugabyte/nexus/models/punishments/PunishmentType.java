package me.pugabyte.nexus.models.punishments;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import static me.pugabyte.nexus.utils.StringUtils.camelCase;

@Getter
@AllArgsConstructor
public enum PunishmentType {
	BAN("banned", true, true, true, true) {
		@Override
		public void action(Punishment punishment) {
			kick(punishment);
		}

		@Override
		public String getDisconnectMessage(Punishment punishment) {
			return punishment.getReason();
		}
	},
	IP_BAN("ip-banned", true, true, true, true) { // TODO onlyOneActive ?

		@Override
		public void action(Punishment punishment) {
			kick(punishment);
			// TODO look for alts, kick
		}

		@Override
		public String getDisconnectMessage(Punishment punishment) {
			return punishment.getReason();
		}
	},
	KICK("kicked", false, false, true, true) {
		@Override
		public void action(Punishment punishment) {
			kick(punishment);
		}

		@Override
		public String getDisconnectMessage(Punishment punishment) {
			return punishment.getReason();
		}
	},
	MUTE("muted", true, true, true, false) {
		@Override
		public void action(Punishment punishment) {
			punishment.send("You have been muted"); // TODO
		}

		@Override
		public void onExpire(Punishment punishment) {
			punishment.send("Your mute has expired");
		}
	},
	WARN("warned", false, false, false, false) {
		@Override
		public void action(Punishment punishment) {
			Punishments.of(punishment).tryShowWarns();
		}
	},
	FREEZE("froze", false, true, true, true) {
		@Override
		public void action(Punishment punishment) {
			punishment.send("&cYou have been frozen! This likely means you are breaking a rule; please pay attention to staff in chat");
		}

		@Override
		public void onExpire(Punishment punishment) {
			punishment.send("&cYou have been unfrozen");
		}
	};

	private final String pastTense;
	@Accessors(fluent = true)
	private final boolean hasTimespan;
	private final boolean onlyOneActive;
	private final boolean automaticallyReceived;
	private final boolean receivedIfAfk;

	public abstract void action(Punishment punishment);

	public void onExpire(Punishment punishment) {
	}

	public String getDisconnectMessage(Punishment punishment) {
		throw new UnsupportedOperationException("Punishment type " + camelCase(this) + " does not have a disconnect message");
	}

	void kick(Punishment punishment) {
		if (punishment.isOnline()) {
			punishment.getPlayer().kick(punishment.getDisconnectMessage());
			punishment.received();
		}
	}
}
