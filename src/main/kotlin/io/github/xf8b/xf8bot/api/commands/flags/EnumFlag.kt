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

package io.github.xf8b.xf8bot.api.commands.flags

import java.util.*
import java.util.function.Function
import java.util.function.Predicate

class EnumFlag<T : Enum<T>>(
    override val shortName: String,
    override val longName: String,
    private val enumClass: Class<T>,
    override val required: Boolean = false,
    override val requiresValue: Boolean = true,
    override val defaultValue: T? = null,
    override val validityPredicate: Predicate<in String> = Predicate { input ->
        enumClass.fields.any { field ->
            field.name.equals(input, ignoreCase = true)
        }
    },
    override val parseFunction: Function<in String, out T> = Function { input ->
        @Suppress("UNCHECKED_CAST")
        enumClass.getField(input.toUpperCase(Locale.ROOT)).get(null) as T
    },
    override val errorMessageFunction: Function<in String, out String> = Function { input ->
        "Invalid value $input! The available values are: ${enumClass.fields.map { it.name.toLowerCase(Locale.ROOT) }}"
    }
) : Flag<T> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EnumFlag<*>

        if (shortName != other.shortName) return false
        if (longName != other.longName) return false
        if (enumClass != other.enumClass) return false
        if (required != other.required) return false
        if (requiresValue != other.requiresValue) return false
        if (defaultValue != other.defaultValue) return false
        if (validityPredicate != other.validityPredicate) return false
        if (parseFunction != other.parseFunction) return false
        if (errorMessageFunction != other.errorMessageFunction) return false

        return true
    }

    override fun hashCode(): Int {
        var result = shortName.hashCode()

        result = 31 * result + longName.hashCode()
        result = 31 * result + enumClass.hashCode()
        result = 31 * result + required.hashCode()
        result = 31 * result + requiresValue.hashCode()
        result = 31 * result + (defaultValue?.hashCode() ?: 0)
        result = 31 * result + validityPredicate.hashCode()
        result = 31 * result + parseFunction.hashCode()
        result = 31 * result + errorMessageFunction.hashCode()

        return result
    }

    override fun toString() = "EnumFlag(" +
            "shortName='$shortName', " +
            "longName='$longName', " +
            "enumClass=$enumClass, " +
            "required=$required, " +
            "requiresValue=$requiresValue, " +
            "defaultValue=$defaultValue, " +
            "validityPredicate=$validityPredicate, " +
            "parseFunction=$parseFunction, " +
            "errorMessageFunction=$errorMessageFunction" +
            ")"
}