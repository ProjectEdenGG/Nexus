package me.pugabyte.nexus.models.geoip;

import com.google.gson.annotations.SerializedName;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.PlayerOwnedObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity("geoip")
@NoArgsConstructor
@Converters(UUIDConverter.class)
public class GeoIP implements PlayerOwnedObject {
	@Id
	private UUID uuid;
	private String ip;
	private String type;
	@SerializedName("continent_code")
	private String continentCode;
	@SerializedName("continent_name")
	private String continentName;
	@SerializedName("country_code")
	private String countryCode;
	@SerializedName("country_name")
	private String countryName;
	@SerializedName("region_code")
	private String regionCode;
	@SerializedName("region_name")
	private String regionName;
	private String city;
	private String zip;
	private Double latitude;
	private Double longitude;
	private Location location;
	@SerializedName("time_zone")
	private Timezone timezone;
	private Currency currency;
	private Connection connection;

	@Data
	public static class Location {
		@SerializedName("geoname_id")
		private int geonameId;
		private String capital;
		private List<Language> languages = new ArrayList<>();
		@SerializedName("country_flag")
		private String countryFlag;
		@SerializedName("country_flag_emoji")
		private String countryFlagEmoji;
		@SerializedName("country_flag_emoji_unicode")
		private String countryFlagEmojiUnicode;
		@SerializedName("calling_code")
		private String callingCode;
		@SerializedName("is_eu")
		private boolean isEu;

		@Data
		public static class Language {
			private String code;
			private String name;
			@SerializedName("native")
			private String nativeName;
		}
	}

	@Data
	public static class Timezone {
		private String id;
		@SerializedName("current_time")
		private String currentTime;
		@SerializedName("gmt_offset")
		private int gmtOffset;
		private String code;
		@SerializedName("is_daylight_saving")
		private boolean isDaylightSaving;
	}

	@Data
	public static class Currency {
		private String code;
		private String name;
		private String plural;
		private String symbol;
		@SerializedName("symbol_native")
		private String symbolNative;
	}

	@Data
	public static class Connection {
		private String asn;
		private String isp;
	}

	@Data
	public static class Security {
		@SerializedName("is_proxy")
		private boolean isProxy;
		@SerializedName("proxy_type")
		private String proxyType;
		@SerializedName("is_crawler")
		private boolean isCrawler;
		@SerializedName("crawler_name")
		private String crawlerName;
		@SerializedName("crawler_type")
		private String crawlerType;
		@SerializedName("is_tor")
		private boolean isTor;
		@SerializedName("threat_level")
		private String threatLevel;
		@SerializedName("threat_types")
		private String threatTypes;
	}

	public String getFriendlyLocationString() {
		return city + ", " + regionName + ", " + countryName;
	}

	@Data
	public static class Distance {
		double distance;
		double miles;
		double kilometers;

		public Distance(GeoIP from, GeoIP to) {
			this(distance(from, to));
		}

		public Distance(double distance) {
			this.distance = distance;
			this.miles = distance * 3961;
			this.kilometers = distance * 6373;
		}

		public static double distance(GeoIP from, GeoIP to) {
			if (from.getLatitude() == null || from.getLongitude() == null)
				throw new InvalidInputException("Could not find " + from.getOfflinePlayer().getName() + "'s location");
			if (to.getLatitude() == null || to.getLongitude() == null)
				throw new InvalidInputException("Could not find " + to.getOfflinePlayer().getName() + "'s location");

			double latFrom = Math.toRadians(from.getLatitude()), longFrom = Math.toRadians(from.getLongitude()),
					latTo = Math.toRadians(to.getLatitude()), longTo = Math.toRadians(to.getLongitude());

			double distanceLat = latTo - latFrom, distanceLong = longTo - longFrom;

			double a = Math.pow(Math.sin(distanceLat / 2), 2);
			double b = Math.cos(latFrom);
			double c = Math.cos(latTo);
			double d = Math.pow(Math.sin(distanceLong / 2), 2);
			double e = a + (b * c * d);
			double f = Math.sqrt(e);
			double g = Math.sqrt(1 - e);

			return Math.atan2(f, g) * 2;
		}
	}
}
