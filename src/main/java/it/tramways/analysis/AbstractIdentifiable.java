package it.tramways.analysis;

import it.tramways.core.Identifiable;
import java.util.UUID;

public abstract class AbstractIdentifiable implements Identifiable {

	private String uuid;

	public AbstractIdentifiable() {
		uuid = UUID.randomUUID().toString();
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
