package gg.projecteden.nexus.features.resourcepack.models.font;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

// TODO: Combine with EmojiUser#Emoji ?
@RequiredArgsConstructor
public enum CustomEmoji {
	UNKNOWN_WORLDGROUP("❌"),

	BOT(""),
	SUPPORTER("💖"),
	BIRTHDAY("🎂"),

	PODIUM_FIRST("🥇"),
	PODIUM_SECOND("🥈"),
	PODIUM_THIRD("🥉"),

	SOCIAL_MEDIA_TWITTER(""),
	SOCIAL_MEDIA_INSTAGRAM(""),
	SOCIAL_MEDIA_SNAPCHAT(""),
	SOCIAL_MEDIA_YOUTUBE(""),
	SOCIAL_MEDIA_TWITCH(""),
	SOCIAL_MEDIA_TIKTOK(""),
	SOCIAL_MEDIA_DISCORD(""),
	SOCIAL_MEDIA_STEAM(""),
	SOCIAL_MEDIA_SPOTIFY(""),
	SOCIAL_MEDIA_QUEUP(""),
	SOCIAL_MEDIA_REDDIT(""),
	SOCIAL_MEDIA_GITHUB(""),
	SOCIAL_MEDIA_VENMO("洱"),
	SOCIAL_MEDIA_PAYPAL("郎"),

	SCREEN_BLACK("鄜"),
	SCREEN_RED_20_OPACITY("滍");

	@NonNull
	final String fontChar;

	public String getChar() {
		return fontChar;
	}
}
