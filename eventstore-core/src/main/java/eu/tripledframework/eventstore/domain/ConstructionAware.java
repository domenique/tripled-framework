/*
 * Copyright 2022 TripleD framework.
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
package eu.tripledframework.eventstore.domain;

/**
 * Marker interface that provides hooks into the reconstruction process to perform specific actions that the domain entity requires.
 */
public interface ConstructionAware {

  /**
   * Method which will be called after the reconstruction process. If this method is called, the object is fully reconstructed the full
   * object state is available.
   */
  void postConstruct();
}
