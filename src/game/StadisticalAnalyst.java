package game;

import ui.Cell;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static app.CovidGame.DAYS_TO_DETERMINE_UNCHANGED_DAYS;

public class StadisticalAnalyst {

    private final List<List<Integer>> dict;
    private final int daysToEndAnalysis;
    private final List<Integer> StateList = Collections.unmodifiableList(List.of(
            Cell.STATE_NOT_INFECTED,
            Cell.STATE_INFECTED,
            Cell.STATE_IMMUNE,
            Cell.STATE_SURROUNDED,
            Cell.STATE_MASKED,
            Cell.STATE_DEAD
    ));

    public StadisticalAnalyst(List<Person> previousDay, List<Person> day, int daysToEndAnalysis){
        if(previousDay.size() != day.size()){
            this.dict = getDayResults(getChangedPersons(generateNotInfectedPersonList(day.size()), day));
        } else{
            this.dict = getDayResults(getChangedPersons(previousDay, day));
        }
        this.daysToEndAnalysis = (evaluateExtensionOfAnalysis())? decreaseDaysToEndAnalysis(daysToEndAnalysis):DAYS_TO_DETERMINE_UNCHANGED_DAYS;
    }

    public StadisticalAnalyst(int daysToEndAnalysis){
        this.dict = null;
        this.daysToEndAnalysis = daysToEndAnalysis;
    }

    public boolean evaluateExtensionOfAnalysis(){
        return dict.stream()
                .limit(dict.size())
                .map(n -> n.get(1))
                .reduce(0, (tmp, e) -> tmp + e) == 0
        ;
    }
    private int decreaseDaysToEndAnalysis(int i){
        return i - 1;
    }

    public List<List<Integer>> getDict(){
        return this.dict;
    }

    public int getDaysToEnd() {
        return daysToEndAnalysis;
    }

    public static List<Person> generateNotInfectedPersonList(int listSize){
        return Stream
                .iterate(0, n -> n + 1)
                .limit(listSize)
                .map(n -> new Person(-1))
                .collect(Collectors.toUnmodifiableList());
    }

    private List<Person> getChangedPersons(List<Person> previousDay, List<Person> day){
        return Stream
                .iterate(0, n -> n + 1)
                .limit(previousDay.size())
                .map(n -> (previousDay.get(n).getState() == day.get(n).getState())?
                        new Person(-1):
                        day.get(n)
                ).collect(Collectors.toUnmodifiableList());
    }

    private List<List<Integer>> getDayResults(List<Person> list){

        return Stream
                .iterate(0, n -> n + 1)
                .limit(StateList.size())
                .map(n -> Stream
                        .iterate(0, i -> i + 1)
                        .limit(2)
                        .map(i -> (i == 0)?
                                StateList.get(n):
                                reduceFrecuencyCell(list, n)
                        ).collect(Collectors.toUnmodifiableList())
                ).collect(Collectors.toUnmodifiableList());
    }

    private int reduceFrecuencyCell(List<Person> list, int n){
        return list.stream()
                .mapToInt(i -> i.getState())
                .reduce(0, (tmp, a) -> (a == n)?
                        tmp + 1:
                        tmp
                );
    }

}
