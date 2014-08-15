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

import com.twitter.finagle.exp.mysql.Row
import com.websudos.morpheus.dsl.Table
import com.websudos.morpheus.query.{QueryAssignment, SQLBuiltQuery}

private[morpheus] trait SchemaSerializer {
  def qb: SQLBuiltQuery
}

private[morpheus] trait AbstractColumn[@specialized(Int, Double, Float, Long, Boolean, Short) T] extends SchemaSerializer {

  type Value = T

  lazy val name: String = getClass.getSimpleName.replaceAll("\\$+", "").replaceAll("(anonfun\\d+.+\\d+)|", "")

  def qb: SQLBuiltQuery

  def sqlType: String

  def table: Table[_, _]

  def toQueryString(v: T): String

  def notNull = false

}


private[morpheus] abstract class SelectColumn[T](val col: AbstractColumn[_]) {
  def apply(r: Row): T
}

private[morpheus] abstract class Column[Owner <: Table[Owner, Record], Record, T](val table: Table[Owner, Record]) extends AbstractColumn[T] {

  def optional(r: Row): Option[T]

  def apply(r: Row): T = optional(r).getOrElse(throw new Exception(s"can't extract required value for column '$name'"))
}


private[morpheus] abstract class AbstractModifyColumn[RR](col: AbstractColumn[RR]) {

  def setTo(value: RR): QueryAssignment = QueryAssignment(col.table.queryBuilder.setTo(col.name, col.toQueryString(value)))
}