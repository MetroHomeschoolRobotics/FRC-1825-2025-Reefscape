package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;

public class printDebug extends Command {
 public printDebug(){}
    public void initialize(){
        System.out.println("BOO!");
    }
    public boolean isFinished(){
        return true;
    }
 
}
