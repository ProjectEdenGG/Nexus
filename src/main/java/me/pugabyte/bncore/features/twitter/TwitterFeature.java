package me.pugabyte.bncore.features.twitter;

import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.annotations.Environments;
import me.pugabyte.bncore.framework.features.Feature;
import me.pugabyte.bncore.utils.Env;
import org.bukkit.configuration.file.FileConfiguration;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@Environments(Env.PROD)
public class TwitterFeature extends Feature {

	@Getter
	private static Twitter twitter;

	@Override
	public void startup() {
		FileConfiguration config = BNCore.getInstance().getConfig();
		ConfigurationBuilder twitterConfig = new ConfigurationBuilder();

		twitterConfig.setDebugEnabled(true)
				.setOAuthConsumerKey(config.getString("tokens.twitter.consumerKey"))
				.setOAuthConsumerSecret(config.getString("tokens.twitter.consumerSecret"))
				.setOAuthAccessToken(config.getString("tokens.twitter.accessToken"))
				.setOAuthAccessTokenSecret(config.getString("tokens.twitter.accessTokenSecret"));

		twitter = new TwitterFactory(twitterConfig.build()).getInstance();
	}

	public static String getUrl(Status status) {
		return "https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId();
	}

}
