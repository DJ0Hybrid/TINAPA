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
import com.tinapaproject.tinapa.database.key.EvolutionKeyValues;
import com.tinapaproject.tinapa.database.key.ItemKeyValues;
import com.tinapaproject.tinapa.database.key.NatureKeyValues;
import com.tinapaproject.tinapa.database.key.OwnedKeyValues;
import com.tinapaproject.tinapa.database.key.PlannedKeyValues;
import com.tinapaproject.tinapa.database.key.TeamKeyValues;

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
    public static final String POKEDEX_POKEMON_EVOLUTION_TABLE = POKEDEX_TABLE + "/pokemon/evolution";
    public static final Uri POKEDEX_POKEMON_EVOLUTION_URI = Uri.parse("content://" + AUTHORITY + "/" + POKEDEX_POKEMON_EVOLUTION_TABLE);
    public static final int POKEDEX_POKEMON_EVOLUTION = 108;

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
    private static final String PLANNED_TEAM_LIST_TABLE = PLANNED_TEAM_TABLE + "/list";
    public static final Uri PLANNED_TEAM_LIST_URI = Uri.parse("content://" + AUTHORITY + "/" + PLANNED_TEAM_LIST_TABLE);
    public static final int PLANNED_TEAM_LIST = 401; // For a list, giving basic details about of the Pokemon.

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
        uriMatcher.addURI(AUTHORITY, POKEDEX_POKEMON_EVOLUTION_TABLE, POKEDEX_POKEMON_EVOLUTION);

        uriMatcher.addURI(AUTHORITY, PLANNED_POKEMON_TABLE, PLANNED_POKEMON);
        uriMatcher.addURI(AUTHORITY, PLANNED_POKEMON_SEARCH_GENERAL_TABLE, PLANNED_POKEMON_SEARCH_GENERAL);

        uriMatcher.addURI(AUTHORITY, OWNED_POKEMON_TABLE, OWNED_POKEMON);
        uriMatcher.addURI(AUTHORITY, OWNED_POKEMON_SEARCH_GENERAL_TABLE, OWNED_POKEMON_SEARCH_GENERAL);

        uriMatcher.addURI(AUTHORITY, PLANNED_TEAM_TABLE, PLANNED_TEAM);
        uriMatcher.addURI(AUTHORITY, PLANNED_TEAM_LIST_TABLE, PLANNED_TEAM_LIST);

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
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsUpdated = 0;
        String table;
        switch (uriType) {
            case OWNED_POKEMON:
                if (selection == null) {
                    throw new UnsupportedOperationException("Selection cannot be null!");
                }
                table = "owned_pokemons";
                rowsUpdated = db.delete(table, "id == " + selection, null);
                if (rowsUpdated != 1) {
                    Log.w(TAG, "There was " + rowsUpdated + " rows deleted, which is not 1!");
                }
                break;
            case PLANNED_POKEMON:
                if (selection == null) {
                    throw new UnsupportedOperationException("Selection cannot be null!");
                }
                table = "planned_pokemons";
                rowsUpdated = db.delete(table, "id == " + selection, null);
                if (rowsUpdated != 1) {
                    Log.w(TAG, "There was " + rowsUpdated + " rows deleted, which is not 1!");
                }
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        return rowsUpdated;
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
            case POKEDEX_POKEMON_IMAGE:
                id = db.insertOrThrow("pokemon_images", null, values);
                return Uri.parse(POKEDEX_POKEMON_IMAGE_TABLE + "/" + id);
            case PLANNED_TEAM:
                id = db.insertOrThrow("planned_teams", null, values);
                return Uri.parse(PLANNED_TEAM_TABLE + "/" + id);
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
                queryBuilder.setTables("pokemon LEFT OUTER JOIN pokemon_species_names ON (pokemon.species_id = pokemon_species_names.pokemon_species_id AND pokemon_species_names.local_language_id = 9) LEFT OUTER JOIN (SELECT name, pokemon_id FROM pokemon_types, type_names WHERE local_language_id = 9 AND pokemon_types.type_id = type_names.type_id AND slot = 1) AS type1 ON (pokemon.id = type1.pokemon_id) LEFT OUTER JOIN (SELECT name, pokemon_id FROM pokemon_types, type_names WHERE local_language_id = 9 AND pokemon_types.type_id = type_names.type_id AND slot = 2) AS type2 ON (pokemon.id = type2.pokemon_id) LEFT OUTER JOIN (SELECT * FROM pokemon_abilities, abilities, ability_names WHERE local_language_id = 9 AND pokemon_abilities.ability_id = abilities.id AND pokemon_abilities.ability_id = ability_names.ability_id AND slot = 1) AS ability1 ON (pokemon.id = ability1.pokemon_id) LEFT OUTER JOIN (SELECT * FROM pokemon_abilities, abilities, ability_names WHERE local_language_id = 9 AND pokemon_abilities.ability_id = abilities.id AND pokemon_abilities.ability_id = ability_names.ability_id AND slot = 2) AS ability2 ON (pokemon.id = ability2.pokemon_id) LEFT OUTER JOIN (SELECT * FROM pokemon_abilities, abilities, ability_names WHERE local_language_id = 9 AND pokemon_abilities.ability_id = abilities.id AND pokemon_abilities.ability_id = ability_names.ability_id AND slot = 3) AS ability3 ON (pokemon.id = ability3.pokemon_id) LEFT OUTER JOIN (SELECT pokemon_id, base_stat, name FROM stats, pokemon_stats, stat_names WHERE stat_names.local_language_id = 9 AND stats.id = stat_names.stat_id AND stat_names.stat_id = pokemon_stats.stat_id AND stats.identifier = \"hp\") AS hp ON (hp.pokemon_id = pokemon.id) LEFT OUTER JOIN (SELECT pokemon_id, base_stat, name FROM stats, pokemon_stats, stat_names WHERE stat_names.local_language_id = 9 AND stats.id = stat_names.stat_id AND stat_names.stat_id = pokemon_stats.stat_id AND stats.identifier = \"attack\") AS att ON (att.pokemon_id = pokemon.id) LEFT OUTER JOIN (SELECT pokemon_id, base_stat, name FROM stats, pokemon_stats, stat_names WHERE stat_names.local_language_id = 9 AND stats.id = stat_names.stat_id AND stat_names.stat_id = pokemon_stats.stat_id AND stats.identifier = \"defense\") AS def ON (def.pokemon_id = pokemon.id) LEFT OUTER JOIN (SELECT pokemon_id, base_stat, name FROM stats, pokemon_stats, stat_names WHERE stat_names.local_language_id = 9 AND stats.id = stat_names.stat_id AND stat_names.stat_id = pokemon_stats.stat_id AND stats.identifier = \"special-attack\") AS satt ON (satt.pokemon_id = pokemon.id) LEFT OUTER JOIN (SELECT pokemon_id, base_stat, name FROM stats, pokemon_stats, stat_names WHERE stat_names.local_language_id = 9 AND stats.id = stat_names.stat_id AND stat_names.stat_id = pokemon_stats.stat_id AND stats.identifier = \"special-defense\") AS sdef ON (sdef.pokemon_id = pokemon.id) LEFT OUTER JOIN (SELECT pokemon_id, base_stat, name FROM stats, pokemon_stats, stat_names WHERE stat_names.local_language_id = 9 AND stats.id = stat_names.stat_id AND stat_names.stat_id = pokemon_stats.stat_id AND stats.identifier = \"speed\") AS spd ON (spd.pokemon_id = pokemon.id) LEFT OUTER JOIN pokemon_images AS default_image ON (default_image.pokemon_id = pokemon.id AND default_image.is_default = 1)\n" +
                        "LEFT OUTER JOIN pokemon_images AS shinny_image ON (shinny_image.pokemon_id = pokemon.id AND shinny_image.is_shinny = 1)\n" +
                        "LEFT OUTER JOIN pokemon_images AS icon_image ON (pokemon.id = icon_image.pokemon_id AND icon_image.is_icon = 1)");
                if (selection != null && !selection.isEmpty()) {
                    queryBuilder.appendWhere(selection);
                }
                selectionArray = new String[]{"pokemon.id AS _id", "pokemon_species_names.name AS " + DexKeyValues.name, "pokemon.species_id AS " + DexKeyValues.number, "type1.name AS " + DexKeyValues.type1, "type2.name AS " + DexKeyValues.type2, "ability1.name AS " + DexKeyValues.ability1Name, "ability1.id AS " + DexKeyValues.ability1ID, "ability2.name AS " + DexKeyValues.ability2Name, "ability2.id AS " + DexKeyValues.ability2ID, "ability3.name AS " + DexKeyValues.ability3Name, "ability3.id AS " + DexKeyValues.ability3ID, "hp.base_stat AS " + DexKeyValues.baseHP, "att.base_stat AS " + DexKeyValues.baseAttack, "def.base_stat AS " + DexKeyValues.baseDefense, "satt.base_stat AS " + DexKeyValues.baseSpecialAttack, "sdef.base_stat AS " + DexKeyValues.baseSpecialDefense, "spd.base_stat AS " + DexKeyValues.baseSpeed, "default_image.image_uri AS " + DexKeyValues.image, "shinny_image.image_uri AS " + DexKeyValues.shinnyImage, "icon_image.image_uri AS " + DexKeyValues.iconImage};
                break;
            case POKEDEX_ALL_SHORT:
                queryBuilder.setTables("pokemon\n" +
                        "LEFT OUTER JOIN pokemon_species_names ON (pokemon.species_id = pokemon_species_names.pokemon_species_id AND pokemon_species_names.local_language_id = 9)\n" +
                        "LEFT OUTER JOIN (SELECT name, pokemon_id FROM pokemon_types, type_names WHERE local_language_id = 9 AND pokemon_types.type_id = type_names.type_id AND slot = 1) AS type1 ON (pokemon.id = type1.pokemon_id) LEFT OUTER JOIN (SELECT name, pokemon_id FROM pokemon_types, type_names WHERE local_language_id = 9 AND pokemon_types.type_id = type_names.type_id AND slot = 2) AS type2 ON (pokemon.id = type2.pokemon_id)\n" +
                        "LEFT OUTER JOIN pokemon_forms ON (pokemon.id = pokemon_forms.pokemon_id)\n" +
                        "LEFT OUTER JOIN pokemon_images ON (pokemon.id = pokemon_images.pokemon_id AND pokemon_images.is_icon = 1)");

                queryBuilder.appendWhere("pokemon_forms.form_order == 1");
                if (!TextUtils.isEmpty(selection)) {
                    queryBuilder.appendWhere(" AND pokemon_species_names.name LIKE '%" + selection + "%'");
                }

                selectionArray = new String[]{"pokemon.id AS _id", "pokemon_species_names.name AS " + DexKeyValues.name, "pokemon.species_id AS " + DexKeyValues.number, "type1.name AS " + DexKeyValues.type1, "type2.name AS " + DexKeyValues.type2, "pokemon_images.image_uri AS " + DexKeyValues.iconImage};
                orderBy = "pokemon.species_id ASC";
                break;
            case POKEDEX_SEARCH_SPECIES:

                break;
            case POKEDEX_POKEMON_MOVES:
                queryBuilder.setTables("moves LEFT OUTER JOIN pokemon_moves ON (moves.id = pokemon_moves.move_id) LEFT OUTER JOIN pokemon_move_methods ON (pokemon_move_methods.id = pokemon_moves.pokemon_move_method_id) LEFT OUTER JOIN move_names ON (moves.id = move_names.move_id AND move_names.local_language_id = 9) LEFT OUTER JOIN move_flavor_text ON (moves.id = move_flavor_text.move_id AND move_flavor_text.language_id = 9 AND move_flavor_text.version_group_id = 15) LEFT OUTER JOIN machines ON (moves.id = machines.move_id AND pokemon_move_methods.id = 4)");
                queryBuilder.appendWhere("pokemon_moves.version_group_id = 15 AND (machines.version_group_id = 15 OR machines.version_group_id IS NULL)");
                if (selection != null && !selection.isEmpty()) {
                    queryBuilder.appendWhere(" AND " + selection);
                }
                orderBy = "pokemon_moves.pokemon_id ASC, level ASC, machines.machine_number ASC, \"order\" ASC, move_names.name ASC";
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
            case POKEDEX_POKEMON_IMAGE:
                queryBuilder.setTables("pokemon_images");
                if (!TextUtils.isEmpty(selection)) {
                    queryBuilder.appendWhere(selection);
                }
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
            case POKEDEX_POKEMON_EVOLUTION:
                // TODO Needs to be expanded.
                queryBuilder.setTables("pokemon\n" +
                        "LEFT OUTER JOIN pokemon_species ON (pokemon_species.id = pokemon.species_id)\n" +
                        "LEFT OUTER JOIN pokemon_evolution ON (pokemon_species.id = pokemon_evolution.evolved_species_id)\n" +
                        "LEFT OUTER JOIN evolution_trigger_prose ON (evolution_trigger_prose.evolution_trigger_id = pokemon_evolution.evolution_trigger_id AND evolution_trigger_prose.local_language_id = 9)\n" +
                        "LEFT OUTER JOIN item_names AS held_item_name ON (held_item_name.item_id = pokemon_evolution.held_item_id AND held_item_name.local_language_id = 9)\n" +
                        "LEFT OUTER JOIN item_names AS use_item_name ON (use_item_name.item_id = pokemon_evolution.trigger_item_id AND use_item_name.local_language_id = 9)\n" +
                        "LEFT OUTER JOIN pokemon_forms ON (pokemon_forms.pokemon_id = pokemon.id)\n" +
                        "LEFT OUTER JOIN pokemon_images ON (pokemon.id = pokemon_images.pokemon_id AND (pokemon_images.is_shinny = null OR pokemon_images.is_shinny = 0) AND (pokemon_images.is_icon = null OR pokemon_images.is_icon = 0))\n" +
                        "LEFT OUTER JOIN (select * from locations, location_names where locations.id = location_names.location_id and location_names.local_language_id = 9) AS locations ON (pokemon_evolution.location_id = locations.location_id)" +
                        "LEFT OUTER JOIN type_names ON (type_names.local_language_id = 9 AND pokemon_evolution.known_move_type_id = type_names.type_id)\n" +
                        "LEFT OUTER JOIN move_names ON (move_names.local_language_id = 9 AND move_names.move_id = known_move_id)\n" +
                        "LEFT OUTER JOIN genders ON (genders.id = pokemon_evolution.gender_id)");
                if (!TextUtils.isEmpty(selection)) {
                    queryBuilder.appendWhere("(pokemon_forms.is_default = 1) AND (pokemon_forms.is_battle_only = 0) AND (locations.region_id = 6 OR locations.region_id IS null) AND pokemon_species.evolution_chain_id = (SELECT evolution_chain_id FROM pokemon_species WHERE id = " + selection + ")");
                }
                orderBy = "pokemon_species.is_baby DESC";
                selectionArray = new String[]{"pokemon.species_id AS " + EvolutionKeyValues.SPECIES_ID, "pokemon_species.evolves_from_species_id AS " + EvolutionKeyValues.EVOLVES_FROM_SPECIES_ID, "evolution_trigger_prose.name AS " + EvolutionKeyValues.EVOLUTION_METHOD_PROSE, "pokemon_evolution.minimum_level AS " + EvolutionKeyValues.EVOLUTION_MIN_LEVEL, "use_item_name.name AS " + EvolutionKeyValues.EVOLUTION_USE_ITEM_NAME, "pokemon_evolution.time_of_day AS " + EvolutionKeyValues.EVOLUTION_TIME_OF_DAY, "pokemon_evolution.minimum_happiness AS " + EvolutionKeyValues.EVOLUTION_MINIMUM_HAPPINESS, "locations.name AS " + EvolutionKeyValues.EVOLUTION_LOCATION_NAME, "type_names.name AS " + EvolutionKeyValues.EVOLUTION_KNOWN_MOVE_TYPE, "pokemon_evolution.minimum_affection AS " + EvolutionKeyValues.EVOLUTION_MINIMUM_AFFECTION, "held_item_name.name AS " + EvolutionKeyValues.EVOLUTION_HELD_ITEM_NAME, "move_names.name AS " + EvolutionKeyValues.EVOLUTION_KNOWN_MOVE, "pokemon_evolution.minimum_beauty AS " + EvolutionKeyValues.EVOLUTION_MINIMUM_BEAUTY, "pokemon_evolution.needs_overworld_rain AS " + EvolutionKeyValues.EVOLUTION_OVER_WORLD_RAIN, "pokemon_evolution.turn_upside_down AS " + EvolutionKeyValues.EVOLUTION_UPSIDE_DOWN, "genders.identifier AS " + EvolutionKeyValues.EVOLUTION_GENDER, "pokemon_images.image_uri AS " + EvolutionKeyValues.POKEMON_IMAGE_URI};
                break;
            case PLANNED_POKEMON:
                // TODO Needs possible work.
                queryBuilder.setTables("planned_pokemons");

                if (!TextUtils.isEmpty(selection)) {
                    queryBuilder.appendWhere("id == " + selection);
                }

                selectionArray = new String[]{"id AS _id", "pokemon_id", "nature_id", "move1_id", "move2_id", "move3_id", "move4_id", "item_id", "ability_id", "ev_hp", "ev_att", "ev_def", "ev_satk", "ev_sdef", "ev_spd", "iv_hp", "iv_att", "iv_def", "iv_satt", "iv_sdef", "iv_spd", "note"};
                break;
            case PLANNED_POKEMON_SEARCH_GENERAL:
                queryBuilder.setTables("planned_pokemons LEFT OUTER JOIN pokemon ON (planned_pokemons.pokemon_id = pokemon.id) LEFT OUTER JOIN pokemon_species_names ON (planned_pokemons.pokemon_id = pokemon_species_names.pokemon_species_id AND pokemon_species_names.local_language_id = 9)" +
                        "LEFT OUTER JOIN pokemon_images AS icon_image ON (pokemon.id = icon_image.pokemon_id AND icon_image.is_icon = 1)");

                if (!TextUtils.isEmpty(selection)) {
                    queryBuilder.appendWhere("pokemon_species_names LIKE %'" + selection + "'%");
                }
                selectionArray = new String[]{"planned_pokemons.id AS _id", "pokemon_species_names.name AS " + PlannedKeyValues.NAME, "icon_image.image_uri AS " + PlannedKeyValues.ICON_IMAGE};
                break;
            case OWNED_POKEMON:
                queryBuilder.setTables("owned_pokemons LEFT OUTER JOIN pokemon ON (pokemon.species_id = owned_pokemons.pokemon_id) LEFT OUTER JOIN pokemon_species_names ON (pokemon.species_id = pokemon_species_names.pokemon_species_id AND pokemon_species_names.local_language_id = 9) LEFT OUTER JOIN (SELECT name, pokemon_id FROM pokemon_types, type_names WHERE local_language_id = 9 AND pokemon_types.type_id = type_names.type_id AND slot = 1) AS type1 ON (pokemon.id = type1.pokemon_id) LEFT OUTER JOIN (SELECT name, pokemon_id FROM pokemon_types, type_names WHERE local_language_id = 9 AND pokemon_types.type_id = type_names.type_id AND slot = 2) AS type2 ON (pokemon.id = type2.pokemon_id) LEFT OUTER JOIN (SELECT ability_names.ability_id AS id, name, effect FROM ability_names, ability_prose WHERE ability_names.ability_id = ability_prose.ability_id AND ability_names.local_language_id = 9 AND ability_prose.local_language_id = 9) AS ability ON (owned_pokemons.ability_id = ability.id) LEFT OUTER JOIN nature_names ON (owned_pokemons.nature_id = nature_names.nature_id AND nature_names.local_language_id = 9) LEFT OUTER JOIN genders ON (genders.id = owned_pokemons.gender_id) LEFT OUTER JOIN move_names AS move1 ON (move1.move_id = owned_pokemons.move1_id AND move1.local_language_id = 9) LEFT OUTER JOIN move_names AS move2 ON (move2.move_id = owned_pokemons.move2_id AND move2.local_language_id = 9) LEFT OUTER JOIN move_names AS move3 ON (move3.move_id = owned_pokemons.move3_id AND move3.local_language_id = 9) LEFT OUTER JOIN move_names AS move4 ON (move4.move_id = owned_pokemons.move4_id AND move4.local_language_id = 9) LEFT OUTER JOIN pokemon_images AS default_image ON (default_image.pokemon_id = pokemon.id AND default_image.is_default = 1)\n" +
                        "LEFT OUTER JOIN pokemon_images AS shinny_image ON (shinny_image.pokemon_id = pokemon.id AND shinny_image.is_shinny = 1)\n" +
                        "LEFT OUTER JOIN pokemon_images AS icon_image ON (pokemon.id = icon_image.pokemon_id AND icon_image.is_icon = 1)");
                if (selection != null && !selection.isEmpty()) {
                    queryBuilder.appendWhere(selection);
                }
                // TODO Select array
                selectionArray = new String[]{"owned_pokemons.id AS _id", "owned_pokemons.nickname AS nickname", "pokemon_species_names.name AS name", "pokemon.species_id AS " + OwnedKeyValues.POKEMON_ID, "owned_pokemons.ability_id AS " + OwnedKeyValues.ABILITY_ID, "icon_image.image_uri AS icon_image", "default_image.image_uri AS image", "shinny_image.image_uri AS shinny_image", "iv_hp AS " + OwnedKeyValues.IV_HP, "iv_att AS " + OwnedKeyValues.IV_ATT, "iv_def AS " + OwnedKeyValues.IV_DEF, "iv_satt AS " + OwnedKeyValues.IV_SATT, "iv_sdef AS " + OwnedKeyValues.IV_SDEF, "iv_spd AS " + OwnedKeyValues.IV_SPD, "ev_hp AS " + OwnedKeyValues.EV_HP, "ev_att AS " + OwnedKeyValues.EV_ATT, "ev_def AS " + OwnedKeyValues.EV_DEF, "ev_satt AS " + OwnedKeyValues.EV_SATT, "ev_sdef AS " + OwnedKeyValues.EV_SDEF, "ev_spd AS " + OwnedKeyValues.EV_SPD, "level AS " + OwnedKeyValues.LEVEL, "shinny AS " + OwnedKeyValues.SHINNY, "note AS " + OwnedKeyValues.NOTE, "nature_names.name AS nature_name", "nature_names.nature_id AS " + OwnedKeyValues.NATURE_ID, "owned_pokemons.move1_id AS " + OwnedKeyValues.MOVE1_ID, "owned_pokemons.move2_id AS " + OwnedKeyValues.MOVE2_ID, "owned_pokemons.move3_id AS " + OwnedKeyValues.MOVE3_ID, "owned_pokemons.move4_id AS " + OwnedKeyValues.MOVE4_ID};
                break;
            case OWNED_POKEMON_SEARCH_GENERAL:
                queryBuilder.setTables("owned_pokemons LEFT OUTER JOIN pokemon ON (pokemon.species_id = owned_pokemons.pokemon_id) LEFT OUTER JOIN pokemon_species_names ON (pokemon.species_id = pokemon_species_names.pokemon_species_id AND pokemon_species_names.local_language_id = 9) LEFT OUTER JOIN (SELECT name, pokemon_id FROM pokemon_types, type_names WHERE local_language_id = 9 AND pokemon_types.type_id = type_names.type_id AND slot = 1) AS type1 ON (pokemon.id = type1.pokemon_id) LEFT OUTER JOIN (SELECT name, pokemon_id FROM pokemon_types, type_names WHERE local_language_id = 9 AND pokemon_types.type_id = type_names.type_id AND slot = 2) AS type2 ON (pokemon.id = type2.pokemon_id) LEFT OUTER JOIN (SELECT ability_names.ability_id AS id, name, effect FROM ability_names, ability_prose WHERE ability_names.ability_id = ability_prose.ability_id AND ability_names.local_language_id = 9 AND ability_prose.local_language_id = 9) AS ability ON (owned_pokemons.ability_id = ability.id) LEFT OUTER JOIN nature_names ON (owned_pokemons.nature_id = nature_names.nature_id AND nature_names.local_language_id = 9) LEFT OUTER JOIN genders ON (genders.id = owned_pokemons.gender_id) LEFT OUTER JOIN move_names AS move1 ON (move1.move_id = owned_pokemons.move1_id AND move1.local_language_id = 9) LEFT OUTER JOIN move_names AS move2 ON (move2.move_id = owned_pokemons.move2_id AND move2.local_language_id = 9) LEFT OUTER JOIN move_names AS move3 ON (move3.move_id = owned_pokemons.move3_id AND move3.local_language_id = 9) LEFT OUTER JOIN move_names AS move4 ON (move4.move_id = owned_pokemons.move4_id AND move4.local_language_id = 9)" +
                        "LEFT OUTER JOIN pokemon_images AS icon_image ON (pokemon.id = icon_image.pokemon_id AND icon_image.is_icon = 1)");

                // TODO Select array
                selectionArray = new String[]{"owned_pokemons.id AS _id", "owned_pokemons.nickname AS " + OwnedKeyValues.NICKNAME, "pokemon_species_names.name AS name", "pokemon.species_id AS " + OwnedKeyValues.POKEMON_ID, "icon_image.image_uri AS image"};
                break;
            case PLANNED_TEAM:
                // TODO
                queryBuilder.setTables("planned_teams\n" +
                        "LEFT OUTER JOIN planned_pokemons AS pokemon1 ON (planned_teams.planned_pokemon1_id = pokemon1.id)\n" +
                        "LEFT OUTER JOIN planned_pokemons AS pokemon2 ON (planned_teams.planned_pokemon2_id = pokemon2.id)\n" +
                        "LEFT OUTER JOIN planned_pokemons AS pokemon3 ON (planned_teams.planned_pokemon3_id = pokemon3.id)\n" +
                        "LEFT OUTER JOIN planned_pokemons AS pokemon4 ON (planned_teams.planned_pokemon4_id = pokemon4.id)\n" +
                        "LEFT OUTER JOIN planned_pokemons AS pokemon5 ON (planned_teams.planned_pokemon5_id = pokemon5.id)\n" +
                        "LEFT OUTER JOIN planned_pokemons AS pokemon6 ON (planned_teams.planned_pokemon6_id = pokemon6.id)");

                if (!TextUtils.isEmpty(selection)) {
                    queryBuilder.appendWhere("planned_teams.id = " + selection);
                }

                selectionArray = new String[]{"planned_teams.id AS _id",
                        "pokemon1.pokemon_id AS " + TeamKeyValues.POKEMON1_ID, "pokemon1.nature_id AS " + TeamKeyValues.POKEMON1_NATURE_ID, "pokemon1.move1_id AS " + TeamKeyValues.POKEMON1_MOVE1_ID, "pokemon1.move2_id AS " + TeamKeyValues.POKEMON1_MOVE2_ID, "pokemon1.move3_id AS " + TeamKeyValues.POKEMON1_MOVE3_ID, "pokemon1.move4_id AS " + TeamKeyValues.POKEMON1_MOVE4_ID, "pokemon1.ability_id AS " + TeamKeyValues.POKEMON1_ABILITY_ID, "pokemon1.item_id AS " + TeamKeyValues.POKEMON1_ITEM_ID, "pokemon1.iv_hp AS " + TeamKeyValues.POKEMON1_IV_HP, "pokemon1.iv_att AS " + TeamKeyValues.POKEMON1_IV_ATT, "pokemon1.iv_def AS " + TeamKeyValues.POKEMON1_IV_DEF, "pokemon1.iv_satt AS " + TeamKeyValues.POKEMON1_IV_SATT, "pokemon1.iv_sdef AS " + TeamKeyValues.POKEMON1_IV_SDEF, "pokemon1.iv_spd AS " + TeamKeyValues.POKEMON1_IV_SPD, "pokemon1.ev_hp AS " + TeamKeyValues.POKEMON1_EV_HP, "pokemon1.ev_att AS " + TeamKeyValues.POKEMON1_EV_ATT, "pokemon1.ev_def AS " + TeamKeyValues.POKEMON1_EV_DEF, "pokemon1.ev_satk AS " + TeamKeyValues.POKEMON1_EV_SATT, "pokemon1.ev_sdef AS " + TeamKeyValues.POKEMON1_EV_SDEF, "pokemon1.ev_spd AS " + TeamKeyValues.POKEMON1_EV_SPD, "pokemon1.note AS " + TeamKeyValues.POKEMON1_NOTE,
                        "pokemon2.pokemon_id AS " + TeamKeyValues.POKEMON2_ID, "pokemon2.nature_id AS " + TeamKeyValues.POKEMON2_NATURE_ID, "pokemon2.move1_id AS " + TeamKeyValues.POKEMON2_MOVE1_ID, "pokemon2.move2_id AS " + TeamKeyValues.POKEMON2_MOVE2_ID, "pokemon2.move3_id AS " + TeamKeyValues.POKEMON2_MOVE3_ID, "pokemon2.move4_id AS " + TeamKeyValues.POKEMON2_MOVE4_ID, "pokemon2.ability_id AS " + TeamKeyValues.POKEMON2_ABILITY_ID, "pokemon2.item_id AS " + TeamKeyValues.POKEMON2_ITEM_ID, "pokemon2.iv_hp AS " + TeamKeyValues.POKEMON2_IV_HP, "pokemon2.iv_att AS " + TeamKeyValues.POKEMON2_IV_ATT, "pokemon2.iv_def AS " + TeamKeyValues.POKEMON2_IV_DEF, "pokemon2.iv_satt AS " + TeamKeyValues.POKEMON2_IV_SATT, "pokemon2.iv_sdef AS " + TeamKeyValues.POKEMON2_IV_SDEF, "pokemon2.iv_spd AS " + TeamKeyValues.POKEMON2_IV_SPD, "pokemon2.ev_hp AS " + TeamKeyValues.POKEMON2_EV_HP, "pokemon2.ev_att AS " + TeamKeyValues.POKEMON2_EV_ATT, "pokemon2.ev_def AS " + TeamKeyValues.POKEMON2_EV_DEF, "pokemon2.ev_satk AS " + TeamKeyValues.POKEMON2_EV_SATT, "pokemon2.ev_sdef AS " + TeamKeyValues.POKEMON2_EV_SDEF, "pokemon2.ev_spd AS " + TeamKeyValues.POKEMON2_EV_SPD, "pokemon2.note AS " + TeamKeyValues.POKEMON2_NOTE,
                        "pokemon3.pokemon_id AS " + TeamKeyValues.POKEMON3_ID, "pokemon3.nature_id AS " + TeamKeyValues.POKEMON3_NATURE_ID, "pokemon3.move1_id AS " + TeamKeyValues.POKEMON3_MOVE1_ID, "pokemon3.move2_id AS " + TeamKeyValues.POKEMON3_MOVE2_ID, "pokemon3.move3_id AS " + TeamKeyValues.POKEMON3_MOVE3_ID, "pokemon3.move4_id AS " + TeamKeyValues.POKEMON3_MOVE4_ID, "pokemon3.ability_id AS " + TeamKeyValues.POKEMON3_ABILITY_ID, "pokemon3.item_id AS " + TeamKeyValues.POKEMON3_ITEM_ID, "pokemon3.iv_hp AS " + TeamKeyValues.POKEMON3_IV_HP, "pokemon3.iv_att AS " + TeamKeyValues.POKEMON3_IV_ATT, "pokemon3.iv_def AS " + TeamKeyValues.POKEMON3_IV_DEF, "pokemon3.iv_satt AS " + TeamKeyValues.POKEMON3_IV_SATT, "pokemon3.iv_sdef AS " + TeamKeyValues.POKEMON3_IV_SDEF, "pokemon3.iv_spd AS " + TeamKeyValues.POKEMON3_IV_SPD, "pokemon3.ev_hp AS " + TeamKeyValues.POKEMON3_EV_HP, "pokemon3.ev_att AS " + TeamKeyValues.POKEMON3_EV_ATT, "pokemon3.ev_def AS " + TeamKeyValues.POKEMON3_EV_DEF, "pokemon3.ev_satk AS " + TeamKeyValues.POKEMON3_EV_SATT, "pokemon3.ev_sdef AS " + TeamKeyValues.POKEMON3_EV_SDEF, "pokemon3.ev_spd AS " + TeamKeyValues.POKEMON3_EV_SPD, "pokemon3.note AS " + TeamKeyValues.POKEMON3_NOTE,
                        "pokemon4.pokemon_id AS " + TeamKeyValues.POKEMON4_ID, "pokemon4.nature_id AS " + TeamKeyValues.POKEMON4_NATURE_ID, "pokemon4.move1_id AS " + TeamKeyValues.POKEMON4_MOVE1_ID, "pokemon4.move2_id AS " + TeamKeyValues.POKEMON4_MOVE2_ID, "pokemon4.move3_id AS " + TeamKeyValues.POKEMON4_MOVE3_ID, "pokemon4.move4_id AS " + TeamKeyValues.POKEMON4_MOVE4_ID, "pokemon4.ability_id AS " + TeamKeyValues.POKEMON4_ABILITY_ID, "pokemon4.item_id AS " + TeamKeyValues.POKEMON4_ITEM_ID, "pokemon4.iv_hp AS " + TeamKeyValues.POKEMON4_IV_HP, "pokemon4.iv_att AS " + TeamKeyValues.POKEMON4_IV_ATT, "pokemon4.iv_def AS " + TeamKeyValues.POKEMON4_IV_DEF, "pokemon4.iv_satt AS " + TeamKeyValues.POKEMON4_IV_SATT, "pokemon4.iv_sdef AS " + TeamKeyValues.POKEMON4_IV_SDEF, "pokemon4.iv_spd AS " + TeamKeyValues.POKEMON4_IV_SPD, "pokemon4.ev_hp AS " + TeamKeyValues.POKEMON4_EV_HP, "pokemon4.ev_att AS " + TeamKeyValues.POKEMON4_EV_ATT, "pokemon4.ev_def AS " + TeamKeyValues.POKEMON4_EV_DEF, "pokemon4.ev_satk AS " + TeamKeyValues.POKEMON4_EV_SATT, "pokemon4.ev_sdef AS " + TeamKeyValues.POKEMON4_EV_SDEF, "pokemon4.ev_spd AS " + TeamKeyValues.POKEMON4_EV_SPD, "pokemon4.note AS " + TeamKeyValues.POKEMON4_NOTE,
                        "pokemon5.pokemon_id AS " + TeamKeyValues.POKEMON5_ID, "pokemon5.nature_id AS " + TeamKeyValues.POKEMON5_NATURE_ID, "pokemon5.move1_id AS " + TeamKeyValues.POKEMON5_MOVE1_ID, "pokemon5.move2_id AS " + TeamKeyValues.POKEMON5_MOVE2_ID, "pokemon5.move3_id AS " + TeamKeyValues.POKEMON5_MOVE3_ID, "pokemon5.move4_id AS " + TeamKeyValues.POKEMON5_MOVE4_ID, "pokemon5.ability_id AS " + TeamKeyValues.POKEMON5_ABILITY_ID, "pokemon5.item_id AS " + TeamKeyValues.POKEMON5_ITEM_ID, "pokemon5.iv_hp AS " + TeamKeyValues.POKEMON5_IV_HP, "pokemon5.iv_att AS " + TeamKeyValues.POKEMON5_IV_ATT, "pokemon5.iv_def AS " + TeamKeyValues.POKEMON5_IV_DEF, "pokemon5.iv_satt AS " + TeamKeyValues.POKEMON5_IV_SATT, "pokemon5.iv_sdef AS " + TeamKeyValues.POKEMON5_IV_SDEF, "pokemon5.iv_spd AS " + TeamKeyValues.POKEMON5_IV_SPD, "pokemon5.ev_hp AS " + TeamKeyValues.POKEMON5_EV_HP, "pokemon5.ev_att AS " + TeamKeyValues.POKEMON5_EV_ATT, "pokemon5.ev_def AS " + TeamKeyValues.POKEMON5_EV_DEF, "pokemon5.ev_satk AS " + TeamKeyValues.POKEMON5_EV_SATT, "pokemon5.ev_sdef AS " + TeamKeyValues.POKEMON5_EV_SDEF, "pokemon5.ev_spd AS " + TeamKeyValues.POKEMON5_EV_SPD, "pokemon5.note AS " + TeamKeyValues.POKEMON5_NOTE,
                        "pokemon6.pokemon_id AS " + TeamKeyValues.POKEMON6_ID, "pokemon6.nature_id AS " + TeamKeyValues.POKEMON6_NATURE_ID, "pokemon6.move1_id AS " + TeamKeyValues.POKEMON6_MOVE1_ID, "pokemon6.move2_id AS " + TeamKeyValues.POKEMON6_MOVE2_ID, "pokemon6.move3_id AS " + TeamKeyValues.POKEMON6_MOVE3_ID, "pokemon6.move4_id AS " + TeamKeyValues.POKEMON6_MOVE4_ID, "pokemon6.ability_id AS " + TeamKeyValues.POKEMON6_ABILITY_ID, "pokemon6.item_id AS " + TeamKeyValues.POKEMON6_ITEM_ID, "pokemon6.iv_hp AS " + TeamKeyValues.POKEMON6_IV_HP, "pokemon6.iv_att AS " + TeamKeyValues.POKEMON6_IV_ATT, "pokemon6.iv_def AS " + TeamKeyValues.POKEMON6_IV_DEF, "pokemon6.iv_satt AS " + TeamKeyValues.POKEMON6_IV_SATT, "pokemon6.iv_sdef AS " + TeamKeyValues.POKEMON6_IV_SDEF, "pokemon6.iv_spd AS " + TeamKeyValues.POKEMON6_IV_SPD, "pokemon6.ev_hp AS " + TeamKeyValues.POKEMON6_EV_HP, "pokemon6.ev_att AS " + TeamKeyValues.POKEMON6_EV_ATT, "pokemon6.ev_def AS " + TeamKeyValues.POKEMON6_EV_DEF, "pokemon6.ev_satk AS " + TeamKeyValues.POKEMON6_EV_SATT, "pokemon6.ev_sdef AS " + TeamKeyValues.POKEMON6_EV_SDEF, "pokemon6.ev_spd AS " + TeamKeyValues.POKEMON6_EV_SPD, "pokemon6.note AS " + TeamKeyValues.POKEMON6_NOTE};
                break;
            case PLANNED_TEAM_LIST:
                queryBuilder.setTables("planned_teams\n" +
                        "LEFT OUTER JOIN planned_pokemons AS pokemon1 ON (planned_teams.planned_pokemon1_id = pokemon1.id)\n" +
                        "LEFT OUTER JOIN pokemon_images AS pokemon1_image ON (pokemon1.pokemon_id = pokemon1_image.pokemon_id AND pokemon1_image.is_icon)\n" +
                        "LEFT OUTER JOIN planned_pokemons AS pokemon2 ON (planned_teams.planned_pokemon2_id = pokemon2.id)\n" +
                        "LEFT OUTER JOIN pokemon_images AS pokemon2_image ON (pokemon2.pokemon_id = pokemon2_image.pokemon_id AND pokemon2_image.is_icon)\n" +
                        "LEFT OUTER JOIN planned_pokemons AS pokemon3 ON (planned_teams.planned_pokemon3_id = pokemon3.id)\n" +
                        "LEFT OUTER JOIN pokemon_images AS pokemon3_image ON (pokemon3.pokemon_id = pokemon3_image.pokemon_id AND pokemon3_image.is_icon)\n" +
                        "LEFT OUTER JOIN planned_pokemons AS pokemon4 ON (planned_teams.planned_pokemon4_id = pokemon4.id)\n" +
                        "LEFT OUTER JOIN pokemon_images AS pokemon4_image ON (pokemon4.pokemon_id = pokemon4_image.pokemon_id AND pokemon4_image.is_icon)\n" +
                        "LEFT OUTER JOIN planned_pokemons AS pokemon5 ON (planned_teams.planned_pokemon5_id = pokemon5.id)\n" +
                        "LEFT OUTER JOIN pokemon_images AS pokemon5_image ON (pokemon5.pokemon_id = pokemon5_image.pokemon_id AND pokemon5_image.is_icon)\n" +
                        "LEFT OUTER JOIN planned_pokemons AS pokemon6 ON (planned_teams.planned_pokemon6_id = pokemon6.id)\n" +
                        "LEFT OUTER JOIN pokemon_images AS pokemon6_image ON (pokemon6.pokemon_id = pokemon6_image.pokemon_id AND pokemon6_image.is_icon)");

                selectionArray = new String[]{"planned_teams.id AS _id", "planned_teams.id AS " + TeamKeyValues.TEAM_ID, "pokemon1_image.image_uri AS " + TeamKeyValues.POKEMON1_ICON, "pokemon2_image.image_uri AS " + TeamKeyValues.POKEMON2_ICON, "pokemon3_image.image_uri AS " + TeamKeyValues.POKEMON3_ICON,
                        "pokemon4_image.image_uri AS " + TeamKeyValues.POKEMON4_ICON, "pokemon5_image.image_uri AS " + TeamKeyValues.POKEMON5_ICON, "pokemon6_image.image_uri AS " + TeamKeyValues.POKEMON6_ICON};
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
                table = "pokemon_images";
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
            case PLANNED_POKEMON:
                if (TextUtils.isEmpty(selection)) {
                    throw new UnsupportedOperationException("Selection cannot be empty!");
                }
                table = "planned_pokemons";
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
