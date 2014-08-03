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

package com.websudos.morpheus.query

import org.scalatest.{Matchers, FlatSpec}

import com.websudos.morpheus.dsl.BasicTable
import com.websudos.morpheus.mysql.Imports._

class SelectQuerySerialisationTest extends FlatSpec with Matchers {

  it should "serialise a simple SELECT ALL query" in {
    BasicTable.select.queryString shouldEqual "SELECT * FROM BasicTable"
  }

  it should  "serialise a simple select all where query" in {
    BasicTable.select.where(_.name eqs "test").queryString shouldEqual "SELECT * FROM BasicTable WHERE name = 'test'"
  }

  it should "serialise a select query with an < operator" in {
    BasicTable.select.where(_.count < 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE count < 5"
  }

  it should "serialise a select query with an lt operator" in {
    BasicTable.select.where(_.count lt 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE count < 5"
  }

  it should "serialise a select query with an <= operator" in {
    BasicTable.select.where(_.count <= 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE count <= 5"
  }

  it should "serialise a select query with an lte operator" in {
    BasicTable.select.where(_.count lte 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE count <= 5"
  }

  it should "serialise a select query with a gt operator" in {
    BasicTable.select.where(_.count gt 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE count > 5"
  }

  it should "serialise a select query with a > operator" in {
    BasicTable.select.where(_.count > 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE count > 5"
  }

  it should "serialise a select query with a gte operator" in {
    BasicTable.select.where(_.count gte 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE count >= 5"
  }

  it should "serialise a select query with a >= operator" in {
    BasicTable.select.where(_.count >= 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE count >= 5"
  }

  it should  "serialise a simple select all where-and query" in {
    BasicTable.select.where(_.name eqs "test").and(_.count eqs 5).queryString shouldEqual "SELECT * FROM BasicTable WHERE name = 'test' AND count = 5"
  }

  it should "serialise a 1 column partial select query" in {
    BasicTable.select(_.name).queryString shouldEqual "SELECT name FROM BasicTable"
  }

  it should "serialise a 1 column partial select query with an where clause" in {
    BasicTable.select(_.name).where(_.count eqs 5).queryString shouldEqual "SELECT name FROM BasicTable WHERE count = 5"
  }

  it should "serialise a 1 column partial select query with an or-where clause" in {
    BasicTable.select(_.name).where(t => { (t.count eqs 5) or (t.count eqs 10) }).queryString shouldEqual "SELECT name FROM BasicTable WHERE (count = 5 OR " +
      "count = 10)"
  }

  it should "serialise a 1 column partial select query with a multiple or-where clause" in {
    BasicTable.select(_.name).where(t => { (t.count eqs 5) or (t.count eqs 10) or (t.count >= 15)}).queryString shouldEqual "SELECT name FROM BasicTable " +
      "WHERE (count = 5 OR count = 10 OR count >= 15)"
  }

  it should "serialise a 2 column partial select query" in {
    BasicTable.select(_.name, _.count).queryString shouldEqual "SELECT name count FROM BasicTable"
  }

  it should "serialise a 2 column partial select query with an WHERE clause" in {
    BasicTable.select(_.name, _.count).queryString shouldEqual "SELECT name count FROM BasicTable"
  }

  it should "serialise a conditional clause with an OR operator" in {
    BasicTable.select.where(_.name eqs "test").and(t => { (t.count eqs 5) or (t.name eqs "test") }).queryString shouldEqual "SELECT * FROM BasicTable WHERE name = " +
      "'test' AND (count = 5 OR name = 'test')"
  }

  it should  "not compile a select query if the value compared against doesn't match the value type of the underlying column" in {
    """BasicTable.select.where(_.name eqs 5).queryString""" shouldNot compile
  }

  it should "serialise a simple SELECT DISTINCT query" in {
    BasicTable.select.distinct.queryString shouldEqual "SELECT DISTINCT * FROM BasicTable"
  }

  it should "serialise a simple SELECT DISTINCT query with an WHERE clause" in {
    BasicTable.select.distinct.where(_.name eqs "test").queryString shouldEqual "SELECT DISTINCT * FROM BasicTable WHERE name = 'test'"
  }

  it should "serialise a partial SELECT DISTINCT query with a single column in the partial select" in {
    BasicTable.select(_.name).distinct.queryString shouldEqual "SELECT DISTINCT name FROM BasicTable"
  }

  it should "serialise a partial SELECT DISTINCT query with a single column in the partial select and a single WHERE clause" in {
    BasicTable.select(_.name).distinct.where(_.count >= 5).queryString shouldEqual "SELECT DISTINCT name FROM BasicTable WHERE count >= 5"
  }

  it should "serialise a partial SELECT DISTINCT query with multiple columns in a partial select" in {
    BasicTable.select(_.name, _.count).distinct.queryString shouldEqual "SELECT DISTINCT name, count FROM BasicTable"
  }

  it should "serialise a partial SELECT DISTINCT query with multiple columns in a partial select and a simple where clause" in {
    BasicTable.select(_.name, _.count).distinct.where(_.count <= 10).queryString shouldEqual "SELECT DISTINCT name, count FROM BasicTable WHERE count <= 10"
  }

  it should "serialise a partial SELECT DISTINCT query with multiple columns in a partial select and an where-and clause" in {
    BasicTable
      .select(_.name, _.count)
      .distinct
      .where(_.count >= 10)
      .and(_.count <= 100).queryString shouldEqual "SELECT DISTINCT name, " +
      "count FROM BasicTable WHERE count >= 10 AND count <= 100"
  }

  it should "serialise a partial SELECT DISTINCT query with multiple columns in a partial select and an where-and-or clause" in {
    BasicTable
      .select(_.name, _.count)
      .distinct
      .where(_.count >= 10)
      .and(t => { (t.count <= 100) or (t.name eqs "test")}).queryString shouldEqual "SELECT DISTINCT name, " +
      "count FROM BasicTable WHERE count >= 10 AND (count <= 100 OR name = 'test')"
  }

  it should "serialise a simple SELECT DISTINCTROW query" in {
    BasicTable.select.distinctRow.queryString shouldEqual "SELECT DISTINCTROW * FROM BasicTable"
  }

  it should "serialise a simple SELECT DISTINCTROW query with an WHERE clause" in {
    BasicTable.select.distinctRow.where(_.name eqs "test").queryString shouldEqual "SELECT DISTINCTROW * FROM BasicTable WHERE name = 'test'"
  }

  it should "serialise a partial SELECT DISTINCTROW query with a single column in the partial select" in {
    BasicTable.select(_.name).distinctRow.queryString shouldEqual "SELECT DISTINCTROW name FROM BasicTable"
  }

  it should "serialise a partial SELECT DISTINCTROW query with a single column in the partial select and a single WHERE clause" in {
    BasicTable.select(_.name).distinctRow.where(_.count >= 5).queryString shouldEqual "SELECT DISTINCTROW name FROM BasicTable WHERE count >= 5"
  }

  it should "serialise a partial SELECT DISTINCTROW query with multiple columns in a partial select" in {
    BasicTable.select(_.name, _.count).distinctRow.queryString shouldEqual "SELECT DISTINCTROW name, count FROM BasicTable"
  }

  it should "serialise a partial SELECT DISTINCTROW query with multiple columns in a partial select and a simple where clause" in {
    BasicTable.select(_.name, _.count).distinctRow.where(_.count <= 10).queryString shouldEqual "SELECT DISTINCTROW name, count FROM BasicTable WHERE count <= 10"
  }

  it should "serialise a partial SELECT DISTINCTROW query with multiple columns in a partial select and an where-and clause" in {
    BasicTable
      .select(_.name, _.count)
      .distinctRow
      .where(_.count >= 10)
      .and(_.count <= 100).queryString shouldEqual "SELECT DISTINCTROW name, count FROM BasicTable WHERE count >= 10 AND count <= 100"
  }

  it should "serialise a partial SELECT DISTINCTROW query with multiple columns in a partial select and an where-and-or clause" in {
    BasicTable
      .select(_.name, _.count)
      .distinctRow
      .where(_.count >= 10)
      .and(t => { (t.count <= 100) or (t.name eqs "test")}).queryString shouldEqual "SELECT DISTINCTROW name, " +
      "count FROM BasicTable WHERE count >= 10 AND (count <= 100 OR name = 'test')"
  }

  it should "serialise a simple in operator query for string columns" in {
    BasicTable.select
      .where(_.name in List("name1", "name2", "name3"))
      .queryString shouldEqual "SELECT * FROM BasicTable WHERE name IN ('name1', 'name2', 'name3')"
  }

  it should "serialise a simple in operator query for string columns followed by an AND-IN clause" in {
    BasicTable.select
      .where(_.name in List("name1", "name2", "name3"))
      .and(_.count in List(5, 10, 15))
      .queryString shouldEqual "SELECT * FROM BasicTable WHERE name IN ('name1', 'name2', 'name3') AND count IN (5, 10, 15)"
  }

  ignore should "serialise a in-or operator query for string columns followed by an AND-IN clause" in {
    BasicTable.select
      .where(t => { (t.name in List("name1", "name2", "name3")) or (t.name in List("name4", "name5")) })
      .and(_.count in List(5, 10, 15))
      .queryString shouldEqual "SELECT * FROM BasicTable WHERE (name IN ('name1', 'name2', 'name3') OR name IN ('name4', 'name5') AND count IN (5, 10, 15)"
  }
}
