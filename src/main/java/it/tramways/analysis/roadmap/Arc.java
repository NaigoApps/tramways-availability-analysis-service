package it.tramways.analysis.roadmap;

public interface Arc<T extends Node> {
	T getSource();
	T getDestination();
}
