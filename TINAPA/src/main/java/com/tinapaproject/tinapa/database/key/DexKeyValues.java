package com.tinapaproject.tinapa.database.key;

public class DexKeyValues {
    public static final String name = "name";
    public static final String number = "number";
    public static final String type1 = "type1";
    public static final String type2 = "type2";
    public static final String ability1Name = "ability1_name";
    public static final String ability1ID = "ability1_id";
    public static final String ability2Name = "ability2_name";
    public static final String ability2ID = "ability2_id";
    public static final String ability3Name = "ability3_name";
    public static final String ability3ID = "ability3_id";
    public static final String baseHP = "base_hp";
    public static final String baseAttack = "base_att";
    public static final String baseDefense = "base_def";
    public static final String baseSpecialAttack = "base_satt";
    public static final String baseSpecialDefense = "base_sdef";
    public static final String baseSpeed = "base_spd";
    public static final String image = "image";
    public static final String shinnyImage = "shinny_image";
    public static final String iconImage = "icon_image";

    public static String insertIntoImageColumnWhereCreation(String id) {
        return String.format("pokemon.id = %s", id);
    }
}
