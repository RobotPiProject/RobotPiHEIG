/**
 * @file test_robot.c
 * @author Anthony Jaccard
 * @brief Test program for the robot. Implements tests that cannot be easily automated and require the developper to actively verify that the robot is doing what the tests intend to. The intention of the tests is described precisely in the function header
 * @version 0.1
 * @date 2021-05-04
 * 
 * @copyright Copyright (c) 2021
 * 
 */

#include "motor.h"
#include <unistd.h>

void test_motor(void);

int main(int argc, char const *argv[])
{
   test_motor();
   return 0;
}

/**
 * @brief Tests for the motor functions
 * 
 * Behaviour:
 * Robot must perform the following movements sequentially for 2 seconds each at half speed, then wait 5 seconds at idle at start again at full speed
 * - Go straight-ish forward (reliable perfectly straight line is complex to implement and isn't the purpose of this project) 
 * - Go straight-ish backward
 * - Rotate on itself counter-clockwise
 * - Rotate on itself clockwise
 * - Wait at idle
 * - Turn right forward
 * - Turn right backward
 * - Turn left backward
 * - Turn left forward
 */
void test_motor(void)
{
   if(!motorInit())
   {
      runForward(50);
      sleep(2);
      runBackward(50);
      sleep(2);
      rotateLeft(50);
      sleep(2);
      rotateRight(50);
      sleep(2);
      idle();
      sleep(2);
      turnRightF(50);
      sleep(2);
      turnRightB(50);
      sleep(2);
      turnLeftB(50);
      sleep(2);
      turnLeftF(50);
      sleep(2);
      idle();
      sleep(5);
      //Start again at full speed
      runForward(100);
      sleep(2);
      runBackward(100);
      sleep(2);
      rotateLeft(100);
      sleep(2);
      rotateRight(100);
      sleep(2);
      idle();
      sleep(2);
      turnRightF(100);
      sleep(2);
      turnRightB(100);
      sleep(2);
      turnLeftB(100);
      sleep(2);
      turnLeftF(100);
      sleep(2);
      idle();

      motorQuit();
   }
}


