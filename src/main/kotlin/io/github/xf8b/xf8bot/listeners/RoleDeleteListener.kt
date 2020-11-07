/*
 * Copyright (c) 2020 xf8b.
 *
 * This file is part of xf8bot.
 *
 * xf8bot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * xf8bot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with xf8bot.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.xf8b.xf8bot.listeners

import discord4j.core.event.domain.role.RoleDeleteEvent
import io.github.xf8b.xf8bot.database.BotMongoDatabase
import io.github.xf8b.xf8bot.database.actions.RemoveAdministratorRoleAction
import io.github.xf8b.xf8bot.util.toMono
import reactor.core.publisher.Mono

class RoleDeleteListener(
    private val botMongoDatabase: BotMongoDatabase
) : EventListener<RoleDeleteEvent> {
    override fun onEventFired(event: RoleDeleteEvent): Mono<RoleDeleteEvent> {
        botMongoDatabase.execute(
            RemoveAdministratorRoleAction(
                event.guildId,
                event.roleId
            )
        )
        return event.toMono()
    }
}