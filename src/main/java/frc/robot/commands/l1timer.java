package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;

public class l1timer extends Command {
    int timer;
 public l1timer(){

 }
 public void initialize(){}
 public void execute(){
    timer+=1;
 }
 public boolean isFinished(){
    return timer>20;
 }
}
