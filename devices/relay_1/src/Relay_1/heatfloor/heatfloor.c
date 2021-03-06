/*
 * heatfloor.c
 *
 * Created: 12.10.2015 15:08:13
 *  Author: gargon
 */ 

#include "heatfloor.h"

#include <avr/io.h>
#include <avr/interrupt.h>
#include <avr/eeprom.h>

signed int (*on_heatfloor_sensor_temperature_request)(unsigned char channel) = 0;
void (*on_heatfloor_control_switch_request)(unsigned char channel, unsigned char on_) = 0;

void (*on_heatfloor_state_message)(heatfloor_channel_infos* infos) = 0;

unsigned char on;				//����� ���/����
unsigned char sensorCheckTimer;

heatfloor_channel_infos channel_infos;

heatfloor_channel_infos* heatfloor_refresh(){
	sensorCheckTimer = 0;
	
	channel_infos.num = 0;
	
	if (on){
		for (unsigned char i = 0; i < HEATFLOOR_CHANNELS_COUNT; i++){
			signed int settingT = heatfloor_dispatcher_resolve_temperature_setting(i);
			//if (settingT != 0){
				
				signed int sensorT = -1;
				signed char solution = 0;
				
				if (settingT > 0){
					
					if (on_heatfloor_sensor_temperature_request){
						sensorT = (*on_heatfloor_sensor_temperature_request)(i);
					}
					
					if (sensorT >= 0){
						//check range
						if (sensorT >= HEATFLOOR_MIN_TEMPERATURE_10 && sensorT <= HEATFLOOR_MAX_TEMPERATURE_10){
							if (sensorT <= settingT - HEATFLOOR_SENSOR_HYSTERESIS_TEMPERATURE_10){
								solution = 1;					//�����������
							}else if (sensorT >= settingT){
								solution = -1;					//����������
							}
								//� ������ ���� �������� �������� � ����������� ��������, ��
								//��������� ���������: ���������� ����������, ���� �� ������ �� ������� �������
								//��� ���������� ����������, ���� �� ������ �� ������ �������
								
								//��� ������� 28 � ���������� ��������� 0.3 - ����� ������������� �� 28(� ��� ������� ������� 0,3) � ����������� �� 27.7
						}else{
							solution = -3;						    //������ ��������� �������� � ������� (85*, ��������)
						}
					}else{
						solution = -2;								//������ ������ �������� � �������
					}
				}else{
					
					//��� ����� ��������, ���������� �� ����������
					//�������� �������� t* �������� �� ����� ������
					switch (settingT){
						case INT16_MIN:		//������ ����������
							solution = -4;
							settingT = -1;	//�������� ������ ��� ������
							break;
						//case  0:			//������� 0 - ��������
						//case -1:			//����� ����
						default:			//������
							solution = -1;	//���������� � ����� ������
							break;
					}
				}
				
				if (solution != 0){
					if (on_heatfloor_control_switch_request){
						(*on_heatfloor_control_switch_request)(i, solution>0);
					}
				}
				
				//��� ����������� ����� �� ���� �������
				heatfloor_channel_info* ci = &channel_infos.channels[channel_infos.num++];
				//ci->num = i;
				ci->mode = heatfloor_modes_info()->channels[i].mode;
				ci->solution = solution;
				ci->sensorT = sensorT;
				ci->settingT = settingT;
				
			//}else{
			//	//����� ��������
			//}
		}
	}
	return &channel_infos;
}

//�������� ������ � ���������� ������� ������
//response_required - ������������ ������� ������ ��� ������ ��� ������� �������� �������
void heatfloor_refresh_responsible(unsigned char response_required){
	heatfloor_channel_infos* infos = heatfloor_refresh();
	if (infos->num || response_required){
		if (on_heatfloor_state_message){
			(*on_heatfloor_state_message)(infos);
		}
	}
}

void heatfloor_tick_second(){
	heatfloor_dispatcher_tick_second();
	
	if (++sensorCheckTimer >= HEATFLOOR_SENSOR_CHECK_TIME){
		heatfloor_refresh_responsible(0);	//�������� ����� ������ ���� ���� �������� ������
	}
}

void heatfloor_init(
		signed int (*hf_sensor_temperature_request)(unsigned char channel),
		void (*hf_control_change_request)(unsigned char channel, unsigned char on_),
		void (*hf_systime_request)(void (*hf_systime_async_response)(unsigned char seconds, unsigned char minutes, unsigned char hours, unsigned char day_of_week))
		){
			
	//������ ��������� �� EEPROM
	on = eeprom_read_byte((void*)EEPROM_ADDRESS_HEATFLOOR);
	
	on_heatfloor_sensor_temperature_request = hf_sensor_temperature_request;
	on_heatfloor_control_switch_request = hf_control_change_request;
	
	heatfloor_dispatcher_init(hf_systime_request);
	
	sensorCheckTimer = HEATFLOOR_SENSOR_CHECK_TIME - 2;		//2 seconds on startup and then refresh
}

heatfloor_channel_infos* heatfloor_state_info(){
	return heatfloor_refresh();
}

void heatfloor_set_on_states_message(void(*f)(heatfloor_channel_infos* infos)){
	on_heatfloor_state_message = f;
}

void heatfloor_on(unsigned char on_){
	if (on_ != on){
		on = on_;
		
		if (!on){
			for (int i=0; i<HEATFLOOR_CHANNELS_COUNT; i++){
				//switch relay off
				if (on_heatfloor_control_switch_request){
					(*on_heatfloor_control_switch_request)(i, 0);
				}
			}
		}
		
		//save channels enable in eeeprom
		eeprom_busy_wait();
		eeprom_write_byte((void*)EEPROM_ADDRESS_HEATFLOOR, on); // ���������� ������
	}
	heatfloor_refresh_responsible(1);	//�������� ����� ������
}

void heatfloor_command(char* data, unsigned char size){
	if (heatfloor_dispatcher_command(data, size)){	//���� �����/��������� ��������, ����� ����������
		heatfloor_refresh_responsible(1);			//� ��������� �����
	}
}