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
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.utils.Env;
import lombok.SneakyThrows;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

// https://developers.google.com/sheets/api/quickstart/java
public class GoogleUtils {

	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "google/tokens";
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);

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

	@SneakyThrows
	private static void login() {
		GoogleClientSecrets secrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(),
			new InputStreamReader(new FileInputStream(Nexus.getFile("google/credentials.json"))));

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

	@SneakyThrows
	public static ValueRange sheet(String spreadsheetId, String sheetId, String range) {
		return SHEETS.spreadsheets().values().get(spreadsheetId, sheetId + "!" + range).execute();
	}

}
