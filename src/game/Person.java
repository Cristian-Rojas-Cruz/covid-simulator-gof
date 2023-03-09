package game;

public class Person {
    private final int state;
    private final int daysOnState;

    public Person(Person oldPerson){
        this.state = oldPerson.getState();
        this.daysOnState = oldPerson.daysOnState + 1;
    }
     public Person(int state){
        this.state = state;
        this.daysOnState = 0;
     }

    public int getState(){
        return this.state;
    }

    public int getDaysOnState(){
        return this.daysOnState;
    }
}
