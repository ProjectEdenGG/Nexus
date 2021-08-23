package gg.projecteden.nexus.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.Sheets.Spreadsheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.ClearValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import gg.projecteden.mongodb.serializers.LocalDateConverter;
import gg.projecteden.mongodb.serializers.LocalDateTimeConverter;
import gg.projecteden.mongodb.serializers.LocalTimeConverter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.utils.Env;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static gg.projecteden.nexus.utils.GoogleUtils.ValueInputOption.USER_ENTERED;
import static gg.projecteden.utils.StringUtils.isNullOrEmpty;

/**
 * https://developers.google.com/sheets/api/quickstart/java
 */
public class GoogleUtils {

	private static final String CREDENTIALS_FILE_PATH = "google/credentials.json";
	private static final String TOKENS_DIRECTORY_PATH = "google/tokens";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);

	private static NetHttpTransport HTTP_TRANSPORT;
	private static LocalServerReceiver receiver;
	public static Credential USER;
	public static Sheets SHEETS;

	static {
		try {
			setup();
			login();
			sheets();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@SneakyThrows
	private static void setup() {
		HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
	}

	/**
	 * If you are prompted with a link in console, log in with the hi@projecteden.gg google credentials,
	 * complete the approval process, then change `localhost` to `projecteden.gg` in the redirect url.
	 * Make sure the port is open on the firewall
	 */
	@SneakyThrows
	private static void login() {
		GoogleClientSecrets secrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(),
			new InputStreamReader(new FileInputStream(Nexus.getFile(CREDENTIALS_FILE_PATH))));

		final int port = 8888 + (Env.values().length - (Nexus.getEnv().ordinal() + 1));
		receiver = new LocalServerReceiver.Builder().setPort(port).build();
		USER = new AuthorizationCodeInstalledApp(
			new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, secrets, SCOPES)
				.setDataStoreFactory(new FileDataStoreFactory(Nexus.getFolder(TOKENS_DIRECTORY_PATH)))
				.setAccessType("offline")
				.build(),
			receiver
		).authorize("user");
	}

	@SneakyThrows
	public static void shutdown() {
		receiver.stop();
	}

	private static void sheets() {
		SHEETS = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, USER)
			.setApplicationName(Nexus.class.getSimpleName())
			.build();
	}

	public static Spreadsheets spreadsheets() {
		return SHEETS.spreadsheets();
	}

	@SneakyThrows
	public static ValueRange sheetValues(String spreadsheetId, String sheetId, String range) {
		return spreadsheets().values().get(spreadsheetId, sheetId + (range == null ? "" : "!" + range)).execute();
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

	@SneakyThrows
	public static UpdateValuesResponse updateEntireSheet(String spreadsheetId, String sheetId, ValueRange values) {
		clearSheet(spreadsheetId, sheetId);
		return spreadsheets().values()
			.update(spreadsheetId, sheetId, values)
			.setValueInputOption(USER_ENTERED.name())
			.execute();
	}

	@SneakyThrows
	public static ClearValuesResponse clearSheet(String spreadsheetId, String sheetId) {
		return spreadsheets().values()
			.clear(spreadsheetId, sheetId, new ClearValuesRequest())
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
		return valueOf(String.join("\n", strings));
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


	@NotNull
	public static List<String> asStringArrayList(Iterator<Object> iterator) {
		return asStringArrayList(iterator, new ArrayList<>());
	}


	@NotNull
	public static List<String> asStringArrayList(Iterator<Object> iterator, List<String> defaultValue) {
		return iterator.hasNext() ? Arrays.asList(((String) iterator.next()).split("\n")) : defaultValue;
	}


	@NotNull
	public static Set<String> asStringLinkedHashSet(Iterator<Object> iterator) {
		return asStringLinkedHashSet(iterator, new LinkedHashSet<>());
	}

	@NotNull
	public static Set<String> asStringLinkedHashSet(Iterator<Object> iterator, Set<String> defaultValue) {
		return iterator.hasNext() ? new LinkedHashSet<>(Arrays.asList(((String) iterator.next()).split("\n"))) : defaultValue;
	}

	public static boolean asBoolean(Iterator<Object> iterator) {
		return asBoolean(iterator, false);
	}

	public static boolean asBoolean(Iterator<Object> iterator, boolean defaultValue) {
		return iterator.hasNext() ? Boolean.parseBoolean((String) iterator.next()) : defaultValue;
	}

	@Nullable
	public static LocalDateTime asLocalDateTime(Iterator<Object> iterator) {
		return asLocalDateTime(iterator, null);
	}

	@Nullable
	public static LocalDateTime asLocalDateTime(Iterator<Object> iterator, LocalDateTime defaultValue) {
		final String value = asString(iterator);
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
		return asLocalDate(asString(iterator), defaultValue);
	}

	@Nullable
	private static LocalDate asLocalDate(String value, LocalDate defaultValue) {
		if (isNullOrEmpty(value))
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
		return asLocalTime(asString(iterator), defaultValue);
	}

	@Nullable
	private static LocalTime asLocalTime(String value, LocalTime defaultValue) {
		if (isNullOrEmpty(value))
			return defaultValue;

		try {
			return new LocalTimeConverter().decode(value);
		} catch (DateTimeParseException ex) {
			Nexus.log("Could not parse " + value + " as time");
			return null;
		}
	}

}
