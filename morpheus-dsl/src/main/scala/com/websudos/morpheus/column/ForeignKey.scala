/*
 *
 *  * Copyright 2014 websudos ltd.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.websudos.morpheus.column

import com.websudos.morpheus.dsl.Table
import com.websudos.morpheus.query.{SQLBuiltQuery, DefaultSQLSyntax}

/**
 * This is a simple mechanism of providing a pre-defined set of FOREIGN KEY constraints.
 * The available values currently follow the MySQL syntax definition available {@link http://dev.mysql.com/doc/refman/5.6/en/create-table-foreign-keys.html}.
 *
 * What follows below is actually the implementation of an Enumeration, however `scala.util.Enum` is not proffered in situations like this,
 * or ever for that matter. The API is poor and weird compared to using a simple set of `case object` definitions.
 * @param value String
 */
sealed class ForeignKeyConstraint(val value: String)


/**
 * This trait encloses all default variations of a FOREIGN KEY constraint as per the MySQL documentation.
 * It is a trait so that the default set of MySQL imports.
 *
 */
private[morpheus] trait DefaultForeignKeyConstraints {
  case object Restrict extends ForeignKeyConstraint(DefaultSQLSyntax.restrict)
  case object Cascade extends ForeignKeyConstraint(DefaultSQLSyntax.cascade)
  case object SetNull extends ForeignKeyConstraint(DefaultSQLSyntax.setNull)
  case object NoAction extends ForeignKeyConstraint(DefaultSQLSyntax.noAction)
}

private[morpheus] object DefaultForeignKeyConstraints extends DefaultForeignKeyConstraints


/**
 * This is the implementation of a ForeignKey column. This is not a value column, therefore the `apply` method is overridden to throw an exception. It is used
 * at reflection time and schema generation time to correctly create the schema for a given table.
 *
 * The peculiar type signature is very simple really. It's using the all known and loved shapeless type inequality constraint,
 * essentially forcing DSL users to specify 2 different owning tables for the origin and reference of a FOREIGN_KEY. It's a way of making FOREIGN_KEY
 * indexes impossible between a table and itself and it's also nicely confining all columns to belong to the same reference table.
 *
 * By default the action performed is DefaultForeignKeyConstraints.NoAction, with respect to the MySQL behaviour.
 *
 * @param origin The table owning the foreign key.
 * @param columns The columns this foreign key references.
 * @tparam T The type of the owner table.
 * @tparam R The type of the record.
 */
abstract class ForeignKey[T <: Table[T, R], R, T1 <: Table[T1, _]](origin: T)(columns: Column[T1, _, _]*)
  extends AbstractColumn[String] with IndexColumn[String] {

  def qb: SQLBuiltQuery = {
    val default = SQLBuiltQuery(DefaultSQLSyntax.foreignKey)
      .forcePad.append(DefaultSQLSyntax.`(`)
      .append(columns.map(col => {s"${col.table.tableName}_${col.name}"}).mkString(", "))
      .append(DefaultSQLSyntax.`)`)
      .forcePad.append(DefaultSQLSyntax.references)
      .forcePad.append(table.tableName)
      .append(DefaultSQLSyntax.`(`)
      .append(columns.map(_.name).mkString(", "))
      .append(DefaultSQLSyntax.`)`)

    val stage2 = if (onUpdate != DefaultForeignKeyConstraints.NoAction) {
      default.forcePad.append(DefaultSQLSyntax.onUpdate).forcePad.append(onUpdate.value)
    } else {
      default
    }

    val stage3 = if (onDelete != DefaultForeignKeyConstraints.NoAction) {
      stage2.forcePad.append(DefaultSQLSyntax.onDelete).forcePad.append(onDelete.value)
    } else {
      stage2
    }
    stage3
  }

  override def sqlType: String = "string"

  override def toQueryString(v: String): String = v

  override def table: Table[_, _] = columns.head.table

  def onUpdate: ForeignKeyConstraint = DefaultForeignKeyConstraints.NoAction

  def onDelete: ForeignKeyConstraint = DefaultForeignKeyConstraints.NoAction

}
