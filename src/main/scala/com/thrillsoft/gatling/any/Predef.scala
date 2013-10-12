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

object Predef {
  def anyCtxParam[C, P](name: String, run: (C, P) => Unit, makeCtx: () => C = null, makeParam: (C) => P = null) = AnyActionBuilder.anyCtxParam(name, run, makeCtx, makeParam)

  def anyParam[P](name: String, run: (P) => Unit, makeParam: () => P = null) = AnyActionBuilder.anyParam(name, run, makeParam)

  def anyCtx[C](name: String, run: (C) => Unit, makeCtx: () => C = null) = AnyActionBuilder.anyCtx(name, run, makeCtx)

  def any(name: String, run: () => Unit) = AnyActionBuilder.any(name, run)

}
