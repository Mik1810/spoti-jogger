package sproject.tinify;
/*
Put header here


 */

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.hc.core5.http.ParseException;

import com.neovisionaries.i18n.CountryCode;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistRequest;
import se.michaelthelin.spotify.requests.data.player.GetUsersCurrentlyPlayingTrackRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

public class FXMLController implements Initializable {
	
	@FXML
	private TextField codeTextField;
	
	@FXML
	private Button dataButton;

	@FXML
	private Label lblOut;
	private static String code;
	private static final String artistId = "7FeObngbQ0GY3SojNwKdKn"; // Bresh artist
	
	private static AuthorizationCodeRequest authorizationCodeRequest = null;
	
	 private static GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = null;
	 
	 private static GetUsersCurrentlyPlayingTrackRequest getUsersCurrentlyPlayingTrackRequest = null;

	@FXML
	private void btnClickAction(ActionEvent event) {
		code = codeTextField.getText();
		
		authorizationCodeRequest = MainApp.spotifyApi
				.authorizationCode(code)
			    .build();
		
		authorizationCode_Sync();
	}
	
	

		  public static void authorizationCode_Sync() {
		    try {
		    	
		      final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
		      System.out.println(code);
		      System.out.println("Token: "+authorizationCodeCredentials.getAccessToken());
		      // Set access and refresh token for further "spotifyApi" object usage
		      MainApp.spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
		      MainApp.spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

		      System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
		      
		      getUsersCurrentlyPlayingTrackRequest = MainApp.spotifyApi.getUsersCurrentlyPlayingTrack()
		    		  .market(CountryCode.IT)
		    		  .additionalTypes("track,episode")
		    		  .build();
		      CurrentlyPlaying currentlyPlaying = getUsersCurrentlyPlayingTrackRequest.execute();
		      
		      System.out.println(currentlyPlaying.getItem());
		    } catch (IOException | SpotifyWebApiException | ParseException e) {
		      System.out.println("Error: " + e.getMessage());
		    }
		  }

	@Override
	public void initialize(URL url, ResourceBundle rb) {

	}
	
	@FXML
	private void getDataAction(ActionEvent event) {
		try {
			
			getCurrentUsersProfileRequest = MainApp.spotifyApi.getCurrentUsersProfile()
		    .build();
			final User user =  getCurrentUsersProfileRequest.execute();

		    System.out.println("Display name: " + user.getDisplayName());
		    System.out.println(user.getEmail());
		} catch (IOException | SpotifyWebApiException | ParseException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
}
