package app;

import game.CovidLogic;
import game.Person;
import game.StadisticalAnalyst;
import game.VirusParameters;
import ui.CovidGameWindow;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CovidGame {

    public static final int ROWS = 100;
    public static final int COLUMNS = 100;
    public static final int MSECONDS_PER_DAY = 1000;
    public static final int VIRUS_TRANSMISSION_PERCENT = 3;
    public static final int VIRUS_TIMELIFE_DAYS = 14;
    public static final int VIRUS_TRANSMISSION_RANGE = 2;
    public static final int VIRUS_LETHALITY = 5;
    public static final int DAYS_TO_DETERMINE_UNCHANGED_DAYS = 10;
    public static final int PEOPLE_AROUND = 5;
    public static final int PROBABLITY_FOR_INFECTED_TO_APPEAR = 1;
    public static final int PROBABLITY_FOR_MASKED_TO_APPEAR = 10;


    public static void main(String[] args) throws InterruptedException {

        final CovidGameWindow game = new CovidGameWindow();
        game.setRowsAndColumns(ROWS , COLUMNS);

        final VirusParameters virusParameters = new VirusParameters(
                VIRUS_TRANSMISSION_PERCENT,
                VIRUS_TIMELIFE_DAYS,
                VIRUS_TRANSMISSION_RANGE,
                VIRUS_LETHALITY

        );

        final CovidLogic covidLogic = new CovidLogic(virusParameters);

        List<Person> population = initializePopulation(ROWS, COLUMNS);

        StadisticalAnalyst analyst;

        analyst = new StadisticalAnalyst(StadisticalAnalyst.generateNotInfectedPersonList(population.size()), population, 20);


        while(analyst.getDaysToEnd()> 0) {
            printIntegerMatrix(analyst.getDict());

            final List<Integer> cellStates = convertToUiCells(population);

            System.out.println("---------------------");

            game.setCellStates(cellStates);

            final List<Person> nextPopulation= covidLogic.advanceDay(population, COLUMNS);

            analyst = new StadisticalAnalyst(population, nextPopulation, analyst.getDaysToEnd());

            population = nextPopulation;

            Thread.sleep(MSECONDS_PER_DAY);

        }
    }

    private static List<Integer> convertToUiCells(List<Person> list){
        return Stream.iterate(0, n -> n +1).limit(list.size()).map(n -> list.get(n).getState()).collect(Collectors.toUnmodifiableList());
    }

    private static List<Person> initializePopulation(int rows, int columns) {
        return IntStream.range(0, rows*columns).mapToObj(n -> (Math.random() * 100 < PROBABLITY_FOR_INFECTED_TO_APPEAR)? new Person(1):(Math.random() * 100 < PROBABLITY_FOR_MASKED_TO_APPEAR)?new Person(4):new Person(0)).collect(Collectors.toUnmodifiableList());
    }

    private static void printIntegerMatrix(List<List<Integer>> list) {
        for (List<Integer> e : list) {
            for (Integer i : e) {
                System.out.print(i + " ");
            }
            System.out.println(" ");
        }
    }
}
