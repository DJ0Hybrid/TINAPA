package com.tinapaproject.tinapa.database.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.tinapaproject.tinapa.database.TinapaDatabaseHelper;
import com.tinapaproject.tinapa.database.key.DexKeyValues;
import com.tinapaproject.tinapa.database.key.OwnedKeyValues;
import com.tinapaproject.tinapa.database.key.PlannedKeyValues;

import org.w3c.dom.Text;

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

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // NOTE: "*" is any series of characters while "#" is any series of numbers.
        uriMatcher.addURI(AUTHORITY, POKEDEX_TABLE, POKEDEX);
        uriMatcher.addURI(AUTHORITY, POKEDEX_ALL_SHORT_TABLE, POKEDEX_ALL_SHORT);
        uriMatcher.addURI(AUTHORITY, POKEDEX_SEARCH_SPECIES_TABLE + "/search/species/*", POKEDEX_SEARCH_SPECIES);
        uriMatcher.addURI(AUTHORITY, POKEDEX_POKEMON_MOVES_TABLE, POKEDEX_POKEMON_MOVES);
        uriMatcher.addURI(AUTHORITY, POKEDEX_POKEMON_ABILITIES_TABLE, POKEDEX_POKEMON_ABILITIES);
        uriMatcher.addURI(AUTHORITY, POKEDEX_POKEMON_IMAGE_TABLE, POKEDEX_POKEMON_IMAGE);

        uriMatcher.addURI(AUTHORITY, PLANNED_POKEMON_TABLE, PLANNED_POKEMON);
        uriMatcher.addURI(AUTHORITY, PLANNED_POKEMON_SEARCH_GENERAL_TABLE, PLANNED_POKEMON_SEARCH_GENERAL);

        uriMatcher.addURI(AUTHORITY, OWNED_POKEMON_TABLE, OWNED_POKEMON);
        uriMatcher.addURI(AUTHORITY, OWNED_POKEMON_SEARCH_GENERAL_TABLE, OWNED_POKEMON_SEARCH_GENERAL);

        uriMatcher.addURI(AUTHORITY, PLANNED_TEAM_TABLE, PLANNED_TEAM);
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
        switch (uriType) {
            case OWNED_POKEMON:
                long id = db.insertOrThrow("owned_pokemons", null, values);
                return Uri.parse(OWNED_POKEMON_TABLE + "/" + id);
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
                queryBuilder.setTables("moves LEFT OUTER JOIN pokemon_moves ON (moves.id = pokemon_moves.move_id AND pokemon_moves.version_group_id = 15) LEFT OUTER JOIN pokemon_move_methods ON (pokemon_move_methods.id = pokemon_moves.pokemon_move_method_id) LEFT OUTER JOIN move_names ON (moves.id = move_names.move_id AND move_names.local_language_id = 9) LEFT OUTER JOIN move_flavor_text ON (moves.id = move_flavor_text.move_id AND move_flavor_text.language_id = 9 AND move_flavor_text.version_group_id = 15)");
                if (selection != null && !selection.isEmpty()) {
                    queryBuilder.appendWhere(selection);
                }
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
                selectionArray = new String[]{"owned_pokemons.id AS _id", "owned_pokemons.nickname AS nickname", "pokemon_species_names.name AS name", "pokemon.species_id AS " + OwnedKeyValues.POKEMON_ID, "owned_pokemons.ability_id AS " + OwnedKeyValues.ABILITY_ID, "icon_image", "image", "shinny_image", "iv_hp", "iv_att", "iv_def", "iv_satt", "iv_sdef", "iv_spd", "ev_hp", "ev_att", "ev_def", "ev_satt", "ev_sdef", "ev_spd", "level", "shinny", "note", "nature_names.name AS nature_name", "nature_names.nature_id AS " + OwnedKeyValues.NATURE_ID};
                break;
            case OWNED_POKEMON_SEARCH_GENERAL:
                queryBuilder.setTables("owned_pokemons LEFT OUTER JOIN pokemon ON (pokemon.species_id = owned_pokemons.pokemon_id) LEFT OUTER JOIN pokemon_species_names ON (pokemon.species_id = pokemon_species_names.pokemon_species_id AND pokemon_species_names.local_language_id = 9) LEFT OUTER JOIN (SELECT name, pokemon_id FROM pokemon_types, type_names WHERE local_language_id = 9 AND pokemon_types.type_id = type_names.type_id AND slot = 1) AS type1 ON (pokemon.id = type1.pokemon_id) LEFT OUTER JOIN (SELECT name, pokemon_id FROM pokemon_types, type_names WHERE local_language_id = 9 AND pokemon_types.type_id = type_names.type_id AND slot = 2) AS type2 ON (pokemon.id = type2.pokemon_id) LEFT OUTER JOIN (SELECT ability_names.ability_id AS id, name, effect FROM ability_names, ability_prose WHERE ability_names.ability_id = ability_prose.ability_id AND ability_names.local_language_id = 9 AND ability_prose.local_language_id = 9) AS ability ON (owned_pokemons.ability_id = ability.id) LEFT OUTER JOIN nature_names ON (owned_pokemons.nature_id = nature_names.nature_id AND nature_names.local_language_id = 9) LEFT OUTER JOIN genders ON (genders.id = owned_pokemons.gender_id) LEFT OUTER JOIN move_names AS move1 ON (move1.move_id = owned_pokemons.move1_id AND move1.local_language_id = 9) LEFT OUTER JOIN move_names AS move2 ON (move2.move_id = owned_pokemons.move2_id AND move2.local_language_id = 9) LEFT OUTER JOIN move_names AS move3 ON (move3.move_id = owned_pokemons.move3_id AND move3.local_language_id = 9) LEFT OUTER JOIN move_names AS move4 ON (move4.move_id = owned_pokemons.move4_id AND move4.local_language_id = 9)");

                // TODO Select array
                selectionArray = new String[]{"pokemon.id AS _id", "owned_pokemons.nickname AS " + OwnedKeyValues.NICKNAME, "pokemon_species_names.name AS name", "icon_image AS image"};
                break;
            case PLANNED_TEAM:

                break;
            default:
                throw new UnsupportedOperationException("Unsupported URI.");
        }
        Cursor cursor = queryBuilder.query(db, selectionArray, null, null, null, null, orderBy);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsUpdated = 0;
        int uriType = uriMatcher.match(uri);
        switch (uriType) {
            case POKEDEX_POKEMON_IMAGE:
                if (TextUtils.isEmpty(selection)) {
                    throw new UnsupportedOperationException("Selection cannot be empty!");
                }
                String table = "pokemon";
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
