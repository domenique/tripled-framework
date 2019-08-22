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
package eu.tripledframework.eventbus.internal.domain;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is inspired from the java.util.concurrent.Executors.DefaultThreadFactory class.
 * <p>
 * It provides a way to define the prefix of a threadName.
 */
class NamedThreadFactory implements ThreadFactory {
  private final AtomicInteger threadNumber = new AtomicInteger(0);
  private final ThreadGroup group;
  private final String namePrefix;

  /**
   * A package protected constructor to prevent usage of this factory outside of this test package.
   *
   * @param namePrefix the prefix of the the thread that should be created.
   */
  NamedThreadFactory(String namePrefix) {
    SecurityManager s = System.getSecurityManager();
    group = (s != null) ? s.getThreadGroup() :
        Thread.currentThread().getThreadGroup();
    this.namePrefix = namePrefix;
  }

  @Override
  public Thread newThread(Runnable r) {
    Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
    if (t.isDaemon()) {
      t.setDaemon(false);
    }
    if (t.getPriority() != Thread.NORM_PRIORITY) {
      t.setPriority(Thread.NORM_PRIORITY);
    }

    return t;
  }
}
