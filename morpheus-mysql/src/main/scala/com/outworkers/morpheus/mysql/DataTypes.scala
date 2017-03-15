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
package com.outworkers.morpheus.mysql

import com.outworkers.morpheus._

object DataTypes {
  object Real {
    val float = "FLOAT"
    val double = "DOUBLE"
    val decimal = "REAL"
  }
}

trait DataTypes {

  implicit object IntPrimitive extends DefaultIntPrimitive

  implicit object FloatPrimitive extends DefaultFloatPrimitive

  implicit object DoublePrimitive extends DefaultDoublePrimitive

  implicit object LongPrimitive extends DefaultLongPrimitive

  implicit object DatePrimitive extends DefaultDatePrimitive

  implicit object SqlDatePrimitive extends DefaultSqlDatePrimitive

  implicit object DateTimePrimitive extends DefaultDateTimePrimitive

  implicit object TimeStampPrimitive extends DefaultTimestampPrimitive

  implicit object ShortPrimitive extends DefaultShortPrimitive

  implicit object StringPrimitive extends DefaultStringPrimitive
}
