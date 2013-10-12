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
import io.gatling.core.result.writer.DataWriter
import io.gatling.core.result.message._
import io.gatling.core.session.Session
import io.gatling.core.result.message.RequestMessage

class AnyAction[C, P](val next: ActorRef, name: String, run: (C, P) => Unit, makeCtx: () => C = null, makeParam: (C) => P = null) extends Chainable {
  val ctxKey = "ctx"

  def execute(session: Session) {
    var start: Long = 0
    var end: Long = 0
    var status: Status = OK
    var actualSession = session
    var ctx: C = null.asInstanceOf[C];

    var errorMessage: Option[String] = None
    try {
      if (makeCtx != null) {
        if (!actualSession.contains(ctxKey))
          actualSession = actualSession.set(ctxKey, makeCtx())
        ctx = actualSession.apply(ctxKey).as[C]
      }
      var param: P = null.asInstanceOf[P];
      if (makeParam != null)
        param = makeParam(ctx)
      start = nowMillis
      run(ctx, param)
    } catch {
      case e: Exception =>
        errorMessage = Some(e.toString)
        logger.error(name + " FAILED", e)
        status = KO
        actualSession.markAsFailed
    } finally {
      end = nowMillis
      DataWriter.tell(RequestMessage(actualSession.scenarioName, actualSession.userId, actualSession.groupStack, name,
        start, start, end, end,
        status, errorMessage, Nil))
      next ! actualSession
    }
  }
}
