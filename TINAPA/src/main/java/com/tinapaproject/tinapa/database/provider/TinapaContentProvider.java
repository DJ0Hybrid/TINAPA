package com.tinapaproject.tinapa.database.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.tinapaproject.tinapa.database.TinapaDatabaseHelper;
import com.tinapaproject.tinapa.database.key.DexKeyValues;
import com.tinapaproject.tinapa.database.key.ItemKeyValues;
import com.tinapaproject.tinapa.database.key.NatureKeyValues;
import com.tinapaproject.tinapa.database.key.OwnedKeyValues;
import com.tinapaproject.tinapa.database.key.PlannedKeyValues;

// http://www.techotopia.com/index.php/An_Android_Content_Provider_Tutorial
public class TinapaContentProvider extends ContentProvider {
    private TinapaDatabaseHelper dbHelper;

    public static final String TAG = "TinapaContentProvider";

    private static final String AUTHORITY =
            "com.tinapaproject.tinapa.database.provider.tinapacontentprovider";

    private static final String POKEDEX_TABLE = "pokedex";
    public static final Uri POKEDEX_URI = Uri.parse("content://" + AUTHORITY + "/" + POKEDEX_TABLE);
    public static final int POKEDEX = 100;  // Everything.
    public static final String POKEDEX_ALL_SHORT_TABLE = POKEDEX_TABLE + "/small";
    public static final Uri POKEDEX_ALL_SHORT_URI = Uri.parse("content://" + AUTHORITY + "/" + POKEDEX_ALL_SHORT_TABLE);
    public static final int POKEDEX_ALL_SHORT = 101; // Everything, but just the name, number, types, and small icon.
    public static final String POKEDEX_SEARCH_SPECIES_TABLE = POKEDEX_TABLE + "/search/species/*";
    public static final Uri POKEDEX_SEARCH_SPECIES_URI = Uri.parse("content://" + AUTHORITY + "/" + POKEDEX_SEARCH_SPECIES_TABLE);
    public static final int POKEDEX_SEARCH_SPECIES = 102;   // Pass species name.
    public static final String POKEDEX_POKEMON_MOVES_TABLE = POKEDEX_TABLE + "/pokemon/moves";
    public static final Uri POKEDEX_POKEMON_MOVES_URI = Uri.parse("content://" + AUTHORITY + "/" + POKEDEX_POKEMON_MOVES_TABLE);
    public static final int POKEDEX_POKEMON_MOVES = 103;    // Pass id.
    public static final String POKEDEX_POKEMON_ABILITIES_TABLE = POKEDEX_TABLE + "/pokemon/abilities";
    public static final Uri POKEDEX_POKEMON_ABILITIES_URI = Uri.parse("content://" + AUTHORITY + "/" + POKEDEX_POKEMON_ABILITIES_TABLE);
    public static final int POKEDEX_POKEMON_ABILITIES = 104;    // Pass id.
    public static final String POKEDEX_POKEMON_IMAGE_TABLE = POKEDEX_TABLE + "/pokemon/image";
    public static final Uri POKEDEX_POKEMON_IMAGE_URI = Uri.parse("content://" + AUTHORITY + "/" + POKEDEX_POKEMON_IMAGE_TABLE);
    public static final int POKEDEX_POKEMON_IMAGE = 105;
    public static final String POKEDEX_POKEMON_EVOLUTION_BASE_LINK_TABLE = POKEDEX_TABLE + "/pokemon/evolution/base";
    public static final Uri POKEDEX_POKEMON_EVOLUTION_BASE_LINK_URI = Uri.parse("content://" + AUTHORITY + "/" + POKEDEX_POKEMON_EVOLUTION_BASE_LINK_TABLE);
    public static final int POKEDEX_POKEMON_EVOLUTION_BASE_LINK = 106;
    public static final String POKEDEX_POKEMON_EVOLUTION_RESULT_LINK_TABLE = POKEDEX_TABLE + "/pokemon/evolution/result";
    public static final Uri POKEDEX_POKEMON_EVOLUTION_RESULT_LINK_URI = Uri.parse("content://" + AUTHORITY + "/" + POKEDEX_POKEMON_EVOLUTION_RESULT_LINK_TABLE);
    public static final int POKEDEX_POKEMON_EVOLUTION_RESULT_LINK = 107;

    private static final String PLANNED_POKEMON_TABLE = "plannedPokemon";
    public static final Uri PLANNED_POKEMON_URI = Uri.parse("content://" + AUTHORITY + "/" + PLANNED_POKEMON_TABLE);
    public static final int PLANNED_POKEMON = 200;  // Everything.
    public static final String PLANNED_POKEMON_SEARCH_GENERAL_TABLE = PLANNED_POKEMON_TABLE + "/search/general/*";
    public static final Uri PLANNED_POKEMON_SEARCH_GENERAL_URI = Uri.parse("content://" + AUTHORITY + "/" + PLANNED_POKEMON_SEARCH_GENERAL_TABLE);
    public static final int PLANNED_POKEMON_SEARCH_GENERAL = 201;   // Search for a planned Pokemon by name or species.

    private static final String OWNED_POKEMON_TABLE = "ownedPokemon";
    public static final Uri OWNED_POKEMON_URI = Uri.parse("content://" + AUTHORITY + "/" + OWNED_POKEMON_TABLE);
    public static final int OWNED_POKEMON = 300;    // Everything.
    public static final String OWNED_POKEMON_SEARCH_GENERAL_TABLE = OWNED_POKEMON_TABLE + "/search/general/*";
    public static final Uri OWNED_POKEMON_SEARCH_GENERAL_URI = Uri.parse("content://" + AUTHORITY + "/" + OWNED_POKEMON_SEARCH_GENERAL_TABLE);
    public static final int OWNED_POKEMON_SEARCH_GENERAL = 301; // Search for an owned Pokemon by name or species.

    private static final String PLANNED_TEAM_TABLE = "plannedTeam";
    public static final Uri PLANNED_TEAM_URI = Uri.parse("content://" + AUTHORITY + "/" + PLANNED_TEAM_TABLE);
    public static final int PLANNED_TEAM = 400; // Everything.

    private static final String NATURE_TABLE = "nature";
    public static final Uri NATURE_URI = Uri.parse("content://" + AUTHORITY + "/" + NATURE_TABLE);
    public static final int NATURE = 500;

    private static final String ITEM_TABLE = "item";
    public static final Uri ITEM_URI = Uri.parse("content://" + AUTHORITY + "/" + ITEM_TABLE);
    public static final int ITEM = 600;
    private static final String ITEM_BATTLE_TABLE = "itemBattle";
    public static final Uri ITEM_BATTLE_URI = Uri.parse("content://" + AUTHORITY + "/" + ITEM_BATTLE_TABLE);
    public static final int ITEM_BATTLE = 601;


    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // NOTE: "*" is any series of characters while "#" is any series of numbers.
        uriMatcher.addURI(AUTHORITY, POKEDEX_TABLE, POKEDEX);
        uriMatcher.addURI(AUTHORITY, POKEDEX_ALL_SHORT_TABLE, POKEDEX_ALL_SHORT);
        uriMatcher.addURI(AUTHORITY, POKEDEX_SEARCH_SPECIES_TABLE + "/search/species/*", POKEDEX_SEARCH_SPECIES);
        uriMatcher.addURI(AUTHORITY, POKEDEX_POKEMON_MOVES_TABLE, POKEDEX_POKEMON_MOVES);
        uriMatcher.addURI(AUTHORITY, POKEDEX_POKEMON_ABILITIES_TABLE, POKEDEX_POKEMON_ABILITIES);
        uriMatcher.addURI(AUTHORITY, POKEDEX_POKEMON_IMAGE_TABLE, POKEDEX_POKEMON_IMAGE);
        uriMatcher.addURI(AUTHORITY, POKEDEX_POKEMON_EVOLUTION_BASE_LINK_TABLE, POKEDEX_POKEMON_EVOLUTION_BASE_LINK);
        uriMatcher.addURI(AUTHORITY, POKEDEX_POKEMON_EVOLUTION_RESULT_LINK_TABLE, POKEDEX_POKEMON_EVOLUTION_RESULT_LINK);

        uriMatcher.addURI(AUTHORITY, PLANNED_POKEMON_TABLE, PLANNED_POKEMON);
        uriMatcher.addURI(AUTHORITY, PLANNED_POKEMON_SEARCH_GENERAL_TABLE, PLANNED_POKEMON_SEARCH_GENERAL);

        uriMatcher.addURI(AUTHORITY, OWNED_POKEMON_TABLE, OWNED_POKEMON);
        uriMatcher.addURI(AUTHORITY, OWNED_POKEMON_SEARCH_GENERAL_TABLE, OWNED_POKEMON_SEARCH_GENERAL);

        uriMatcher.addURI(AUTHORITY, PLANNED_TEAM_TABLE, PLANNED_TEAM);

        uriMatcher.addURI(AUTHORITY, NATURE_TABLE, NATURE);

        uriMatcher.addURI(AUTHORITY, ITEM_TABLE, ITEM);
        uriMatcher.addURI(AUTHORITY, ITEM_BATTLE_TABLE, ITEM_BATTLE);
        // TODO: Add more URIs to the matcher.
    }

    @Override
    public boolean onCreate() {
        dbHelper = new TinapaDatabaseHelper(getContext());
        return false;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (values == null) {
            throw new UnsupportedOperationException("ContentValues == null");
        }
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id;
        switch (uriType) {
            case OWNED_POKEMON:
                id = db.insertOrThrow("owned_pokemons", null, values);
                return Uri.parse(OWNED_POKEMON_TABLE + "/" + id);
            case PLANNED_POKEMON:
                id = db.insertOrThrow("planned_pokemons", null, values);
                return Uri.parse(PLANNED_POKEMON_TABLE + "/" + id);
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String[] selectionArray = null;
        String orderBy = null;
        String groupBy = null;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        int uriType = uriMatcher.match(uri);
        switch (uriType) {
            case POKEDEX:
                // TODO: This still needs more stuff.
                queryBuilder.setTables("pokemon LEFT OUTER JOIN pokemon_species_names ON (pokemon.species_id = pokemon_species_names.pokemon_species_id AND pokemon_species_names.local_language_id = 9) LEFT OUTER JOIN (SELECT name, pokemon_id FROM pokemon_types, type_names WHERE local_language_id = 9 AND pokemon_types.type_id = type_names.type_id AND slot = 1) AS type1 ON (pokemon.id = type1.pokemon_id) LEFT OUTER JOIN (SELECT name, pokemon_id FROM pokemon_types, type_names WHERE local_language_id = 9 AND pokemon_types.type_id = type_names.type_id AND slot = 2) AS type2 ON (pokemon.id = type2.pokemon_id) LEFT OUTER JOIN (SELECT * FROM pokemon_abilities, abilities, ability_names WHERE local_language_id = 9 AND pokemon_abilities.ability_id = abilities.id AND pokemon_abilities.ability_id = ability_names.ability_id AND slot = 1) AS ability1 ON (pokemon.id = ability1.pokemon_id) LEFT OUTER JOIN (SELECT * FROM pokemon_abilities, abilities, ability_names WHERE local_language_id = 9 AND pokemon_abilities.ability_id = abilities.id AND pokemon_abilities.ability_id = ability_names.ability_id AND slot = 2) AS ability2 ON (pokemon.id = ability2.pokemon_id) LEFT OUTER JOIN (SELECT * FROM pokemon_abilities, abilities, ability_names WHERE local_language_id = 9 AND pokemon_abilities.ability_id = abilities.id AND pokemon_abilities.ability_id = ability_names.ability_id AND slot = 3) AS ability3 ON (pokemon.id = ability3.pokemon_id) LEFT OUTER JOIN (SELECT pokemon_id, base_stat, name FROM stats, pokemon_stats, stat_names WHERE stat_names.local_language_id = 9 AND stats.id = stat_names.stat_id AND stat_names.stat_id = pokemon_stats.stat_id AND stats.identifier = \"hp\") AS hp ON (hp.pokemon_id = pokemon.id) LEFT OUTER JOIN (SELECT pokemon_id, base_stat, name FROM stats, pokemon_stats, stat_names WHERE stat_names.local_language_id = 9 AND stats.id = stat_names.stat_id AND stat_names.stat_id = pokemon_stats.stat_id AND stats.identifier = \"attack\") AS att ON (att.pokemon_id = pokemon.id) LEFT OUTER JOIN (SELECT pokemon_id, base_stat, name FROM stats, pokemon_stats, stat_names WHERE stat_names.local_language_id = 9 AND stats.id = stat_names.stat_id AND stat_names.stat_id = pokemon_stats.stat_id AND stats.identifier = \"defense\") AS def ON (def.pokemon_id = pokemon.id) LEFT OUTER JOIN (SELECT pokemon_id, base_stat, name FROM stats, pokemon_stats, stat_names WHERE stat_names.local_language_id = 9 AND stats.id = stat_names.stat_id AND stat_names.stat_id = pokemon_stats.stat_id AND stats.identifier = \"special-attack\") AS satt ON (satt.pokemon_id = pokemon.id) LEFT OUTER JOIN (SELECT pokemon_id, base_stat, name FROM stats, pokemon_stats, stat_names WHERE stat_names.local_language_id = 9 AND stats.id = stat_names.stat_id AND stat_names.stat_id = pokemon_stats.stat_id AND stats.identifier = \"special-defense\") AS sdef ON (sdef.pokemon_id = pokemon.id) LEFT OUTER JOIN (SELECT pokemon_id, base_stat, name FROM stats, pokemon_stats, stat_names WHERE stat_names.local_language_id = 9 AND stats.id = stat_names.stat_id AND stat_names.stat_id = pokemon_stats.stat_id AND stats.identifier = \"speed\") AS spd ON (spd.pokemon_id = pokemon.id)");
                if (selection != null && !selection.isEmpty()) {
                    queryBuilder.appendWhere(selection);
                }
                selectionArray = new String[]{"pokemon.id AS _id", "pokemon_species_names.name AS " + DexKeyValues.name, "pokemon.species_id AS " + DexKeyValues.number, "type1.name AS " + DexKeyValues.type1, "type2.name AS " + DexKeyValues.type2, "ability1.name AS " + DexKeyValues.ability1Name, "ability1.id AS " + DexKeyValues.ability1ID, "ability2.name AS " + DexKeyValues.ability2Name, "ability2.id AS " + DexKeyValues.ability2ID, "ability3.name AS " + DexKeyValues.ability3Name, "ability3.id AS " + DexKeyValues.ability3ID, "hp.base_stat AS " + DexKeyValues.baseHP, "att.base_stat AS " + DexKeyValues.baseAttack, "def.base_stat AS " + DexKeyValues.baseDefense, "satt.base_stat AS " + DexKeyValues.baseSpecialAttack, "sdef.base_stat AS " + DexKeyValues.baseSpecialDefense, "spd.base_stat AS " + DexKeyValues.baseSpeed, "image AS " + DexKeyValues.image, "shinny_image AS " + DexKeyValues.shinnyImage, "icon_image AS " + DexKeyValues.iconImage};
                break;
            case POKEDEX_ALL_SHORT:
                queryBuilder.setTables("pokemon LEFT OUTER JOIN pokemon_species_names ON (pokemon.species_id = pokemon_species_names.pokemon_species_id AND pokemon_species_names.local_language_id = 9) LEFT OUTER JOIN (SELECT name, pokemon_id FROM pokemon_types, type_names WHERE local_language_id = 9 AND pokemon_types.type_id = type_names.type_id AND slot = 1) AS type1 ON (pokemon.id = type1.pokemon_id) LEFT OUTER JOIN (SELECT name, pokemon_id FROM pokemon_types, type_names WHERE local_language_id = 9 AND pokemon_types.type_id = type_names.type_id AND slot = 2) AS type2 ON (pokemon.id = type2.pokemon_id)");
                if (!TextUtils.isEmpty(selection)) {
                    queryBuilder.appendWhere("pokemon_species_names.name LIKE '%" + selection + "%'");
                }
                selectionArray = new String[]{"pokemon.id AS _id", "pokemon_species_names.name AS " + DexKeyValues.name, "pokemon.species_id AS " + DexKeyValues.number, "type1.name AS " + DexKeyValues.type1, "type2.name AS " + DexKeyValues.type2, "icon_image AS " + DexKeyValues.iconImage};
                break;
            case POKEDEX_SEARCH_SPECIES:

                break;
            case POKEDEX_POKEMON_MOVES:
                queryBuilder.setTables("moves LEFT OUTER JOIN pokemon_moves ON (moves.id = pokemon_moves.move_id) LEFT OUTER JOIN pokemon_move_methods ON (pokemon_move_methods.id = pokemon_moves.pokemon_move_method_id) LEFT OUTER JOIN move_names ON (moves.id = move_names.move_id AND move_names.local_language_id = 9) LEFT OUTER JOIN move_flavor_text ON (moves.id = move_flavor_text.move_id AND move_flavor_text.language_id = 9 AND move_flavor_text.version_group_id = 15)");
                if (selection != null && !selection.isEmpty()) {
                    queryBuilder.appendWhere(selection);
                }
                orderBy = "level ASC, \"order\" ASC";
                groupBy = "moves.id, pokemon_move_methods.id, pokemon_moves.level";
                selectionArray = new String[]{"moves.id AS _id", "name", "flavor_text", "pokemon_move_methods.identifier AS identifier"};
                break;
            case POKEDEX_POKEMON_ABILITIES:
                queryBuilder.setTables("pokemon_abilities, abilities, ability_names");
                queryBuilder.appendWhere("local_language_id = 9 AND pokemon_abilities.ability_id = abilities.id AND pokemon_abilities.ability_id = ability_names.ability_id");
                if (!TextUtils.isEmpty(selection)) {
                    queryBuilder.appendWhere(" AND " + selection);
                }
                selectionArray = new String[]{"id AS _id", "name AS name", "pokemon_id AS pokemon_id", "slot AS slot", "is_hidden AS is_hidden"};
                orderBy = "slot ASC";
                break;
            case POKEDEX_POKEMON_EVOLUTION_BASE_LINK:
                // TODO Selection array.
                queryBuilder.setTables("pokemon_evolution JOIN pokemon_species AS evolved_species ON pokemon_evolution.evolved_species_id = evolved_species.id JOIN pokemon_species AS base_species ON base_species.id = evolved_species.evolves_from_species_id JOIN evolution_trigger_prose ON (pokemon_evolution.evolution_trigger_id = evolution_trigger_prose.evolution_trigger_id AND evolution_trigger_prose.local_language_id = 9)");
                if (!TextUtils.isEmpty(selection)) {
                    queryBuilder.appendWhere("base_species.id = " + selection);
                }
                break;
            case POKEDEX_POKEMON_EVOLUTION_RESULT_LINK:
                // TODO Selection array.
                queryBuilder.setTables("pokemon_evolution JOIN pokemon_species AS evolved_species ON pokemon_evolution.evolved_species_id = evolved_species.id JOIN pokemon_species AS base_species ON base_species.id = evolved_species.evolves_from_species_id JOIN evolution_trigger_prose ON (pokemon_evolution.evolution_trigger_id = evolution_trigger_prose.evolution_trigger_id AND evolution_trigger_prose.local_language_id = 9)");
                if (!TextUtils.isEmpty(selection)) {
                    queryBuilder.appendWhere("evolved_species.id = " + selection);
                }
                break;
            case PLANNED_POKEMON:

                break;
            case PLANNED_POKEMON_SEARCH_GENERAL:
                queryBuilder.setTables("planned_pokemons LEFT OUTER JOIN pokemon ON (planned_pokemons.pokemon_id = pokemon.id) LEFT OUTER JOIN pokemon_species_names ON (planned_pokemons.pokemon_id = pokemon_species_names.pokemon_species_id AND pokemon_species_names.local_language_id = 9)");

                if (!TextUtils.isEmpty(selection)) {
                    queryBuilder.appendWhere("pokemon_species_names LIKE %'" + selection + "'%");
                }
                selectionArray = new String[]{"planned_pokemons.id AS _id", "pokemon_species_names.name AS " + PlannedKeyValues.NAME, "pokemon.icon_image AS " + PlannedKeyValues.ICON_IMAGE};
                break;
            case OWNED_POKEMON:
                queryBuilder.setTables("owned_pokemons LEFT OUTER JOIN pokemon ON (pokemon.species_id = owned_pokemons.pokemon_id) LEFT OUTER JOIN pokemon_species_names ON (pokemon.species_id = pokemon_species_names.pokemon_species_id AND pokemon_species_names.local_language_id = 9) LEFT OUTER JOIN (SELECT name, pokemon_id FROM pokemon_types, type_names WHERE local_language_id = 9 AND pokemon_types.type_id = type_names.type_id AND slot = 1) AS type1 ON (pokemon.id = type1.pokemon_id) LEFT OUTER JOIN (SELECT name, pokemon_id FROM pokemon_types, type_names WHERE local_language_id = 9 AND pokemon_types.type_id = type_names.type_id AND slot = 2) AS type2 ON (pokemon.id = type2.pokemon_id) LEFT OUTER JOIN (SELECT ability_names.ability_id AS id, name, effect FROM ability_names, ability_prose WHERE ability_names.ability_id = ability_prose.ability_id AND ability_names.local_language_id = 9 AND ability_prose.local_language_id = 9) AS ability ON (owned_pokemons.ability_id = ability.id) LEFT OUTER JOIN nature_names ON (owned_pokemons.nature_id = nature_names.nature_id AND nature_names.local_language_id = 9) LEFT OUTER JOIN genders ON (genders.id = owned_pokemons.gender_id) LEFT OUTER JOIN move_names AS move1 ON (move1.move_id = owned_pokemons.move1_id AND move1.local_language_id = 9) LEFT OUTER JOIN move_names AS move2 ON (move2.move_id = owned_pokemons.move2_id AND move2.local_language_id = 9) LEFT OUTER JOIN move_names AS move3 ON (move3.move_id = owned_pokemons.move3_id AND move3.local_language_id = 9) LEFT OUTER JOIN move_names AS move4 ON (move4.move_id = owned_pokemons.move4_id AND move4.local_language_id = 9)");
                if (selection != null && !selection.isEmpty()) {
                    queryBuilder.appendWhere(selection);
                }
                // TODO Select array
                selectionArray = new String[]{"owned_pokemons.id AS _id", "owned_pokemons.nickname AS nickname", "pokemon_species_names.name AS name", "pokemon.species_id AS " + OwnedKeyValues.POKEMON_ID, "owned_pokemons.ability_id AS " + OwnedKeyValues.ABILITY_ID, "icon_image", "image", "shinny_image", "iv_hp AS " + OwnedKeyValues.IV_HP, "iv_att AS " + OwnedKeyValues.IV_ATT, "iv_def AS " + OwnedKeyValues.IV_DEF, "iv_satt AS " + OwnedKeyValues.IV_SATT, "iv_sdef AS " + OwnedKeyValues.IV_SDEF, "iv_spd AS " + OwnedKeyValues.IV_SPD, "ev_hp AS " + OwnedKeyValues.EV_HP, "ev_att AS " + OwnedKeyValues.EV_ATT, "ev_def AS " + OwnedKeyValues.EV_DEF, "ev_satt AS " + OwnedKeyValues.EV_SATT, "ev_sdef AS " + OwnedKeyValues.EV_SDEF, "ev_spd AS " + OwnedKeyValues.EV_SPD, "level AS " + OwnedKeyValues.LEVEL, "shinny AS " + OwnedKeyValues.SHINNY, "note AS " + OwnedKeyValues.NOTE, "nature_names.name AS nature_name", "nature_names.nature_id AS " + OwnedKeyValues.NATURE_ID, "owned_pokemons.move1_id AS " + OwnedKeyValues.MOVE1_ID, "owned_pokemons.move2_id AS " + OwnedKeyValues.MOVE2_ID, "owned_pokemons.move3_id AS " + OwnedKeyValues.MOVE3_ID, "owned_pokemons.move4_id AS " + OwnedKeyValues.MOVE4_ID};
                break;
            case OWNED_POKEMON_SEARCH_GENERAL:
                queryBuilder.setTables("owned_pokemons LEFT OUTER JOIN pokemon ON (pokemon.species_id = owned_pokemons.pokemon_id) LEFT OUTER JOIN pokemon_species_names ON (pokemon.species_id = pokemon_species_names.pokemon_species_id AND pokemon_species_names.local_language_id = 9) LEFT OUTER JOIN (SELECT name, pokemon_id FROM pokemon_types, type_names WHERE local_language_id = 9 AND pokemon_types.type_id = type_names.type_id AND slot = 1) AS type1 ON (pokemon.id = type1.pokemon_id) LEFT OUTER JOIN (SELECT name, pokemon_id FROM pokemon_types, type_names WHERE local_language_id = 9 AND pokemon_types.type_id = type_names.type_id AND slot = 2) AS type2 ON (pokemon.id = type2.pokemon_id) LEFT OUTER JOIN (SELECT ability_names.ability_id AS id, name, effect FROM ability_names, ability_prose WHERE ability_names.ability_id = ability_prose.ability_id AND ability_names.local_language_id = 9 AND ability_prose.local_language_id = 9) AS ability ON (owned_pokemons.ability_id = ability.id) LEFT OUTER JOIN nature_names ON (owned_pokemons.nature_id = nature_names.nature_id AND nature_names.local_language_id = 9) LEFT OUTER JOIN genders ON (genders.id = owned_pokemons.gender_id) LEFT OUTER JOIN move_names AS move1 ON (move1.move_id = owned_pokemons.move1_id AND move1.local_language_id = 9) LEFT OUTER JOIN move_names AS move2 ON (move2.move_id = owned_pokemons.move2_id AND move2.local_language_id = 9) LEFT OUTER JOIN move_names AS move3 ON (move3.move_id = owned_pokemons.move3_id AND move3.local_language_id = 9) LEFT OUTER JOIN move_names AS move4 ON (move4.move_id = owned_pokemons.move4_id AND move4.local_language_id = 9)");

                // TODO Select array
                selectionArray = new String[]{"owned_pokemons.id AS _id", "owned_pokemons.nickname AS " + OwnedKeyValues.NICKNAME, "pokemon_species_names.name AS name", "pokemon.species_id AS " + OwnedKeyValues.POKEMON_ID, "icon_image AS image"};
                break;
            case PLANNED_TEAM:

                break;
            case NATURE:
                queryBuilder.setTables("nature_names LEFT OUTER JOIN natures ON (nature_names.nature_id = natures.id) LEFT OUTER JOIN stat_names AS increased_stat ON (natures.increased_stat_id = increased_stat.stat_id AND increased_stat.local_language_id = 9) LEFT OUTER JOIN stat_names AS decreased_stat ON (natures.decreased_stat_id = decreased_stat.stat_id AND decreased_stat.local_language_id = 9)");

                selectionArray = new String[]{"nature_names.nature_id AS _id", "nature_names.name AS " + NatureKeyValues.NATURE_NAME, "nature_names.nature_id AS " + NatureKeyValues.NATURE_ID, "increased_stat.name AS " + NatureKeyValues.INCREASED_STAT_NAME, "decreased_stat.name AS " + NatureKeyValues.DECREASED_STAT_NAME};

                queryBuilder.appendWhere("nature_names.local_language_id = 9");
                break;
            case ITEM:

                break;
            case ITEM_BATTLE:
                queryBuilder.setTables("items\n" +
                        "LEFT OUTER JOIN item_names ON (items.id = item_names.item_id AND item_names.local_language_id = 9)\n" +
                        "LEFT OUTER JOIN item_prose ON (items.id = item_prose.item_id AND item_prose.local_language_id = 9)");

                selectionArray = new String[]{"items.id AS _id", "item_names.name AS " + ItemKeyValues.name, "item_prose.short_effect AS " + ItemKeyValues.shortProse};

                orderBy = "items.category_id ASC";

                queryBuilder.appendWhere("items.category_id = 3 OR items.category_id = 4 OR items.category_id = 5 OR items.category_id = 6 OR items.category_id = 12 OR items.category_id = 13 OR items.category_id = 14 OR items.category_id = 15 OR items.category_id = 17 OR items.category_id = 18 OR items.category_id = 19 OR items.category_id = 36 OR items.category_id = 42 OR items.category_id = 44");
                break;
            default:
                throw new UnsupportedOperationException("Unsupported URI.");
        }
        Cursor cursor = queryBuilder.query(db, selectionArray, null, null, groupBy, null, orderBy);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String table;
        int rowsUpdated = 0;
        int uriType = uriMatcher.match(uri);
        switch (uriType) {
            case POKEDEX_POKEMON_IMAGE:
                if (TextUtils.isEmpty(selection)) {
                    throw new UnsupportedOperationException("Selection cannot be empty!");
                }
                table = "pokemon";
                rowsUpdated = db.update(table, values, selection, null);
                if (rowsUpdated != 1) {
                    Log.w(TAG, "There was " + rowsUpdated + " rows updated, which is not 1!");
                }
                break;
            case OWNED_POKEMON:
                if (TextUtils.isEmpty(selection)) {
                    throw new UnsupportedOperationException("Selection cannot be empty!");
                }
                table = "owned_pokemons";
                rowsUpdated = db.update(table, values, selection, null);
                if (rowsUpdated != 1) {
                    Log.w(TAG, "There was " + rowsUpdated + " rows updated, which is not 1!");
                }
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        return rowsUpdated;
    }
}
