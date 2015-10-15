package com.tinapaproject.tinapa.events;

public class CreatePlannedPokemonEvent {
    private final String plannedId;
    private final String speciesId;
    private final String abilityId;
    private final String move1Id;
    private final String move2Id;
    private final String move3Id;
    private final String move4Id;
    private final String natureId;
    private final String itemId;
    private final String evHP;
    private final String evAtt;
    private final String evDef;
    private final String evSAtt;
    private final String evSDef;
    private final String evSpd;
    private final String ivHP;
    private final String ivAtt;
    private final String ivDef;
    private final String ivSAtt;
    private final String ivSDef;
    private final String ivSpd;
    private final String notes;
    private final boolean newSave;

    public CreatePlannedPokemonEvent(String plannedId, String speciesId, String abilityId, String move1Id, String move2Id, String move3Id, String move4Id, String natureId, String itemId, String evHP, String evAtt, String evDef, String evSAtt, String evSDef, String evSpd, String ivHP, String ivAtt, String ivDef, String ivSAtt, String ivSDef, String ivSpd, String notes, boolean newSave) {
        this.plannedId = plannedId;
        this.speciesId = speciesId;
        this.abilityId = abilityId;
        this.move1Id = move1Id;
        this.move2Id = move2Id;
        this.move3Id = move3Id;
        this.move4Id = move4Id;
        this.natureId = natureId;
        this.itemId = itemId;
        this.evHP = evHP;
        this.evAtt = evAtt;
        this.evDef = evDef;
        this.evSAtt = evSAtt;
        this.evSDef = evSDef;
        this.evSpd = evSpd;
        this.ivHP = ivHP;
        this.ivAtt = ivAtt;
        this.ivDef = ivDef;
        this.ivSAtt = ivSAtt;
        this.ivSDef = ivSDef;
        this.ivSpd = ivSpd;
        this.notes = notes;
        this.newSave = newSave;
    }

    public String getPlannedId() {
        return plannedId;
    }

    public String getEvDef() {
        return evDef;
    }

    public String getEvSDef() {
        return evSDef;
    }

    public String getEvSpd() {
        return evSpd;
    }

    public String getIvHP() {
        return ivHP;
    }

    public String getIvAtt() {
        return ivAtt;
    }

    public String getIvDef() {
        return ivDef;
    }

    public String getIvSAtt() {
        return ivSAtt;
    }

    public String getIvSDef() {
        return ivSDef;
    }

    public String getIvSpd() {
        return ivSpd;
    }

    public String getNotes() {
        return notes;
    }

    public String getSpeciesId() {

        return speciesId;
    }

    public String getAbilityId() {
        return abilityId;
    }

    public String getMove1Id() {
        return move1Id;
    }

    public String getMove2Id() {
        return move2Id;
    }

    public String getMove3Id() {
        return move3Id;
    }

    public String getMove4Id() {
        return move4Id;
    }

    public String getNatureId() {
        return natureId;
    }

    public String getItemId() {
        return itemId;
    }

    public String getEvHP() {
        return evHP;
    }

    public String getEvAtt() {
        return evAtt;
    }

    public String getEvSAtt() {
        return evSAtt;
    }

    public boolean isNewSave() {
        return newSave;
    }
}
