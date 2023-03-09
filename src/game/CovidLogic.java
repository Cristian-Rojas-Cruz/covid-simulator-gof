package game;

import app.CovidGame;
import ui.Cell;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CovidLogic {

    private final VirusParameters parameters;
    private final List<List<Integer>> cellsToCheck;

    public CovidLogic(VirusParameters parameters) {
        this.parameters = parameters;
        if(parameters.transmissionRange > 0){
            this.cellsToCheck = generateCellsToCheck(parameters.transmissionRange);
        } else {
            this.cellsToCheck = Collections.unmodifiableList(List.of(
                    List.of(-1, -1),
                    List.of(-1, 0),
                    List.of(-1, 1),
                    List.of(0, -1),
                    List.of(0, 1),
                    List.of(1, -1),
                    List.of(1, 0),
                    List.of(1, 1)
            ));
        }
    }

    public List<List<Integer>>  generateCellsToCheck(int range){
        final List<List<Integer>> list = Stream
                .iterate((range - (range*2)), n -> n + 1)
                .limit(range+range + 1)
                .map(o -> Stream
                        .iterate((range - (range*2)), n -> n + 1)
                        .limit(range + range + 1)
                        .collect(Collectors.toUnmodifiableList())
                )
                .collect(Collectors.toUnmodifiableList());

        return Stream
                .iterate(0, n -> n + 1)
                .limit((range*2 + 1)*(range*2 + 1))
                .map(i -> Stream
                        .iterate(0, u -> u + 1)
                        .limit(2)
                        .map(o -> (o == 0)?
                                list.get(0).get(i/(range*2 + 1)):
                                list.stream()
                                        .flatMap(Collection::stream)
                                        .collect(Collectors.toUnmodifiableList()).get(i)
                        ).collect(Collectors.toUnmodifiableList())
                ).filter(n -> !(n.get(0) == 0 && n.get(1) == 0))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Person> advanceDay(List<Person> population, int column) {
        final List<List<Person>> formatedPopulation = splitEach(population, column);
        return Stream
                .iterate(0, n -> n + 1)
                .limit(formatedPopulation.size())
                .map(n -> Stream
                        .iterate(0, i -> i + 1)
                        .limit(formatedPopulation.get(n).size())
                        .map(i -> checkSurroundingCells(
                                formatedPopulation,
                                n,
                                i)
                        ).collect(Collectors.toUnmodifiableList())
                ).collect(Collectors.toUnmodifiableList())
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableList());
    }

    public Person checkSurroundingCells(List<List<Person>> formatedPopulation, int n, int i){
        return reduceCellNumber(Stream
                .iterate(0, o -> o + 1)
                .limit(cellsToCheck.size() + 1)
                .map( o -> (o == cellsToCheck.size())?
                        formatedPopulation.get(n).get(i):
                        (cellsToCheck.get(o).get(0) + n > -1
                                && cellsToCheck.get(o).get(1) + i > -1
                                && cellsToCheck.get(o).get(0) + n < formatedPopulation.size()
                                && cellsToCheck.get(o).get(1) + i < formatedPopulation.get(n).size())?
                                new Person( formatedPopulation
                                        .get(n + cellsToCheck.get(o).get(0))
                                        .get(i + cellsToCheck.get(o).get(1))):
                                new Person(-2)
                ).collect(Collectors.toUnmodifiableList())
        );
    }

    private int reducedInfectedCells(List<Person> list){
        return Stream
                .iterate(0, n -> n + 1)
                .limit(list.size())
                .map(n -> list.get(n).getState())
                .reduce(0, (tmp, a) -> (a == 1)? tmp + 1: tmp);
    }

    public Person reduceCellNumber(List<Person> list){


        return list
                .stream()
                .reduce(list.get(list.size() - 1), (tmp, a) ->
                (list.get(list.size() - 1).getState() == Cell.STATE_NOT_INFECTED
                        || list.get(list.size() - 1).getState() == Cell.STATE_SURROUNDED
                        || list.get(list.size() - 1).getState() == Cell.STATE_MASKED)?
                        (a.getState() == Cell.STATE_INFECTED && Math.random() * 100 < parameters.transmissionPercent)?
                                new Person(Cell.STATE_INFECTED):
                                (tmp.getState() == Cell.STATE_INFECTED)?
                                new Person(Cell.STATE_INFECTED):
                                (reducedInfectedCells(list) > CovidGame.PEOPLE_AROUND)?
                                        new Person(Cell.STATE_SURROUNDED):
                                        (list.get(list.size() - 1).getState() == Cell.STATE_MASKED && Math.random() * 100 < parameters.transmissionPercent/2)?
                                        new Person(1): new Person(list.get(list.size() - 1)):
                        (list.get(list.size() - 1).getState() == Cell.STATE_INFECTED)?
                                (list.get(list.size() - 1).getDaysOnState() + 1 > parameters.lifetimeInDays)?
                                    new Person(Cell.STATE_IMMUNE):
                                        ((Math.random() * 100 < parameters.lethality))?
                                            new Person(Cell.STATE_DEAD):
                                            new Person(list.get(list.size() - 1)):
                                new Person(list.get(list.size() - 1))
                );
    }

    public static List<List<Person>> splitEach(List<Person> input, int i) {
        return ((input.size()/i)*i < input.size())?
                Stream.iterate(0, n -> n + 1)
                        .map(n -> (n == input.size()/i)?
                                input.subList(n*i,input.size()):
                                input.subList(n*i,n*i+i))
                        .limit((input.size()/i)+1)
                        .collect(Collectors.toUnmodifiableList()):
                Stream.iterate(0, n -> n + 1)
                        .map(n -> input.subList(n*i,n*i+i))
                        .limit(input.size()/i)
                        .collect(Collectors.toUnmodifiableList());
    }
}
