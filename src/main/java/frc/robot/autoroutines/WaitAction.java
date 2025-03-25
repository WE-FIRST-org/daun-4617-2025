package frc.robot.autoroutines;

import edu.wpi.first.wpilibj.Timer;

public class WaitAction extends Action{
    Timer timer = new Timer();
    public WaitAction() {
        timer.reset();
        timer.start();
    } 

    public boolean run() {
        if (timer.get() > 5 ) {
            return true;
        } else return false;
    }
    
}
