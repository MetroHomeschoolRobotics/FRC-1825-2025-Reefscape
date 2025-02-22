// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.util.Units;

/** Add your docs here. */
public class Constants {
    public static class CameraPositions {
    public static final Transform3d frontLeftTranslation = new Transform3d(
                                                            Units.inchesToMeters(11.147131),
                                                            Units.inchesToMeters(11.404959),
                                                            Units.inchesToMeters(9.321819), 
                                                            new Rotation3d(
                                                              Units.degreesToRadians(0),
                                                              Units.degreesToRadians(10),
                                                              Units.degreesToRadians(-30)));
    public static final Transform3d backTranslation = new Transform3d(0,0,0, new Rotation3d(0,0,0));
    public static final Transform3d leftTranslation = new Transform3d(0,0,0, new Rotation3d(0,0,0));
    public static final Transform3d rightTranslation = new Transform3d(0,0,0, new Rotation3d(0,0,0));
  }
}
