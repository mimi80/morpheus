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

package com.websudos.morpheus.keys

import com.websudos.morpheus.column.AbstractColumn
import com.websudos.morpheus.query.{DefaultSQLSyntax, SQLBuiltQuery}

private[phantom] trait Key[ValueType, KeyType <: Key[ValueType, KeyType]] {
  self: AbstractColumn[ValueType] =>

  protected[this] def qb: SQLBuiltQuery
  def keyToQueryString: String
}

trait PrimaryKey[ValueType] extends Key[ValueType, PrimaryKey[ValueType]] {
  self: AbstractColumn[ValueType] =>

  protected[this] def qb: SQLBuiltQuery = SQLBuiltQuery(DefaultSQLSyntax.primaryKey)

}

trait ForeignKey[ValueType] extends Key[ValueType, PrimaryKey[ValueType]] {
  self: AbstractColumn[ValueType] =>

  protected[this] def qb: SQLBuiltQuery = SQLBuiltQuery(DefaultSQLSyntax.foreignKey)
}


trait UniqueKey[ValueType] extends Key[ValueType, PrimaryKey[ValueType]] {
  self: AbstractColumn[ValueType] =>

  protected[this] def qb: SQLBuiltQuery = SQLBuiltQuery(DefaultSQLSyntax.uniqueKey)
}

abstract class Index(column: AbstractColumn[_]*) extends Key[_, Index] {
  protected[this] def qb: SQLBuiltQuery = SQLBuiltQuery(DefaultSQLSyntax.index)
}
