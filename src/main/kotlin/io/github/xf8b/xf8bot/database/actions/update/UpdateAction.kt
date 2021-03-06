/*
 * Copyright (c) 2020, 2021 xf8b.
 *
 * This file is part of xf8bot.
 *
 * xf8bot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * xf8bot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with xf8bot.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.xf8b.xf8bot.database.actions.update

import io.github.xf8b.xf8bot.database.DatabaseAction
import io.r2dbc.spi.Connection
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono

open class UpdateAction(
    override val table: String,
    private val setFields: Map<String, Any>,
    private val criteria: Map<String, Any>
) : DatabaseAction<Mono<out Void>> {
    private fun internalUpdate(
        connection: Connection,
        setFields: Map<String, Any>,
        criteria: Map<String, Any>
    ): Mono<out Void> {
        var sql = "UPDATE $table SET "
        val setFieldsIndexedParameters = mutableListOf<String>()
        val criteriaIndexedParameters = mutableListOf<String>()
        val fieldsAndCriteria = setFields + criteria

        for (i in 1..setFields.size) {
            setFieldsIndexedParameters += "${setFields.keys.toList()[i - 1]} = $$i"
        }

        for (i in setFields.size + 1..fieldsAndCriteria.size) {
            criteriaIndexedParameters += "${criteria.keys.toList()[i - (1 + setFields.size)]} = $$i"
        }

        sql += setFieldsIndexedParameters.joinToString()
        sql += " WHERE "
        sql += criteriaIndexedParameters.joinToString(separator = " AND ")

        return connection.createStatement(sql)
            .apply {
                for (i in 1..setFields.size) {
                    bind("$$i", setFields.values.toList()[i - 1])
                }

                for (i in setFields.size + 1..fieldsAndCriteria.size) {
                    bind("$$i", criteria.values.toList()[i - (1 + setFields.size)])
                }
            }
            .execute()
            .toFlux()
            .flatMap { it.rowsUpdated.toMono() }
            .then()
    }

    override fun invoke(connection: Connection) = internalUpdate(connection, setFields, criteria)

    /*
    override fun runEncrypted(connection: Connection, keySetHandle: KeysetHandle): Mono<Void> {
        val primitive: Aead = keySetHandle.getPrimitive(Aead::class.java)

        val encryptedSetFields = setFields.mapKeys {
            primitive.encrypt(it.toString().toByteArray(), null).decodeToString()
        }
        val encryptedCriteria = criteria.mapValues { (_, value) ->
            primitive.encrypt(value.toString().toByteArray(), null).decodeToString()
        }

        return update(connection, encryptedSetFields, encryptedCriteria)
    }
    */
}