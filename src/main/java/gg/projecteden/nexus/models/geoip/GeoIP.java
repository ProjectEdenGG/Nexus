package gg.projecteden.nexus.models.geoip;

import com.google.gson.annotations.SerializedName;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.HttpUtils;
import gg.projecteden.nexus.utils.Utils.SerializedExclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

@Data
@Entity(value = "geoip", noClassnameStored = true)
@NoArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class GeoIP implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private String ip;
	private LocalDateTime timestamp;
	private TimeFormat timeFormat = TimeFormat.TWELVE;

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
	@Accessors(fluent = true)
	private Location location;
	@SerializedName("time_zone")
	private Timezone timezone;
	private Currency currency;
	private Connection connection;
	@SerializedExclude // Using ipqualityscore instead of ipstack for this
	private Security security;
	private boolean mitigated;

	public static boolean exists(GeoIP geoip) {
		if (geoip == null)
			return false;

		Timezone timeZone = geoip.getTimezone();
		if (timeZone == null || timeZone.getId() == null)
			return false;

		if (geoip.getCurrentTime() == null)
			return false;

		return true;
	}

	public Security getSecurity(String ip) {
		if (ip == null)
			throw new InvalidInputException("Cannot check IP security on null IP");

		if (!ip.equals(this.ip) || security == null) {
			this.ip = ip;
			security = Security.call(ip);
		}

		return security;
	}

	public List<DayOfWeek> getWeek() {
		var first = FIRST_DAY_OF_WEEK.keySet()
			.stream()
			.filter(day -> FIRST_DAY_OF_WEEK.get(day).contains(countryCode))
			.findFirst()
			.orElse(DayOfWeek.SUNDAY);

		List<DayOfWeek> week = new ArrayList<>();
		for (int i = 0; i < 7; i++)
			week.add(first.plus(i));
		return week;
	}

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
		private static final String parameters = HttpUtils.formatParameters(Map.of(
				"strictness", "1",
				"fast", "true"
		));

		private static final String URL = "https://www.ipqualityscore.com/api/json/ip/%s/%s?" + parameters;
		private static final String API_KEY = Nexus.getInstance().getConfig().getString("tokens.ipqualityscore");

		@SneakyThrows
		public static Security call(String ip) {
			return HttpUtils.mapJson(Security.class, URL, API_KEY, ip);
		}

		@SerializedName("request_id")
		private String requestId;
		private LocalDateTime timestamp = LocalDateTime.now();
		private boolean success;
		private String message;
		@SerializedName("fraud_score")
		private int fraudScore;
		@SerializedName("country_code")
		private String countryCode;
		private String region;
		private String city;
		private String isp;
		private int asn;
		private String organization;
		private double latitude;
		private double longitude;
		@SerializedName("is_crawler")
		private boolean isCrawler;
		private String timezone;
		private boolean mobile;
		private String host;
		private boolean proxy;
		private boolean vpn;
		private boolean tor;
		@SerializedName("active_vpn")
		private boolean activeVpn;
		@SerializedName("active_tor")
		private boolean activeTor;
		@SerializedName("recent_abuse")
		private boolean recentAbuse;
		@SerializedName("bot_status")
		private boolean botStatus;
		@SerializedName("connection_type")
		private String connectionType;
		@SerializedName("abuse_velocity")
		private String abuseVelocity;
	}

	@AllArgsConstructor
	public enum TimeFormat {
		TWELVE("h:mm a", "hh:mm a"),
		TWENTY_FOUR("H:mm", "HH:mm"),
		;

		private final String shortFormat, longFormat;

		public String formatShort(TemporalAccessor time) {
			return DateTimeFormatter.ofPattern(shortFormat).format(time);
		}

		public String formatLong(TemporalAccessor time) {
			return DateTimeFormatter.ofPattern(longFormat).format(time);
		}

	}

	public String getFriendlyLocationString() {
		return city + ", " + regionName + ", " + countryName;
	}

	public ZonedDateTime getCurrentTime() {
		final TimeZone timezone = TimeZone.getTimeZone(getTimezone().getId());
		return ZonedDateTime.now().toOffsetDateTime().atZoneSameInstant(timezone.toZoneId());
	}

	public String getCurrentTimeShort() {
		return timeFormat.formatShort(getCurrentTime());
	}

	public String getCurrentTimeLong() {
		return timeFormat.formatLong(getCurrentTime());
	}

	// https://github.com/unicode-org/cldr-json/releases cldr-core/supplemental/weekData.json$supplemental.weekData.firstDay
	private static final Map<DayOfWeek, List<String>> FIRST_DAY_OF_WEEK = Map.of(
		DayOfWeek.SATURDAY, List.of("AF","BH","DJ","DZ","EG","IQ","IR","JO","KW","LY","OM","QA","SD","SY"),
		DayOfWeek.SUNDAY, List.of("AG","AS","BD","BR","BS","BT","BW","BZ","CA","CO","DM","DO","ET","GT","GU","HK","HN","ID","IL","IN","IS","JM","JP","KE","KH","KR","LA","MH","MM","MO","MT","MX","MZ","NI","NP","PA","PE","PH","PK","PR","PT","PY","SA","SG","SV","TH","TT","TW","UM","US","VE","VI","WS","YE","ZA","ZW"),
		DayOfWeek.MONDAY, List.of("AD","AE","AI","AL","AM","AN","AR","AT","AU","AX","AZ","BA","BE","BG","BM","BN","BY","CH","CL","CM","CN","CR","CY","CZ","DE","DK","EC","EE","ES","FI","FJ","FO","FR","GB","GE","GF","GP","GR","HR","HU","IE","IT","KG","KZ","LB","LI","LK","LT","LU","LV","MC","MD","ME","MK","MN","MQ","MY","NL","NO","NZ","PL","RE","RO","RS","RU","SE","SI","SK","SM","TJ","TM","TR","UA","UY","UZ","VA","VN","XK"),
		DayOfWeek.FRIDAY, List.of("MV")
	);

	@Data
	public static class Distance implements Comparable<Distance> {
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
				throw new InvalidInputException("Could not find " + from.getName() + "'s location");
			if (to.getLatitude() == null || to.getLongitude() == null)
				throw new InvalidInputException("Could not find " + to.getName() + "'s location");

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

		public String getMilesFormatted() {
			if (miles > 10)
				return StringUtils.getCnf().format(miles);
			else
				return StringUtils.getCdf().format(miles);
		}

		public String getKilometersFormatted() {
			if (kilometers > 10)
				return StringUtils.getCnf().format(kilometers);
			else
				return StringUtils.getCdf().format(kilometers);
		}

		@Override
		public int compareTo(@NotNull Distance other) {
			return Double.compare(distance, other.getDistance());
		}

	}
}
