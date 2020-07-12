#ifndef Cokle_h
#define Cokle_h

#define CLUNET_ID 0x85
#define CLUNET_DEVICE "SwingSocle"

#define SERVO_PIN 13
#define SERVO_STEP_DELAY 2
#define SERVO_UP_ANGLE 30
#define SERVO_DOWN_ANGLE 180
#define SERVO_DOWN_TIMEOUT_DEFAULT 50
#define SERVO_UP_TIMEOUT_DEFAULT 30000


#define TASKER_GROUP_SERVO 1
#define TASKER_GROUP_MIIO 2
#define TASKER_GROUP_LED_RED 3


#define BUZZER_PORT 15

#define BEEP_WARNING_FREQ 400
#define BEEP_WARNING_DELAY 300
#define BEEP_WARNING_REPEATS 2

#define BEEP_ERROR_FREQ 80
#define BEEP_ERROR_DELAY 500

#define BEEP_INFO_FREQ 500
#define BEEP_INFO_DELAY 1000

#define BEEP_APP_START_FREQ 700
#define BEEP_APP_START_DELAY 500


#define LED_RED_PORT 4
#define LED_BLUE_PORT 5


#define MIROBO_CHECK_STATUS_PERIOD 5000
#define MIROBO_MAX_GOING_HOME_PERIOD 180000


#endif
