package sproject.tinify;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

public class MainApp extends Application {

	private static Stage stage;
	private static final String clientId = "";
	private static final String clientSecret = "";
	private static final URI redirectUri = SpotifyHttpManager.makeUri("https://example.com/spotify-redirect");

	static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
			.setClientId(clientId)
			.setClientSecret(clientSecret)
			.setRedirectUri(redirectUri)
			.build();

	private static final AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
			.scope("user-read-currently-playing")
			.show_dialog(true)
			.build();

	public static void authorizationCodeUri_Sync() {
		try {
			final URI uri = authorizationCodeUriRequest.execute();
			Desktop d = Desktop.getDesktop();
			d.browse(uri);

			System.out.println("URI: " + uri.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start(@SuppressWarnings("exports") Stage s) throws IOException {
		stage = s;
		setRoot("primary", "");
		authorizationCodeUri_Sync();
	}

	static void setRoot(String fxml) throws IOException {
		setRoot(fxml, stage.getTitle());
	}

	static void setRoot(String fxml, String title) throws IOException {
		Scene scene = new Scene(loadFXML(fxml));
		stage.setTitle(title);
		stage.setScene(scene);
		stage.show();
	}

	private static Parent loadFXML(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/fxml/" + fxml + ".fxml"));
		return fxmlLoader.load();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
