package com.tinapaproject.tinapa.events;

public class TeamListSelectedEvent {
    private final String teamId;

    public TeamListSelectedEvent(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamId() {
        return teamId;
    }
}
