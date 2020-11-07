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

package io.github.xf8b.xf8bot.commands.botadministrator

import discord4j.rest.util.Color
import io.github.xf8b.xf8bot.api.commands.AbstractCommand
import io.github.xf8b.xf8bot.api.commands.CommandFiredContext
import io.github.xf8b.xf8bot.util.setTimestampToNow
import io.github.xf8b.xf8bot.util.toSingletonImmutableList
import org.apache.commons.lang3.time.DurationFormatUtils
import reactor.core.publisher.Mono
import java.lang.management.ManagementFactory
import java.lang.management.OperatingSystemMXBean

class HostInformationCommand : AbstractCommand(
    name = "\${prefix}hostinformation",
    description = "Gets information about the host.",
    commandType = CommandType.BOT_ADMINISTRATOR,
    aliases = "\${prefix}hostinfo".toSingletonImmutableList(),
    isBotAdministratorOnly = true
) {
    override fun onCommandFired(context: CommandFiredContext): Mono<Void> {
        val operatingSystemMXBean: OperatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean()
        val runtimeMXBean = ManagementFactory.getRuntimeMXBean()
        val memoryMXBean = ManagementFactory.getMemoryMXBean()
        val threadMXBean = ManagementFactory.getThreadMXBean()
        val arch = operatingSystemMXBean.arch
        val os = operatingSystemMXBean.name
        val osVersion = operatingSystemMXBean.version
        val availableProcessors = operatingSystemMXBean.availableProcessors
        val uptime = DurationFormatUtils.formatDurationHMS(runtimeMXBean.uptime)
        val jvmVendor = runtimeMXBean.vmVendor
        val jvmVersion = runtimeMXBean.vmVersion
        val jvmSpecName = runtimeMXBean.specName
        val jvmSpecVendor = runtimeMXBean.specVendor
        val jvmSpecVersion = runtimeMXBean.specVersion
        val heapMemoryUsage = memoryMXBean.heapMemoryUsage
        val nonHeapMemoryUsage = memoryMXBean.nonHeapMemoryUsage
        val threadCount = threadMXBean.threadCount

        return context.channel.flatMap { channel ->
            channel.createEmbed { spec ->
                spec.setTitle("Host Information")
                    .setDescription("Information about the computer that xf8bot is currently running on.")
                    .addField("OS", os, true)
                    .addField("OS Version", osVersion, true)
                    .addField("Arch", arch, true)
                    .addField("Uptime", uptime, false)
                    .addField("Available Processors", availableProcessors.toString(), false)
                    .addField("JVM Vendor", jvmVendor, true)
                    .addField("JVM Version", jvmVersion, true)
                    .addField("JVM Spec", "$jvmSpecName version $jvmSpecVersion by $jvmSpecVendor", false)
                    .addField("Thread Count", threadCount.toString(), false)
                    .addField(
                        "Heap Memory Usage",
                        "${heapMemoryUsage.used / (1024 * 1024)}MB used, ${
                            heapMemoryUsage.max.let {
                                if (it == -1L) "no maximum"
                                else "${it / (1024 * 1024)}MB maximum"
                            }
                        }",
                        true
                    )
                    .addField(
                        "Non Heap Memory Usage",
                        "${nonHeapMemoryUsage.used / (1024 * 1024)}MB used, ${
                            nonHeapMemoryUsage.max.let {
                                if (it == -1L) "no maximum"
                                else "${it / (1024 * 1024)}MB maximum"
                            }
                        }",
                        true
                    )
                    .setFooter(
                        """
                        i took some of the code for this from stack overflow
                        if you are a fellow java/kotlin programmer see https://stackoverflow.com/a/15733233
                        """.trimIndent(),
                        null
                    )
                    .setUrl("https://stackoverflow.com/a/15733233")
                    .setTimestampToNow()
                    .setColor(Color.BLUE)
            }
        }.then()
    }
}