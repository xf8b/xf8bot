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

package io.github.xf8b.xf8bot.commands

import io.github.xf8b.xf8bot.api.commands.AbstractCommand
import io.github.xf8b.xf8bot.api.commands.CommandFiredEvent
import io.github.xf8b.xf8bot.audio.GuildMusicHandler
import reactor.core.publisher.Mono

class PauseCommand : AbstractCommand(
        name = "\${prefix}pause",
        description = "Pauses the current audio playing.",
        commandType = CommandType.MUSIC
) {
    override fun onCommandFired(event: CommandFiredEvent): Mono<Void> {
        val guildId = event.guild.map { it.id }.block()!!
        val guildMusicHandler = GuildMusicHandler.getMusicHandler(
                guildId,
                event.xf8bot.audioPlayerManager,
                event.channel.block()!!
        )
        return event.client.voiceConnectionRegistry.getVoiceConnection(guildId)
                .flatMap {
                    Mono.fromRunnable<Void> {
                        guildMusicHandler.setPaused(!guildMusicHandler.isPaused())
                    }.then(event.channel.flatMap {
                        it.createMessage(if (guildMusicHandler.isPaused()) {
                            "Successfully paused the current video!"
                        } else {
                            "Successfully unpaused the current video!"
                        })
                    })
                }
                .switchIfEmpty(event.channel.flatMap { it.createMessage("I am not connected to a VC!") })
                .then()
    }
}