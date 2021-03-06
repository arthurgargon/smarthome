/*
 * MeteoOutdoor.c
 *
 * Created: 09.01.2017 13:26:45
 *  Author: gargon
 */ 


#include <avr/io.h>
#include <util/delay.h>
#include <avr/wdt.h>
#include <avr/sleep.h>
#include <avr/interrupt.h>

#include "VirtualWire/VirtualWire.h"
#include "USI_TWI/USI_TWI_Master.h"
#include "bme280/bme280.h"
#include "bht1750/bht1750.h"

char data[14];

#define NUMBER_VW_SEND 5

ISR (WDT_vect) {
	//wdt_reset();
	//WDTCSR |= _BV(WDIE); // ��������� ���������� �� ��������. ����� ����� �����.
}

int main(void){
	sei();
	set_sleep_mode(SLEEP_MODE_PWR_DOWN); // ���� ����� - �� �� ������
	
	USI_TWI_Master_Initialise();
	vw_setup(2000);

	//ADMUX = (1<<REFS0)|(1<<MUX5)|(1<<MUX0);
	//wait 1 ms at least
	
	
	while(1){
		 bme280_start_force(1, 1, 1);
		 bht1750_start_mtreg(BH1750_ONE_TIME_HIGH_RES_MODE, 90); //����������� ���������� �� 30% ��-�� ������� ���� (���� 69, 69+30% = 90)
		 
		 //�� ����� 2,42 - 2,38  ���
		 wdt_enable(WDTO_2S);
		 WDTCSR |= _BV(WDIE);  // ��������� ���������� �� ��������. ����� ����� �����.
		 
		 sleep_enable(); // ��������� ���
		 sleep_cpu();    // �����!
		 
		 
		 //��� ��� ��������� ���������� �������
		 
		 //�������� ���
		 ADCSRA = (1<<ADEN);
		 
		 //REFS1..0 = 1 AREF ���� ��� �������� �������� ����������
		 //MUX5..0 = 100001 �������� ���������� 1.1V
		 ADMUX = (1<<REFS0)|(1<<MUX5)|(1<<MUX0);
		 //���� 1 ��, ��� �������� � ��������
		 //� ��� ����� �� ����� ���� �������� �� �����
		 _delay_ms(1);
		 
		 //ADEN = 1 - ��������� ���
		 //ADSC = 1 ��������� ��������������
		 //ADPS2..0 = 6 �������� ������� �� 128
		 ADCSRA = (1<<ADEN)|(1<<ADSC)|(7<<ADPS0);	
		 		 //���� ���������� �������������� ���
				 while (ADCSRA & (1<<ADSC));
				 uint8_t lo = ADCL;
				 uint8_t hi = ADCH;
				 //�������� ���
				 ADCSRA = 0;
		 uint8_t size_0 = bme280_readValues((char*)(&data[0]));
		 if (size_0){
		 	 uint8_t size_1 = bht1750_readValues((char*)(&data[size_0]));
		 	 if (size_1){
				size_0 += size_1;
				

				
				//�������� ����������:
				//������ ���������� 1,1� ������������ AREF ���������� � �������
				//AREF -> 1023
				//1,16 -> x
				//AREF = 1.1 * 1023 / x
				
				//������� �� 100 � �������� �� 2
				//�������� ������ ��������, �� ���� ������� � 2 �����
				//AREF = 56265 / (x/2)
				
				uint16_t t2 = ((hi<<8) | lo) >> 1;
				t2 = 56265 / t2;
				
				data[size_0++] = (t2 >> 8 ) & 0xFF;
				data[size_0++] = t2 & 0xFF;
				
				
				//���������� 5 ���
				//� 11.03.17 - 20.01.19 ���� 2 �������� (������� �������� ���������� 2.45-2.5�)
				for (int i=0; i<NUMBER_VW_SEND; i++){
					vw_send((uint8_t *)data, size_0);
					vw_wait_tx();
				}
			 }
		 }

		 //���� 56+- ���
		 for (int i=0; i<7; i++){
			 wdt_enable(WDTO_8S);
			 WDTCSR |= _BV(WDIE);  // ��������� ���������� �� ��������. ����� ����� �����.
					  
			sleep_enable(); // ��������� ���
			sleep_cpu();    // �����!
		 }
    }
}