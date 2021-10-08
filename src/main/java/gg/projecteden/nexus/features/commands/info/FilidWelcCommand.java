package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;

public class FilidWelcCommand extends CustomCommand {

	public FilidWelcCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		line(2);
		send("&eGreetings &b" + name() + "&e!");
		send("&3Welcome to Project Eden. My name is Filid. Glad to meet you :)");
		send("&3Here are some of the most important aspects of our &ecommunity &3and what makes Project Eden unique.");
		line();
		send(json("&3[+] &eMinigames").command("/minigames").hover(
			"&3Join us on &eSaturdays&3, &e4&3-&e6 &ePM &eEST ",
			"&3for &eMinigame Night&3!",
			"",
			"&eFor different timezones:",
			"&3California: &eSat. 1-3 PM",
			"&3London: &eSat. 9-11 PM",
			"&3Sydney: &eSun. 6-8 AM",
			"&eMake sure to join &c/discord &etoo!"
		));
		send(json("&3[+] &eDiscord").command("/discord").hover(
			"&eA community skype-like program including",
			"&etext chats and voice chats"
		));
		send(json("&3[+] &eWebsite").url(EdenSocialMediaSite.WEBSITE.getUrl()).hover("&eThe homepage for the server"));
		send(json("&3[+] &eVoting").command("/vote").hover("&eVote for our server to support us!"));
		line();
		runCommandAsConsole("curiosity cake " + name());

	}

}
