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
    public static final Transform3d frontTranslation = new Transform3d(
                                                            Units.inchesToMeters(12.5),
                                                            Units.inchesToMeters(-11.5),
                                                            Units.inchesToMeters(8), 
                                                            new Rotation3d(
                                                              Units.degreesToRadians(-15),
                                                              0,
                                                              Units.degreesToRadians(45)));
    public static final Transform3d backTranslation = new Transform3d(0,0,0, new Rotation3d(0,0,0));
    public static final Transform3d leftTranslation = new Transform3d(0,0,0, new Rotation3d(0,0,0));
    public static final Transform3d rightTranslation = new Transform3d(0,0,0, new Rotation3d(0,0,0));
  }
}
