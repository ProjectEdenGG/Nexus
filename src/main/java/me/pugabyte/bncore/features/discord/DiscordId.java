package me.pugabyte.bncore.features.discord;

import lombok.Getter;

public class DiscordId {

	public enum Channel {
		STAFF_PROMOTIONS("133949148251553792");

		@Getter
		private String id;

		Channel(String id) {
			this.id = id;
		}
	}

	public enum User {
		PUGABYTE("115552359458799616");

		@Getter
		private String id;

		User(String id) {
			this.id = id;
		}
	}
}
