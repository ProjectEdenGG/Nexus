package me.pugabyte.bncore.features.chat.models;

import java.util.Set;

public interface Channel {

	Set<Chatter> getRecipients(Chatter chatter);

	String getAssignMessage(Chatter chatter);

}
