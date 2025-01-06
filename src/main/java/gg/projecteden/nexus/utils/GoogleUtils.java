package gg.projecteden.nexus.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets.Values;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.ClearValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.mongodb.serializers.LocalDateConverter;
import gg.projecteden.api.mongodb.serializers.LocalDateTimeConverter;
import gg.projecteden.api.mongodb.serializers.LocalTimeConverter;
import gg.projecteden.nexus.Nexus;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * https://developers.google.com/sheets/api/quickstart/java
 */
public class GoogleUtils {

	private static final String CREDENTIALS_FILE_PATH = "google/credentials.json";
	private static final String TOKENS_DIRECTORY_PATH = "google/tokens";
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);

	private static NetHttpTransport HTTP_TRANSPORT;
	private static LocalServerReceiver receiver;
	public static Credential USER;

	public static boolean hasStarted() {
		return receiver != null;
	}

	public static void startup() {
		shutdown();

		setup();
		login();
		SheetsUtils.startup();
	}

	@SneakyThrows
	public static void shutdown() {
		if (hasStarted())
			receiver.stop();
	}

	@SneakyThrows
	private static void setup() {
		HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
	}

	/**
	 * When prompted with an authorization link in console, log in with the hi@projecteden.gg google
	 * credentials, complete the approval process, then copy the redirect url and curl it on the server
	 */
	@SneakyThrows
	private static void login() {
		GoogleClientSecrets secrets = GoogleClientSecrets.load(JSON_FACTORY,
			new InputStreamReader(new FileInputStream(IOUtils.getPluginFile(CREDENTIALS_FILE_PATH))));

		final int port = 8888 + (Env.values().length - (Nexus.getEnv().ordinal() + 1));
		receiver = new LocalServerReceiver.Builder().setPort(port).build();
		USER = new AuthorizationCodeInstalledApp(
			new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, secrets, SCOPES)
				.setDataStoreFactory(new FileDataStoreFactory(IOUtils.getPluginFolder(TOKENS_DIRECTORY_PATH)))
				.setAccessType("offline")
				.build(),
			receiver
		).authorize("user");
	}

	public static class SheetsUtils {

		public static Sheets SHEETS;
		private static final String SHEETS_CONFIG_FILE_PATH = "google/spreadsheets.yml";

		static {
			startup();
		}

		public static void startup() {
			SHEETS = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, USER)
				.setApplicationName(Nexus.class.getSimpleName())
				.build();
		}

		@NotNull
		private static YamlConfiguration getConfig() {
			return IOUtils.getNexusConfig(SHEETS_CONFIG_FILE_PATH);
		}

		public static void shutdown() {
			SHEETS = null;
		}

		@Getter
		public enum EdenSpreadsheet {
			REMINDERS,
			;

			private final String id;

			EdenSpreadsheet() {
				this.id = getConfig().getString("spreadsheetIds." + name().toLowerCase());
			}
		}

		public static Spreadsheets spreadsheets() {
			return SHEETS.spreadsheets();
		}

		public static Values values() {
			return spreadsheets().values();
		}

		@SneakyThrows
		public static ValueRange sheetValues(EdenSpreadsheet spreadsheet, String sheetId, String range) {
			return values().get(spreadsheet.getId(), sheetId + (range == null ? "" : "!" + range)).execute();
		}

		@SneakyThrows
		public static UpdateValuesResponse updateEntireSheet(EdenSpreadsheet spreadsheet, String sheetId, ValueRange values) {
			clearSheet(spreadsheet, sheetId);
			return values()
				.update(spreadsheet.getId(), sheetId, values)
				.setValueInputOption(ValueInputOption.USER_ENTERED.name())
				.execute();
		}

		@SneakyThrows
		public static ClearValuesResponse clearSheet(EdenSpreadsheet spreadsheet, String sheetId) {
			return values()
				.clear(spreadsheet.getId(), sheetId, new ClearValuesRequest())
				.execute();
		}

		// Serialization

		@NotNull
		public static Object valueOf(String string) {
			return string == null ? "" : string;
		}

		@NotNull
		public static Object valueOf(boolean bool) {
			return valueOf(String.valueOf(bool));
		}

		@NotNull
		public static Object valueOf(Collection<String> strings) {
			return Nullables.isNullOrEmpty(strings) ? "" : valueOf(String.join("\n", strings));
		}

		@NotNull
		public static Object valueOf(LocalDateTime dateTime) {
			return valueOf((String) new LocalDateTimeConverter().encode(dateTime));
		}

		public static Object valueOf(LocalDate date) {
			return valueOf((String) new LocalDateConverter().encode(date));
		}

		@NotNull
		public static Object valueOf(LocalTime time) {
			return valueOf((String) new LocalTimeConverter().encode(time));
		}

		@NotNull
		public static Object valueOf(Enum<?> enumeration) {
			return valueOf(enumeration == null ? null : enumeration.name());
		}

		// Deserialization

		@Nullable
		public static String asString(Iterator<Object> iterator) {
			return asString(iterator, null);
		}

		@Nullable
		public static String asString(Iterator<Object> iterator, String defaultValue) {
			Object next = iterator.hasNext() ? iterator.next() : defaultValue;
			if (next == null)
				return null;
			if (next instanceof String string)
				return string;

			Nexus.warn("[GoogleUtils] Object is not a string: " + next.getClass().getSimpleName() + ": " + next);
			return next.toString();
		}

		@Nullable
		public static String asTrimmedString(Iterator<Object> iterator) {
			String string = asString(iterator, null);
			if (string != null)
				string = string.trim();
			if (Nullables.isNullOrEmpty(string))
				return null;
			return string;
		}

		@NotNull
		public static List<String> asStringArrayList(Iterator<Object> iterator) {
			return asStringArrayList(iterator, new ArrayList<>());
		}

		@NotNull
		public static List<String> asStringArrayList(Iterator<Object> iterator, List<String> defaultValue) {
			if (!iterator.hasNext())
				return defaultValue;

			final String string = asTrimmedString(iterator);
			if (string == null)
				return defaultValue;

			return Arrays.asList(string.split("\n"));
		}

		@NotNull
		public static Set<String> asStringLinkedHashSet(Iterator<Object> iterator) {
			return asStringLinkedHashSet(iterator, new LinkedHashSet<>());
		}

		@NotNull
		public static Set<String> asStringLinkedHashSet(Iterator<Object> iterator, Set<String> defaultValue) {
			if (!iterator.hasNext())
				return defaultValue;

			final String string = asTrimmedString(iterator);
			if (string == null)
				return defaultValue;

			return new LinkedHashSet<>(Arrays.asList(string.split("\n")));
		}

		public static boolean asBoolean(Iterator<Object> iterator) {
			return asBoolean(iterator, false);
		}

		public static boolean asBoolean(Iterator<Object> iterator, boolean defaultValue) {
			return iterator.hasNext() ? Boolean.parseBoolean(asTrimmedString(iterator)) : defaultValue;
		}

		@Nullable
		public static LocalDateTime asLocalDateTime(Iterator<Object> iterator) {
			return asLocalDateTime(iterator, null);
		}

		@Nullable
		public static LocalDateTime asLocalDateTime(Iterator<Object> iterator, LocalDateTime defaultValue) {
			final String value = asTrimmedString(iterator);
			final LocalDate date = asLocalDate(value, null);
			if (date == null)
				return defaultValue;

			final LocalTime time = asLocalTime(iterator);
			return time == null ? date.atStartOfDay() : date.atTime(time);
		}

		@Nullable
		public static LocalDate asLocalDate(Iterator<Object> iterator) {
			return asLocalDate(iterator, null);
		}

		@Nullable
		public static LocalDate asLocalDate(Iterator<Object> iterator, LocalDate defaultValue) {
			return asLocalDate(asTrimmedString(iterator), defaultValue);
		}

		@Nullable
		private static LocalDate asLocalDate(String value, LocalDate defaultValue) {
			if (Nullables.isNullOrEmpty(value))
				return defaultValue;

			try {
				return new LocalDateConverter().decode(value);
			} catch (DateTimeParseException ex) {
				Nexus.log("Could not parse " + value + " as date");
				return null;
			}
		}

		@Nullable
		public static LocalTime asLocalTime(Iterator<Object> iterator) {
			return asLocalTime(iterator, null);
		}

		@Nullable
		public static LocalTime asLocalTime(Iterator<Object> iterator, LocalTime defaultValue) {
			return asLocalTime(asTrimmedString(iterator), defaultValue);
		}

		@Nullable
		private static LocalTime asLocalTime(String value, LocalTime defaultValue) {
			if (Nullables.isNullOrEmpty(value))
				return defaultValue;

			try {
				return new LocalTimeConverter().decode(value);
			} catch (DateTimeParseException ex) {
				Nexus.log("Could not parse " + value + " as time");
				return null;
			}
		}

		/**
		 * https://developers.google.com/sheets/api/reference/rest/v4/ValueInputOption
		 */
		public enum ValueInputOption {
			/**
			 * The values the user has entered will not be parsed and will be stored as-is.
			 */
			RAW,

			/**
			 * The values will be parsed as if the user typed them into the UI. Numbers will stay
			 * as numbers, but strings may be converted to numbers, dates, etc. following the same
			 * rules that are applied when entering text into a cell via the Google Sheets UI.
			 */
			USER_ENTERED,
		}

	}

}
