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

import com.outworkers.morpheus.dsl._
import com.outworkers.morpheus.sql._
import org.scalatest.{FlatSpec, Matchers}

class UpdateQuerySerialisationTest extends FlatSpec with Matchers {

  it should "serialise a simple UPDATE query" in {
    BasicTable.update.queryString shouldEqual "UPDATE `BasicTable`;"
  }

  it should  "serialise a simple UPDATE where query" in {
    BasicTable.update.set(_.count setTo 10).where(_.name eqs "test")
      .queryString shouldEqual "UPDATE `BasicTable` SET count = 10 WHERE name = 'test';"
  }

  it should "serialise an UPDATE query with an < operator" in {
    BasicTable.update.set(_.count setTo 10).where(_.count < 5).queryString shouldEqual "UPDATE `BasicTable` SET count = 10 WHERE count < 5;"
  }

  it should "serialise an UPDATE query with an lt operator" in {
    BasicTable.update.set(_.count setTo 10).where(_.count lt 5).queryString shouldEqual "UPDATE `BasicTable` SET count = 10 WHERE count < 5;"
  }

  it should "serialise an UPDATE query with an <= operator" in {
    BasicTable.update.set(_.count setTo 10).where(_.count <= 5).queryString shouldEqual "UPDATE `BasicTable` SET count = 10 WHERE count <= 5;"
  }

  it should "serialise an UPDATE query with an lte operator" in {
    BasicTable.update.set(_.count setTo 10).where(_.count lte 5).queryString shouldEqual "UPDATE `BasicTable` SET count = 10 WHERE count <= 5;"
  }

  it should "serialise an UPDATE query with a gt operator" in {
    BasicTable.update.set(_.count setTo 10).where(_.count gt 5).queryString shouldEqual "UPDATE `BasicTable` SET count = 10 WHERE count > 5;"
  }

  it should "serialise an UPDATE query with a > operator" in {
    BasicTable.update.set(_.count setTo 10).where(_.count > 5).queryString shouldEqual "UPDATE `BasicTable` SET count = 10 WHERE count > 5;"
  }

  it should "serialise an UPDATE query with a gte operator" in {
    BasicTable.update.set(_.count setTo 10).where(_.count gte 5).queryString shouldEqual "UPDATE `BasicTable` SET count = 10 WHERE count >= 5;"
  }

  it should "serialise an UPDATE query with a >= operator" in {
    BasicTable.update.set(_.count setTo 10).where(_.count >= 5).queryString shouldEqual "UPDATE `BasicTable` SET count = 10 WHERE count >= 5;"
  }

  it should  "not allow specifying the WHERE part before the SET in an UPDATE query" in {
    """BasicTable.update.where(_.name eqs 5).queryString""" shouldNot compile
  }

  it should "serialise a simple assignments query" in {
    BasicTable.update.set(_.name setTo "test").queryString shouldEqual "UPDATE `BasicTable` SET name = 'test';"
  }

  it should "serialise a simple assignments query with a single where clause" in {
    BasicTable.update
      .set(_.name setTo "test")
      .where(_.count eqs 15)
      .queryString shouldEqual "UPDATE `BasicTable` SET name = 'test' WHERE count = 15;"
  }

  it should "serialise a multiple assignments query with a single where clause" in {
    BasicTable.update
      .set(_.name setTo "test2")
      .andSet(_.count setTo 15)
      .where(_.name eqs "test")
      .queryString shouldEqual "UPDATE `BasicTable` SET name = 'test2', count = 15 WHERE name = 'test';"
  }

  it should "serialise a multiple assignments query with a multiple where clause" in {
    BasicTable.update
      .set(_.name setTo "test2")
      .andSet(_.count setTo 15)
      .where(_.name eqs "test")
      .and(_.count eqs 10)
      .queryString shouldEqual "UPDATE `BasicTable` SET name = 'test2', count = 15 WHERE name = 'test' AND count = 10;"
  }


  it should  "serialise a simple UPDATE where-and query" in {
    BasicTable.update
      .set(_.count setTo 10)
      .where(_.name eqs "test")
      .and(_.count eqs 5).queryString shouldEqual "UPDATE `BasicTable` SET count = 10 WHERE name = 'test' AND count = 5;"
  }

  it should "serialise a conditional UPDATE clause with an OR operator" in {
    BasicTable.update
      .set(_.count setTo 10)
      .where(_.name eqs "test")
      .and(t => { (t.count eqs 5) or (t.name eqs "test") })
      .queryString shouldEqual "UPDATE `BasicTable` SET count = 10 WHERE name = 'test' AND (count = 5 OR name = 'test');"
  }

  it should "serialise a conditional UPDATE clause with an a double WHERE-OR operator" in {
    BasicTable.update
      .set(_.count setTo 10)
      .where(t => { (t.count eqs 15) or (t.name eqs "test5") })
      .and(t => { (t.count eqs 5) or (t.name eqs "test") })
      .queryString shouldEqual "UPDATE `BasicTable` SET count = 10 WHERE (count = 15 OR name = 'test5') AND (count = 5 OR name = 'test');"
  }

}
