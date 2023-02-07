package sproject.tinify;
/*
Put header here


 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import org.apache.hc.core5.http.ParseException;

import com.neovisionaries.i18n.CountryCode;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.player.GetUsersCurrentlyPlayingTrackRequest;

public class FXMLController implements Initializable {

	@FXML
	private TextField codeTextField;

	@FXML
	private TextField cliendTextField;

	@FXML
	private TextField secretTextField;

	@FXML
	private Button codeButton;

	@FXML
	private Button tokenButton;

	private static String clientId = "";
	private static String clientSecret = "";
	private static String code;

	private static final URI redirectUri = SpotifyHttpManager.makeUri("https://example.com/spotify-redirect");

	static SpotifyApi spotifyApi = new SpotifyApi.Builder()
			.setClientId(clientId)
			.setClientSecret(clientSecret)
			.setRedirectUri(redirectUri)
			.build();

	private static final AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi
			.authorizationCodeUri()
			.scope("user-read-currently-playing, user-read-playback-state")
			.show_dialog(true)
			.build();

	private static AuthorizationCodeRequest authorizationCodeRequest = null;
	private static GetUsersCurrentlyPlayingTrackRequest getUsersCurrentlyPlayingTrackRequest = null;
	private static AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = null;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		final URI uri = authorizationCodeUriRequest.execute();
		System.out.println(uri.toString());
	}

	@FXML
	private void setCode(ActionEvent event) {
		code = codeTextField.getText();
		authorizationCodeRequest = spotifyApi.authorizationCode(code).build();
		setToken();
		start();
	}

	public void setToken() {
		try {
			AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
			System.out.println("Access Token: " + authorizationCodeCredentials.getAccessToken());

			// Set access and refresh token for further "spotifyApi" object usage
			spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
			spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
			System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			e.printStackTrace();
		}
	}

	public static void start() {
		try {
			// Request for the current song playing
			getUsersCurrentlyPlayingTrackRequest = spotifyApi
					.getUsersCurrentlyPlayingTrack()
					.market(CountryCode.IT)
					.additionalTypes("track,episode")
					.build();

			writeDownSong("Token: " + spotifyApi.getAccessToken());

			CurrentlyPlaying currentlyPlaying = getUsersCurrentlyPlayingTrackRequest.execute();
			String song = getSongString((Track) currentlyPlaying.getItem());
			String song2 = null;
			writeDownSong(song);

			while (currentlyPlaying.getIs_playing() == true) {
				try {
					Thread.sleep(1000);
					song2 = getSongString((Track) getUsersCurrentlyPlayingTrackRequest.execute().getItem());

					if (!song2.equals(song)) {
						System.out.println(song2);
						song = song2;
						writeDownSong(song);

					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SpotifyWebApiException e) {
			e.printStackTrace();
		}
	}

	private static String getSongString(Track track) {
		String p = track.getName() + " - ";
		for (ArtistSimplified artist : track.getArtists())
			p += " " + artist.getName();

		return p;
	}

	private static void writeDownSong(String song) {
		try (FileWriter f = new FileWriter("src/main/resources/songs.txt", true);
			BufferedWriter b = new BufferedWriter(f);
			PrintWriter p = new PrintWriter(b);) {
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			p.println("[" + formatter.format(LocalDateTime.now()) + "] " + song);

		} catch (IOException i) {
			i.printStackTrace();
		}
	}

	@FXML
	private void refreshToken(ActionEvent event) {
		try {
			authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh().build();
			final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();

			// Set access and refresh token for further "spotifyApi" object usage
			spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());

			System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
			start();
		} catch (IOException | SpotifyWebApiException | ParseException e) {
			System.out.println("Error: " + e.getMessage());
		}

	}
}
