/*
 * #%L
 * gatling-any
 * %%
 * Copyright (C) 2013 Thrillsoft
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.thrillsoft.gatling.any

import com.thrillsoft.gatling.any.Predef._
import io.gatling.core.Predef._
import scala.util.Random

class AnySimulation extends Simulation {
  setUp(
    scenario("count").exec(any("count",
      () => {
        count(Int.MaxValue - 1)
      })).inject(atOnceUsers(5)),
    scenario("sort").exec(anyParam[Seq[Int]]("sort",
      (s: Seq[Int]) => {
        sort(s)
      }, () => {
        random(1000)
      })).inject(atOnceUsers(10)),
    scenario("permute").exec(anyCtx[String]("permute",
      (s: String) => {
        permute(s)
      }, () => newUser)).inject(atOnceUsers(5)),
    scenario("sum").exec(anyCtxParam[String, Seq[Int]]("sum",
      (c: String, p: Seq[Int]) => {
        sum(c, p)
      }, () => newUser
      , (c: String) => {
        random(10000)
      })).inject(atOnceUsers(20)))

  def count = (x: Int) => {
    for (a <- 0 to x by 10000)
      if (a % 1000000 == 0)
        println((a / 1000000) + " millions")
  }

  def random = (n: Int) => {
    val s: Seq[Int] = Seq.fill(n)(Random.nextInt)
    println("Random : " + s)
    s
  }

  def sort = (s: Seq[Int]) => {
    val sortedS: Seq[Int] = s.sorted
    println("Sorted : " + sortedS)
  }

  def sum = (user: String, s: Seq[Int]) => {
    val sum: Int = s.sum
    println("Sum of " + user + " : " + sum)
  }

  def permute = (s: String) => {
    val perm = s.permutations
    while (perm.hasNext)
      println(perm.next())
  }

  var i: Int = 0

  def newUser = {
    i = i + 1
    val user = "USER" + i
    println("New user : " + user)
    user
  }
}
