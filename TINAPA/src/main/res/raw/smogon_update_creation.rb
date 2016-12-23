require 'fileutils'

# File paths
oldDatabasePath = "database_creation.sql"

abilitiesInfoPath = "abilities_info.txt"
alolaFormEvoPath = "alola_form_evolution.txt"
itemsInfoPath = "items_info.txt"
movesInfoPath = "moves_info.txt"
pokemonMovesPath = "pokemon_moves.txt"

newDatabaseFolder = "update_v3"
newDatabasePath = "update_v3.sql"
newPokemonPath = "pokemon_update_v3.sql"
newAbilityPath = "ability_update_v3.sql"
newMovePath = "move_update_v3.sql"

errorPokemonFolder = "errorPokemon"

#newDatabaseFile = File.new(newDatabasePath, "w")


# Old Database Table Names
languageId = "9"

abilities = "abilities"
abilityNames = "ability_names"
abilityText = "ability_flavor_text"

eggGroups = "egg_groups"

itemNames = "item_names"
itemText = "item_flavor_text"
items = "items"

moveNames = "move_names"
moves = "moves"
moveDamageClasses = "move_damage_classes"
moveEffects = "move_effects"
moveTargets = "move_targets"

pokemon = "pokemon"
pokemonAbilities = "pokemon_abilities"
pokemonSpecies = "pokemon_species"
pokemonName = "pokemon_species_names"

types = "types"


# Load up the old database's information.
puts "Beginning to read in old database."
oldTypes = Hash.new()

oldMoveTargets = Hash.new()

oldMoveDamageTypes = Hash.new()

oldMoves = Hash.new()
highestMoveId = -1

oldAbilities = Hash.new()
highestAbilityId = -1

oldPokemon = Hash.new()

oldEggGroups = Hash.new()
# oldItems = Hash.new()   # All the items are new, so don't need this.
begin
  file = File.read(oldDatabasePath).split(";").map(&:strip).map {|x| x + ";"}
  currentTable = nil
  currentTableName = nil
  file.each do |line|
    if (line.start_with?"DROP TABLE IF EXISTS")
      if (currentTable != nil && currentTableName != nil)
        #oldDatabase[currentTableName] = currentTable
      end
    elsif (line.start_with?"CREATE TABLE")
      currentTableName = line[/#{Regexp.escape("CREATE TABLE")}(.*?)#{Regexp.escape("(")}/m, 1].tr_s("\"", "").strip
      #puts currentTableName
      #currentTable = Hash.new()
    elsif (line.start_with?"INSERT INTO")
      #currentTable[line] = line
      if (currentTableName.casecmp(abilities) == 0)
        # int id, text identifier, int generation, int bool isMainSeries
        abilityValues = line.split("VALUES")[1]
        abilityValues = abilityValues.delete "'();"
        abilityValuesList = abilityValues.split(",")

        if (abilityValuesList[0].to_i > highestAbilityId)
          highestAbilityId = abilityValuesList[0].to_i
        end

        if (abilityValuesList[3] == "1")
          oldAbilities[abilityValuesList[1]] = abilityValuesList
        end
      elsif (currentTableName.casecmp(eggGroups) == 0)
        # int id, text identifier
        eggValues = line.split("VALUES")[1]
        eggValues = eggValues.delete "'();"
        eggValuesList = eggValues.split(",")
        oldEggGroups[eggValuesList[1]] = eggValuesList
      elsif (currentTableName.casecmp(moves) == 0)
        # int id, text identifier, int generation, int type, int power, int pp, int accuracy, int priority, int targetId, int damageClassId, int effectId, int effectChance, int contestTypeId, int contestEffectId, int superContestEffectId
        movesValues = line.split("VALUES")[1]
        movesValues = movesValues.delete "'();"
        movesValuesList = movesValues.split(",")

        if (movesValuesList[3].to_i != 10002)
          if (movesValuesList[0].to_i > highestMoveId)
            highestMoveId = movesValuesList[0].to_i
          end

          oldMoves[movesValuesList[1]] = movesValuesList
        end

      elsif (currentTableName.casecmp(types) == 0)
        # int id, text identifier, don't care...
        typeValues = line.split("VALUES")[1]
        typeValues = typeValues.delete "'();"
        typeValuesList = typeValues.split(",")
        oldTypes[typeValuesList[1]] = typeValuesList
      elsif (currentTableName.casecmp(moveTargets) == 0)
        # ints id, text identifier
        targetValues = line.split("VALUES")[1]
        targetValues = targetValues.delete "'();"
        targetValuesList = targetValues.split(",")
        oldMoveTargets[targetValuesList[1]] = targetValuesList
      elsif (currentTableName.casecmp(moveDamageClasses) == 0)
        # int id, text identifier
        damageTypeValues = line.split("VALUES")[1]
        damageTypeValues = damageTypeValues.delete "'();"
        damageTypeValuesList = damageTypeValues.split(",")
        oldMoveDamageTypes[damageTypeValuesList[1]] = damageTypeValuesList
      end
    end
  end
rescue => err
  puts "Exception in reading old database: #{err}"
  err
end
puts "Finished reading old database."


FileUtils.mkdir_p newDatabaseFolder

puts "Reading in new abilities. All are new."
newAbilityInsertFile = File.new(newDatabaseFolder + "\\" + newAbilityPath, "w")

begin
  file = File.read(abilitiesInfoPath).split("\n\n")
  file.each_with_index do |ability, index|
    # Setup to be added to the list
    abilityName = ability.split("\n", 2)[0].gsub(/[^a-zA-Z\s\-\:]/, "")
    abilityDescription = ability.split("\n", 2)[1]
    abilityId = highestAbilityId + index + 1
    abilityDescriptor = abilityName.downcase.gsub(" ", "-")
    abilityList = [abilityId, abilityDescriptor, 7, 1]
    oldAbilities[abilityDescriptor] = abilityList

    # Write
    newAbilityInsertFile.puts("INSERT INTO \"abilities\" VALUES(#{abilityId}, '#{abilityDescriptor}', 7, 1);")
    newAbilityInsertFile.puts("INSERT INTO \"ability_names\" VALUES(#{abilityId}, 9, '#{abilityName}');")
    newAbilityInsertFile.puts("INSERT INTO \"ability_flavor_text\" VALUES(#{abilityId}, 7, 9, '#{abilityDescription}');")
  end
rescue => err
  puts "Exception in reading Abilities file: #{err}"
  err
end

newAbilityInsertFile.close


puts "Reading in moves."
newMovesInsertFile = File.new(newDatabaseFolder + "\\" + newMovePath, "w")

begin
  file = File.read(movesInfoPath).split("\n\n")
  file.each_with_index do |move, index|
    # Go through the moves and figure out what is new and what isn't.
    moveReadValues = move.split("\n")
    if (moveReadValues.size < 8)
      # Maybe put in an error file (someday.)
    elsif
      moveName = moveReadValues[0].gsub(/[^a-zA-Z\s\-\:]/, "")
      moveDescription = moveReadValues[1]
      moveId = highestMoveId + index + 1
      moveDescriptor = moveName.downcase.gsub(" ", "-")
      moveTypeId = oldTypes[moveReadValues[2].downcase.gsub(" ", "-")][0]
      moveDamageClassId = oldMoveDamageTypes[moveReadValues[3].downcase.gsub(" ", "-")][0]
      # Need to do these at some point.
      #movePower = moveReadValues[4].split(" ")[0]
      #moveAccuracy = moveReadValues[5].split("%")[0]
      #movePP = moveReadValues[6].split(" ")[0]
      #movePriority = "0"
      #moveTarget = ""

      if (oldMoves[moveDescriptor] == nil)
        oldMoves[moveDescriptor] = [moveId, moveDescriptor, 7, moveTypeId, nil, nil, nil, nil, nil, moveDamageClassId, nil, nil, nil, nil, nil]
        newMovesInsertFile.puts("INSERT INTO \"moves\" VALUES(#{moveId},'#{moveDescriptor}',7,#{moveTypeId},NULL,NULL,NULL,NULL,NULL,#{moveDamageClassId},NULL,NULL,NULL,NULL,NULL);")
        newMovesInsertFile.puts("INSERT INTO \"move_names\" VALUES(#{moveId}, 9, \"#{moveName}\");")
        newMovesInsertFile.puts("INSERT INTO \"move_flavor_text\" VALUES(#{moveId}, 7, 9, \"#{moveDescription}\");")
      end
    end
  end
rescue => err
  puts "Exception in reading Moves file: #{err}"
  err
end


newMovesInsertFile.close



puts "Reading in the Pokemon Moves, updating the Pokemon and Moves as needed."
newPokemonInsertFile = File.new(newDatabaseFolder + "\\" + newPokemonPath, "w")

pokemonSpecies = Hash.new()

begin
  file = File.read(pokemonMovesPath).split("\n\n")
  file.each_with_index do |pokemon, index|
    name = pokemon.split("\n", 2)[0].gsub(/[^a-zA-Z\s\-]/, "")
    pokemonInfoAndMoves = pokemon.split("\n-", 2)
    if (pokemonInfoAndMoves.length != 2)
      FileUtils.mkdir_p errorPokemonFolder
      File.open(errorPokemonFolder + "\\" + index.to_s + " " + name + ".txt", "w") { |file| file.write("Issues with splitting the moves by the -.\n" + pokemonInfoAndMoves.to_s) }
    else
      pokemonInfoAndMoves[1] = pokemonInfoAndMoves[1].insert(0, '-')

      # Pokemon Info
      pokemonInfo = pokemonInfoAndMoves[0].split("\n")
      if (pokemonInfo.size < 10)
        FileUtils.mkdir_p errorPokemonFolder
        File.open(errorPokemonFolder + "\\" + index.to_s + " " + name + ".txt", "w") { |file| file.write("This Pokemon does not have enough data.\n" + pokemonInfoAndMoves.to_s) }
      else
        stats = pokemonInfo[1].split("/")

        types = pokemonInfo[2].split("/")
        typeIds = Hash.new()
        typeIds[0] = oldTypes[types[0].downcase.gsub(" ", "-")][0]
        if (types.size == 2)
          typeIds[1] = oldTypes[types[1].downcase.gsub(" ", "-")][0]
        end

        normalAbilities = pokemonInfo[3].split("/")
        normalAbilityIds = Hash.new()
        normalAbilityIds[0] = oldAbilities[normalAbilities[0].downcase.gsub(" ", "-")][0]
        if (normalAbilities.size == 2)
          normalAbilityIds[1] = oldAbilities[normalAbilities[1].downcase.gsub(" ", "-")][0]
        end

        specialAbilityId = "-1"
        if (oldAbilities[pokemonInfo[4].downcase.gsub(" ", "-")] != nil)
          specialAbilityId = oldAbilities[pokemonInfo[4].downcase.gsub(" ", "-")][0]
        end

      end

      # Pokemon Moves
      pokemonInfoAndMoves[1].split("\n") do |moveLine|
        moveData = moveLine.split("\t")
        moveName = moveData[1]
        moveLevel = ""
        moveTM = ""
        moveEgg = false
        if (moveData[0].start_with?("-"))
          moveLevel = "0"
        elif (moveData[0].start_with?("TM"))
          moveTM = moveData[0].delete "TM"
        elif (moveData[0].start_with?("Egg"))
          moveEgg = true
        else
          moveLevel = moveData[0]
        end

        # Write out the data for move info.


      end
    end
  end
rescue => err
  puts "Exception in reading Pokemon Moves file: #{err}"
  err
end

newPokemonInsertFile.close