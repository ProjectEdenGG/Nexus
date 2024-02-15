package gg.projecteden.nexus.features.titan;

import gg.projecteden.nexus.features.titan.models.Clientbound;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ClientMessage {

	@Getter
	private List<Player> players;
	@Getter
	private Clientbound message;
	private boolean sent;

	private ClientMessage() { }

	public static ClientMessage builder() {
		return new ClientMessage();
	}

	public ClientMessage message(Clientbound message) {
		this.message = message;
		return this;
	}

	public ClientMessage players(List<Player> players) {
		this.players = new ArrayList<>(players);
		return this;
	}

	public ClientMessage players(Player player) {
		this.players = new ArrayList<>(Collections.singletonList(player));
		return this;
	}

	public ClientMessage players(Player... players) {
		this.players = new ArrayList<>(Arrays.asList(players));
		return this;
	}

	public void send() {
		if (sent)
			throw new UnsupportedOperationException("Message already sent");

		this.sent = true;
		ServerClientMessaging.toSend.add(this);
	}

}
