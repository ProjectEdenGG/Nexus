package me.pugabyte.nexus.features.commands.info;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

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
		send(json("&3[+] &eMinigames").command("/minigames").hover("&3Join us on &eSaturdays&3, &e4&3-&e6 &ePM &eEST " +
				"\n&3for &eMinigame Night&3!\n" +
				"\n&eFor different timezones:" +
				"\n&3California: &eSat. 1-3 PM" +
				"\n&3London: &eSat. 9-11 PM" +
				"\n&3Sydney: &eSun. 6-8 AM" +
				"\n&eMake sure to join &c/discord &etoo!"));
		send(json("&3[+] &eDiscord").command("/discord").hover("&eA community skype-like program including " +
				"\n&etext chats and voice chats"));
		send(json("&3[+] &eWebsite").url("https://projecteden.gg/").hover("&eThe homepage for the server"));
		send(json("&3[+] &eVoting").command("/vote").hover("&eVote for our server to support us!"));
		line();
		runCommandAsConsole("curiosity cake " + name());

	}

}
