#include "include/motor.h"

void setMotor(char motor, signed char velocity)
{
   if(motor == MOTOR_R || motor == MOTOR_L)
   {
      unsigned char speed;
      DIR direction;
      if (velocity > 0)
      {
         direction = FORWARD;
         speed = velocity > 100 ? 100 : velocity;
      }
      else if (velocity < 0)
      {
         direction = BACKWARD;
         speed = velocity < -100 ? 100 : -velocity;
      } 
      else
      {
         Motor_Stop(motor);
         return;
      }
      speed = (unsigned char)(speed / 100. * SPEED_RANGE) + MIN_SPEED;
      Motor_run(motor, direction, speed);
   }
}

void runForward(unsigned char speed)
{
   speed = speed > 100 ? 100 : speed;
   setMotor(MOTOR_R, speed);
   setMotor(MOTOR_L, speed);
}

//TODO: correct copy/paste
void runBackward(unsigned char speed)
{
   speed = speed > 100 ? 100 : speed;
   setMotor(MOTOR_R, -speed);
   setMotor(MOTOR_L, -speed);
}

void turnRightF(unsigned char speed)
{
   speed = speed > 100 ? 100 : speed;
   setMotor(MOTOR_R, speed * 0.5);
   setMotor(MOTOR_L, speed);
}

void turnLeftF(unsigned char speed)
{
   speed = speed > 100 ? 100 : speed;
   setMotor(MOTOR_R, speed);
   setMotor(MOTOR_L, speed * 0.5);
}

void turnRightB(unsigned char speed)
{
   speed = speed > 100 ? 100 : speed;
   setMotor(MOTOR_R, -(speed * 0.5));
   setMotor(MOTOR_L, -speed);
}

void turnLeftB(unsigned char speed)
{
   speed = speed > 100 ? 100 : speed;
   setMotor(MOTOR_R, -speed);
   setMotor(MOTOR_L, -(speed * 0.5));
}

void rotateRight(unsigned char speed)
{
   speed = speed > 100 ? 100 : speed;
   setMotor(MOTOR_R, -speed);
   setMotor(MOTOR_L, speed);
}

void rotateLeft(unsigned char speed)
{
   speed = speed > 100 ? 100 : speed;
   setMotor(MOTOR_R, speed);
   setMotor(MOTOR_L, -speed);
}

void idle()
{
   Motor_Stop(MOTOR_R);
   Motor_Stop(MOTOR_L);
}

void motorInit()
{
   //consult main.c of code given for library
}

void motorQuit()
{
   //consult main.c of code given for library
} 