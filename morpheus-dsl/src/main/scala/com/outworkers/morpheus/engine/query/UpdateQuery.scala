/*
 * Copyright 2013 - 2017 Outworkers Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.outworkers.morpheus.engine.query

import com.outworkers.morpheus.Row
import com.outworkers.morpheus.sql.DefaultRow
import com.outworkers.morpheus.builder.{AbstractSQLSyntax, AbstractSyntaxBlock, DefaultSQLSyntax, SQLBuiltQuery}
import com.outworkers.morpheus.dsl.BaseTable
import shapeless.{HList, HNil}

import scala.annotation.implicitNotFound

private[morpheus] class RootUpdateSyntaxBlock(query: String, tableName: String) extends AbstractSyntaxBlock {

  protected[this] val qb = SQLBuiltQuery(query)

  def all: SQLBuiltQuery = {
    qb.pad.appendEscape(tableName)
  }

  def syntax: AbstractSQLSyntax = DefaultSQLSyntax
}


/**
 * This is the implementation of a root UPDATE query, a wrapper around an abstract syntax block.
 *
 * This is used as the entry point to an SQL query, and it requires the user to provide "one more method" to fully specify a SELECT query.
 * The implicit conversion from a RootSelectQuery to a SelectQuery will automatically pick the "all" strategy below.
 *
 * @param table The table owning the record.
 * @param st The Abstract syntax block describing the possible decisions.
 * @param rowFunc The function used to map a result to a type-safe record.
 * @tparam T The type of the owning table.
 * @tparam R The type of the record.
 */
private[morpheus] class RootUpdateQuery[T <: BaseTable[T, _, TableRow], R, TableRow <: Row](
  val table: T,
  val st: RootUpdateSyntaxBlock, val rowFunc: TableRow => R
) {

  protected[this] type BaseUpdateQuery = UpdateQuery[
    T,
    R,
    TableRow,
    Ungroupped,
    Unordered,
    Unlimited,
    Unchainned,
    AssignUnchainned,
    HNil
  ]

  private[morpheus] def all: BaseUpdateQuery = {
    new UpdateQuery(table, st.all, rowFunc)
  }
}

private[morpheus] class DefaultRootUpdateQuery[
  T <: BaseTable[T, R, DefaultRow],
  R
](
  table: T,
  st: RootUpdateSyntaxBlock,
  rowFunc: DefaultRow => R
) extends RootUpdateQuery[T, R, DefaultRow](table, st, rowFunc) {}

trait AssignBind
sealed abstract class AssignChainned extends AssignBind
sealed abstract class AssignUnchainned extends AssignBind


/**
 * This bit of magic allows all extending sub-classes to implement the "set" and "and" SQL clauses with all the necessary operators,
 * in a type safe way. By providing the third type argument and a custom way to subclass with the predetermined set of arguments,
 * all DSL representations of an UPDATE query can use the implementation without violating DRY.
 *
 * @tparam T The type of the table owning the record.
 * @tparam R The type of the record held in the table.
 */
class UpdateQuery[T <: BaseTable[T, _, TableRow],
  R,
  TableRow <: Row,
  Group <: GroupBind,
  Order <: OrderBind,
  Limit <: LimitBind,
  Chain <: ChainBind,
  AssignChain <: AssignBind,
  Status <: HList
](table: T,
  init: SQLBuiltQuery,
  rowFunc: TableRow => R
) extends Query[T, R, TableRow, Group, Order, Limit, Chain, AssignChain, Status](table, init, rowFunc) {

  protected[this] type QueryType[
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    S <: ChainBind,
    C <: AssignBind,
    P <: HList
  ] = UpdateQuery[T, R, TableRow, G, O, L, S, C, P]

  override protected[this] def create[
    G <: GroupBind,
    O <: OrderBind,
    L <: LimitBind,
    S <: ChainBind,
    C <: AssignBind,
    P <: HList
  ](t: T, q: SQLBuiltQuery, r: TableRow => R): QueryType[G, O, L, S, C, P] = {
    new UpdateQuery(t, q, r)
  }

  @implicitNotFound("You cannot use two where clauses on a single query")
  def where(condition: T => QueryCondition)(
    implicit ev: Chain =:= Unchainned
  ): QueryType[Group, Order, Limit, Chainned, AssignChain,
    Status] = {
    new UpdateQuery(table, table.queryBuilder.where(query, condition(table).clause), rowFunc)
  }

  @implicitNotFound("You cannot use two where clauses on a single query")
  def where(condition: QueryCondition)(
    implicit ev: Chain =:= Unchainned
  ): QueryType[Group, Order, Limit, Chainned, AssignChain, Status] = {
    new UpdateQuery(table, table.queryBuilder.where(query, condition.clause), rowFunc)
  }

  @implicitNotFound("You can't use 2 SET parts on a single UPDATE query")
  def set(condition: T => QueryAssignment)(
    implicit ev: AssignChain =:= AssignUnchainned,
    ev1: Status =:= HNil
  ): QueryType[Group, Order, Limit, Chain, AssignChainned, Status] = {
    new UpdateQuery(
      table,
      table.queryBuilder.set(query, condition(table).clause),
      rowFunc
    )
  }

  @implicitNotFound("""You need to use the "set" method before using the "and"""")
  def andSet(condition: T => QueryAssignment, signChange: Int = 0)(
    implicit ev: AssignChain =:= AssignChainned
  ): QueryType[Group, Order, Limit, Chain, AssignChainned, Status] = {
    new UpdateQuery(
      table,
      table.queryBuilder.andSet(query, condition(table).clause),
      rowFunc
    )
  }

  @implicitNotFound("You need to use the where method first")
  def and(condition: T => QueryCondition)(
    implicit ev: Chain =:= Chainned
  ): QueryType[Group, Order, Limit, Chain, AssignChainned, Status]  = {
    new UpdateQuery(table, table.queryBuilder.and(query, condition(table).clause), rowFunc)
  }

  @implicitNotFound("You need to use the where method first")
  def and(condition: QueryCondition)(
    implicit ev: Chain =:= Chainned
  ): QueryType[Group, Order, Limit, Chain, AssignChainned, Status]  = {
    new UpdateQuery(table, table.queryBuilder.and(query, condition.clause), rowFunc)
  }

  override protected[morpheus] def query: SQLBuiltQuery = init
}
