package it.tramways.analysis.availability;

import java.util.ArrayList;
import java.util.List;

public class DefaultMessagesCollector implements MessagesCollector {

	private List<String> messages;

	public DefaultMessagesCollector() {
		messages = new ArrayList<>();
	}

	@Override
	public boolean addMessage(String message) {
		messages.add(message);
		return false;
	}

	@Override
	public List<String> listMessages() {
		return new ArrayList<>(messages);
	}

}
