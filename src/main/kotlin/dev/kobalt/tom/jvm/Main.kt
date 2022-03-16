/*
 * dev.kobalt.tom
 * Copyright (C) 2022 Tom.K
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.kobalt.tom.jvm

import dev.kobalt.tom.jvm.blog.BlogTable
import dev.kobalt.tom.jvm.config.ConfigTable
import dev.kobalt.tom.jvm.database.DatabaseRepository
import dev.kobalt.tom.jvm.extension.transaction
import dev.kobalt.tom.jvm.link.LinkTable
import dev.kobalt.tom.jvm.project.ProjectTable
import dev.kobalt.tom.jvm.stuff.StuffTable
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import org.jetbrains.exposed.sql.SchemaUtils
import kotlin.concurrent.thread

fun main(args: Array<String>) {
    val parser = ArgParser("tom")
    val dbServerPort by parser.option(ArgType.Int, "dbServerPort", null, null)
    parser.parse(args)
    dbServerPort?.let { port ->
        DatabaseRepository.apply {
            this.dbServerPort = port
            this.main.transaction {
                SchemaUtils.createMissingTablesAndColumns(
                    BlogTable,
                    ConfigTable,
                    LinkTable,
                    ProjectTable,
                    StuffTable
                )
            }
            this.server.start()
        }
        Runtime.getRuntime().addShutdownHook(thread(start = false) {
            DatabaseRepository.server.shutdown()
        })
    }
}
