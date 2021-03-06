/*
 *
 *  * Licensed to the Apache Software Foundation (ASF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package org.apache.storm.sql.planner.trident;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.rel.rules.CalcMergeRule;
import org.apache.calcite.rel.rules.FilterCalcMergeRule;
import org.apache.calcite.rel.rules.FilterProjectTransposeRule;
import org.apache.calcite.rel.rules.FilterToCalcRule;
import org.apache.calcite.rel.rules.ProjectCalcMergeRule;
import org.apache.calcite.rel.rules.ProjectFilterTransposeRule;
import org.apache.calcite.rel.rules.ProjectRemoveRule;
import org.apache.calcite.rel.rules.ProjectToCalcRule;
import org.apache.calcite.rel.rules.PruneEmptyRules;
import org.apache.calcite.rel.rules.ReduceExpressionsRule;
import org.apache.calcite.rel.rules.SortRemoveRule;
import org.apache.calcite.rel.rules.UnionEliminatorRule;
import org.apache.calcite.rel.stream.StreamRules;
import org.apache.calcite.tools.RuleSet;
import org.apache.storm.sql.planner.trident.rules.TridentCalcRule;
import org.apache.storm.sql.planner.trident.rules.TridentFilterRule;
import org.apache.storm.sql.planner.trident.rules.TridentScanRule;
import org.apache.storm.sql.planner.trident.rules.TridentAggregateRule;
import org.apache.storm.sql.planner.trident.rules.TridentJoinRule;
import org.apache.storm.sql.planner.trident.rules.TridentModifyRule;
import org.apache.storm.sql.planner.trident.rules.TridentProjectRule;

import java.util.Iterator;

public class TridentStormRuleSets {
    private static final ImmutableSet<RelOptRule> calciteToStormConversionRules =
            ImmutableSet.<RelOptRule>builder().add(
                    SortRemoveRule.INSTANCE,

                    FilterToCalcRule.INSTANCE,
                    ProjectToCalcRule.INSTANCE,
                    FilterCalcMergeRule.INSTANCE,
                    ProjectCalcMergeRule.INSTANCE,
                    CalcMergeRule.INSTANCE,

                    PruneEmptyRules.FILTER_INSTANCE,
                    PruneEmptyRules.PROJECT_INSTANCE,
                    PruneEmptyRules.UNION_INSTANCE,

                    ProjectFilterTransposeRule.INSTANCE,
                    FilterProjectTransposeRule.INSTANCE,
                    ProjectRemoveRule.INSTANCE,

                    ReduceExpressionsRule.FILTER_INSTANCE,
                    ReduceExpressionsRule.PROJECT_INSTANCE,
                    ReduceExpressionsRule.CALC_INSTANCE,

                    // merge and push unions rules
                    UnionEliminatorRule.INSTANCE,

                    TridentScanRule.INSTANCE,
                    TridentFilterRule.INSTANCE,
                    TridentProjectRule.INSTANCE,
                    TridentAggregateRule.INSTANCE,
                    TridentJoinRule.INSTANCE,
                    TridentModifyRule.INSTANCE,
                    TridentCalcRule.INSTANCE
            ).build();

    public static RuleSet[] getRuleSets() {
        return new RuleSet[]{
                new StormRuleSet(StreamRules.RULES),
                new StormRuleSet(ImmutableSet.<RelOptRule>builder().addAll(StreamRules.RULES).addAll(calciteToStormConversionRules).build())
        };
    }

    private static class StormRuleSet implements RuleSet {
        final ImmutableSet<RelOptRule> rules;

        public StormRuleSet(ImmutableSet<RelOptRule> rules) {
            this.rules = rules;
        }

        public StormRuleSet(ImmutableList<RelOptRule> rules) {
            this.rules = ImmutableSet.<RelOptRule>builder()
                    .addAll(rules)
                    .build();
        }

        @Override
        public Iterator<RelOptRule> iterator() {
            return rules.iterator();
        }
    }

}
