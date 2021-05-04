#ifndef MOTOR_H
#define MOTOR_H

#include <MotorDriver.h>
#include <stdio.h>

#define MAX_SPEED 70 //Used to prevent overvoltage of motors
#define MIN_SPEED 10 //Minimum speed at which the robot moves significantly
#define SPEED_RANGE MAX_SPEED - MIN_SPEED

#define MOTOR_L MOTORA
#define MOTOR_R MOTORB

/**
 * @brief Sets the speed of the specified motor
 * 
 * @param motor The motor to command {MOTOR_R, MOTOR_L}
 * @param velocity The velocity to run the motor at [-100, 100]
 * 
 * Motors are wired such that velocity > 0 will always make the motor go forward (and inversely) even though they are physically mounted in mirror
 * Function has no effect if motor value isn't valid
 * Velocity is clamped if value is < -100 or > 100
 */
void setMotor(char motor, signed char velocity);

/**
 * @brief Makes the robot run forward
 * 
 * @param speed The speed to run the motors at [0, 100]
 */
void runForward(unsigned char speed);

/**
 * @brief Makes the robot run backward
 * 
 * @param speed The speed to run the motors at [0, 100]
 */
void runBackward(unsigned char speed);

/**
 * @brief Makes the robot turn right forward
 * 
 * @param speed The speed to run the motors at [0, 100]
 */
void turnRightF(unsigned char speed);

/**
 * @brief Makes the robot turn left forward
 * 
 * @param speed The speed to run the motors at [0, 100]
 */
void turnLeftF(unsigned char speed);

/**
 * @brief Makes the robot turn right backward
 * 
 * @param speed The speed to run the motors at [0, 100]
 */
void turnRightB(unsigned char speed);

/**
 * @brief Makes the robot turn left backward
 * 
 * @param speed The speed to run the motors at [0, 100]
 */
void turnLeftB(unsigned char speed);

/**
 * @brief Makes the robot rotate on itself clockwise
 * 
 * @param speed The speed to run the motors at [0, 100]
 */
void rotateRight(unsigned char speed);

/**
 * @brief Makes the robot rotate on itself anti-clockwise
 * 
 * @param speed The speed to run the motors at [0, 100]
 */
void rotateLeft(unsigned char speed);

/**
 * @brief Stops the robot
 * 
 */
void idle();

/**
 * @brief Initialises the systems required to make the motors run
 * 
 * @return int 1 if there was an error, 0 otherwise
 */
int motorInit();

/**
 * @brief Necessary steps before exiting
 * 
 */
void motorQuit();

#endif