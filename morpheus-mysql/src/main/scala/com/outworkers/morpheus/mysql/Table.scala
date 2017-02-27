/*
 * Copyright 2013 - 2017 Outworkers, Limited.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.outworkers
package morpheus
package mysql

import com.outworkers.morpheus.dsl.{BaseTable, SelectTable}
import com.outworkers.morpheus.mysql.query._

abstract class Table[Owner <: BaseTable[Owner, Record, mysql.Row], Record]
  extends BaseTable[Owner, Record, Row]
  with SelectTable[Owner, Record, Row, RootSelectQuery, SelectSyntaxBlock] {

  val queryBuilder = QueryBuilder

  val syntax = Syntax

  protected[this] def createRootSelect[A <: BaseTable[A, _, Row], B](
    table: A,
    block: SelectSyntaxBlock,
    rowFunc: Row => B
  ): RootSelectQuery[A, B] = {
    new RootSelectQuery[A, B](table, block, rowFunc)
  }

  protected[this] def selectBlock(query: String, tableName: String, cols: List[String] = List("*")): SelectSyntaxBlock = {
    new SelectSyntaxBlock(query, tableName, cols)
  }

  def update: RootUpdateQuery[Owner, Record] = new RootUpdateQuery(
    this.asInstanceOf[Owner],
    UpdateSyntaxBlock(syntax.update, tableName),
    fromRow
  )

  def delete: RootDeleteQuery[Owner, Record] = new RootDeleteQuery(
    this.asInstanceOf[Owner],
    DeleteSyntaxBlock(syntax.delete, tableName),
    fromRow
  )

  def insert: RootInsertQuery[Owner, Record] = new RootInsertQuery(
    this.asInstanceOf[Owner],
    new InsertSyntaxBlock(syntax.insert, tableName),
    fromRow
  )

  def create: RootCreateQuery[Owner, Record] = new RootCreateQuery(
    this.asInstanceOf[Owner],
    new CreateSyntaxBlock(syntax.create, tableName),
    fromRow
  )

}