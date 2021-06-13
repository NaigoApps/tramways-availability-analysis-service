package it.tramways.analysis.availability;

import java.util.List;

public interface MessagesCollector {
	public boolean addMessage(String message);
	public List<String> listMessages();
}
