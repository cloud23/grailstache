databaseChangeLog = {

	changeSet(author: "efren (generated)", id: "1368705284467-1") {
		createTable(tableName: "song") {
			column(autoIncrement: "true", name: "id", type: "bigint") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "songPK")
			}

			column(name: "version", type: "bigint") {
				constraints(nullable: "false")
			}

			column(name: "artist", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "duration", type: "integer") {
				constraints(nullable: "false")
			}

			column(name: "title", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}
}
