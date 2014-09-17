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

import akka.actor.ActorRef
import io.gatling.core.action.Chainable
import io.gatling.core.util.TimeHelper._
import io.gatling.core.result.writer.{RequestMessage, DataWriter}
import io.gatling.core.result.message._
import io.gatling.core.session.Session

object AnyAction {
  val ctxKey = "ctx"
}

class AnyAction[C, P](val next: ActorRef, name: String, run: (C, P) => Unit, makeCtx: Option[() => C], makeParam: Option[(C) => P]) extends Chainable {
  override def execute(session: Session) {
    var start: Long = nowMillis
    var end: Long = 0
    var status: Status = OK
    var actualSession = session
    var ctx: C = null.asInstanceOf[C]
    var errorMessage: Option[String] = None
    try {
      makeCtx.map {
        makeCtxFn =>
          if (!actualSession.contains(AnyAction.ctxKey)) {
            ctx = makeCtxFn()
            actualSession = actualSession.set(AnyAction.ctxKey, ctx)
            logger.debug("ctx initialized in session userId={} : {}", session.userId, ctx.toString)
          }
          ctx = actualSession.apply(AnyAction.ctxKey).as[C]
      }
      val param: P = makeParam.map(_(ctx)).getOrElse(null.asInstanceOf[P])
      start = nowMillis
      run(ctx, param)
    } catch {
      case e: Exception =>
        errorMessage = Some(e.toString)
        logger.error(s"$name FAILED", e)
        status = KO
        actualSession.markAsFailed
    } finally {
      end = nowMillis
      DataWriter.dispatch(RequestMessage(actualSession.scenarioName, actualSession.userId, actualSession.groupHierarchy, name,
        start, start, end, end,
        status, errorMessage, Nil))
      next ! actualSession
    }
  }
}
