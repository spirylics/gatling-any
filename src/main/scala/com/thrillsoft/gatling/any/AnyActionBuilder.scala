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

import io.gatling.core.action.builder.ActionBuilder
import akka.actor.{Props, ActorRef}
import io.gatling.core.config.Protocols

class AnyActionBuilder[C, P](name: String, run: (C, P) => Unit, makeCtx: Option[() => C] = None, makeParam: Option[(C) => P] = None) extends ActionBuilder {

  override def build(next: ActorRef, protocols: Protocols) =
    system.actorOf(Props(new AnyAction[C, P](next, name, run, makeCtx, makeParam)))
}
