package it.tramways.analysis.roadmap;

import it.tramways.analysis.AbstractConfigurable;

public class LaneSegment extends AbstractConfigurable implements Arc<NetworkPoint> {

	private NetworkPoint source;
	private NetworkPoint destination;

	@Override
	public NetworkPoint getSource() {
		return source;
	}

	public void setSource(NetworkPoint source) {
		this.source = source;
	}

	@Override
	public NetworkPoint getDestination() {
		return destination;
	}

	public void setDestination(NetworkPoint destination) {
		this.destination = destination;
	}

}
