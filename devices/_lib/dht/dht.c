
#include "dht.h"

#include <util/delay.h>
#include <string.h>


uint32_t cache_time = -1;
 
int16_t cached_temperature;
int16_t cached_humidity;

/*
 * get data from sensor
 */
int8_t dht_getdata(int16_t *temperature, int16_t *humidity) {
	uint8_t bits[5];
	uint8_t i,j = 0;

	memset(bits, 0, sizeof(bits));

	//reset port
	//DHT_DDR |= (1<<DHT_INPUTPIN); //output
	DHT_OUTPUT;
	//DHT_PORT |= (1<<DHT_INPUTPIN); //high
	DHT_HIGH;
	_delay_ms(100);

	//send request
	//DHT_PORT &= ~(1<<DHT_INPUTPIN); //low
	DHT_LOW;
	#if DHT_TYPE == DHT_DHT11
		_delay_ms(18);
	#elif DHT_TYPE == DHT_DHT22
		_delay_us(500);
	#endif
	//DHT_PORT |= (1<<DHT_INPUTPIN); //high
	DHT_HIGH;
	//DHT_DDR &= ~(1<<DHT_INPUTPIN); //input
	DHT_INPUT;
	_delay_us(40);

	//check start condition 1
	// (DHT_PIN & (1<<DHT_INPUTPIN))
	if(DHT_TEST) {
		return 0;
	}
	_delay_us(80);
	//check start condition 2
	//!(DHT_PIN & (1<<DHT_INPUTPIN))
	if(!DHT_TEST) {
		return 0;
	}
	_delay_us(80);

	//read the data
	uint16_t timeoutcounter = 0;
	for (j=0; j<5; j++) { //read 5 byte
		uint8_t result=0;
		for(i=0; i<8; i++) {//read every bit
			timeoutcounter = 0;
			//!(DHT_PIN & (1<<DHT_INPUTPIN))
			while(!DHT_TEST) { //wait for an high input (non blocking)
				timeoutcounter++;
				if(timeoutcounter > DHT_TIMEOUT) {
					return 0; //timeout
				}
			}
			_delay_us(30);
			//DHT_PIN & (1<<DHT_INPUTPIN)
			if(DHT_TEST) //if input is high after 30 us, get result
				result |= (1<<(7-i));
			timeoutcounter = 0;
			//DHT_PIN & (1<<DHT_INPUTPIN)
			while(DHT_TEST) { //wait until input get low (non blocking)
				timeoutcounter++;
				if(timeoutcounter > DHT_TIMEOUT) {
					return 0; //timeout
				}
			}
		}
		bits[j] = result;
	}

	//reset port
	//DHT_DDR |= (1<<DHT_INPUTPIN); //output
	DHT_OUTPUT;
	//DHT_PORT |= (1<<DHT_INPUTPIN); //high
	DHT_HIGH;
	_delay_ms(100);

	//check checksum
	if ((uint8_t)(bits[0] + bits[1] + bits[2] + bits[3]) == bits[4]) {
		//return temperature and humidity
		#if DHT_TYPE == DHT_DHT11
			*temperature = bits[2];
			*humidity = bits[0];
		#elif DHT_TYPE == DHT_DHT22
			*humidity = bits[0]<<8 | bits[1];
			*temperature = bits[2]<<8 | bits[3];
			if(*temperature & 0x8000) {
				*temperature = -(*temperature & 0x7FFF);
			}
		#endif
		return 1;
	}
	return 0;
}

/*
 * get temperature
 */
int8_t dht_gettemperature(int16_t *temperature) {
	int16_t humidity = 0;
	return dht_getdata(temperature, &humidity);
}

/*
 * get humidity
 */
int8_t dht_gethumidity(int16_t *humidity) {
	int16_t temperature = 0;
return dht_getdata(&temperature, humidity);
}

/*
 * get temperature and humidity
 */

int8_t dht_gettemperaturehumidity(int16_t *temperature, int16_t *humidity) {
	return dht_getdata(temperature, humidity);
}


/************************************************************************/
/* cache functions routines												*/
/************************************************************************/

int8_t dht_cache(uint32_t systime){
	if (((uint32_t)(systime-cache_time)) > DHT_CACHE_TIMEOUT){
		if (dht_gettemperaturehumidity(&cached_temperature, &cached_humidity)){
			cache_time = systime;
			return 1;	//�������� ���
		}
		return 0; // �� ������� �������� �������� � �������
	}
	return 1;	//����� ����� �� ����
}


int8_t dht_gettemperature_cached(int16_t *temperature, uint32_t systime){
	if (dht_cache(systime)){
		*temperature = cached_temperature;
		return 1;
	}
	return 0;
}
	
int8_t dht_gethumidity_cached(int16_t *humidity, uint32_t systime){
	if (dht_cache(systime)){
		*humidity = cached_humidity;
		return 1;
	}
	return 0;
}
	
int8_t dht_gettemperaturehumidity_cached(int16_t *temperature, int16_t *humidity, uint32_t systime){
	if (dht_cache(systime)){
		*temperature = cached_temperature;
		*humidity = cached_humidity;
		return 1;
	}
	return 0;
}
