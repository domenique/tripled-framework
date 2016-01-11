/*
 * Copyright 2016 TripleD framework.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.tripledframework.eventbus.internal.infrastructure.unitofwork;

import eu.tripledframework.eventbus.internal.domain.UnitOfWork;

public final class UnitOfWorkManager {

  private static final ThreadLocal<UnitOfWork> holder = new ThreadLocal<>();

  private UnitOfWorkManager() {
    // cannot be instantiated.
  }

  public static UnitOfWork get() {
    return holder.get();
  }

  public static void clear() {
    holder.remove();
  }

  public static void store(UnitOfWork unitOfWork) {
    holder.set(unitOfWork);
  }

  public static boolean isRunning() {
    return holder.get() != null && holder.get().isRunning();
  }
}
