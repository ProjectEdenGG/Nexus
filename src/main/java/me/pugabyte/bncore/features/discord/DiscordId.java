package me.pugabyte.bncore.features.discord;

import lombok.Getter;

public class DiscordId {

	public enum Channel {
		GENERAL("132680070480396288"),
		ANNOUNCEMENTS("133970047382061057"),
		BOT_COMMANDS("223897739082203137"),

		BRIDGE("331277920729432065"),
		STAFF_BRIDGE("331842528854802443"),
		STAFF_OPS_BRIDGE("331846903266279424"),
		STAFF_ADMINS("133950052249894913"),

		STAFF_LOG("256866302176526336"),
		ADMIN_LOG("390751748261937152"),

		STAFF_ALERTS("265688885839921152"),
		STAFF_PROMOTIONS("133949148251553792"),

		TEST("241774576822910976");

		@Getter
		private String id;

		Channel(String id) {
			this.id = id;
		}
	}

	public enum VoiceChannel {
		MINIGAMES("133782271822921728"),
		RED("133785819432353792"),
		BLUE("133785864890351616"),
		GREEN("133785902680899585"),
		YELLOW("133785902680899585"),
		WHITE("360496040501051392");

		@Getter
		private String id;

		VoiceChannel(String id) {
			this.id = id;
		}
	}

	public enum User {
		PUGABYTE("115552359458799616"),
		RELAY("352231755551473664"),
		KODA("223794142583455744"),
		UBER("85614143951892480");

		@Getter
		private String id;

		User(String id) {
			this.id = id;
		}
	}

	public enum Guild {
		BEAR_NATION("132680070480396288");

		@Getter
		private String id;

		Guild(String id) {
			this.id = id;
		}
	}

	public enum Role {
		SENIOR_STAFF("230043171407527938"),
		STAFF("230043287782817792"),
		SUPPORTER("269916102199476236"),
		VERIFIED("411658569444884495"),
		NERD("387029729401896980"),
		KODA("331634959351545857");

		@Getter
		private String id;

		Role(String id) {
			this.id = id;
		}
	}
}
