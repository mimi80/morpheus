/*
 * Copyright 2014 websudos ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.websudos.morpheus.query

import org.scalatest.{Matchers, FlatSpec}
import com.websudos.morpheus.dsl.BasicTable
import com.websudos.morpheus.mysql.Imports._

class InFlightOperatorsTest extends FlatSpec with Matchers {
  it should "serialise an inFlight usage of a NOT EXISTS operator" in {
    notExists(BasicTable.select.where(_.count eqs 10)).clause.queryString shouldEqual "NOT EXISTS (SELECT * FROM BasicTable WHERE count = 10)"

  }

  it should "serialise a nested NOT EXISTS sub-query" in {
    BasicTable.select
      .where(notExists(BasicTable.select.where(_.count eqs 10)))
      .queryString shouldEqual "SELECT * FROM BasicTable WHERE NOT EXISTS (SELECT * FROM BasicTable WHERE count = 10)"
  }
}
