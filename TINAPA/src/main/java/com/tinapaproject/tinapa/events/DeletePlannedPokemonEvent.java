package com.tinapaproject.tinapa.events;

/**
 * Created by Ethan on 10/15/2015.
 */
public class DeletePlannedPokemonEvent {
    private final String plannedId;

    public DeletePlannedPokemonEvent(String plannedId) {
        this.plannedId = plannedId;
    }

    public String getPlannedId() {
        return plannedId;
    }
}
