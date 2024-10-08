package de.tectoast.games

import io.ktor.server.sessions.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.upsert

object DiscordAuthDB : Table("discordauth"), SessionStorage {
    val ID = varchar("id", 32)
    val VALUE = varchar("value", 256)
    override val primaryKey = PrimaryKey(ID)

    override suspend fun read(id: String): String {
        return newSuspendedTransaction(db = discordAuthDB) {
            selectAll().where { ID eq id }.firstOrNull()?.get(VALUE) ?: throw NoSuchElementException()
        }
    }

    override suspend fun write(id: String, value: String) {
        newSuspendedTransaction(db = discordAuthDB) {
            upsert {
                it[this.ID] = id
                it[this.VALUE] = value
            }
        }
    }

    override suspend fun invalidate(id: String) {
        newSuspendedTransaction(db = discordAuthDB) {
            deleteWhere { this.ID eq id }
        }
    }
}
