require 'fileutils'

oldDatabasePath = "database_creation.sql"
updatedDatabasePath = "veekun-pokedex.sql"
newDatabasePath = "update_v2.sql"

newDatabaseFile = File.new(newDatabasePath, "w")

# Load up the old database's information.
puts "Beginning to read in old database."
oldDatabase = Hash.new()
begin
  # file = File.new(oldDatabasePath, "r")
  file = File.read(oldDatabasePath).split(";").map(&:strip).map {|x| x + ";"}
  currentTable = nil
  currentTableName = nil
  file.each do |line|
    if (line.start_with?"DROP TABLE IF EXISTS")
      if (currentTable != nil && currentTableName != nil)
        oldDatabase[currentTableName] = currentTable
      end
    elsif (line.start_with?"CREATE TABLE")
      currentTableName = line[/#{Regexp.escape("CREATE TABLE")}(.*?)#{Regexp.escape("(")}/m, 1].tr_s("\"", "").strip
      currentTable = Hash.new()
    elsif (line.start_with?"INSERT INTO")
      currentTable[line] = line
    end
    
  end  
  # file.close
rescue => err
  puts "Exception in reading old database: #{err}"
  err
end

puts "Finished reading old database."

# Go through the new database.
# If the table is still there, just add the new lines that don't exit.
# If the table doesn't exist, put it in a new list to add.
puts "Now reading in new database and making appropriate changes."
begin
  #file = File.new(updatedDatabasePath, "r")
  file = File.read(updatedDatabasePath).split(";").map(&:strip).map {|x| x + ";"}
  currentTable = nil
  currentTableName = nil
  file.each do |line|
    if (line.start_with?"DROP TABLE IF EXISTS")
      if (currentTableName != nil && currentTable != nil)
        # Check the currentTable, given that it is called currentTableName
        if (oldDatabase.has_key?(currentTableName))
            currentTable.delete(currentTableName)
            currentTable.each do |key, value|
              if (!oldDatabase[currentTableName].has_key?(key))
                newDatabaseFile.puts(key)
              end
            end
        else
          # The table doesn't exist, so create a file to create it.
          FileUtils.mkdir_p 'newTables'
          File.open("newTables\\" + currentTableName + ".sql", "w") do |newTableFile|
            # newTableFile.write(currentTable.map{|k,v| "#{v}"})
            currentTable.each {|k,v| newTableFile.puts("#{v}")}
          end
          puts "Created file " + currentTableName
        end
      end
      currentTableName = nil
      currentTable = nil
    elsif (line.start_with?"CREATE TABLE")
      currentTableName = line[/#{Regexp.escape("CREATE TABLE")}(.*?)#{Regexp.escape("(")}/m, 1].tr_s("\"", "").strip
      currentTable = Hash.new()
      currentTable[currentTableName] = line
    elsif (line.start_with?"INSERT INTO")
      currentTable[line] = line
    end
  end
  # file.close
rescue => err
  puts "Exception in reading new database: #{err}"
  err
end
newDatabaseFile.close
puts "Finished."