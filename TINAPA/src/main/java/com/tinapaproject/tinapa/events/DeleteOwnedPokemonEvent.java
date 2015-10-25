package com.tinapaproject.tinapa.events;

/**
 * Created by Ethan on 10/15/2015.
 */
public class DeleteOwnedPokemonEvent {
    private final String ownedId;

    public DeleteOwnedPokemonEvent(String ownedId) {
        this.ownedId = ownedId;
    }

    public String getOwnedId() {
        return ownedId;
    }
}
