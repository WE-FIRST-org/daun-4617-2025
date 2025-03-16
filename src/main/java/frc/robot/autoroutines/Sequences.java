package frc.robot.autoroutines;

public class Sequences {
    private Action[] actionArray;
    private int numberOfActions;
    public int actionStep;
    public boolean actionComplete;

    public Sequences(Action[] arr) {
        this.actionArray = arr;
        this.actionStep = 0;
        this.numberOfActions = this.actionArray.length;
        this.actionComplete = false;
    }

    public void run() {
        this.actionComplete = false;

        if (this.actionStep == this.numberOfActions) {
            return;
        }

        this.actionComplete = this.actionArray[this.actionStep].run();
        if (this.actionComplete) {
            this.actionStep++;
        }
    }
}
