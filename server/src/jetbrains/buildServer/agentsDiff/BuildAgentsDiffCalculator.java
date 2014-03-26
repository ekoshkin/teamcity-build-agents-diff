/*
 * Copyright 2000-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.agentsDiff;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import jetbrains.buildServer.serverSide.BuildAgentEx;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author Evgeniy.Koshkin
 */
public class BuildAgentsDiffCalculator {
  public BuildAgentsDiffBean calculateDiff(BuildAgentEx agentA, BuildAgentEx agentB) {
    final Map<String, String> configParamsA = agentA.getAvailableParameters();
    final Map<String, String> configParamsB = agentB.getAvailableParameters();

    final Collection<BuildAgentsDiffEntry> entries = new LinkedList<BuildAgentsDiffEntry>();

    final MapDifference<String, String> mapDifference = Maps.difference(configParamsA, configParamsB);
    if(!mapDifference.areEqual()){
      final Map<String, MapDifference.ValueDifference<String>> stringValueDifferenceMap = mapDifference.entriesDiffering();
      for(String key : stringValueDifferenceMap.keySet()){
        final MapDifference.ValueDifference<String> stringValueDifference = stringValueDifferenceMap.get(key);
        entries.add(new BuildAgentsDiffEntry(BuildAgentsDiffEntryType.PARAMETER_VALUE, key, stringValueDifference.leftValue(), stringValueDifference.rightValue()));
      }

      Map<String, String> map = mapDifference.entriesOnlyOnLeft();
      for(String key : map.keySet()){
        entries.add(new BuildAgentsDiffEntry(BuildAgentsDiffEntryType.PARAMETER_NAME, key, map.get(key), null));
      }

      map = mapDifference.entriesOnlyOnRight();
      for(String key : map.keySet()){
        entries.add(new BuildAgentsDiffEntry(BuildAgentsDiffEntryType.PARAMETER_NAME, key, null, map.get(key)));
      }
    }
    return new BuildAgentsDiffBean(entries);
  }
}
