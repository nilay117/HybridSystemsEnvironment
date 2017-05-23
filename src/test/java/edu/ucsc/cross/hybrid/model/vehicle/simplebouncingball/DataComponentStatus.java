package edu.ucsc.cross.hybrid.model.vehicle.simplebouncingball;

public enum DataComponentStatus
{
	OFF,
	IDLE,
	READING,
	WRITING,
	SEQUENTIAL_READING,
	SEQUENTIAL_WRITING,
	RANDOM_READING,
	RANDOM_WRITING,
	NEW_DATA_READY,
	ERASING,
	NEW_DATA;
}
