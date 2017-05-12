/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.panel;

import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.drools.workbench.services.verifier.api.client.reporting.CheckType;
import org.drools.workbench.services.verifier.api.client.reporting.ExplanationProvider;
import org.drools.workbench.services.verifier.api.client.reporting.Issue;

public class IssuesSet
        extends TreeSet<Issue> {

    private static Set<CheckType> MERGEABLE_ISSUES = EnumSet.of(
            CheckType.EMPTY_RULE,
            CheckType.MISSING_RESTRICTION,
            CheckType.MISSING_ACTION,
            CheckType.IMPOSSIBLE_MATCH
                                                               );

    private static Set<CheckType> TRANSITIVE_ISSUES = EnumSet.of(
            CheckType.CONFLICTING_ROWS
    );

    public IssuesSet( List<Issue> issues ) {
        super( new Comparator<Issue>() {
            @Override
            public int compare( final Issue issue,
                                final Issue other ) {
                int compareToSeverity = issue.getSeverity()
                        .compareTo( other.getSeverity() );

                if ( compareToSeverity == 0 ) {
                    final String thisTitle = ExplanationProvider.toTitle( issue );
                    final String otherTitle = ExplanationProvider.toTitle( other );
                    int compareToTitle = thisTitle.compareTo( otherTitle );
                    if ( compareToTitle == 0 ) {
                        return compareRowNumbers( issue.getRowNumbers(),
                                                  other.getRowNumbers() );
                    } else {
                        return compareToTitle;
                    }
                } else {
                    return compareToSeverity;
                }

            }

            private int compareRowNumbers( final Set<Integer> rowNumbers,
                                           final Set<Integer> other ) {
                if ( rowNumbers.equals( other ) ) {
                    return 0;
                } else {
                    for ( Integer a : rowNumbers ) {
                        for ( Integer b : other ) {
                            if ( a < b ) {
                                return -1;
                            }
                        }
                    }
                    return 1;
                }
            }
        } );

        addAll( MERGEABLE_ISSUES.stream()
                                .map( typeToMerge -> mergeIssues( issues, typeToMerge ) )
                                .filter( Optional::isPresent ).map( Optional::get ).collect( Collectors.toSet() ) );

        for(CheckType checkType : TRANSITIVE_ISSUES) {
            addAll(mergeTransitiveIssues(issues, checkType));
        }


        issues.stream()
                .filter( issue -> !MERGEABLE_ISSUES.contains( issue.getCheckType() ) )
                .filter( issue -> !TRANSITIVE_ISSUES.contains( issue.getCheckType() ) )
                .forEach( issue -> add( issue ) );
    }

    private Set<Issue> mergeTransitiveIssues(List<Issue> issues, CheckType typeToMerge) {
        Set<Issue> newIssues = new HashSet<>();

        Set<Issue> remainingIssues = new HashSet<>(issues);
        Set<Issue> issuesToMerge = new HashSet<>();
        Set<Issue> alreadyMerged = new HashSet<>();

        for (Issue issue1 : issues) {
            if(!alreadyMerged.contains(issue1)) {
                issuesToMerge.clear();
                issuesToMerge.add(issue1);
                for (Issue issue2 : remainingIssues) {
                    Set<Integer> issue1RowNumbers = new HashSet<>(issue1.getRowNumbers());
                    if (issue1RowNumbers.retainAll(issue2.getRowNumbers()) && !issue1RowNumbers.isEmpty()) {
                        issuesToMerge.add(issue2);
                        alreadyMerged.add(issue2);
                    }
                }
                Optional<Issue> newIssue = mergeIssues(issuesToMerge,
                                                       typeToMerge);
                if (newIssue.isPresent()) {
                    newIssues.add(newIssue.get());
                }
                remainingIssues.removeAll(issuesToMerge);
            }
        }

        return newIssues;
    }

    private Optional<Issue> mergeIssues( final Collection<Issue> issues,
                                         final CheckType typeToMerge ) {
        Set<Issue> issuesToMerge = issues.stream()
                                         .filter( issue -> issue.getCheckType() == typeToMerge )
                                         .collect( Collectors.toSet() );

        Set<Integer> affectedRows = issuesToMerge.stream()
                                                 .flatMap( issue -> issue.getRowNumbers().stream() )
                                                 .sorted()
                                                 .collect( Collectors.toSet() );

        return issuesToMerge.stream()
                            .findFirst() // Will be Optional.empty() if no issue of "typeToMerge" was present
                            .map( issue -> new Issue( issue.getSeverity(), typeToMerge, affectedRows ) );
    }
}
