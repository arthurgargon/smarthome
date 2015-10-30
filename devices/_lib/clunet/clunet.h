/* Name: clunet.h
 * Project: CLUNET network driver
 * Author: Alexey Avdyukhin
 * Creation Date: 2013-09-09
 * License: DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 */
 
#ifndef __clunet_h_included__
#define __clunet_h_included__

#include "utils/bits.h"
#include "clunet_config.h"

#define CLUNET_SENDING_STATE_IDLE 0
#define CLUNET_SENDING_STATE_INIT 1
#define CLUNET_SENDING_STATE_PRIO1 2
#define CLUNET_SENDING_STATE_PRIO2 3
#define CLUNET_SENDING_STATE_DATA 4
#define CLUNET_SENDING_STATE_WAITING_LINE 6
#define CLUNET_SENDING_STATE_PREINIT 7
#define CLUNET_SENDING_STATE_STOP 8
#define CLUNET_SENDING_STATE_DONE 9

#define CLUNET_READING_STATE_IDLE 0
#define CLUNET_READING_STATE_INIT 1
#define CLUNET_READING_STATE_PRIO1 2
#define CLUNET_READING_STATE_PRIO2 3
#define CLUNET_READING_STATE_HEADER 4
#define CLUNET_READING_STATE_DATA 5

#define CLUNET_OFFSET_SRC_ADDRESS 0
#define CLUNET_OFFSET_DST_ADDRESS 1
#define CLUNET_OFFSET_COMMAND 2
#define CLUNET_OFFSET_SIZE 3
#define CLUNET_OFFSET_DATA 4
#define CLUNET_BROADCAST_ADDRESS 0xFF


/************COMMANDS****************/
#define CLUNET_COMMAND_DISCOVERY 0x00
/* Поиск других устройств, параметров нет */

#define CLUNET_COMMAND_DISCOVERY_RESPONSE 0x01
/* Ответ устройств на поиск, в качестве параметра - название устройства (текст) */

#define CLUNET_COMMAND_BOOT_CONTROL 0x02
/* Работа с загрузчиком. Данные - субкоманда.
<-0 - загрузчик запущен
->1 - перейти в режим обновления прошивки
<-2 - подтверждение перехода, плюс два байта - размер страницы
->3 запись прошивки, 4 байта - адрес, всё остальное - данные (равные размеру страницы)
<-4 блок прошивки записан
->5 выход из режима прошивки */

#define CLUNET_COMMAND_REBOOT 0x03
/* Перезагружает устройство в загрузчик. */

#define CLUNET_COMMAND_BOOT_COMPLETED 0x04
/* Посылается устройством после инициализации библиотеки, сообщает об успешной загрузке устройства. Параметр - содержимое MCU регистра, говорящее о причине перезагрузки. */

#define CLUNET_COMMAND_PING 0xFE
/* Пинг, на эту команду устройство должно ответить следующей командой, возвратив весь буфер */

#define CLUNET_COMMAND_PING_REPLY 0xFF
/* Ответ на пинг, в данных то, что было прислано в предыдущей команде */


#define CLUNET_COMMAND_CHANNEL 0x10
/* Команда переключения канала:
	1-ый байт: режим
		0x00 - по значению
		0x01 - следующий
		0x02 - предыдущий
		0xFF - запрос текущего значения
	2-ой байт: значение канала (только для режима 0)
*/

#define CLUNET_COMMAND_CHANNEL_INFO 0x11
/* Сообщение о номере текущего канала:
	1-ый байт: текущий номер канала
*/

#define CLUNET_COMMAND_VOLUME 0x15
/* Команда управления громкостью устройства: 
		1-ый байт: режим
			0x00 - проценты
			0x01 - Дб
			0x02 - прибавить
			0x03 - убавить
			0xFF - запрос текущего значения
		2-ой байт: значение уровня громкости(только для режимов 0 и 1)
*/

#define CLUNET_COMMAND_VOLUME_INFO 0x16
/* Сообщение о состоянии громкости устройства:
		1-ый байт: значение в процентах
		2-ой байт: значение в Дб
*/

#define CLUNET_COMMAND_MUTE 0x17
/* Команда отключения звука устройства:
		1-ый байт: режим
			0 - отключение звука
			1 - переключение (повторный вызов вернет к предыдущему уровню громкости)
*/

#define CLUNET_COMMAND_EQUALIZER 0x18
/* Команда управления эквалайзером:
		1-ый байт: режим
			0x00 - сбросить (отключить) эквалайзер
			0x01 - усиление (gain)
			0x02 - высокие частоты (treble)
			0x03 - низкие частоты (bass)
			0xFF - запрос текущего значения
		2-ой байт: только для режимов 1,2,3 - задает тип изменения величины
			0x00 - сбросить (0 dB)
			0x01 - Дб
			0x02 - прибавить
			0x03 - убавить
		3-ий байт: только для режимов 1,2,3 - задает значение величины
*/

#define CLUNET_COMMAND_EQUALIZER_INFO 0x19
/* Сообщение о состоянии уровней эквалайзера:
		1-ый байт: значение усиления (gain), в дБ
		2-ой байт: значение высокие частот (treble), в дБ
		3-ий байт: значение низких частот (bass), в дБ
*/



#define CLUNET_COMMAND_SWITCH 0x20
/* Команда управления выключателями/реле:
		1-ый байт: режим
			0x00 - отключение
			0x01 - включение
			0x02 - переключение
			0x03 - одновременное управление всеми выключателями этого устройства
			0xFF - запрос состояния выключателей устройства
		2-ой байт: 
			для режимов 0x00-0x02: дополнительный идентификатор устройства
			для режима 0x03: битовая маска состояний выключателей
			
*/

#define CLUNET_COMMAND_SWITCH_INFO 0x21
/* Сообщение о состоянии всех выключателей устройства в виде битовой маски:
			значение бита = 0 - выключатель отключен
			значение бита = 1 - выключатель включен
*/

#define CLUNET_COMMAND_BUTTON 0x22
/* Команда запроса состояния нефиксируемых кнопок:
*/

#define CLUNET_COMMAND_BUTTON_INFO 0x23
/* Сообщает о состоянии нефиксируемой кнопки:
	1-ый байт: 
		идентифкатор устройства
	2-ой байт:
		0x00 - кнопка не нажата
		0x01 - кнопка нажата
*/

#define CLUNET_COMMAND_TEMPERATURE 0x25
/* Команда запроса текущей температуры:
		1-ый байт: режим
			0x00 - все устройства
			0x01 - устройства с типом, описанном во втором байте
			0x02 - то же, что и 0x01, но в байтах 3-10 задается серийный номер запрашиваемого устройства
		2-ой байт:
			для режимов 1-2 - тип устройств
				0 - 1-wire
				1 - dht
		3-10 байт:
			только для режима 2 - задается серийный номер опрашиваемого устройства
		
 */

#define CLUNET_COMMAND_TEMPERATURE_INFO 0x26
/* Сообщение о температуре:
		1 байт - кол-во N ответивших устройств
		
		N раз:
		2 байт - тип устройства
			0 - 1-wire
			1 - DHT
		3-10 байты - серийный номер устройства
		11-12 байты - значение температуры умноженное на 10
*/

#define CLUNET_COMMAND_HUMIDITY 0x27
/* Команда запроса текущей влажности:	
 */

#define CLUNET_COMMAND_HUMIDITY_INFO 0x28
/* Сообщает об уровне влажности
	1-2 байты: значение влажности, умноженное на 10 (или 0xFFFF если ошибка)
 */

#define CLUNET_COMMAND_ONEWIRE_SEARCH 0x30
/* Команда поиска 1-wire устройств, данные пустые. */

#define CLUNET_COMMAND_ONEWIRE_INFO 0x31
/* Сообщает о найденном 1-wire устройстве:
		1 байт - кол-во N найденных устройств		
		N раз:
		2-9 байт - серийный номер уcтройства (в 1-ом байте хранится family ID)
*/

#define CLUNET_COMMAND_MOTION 0x40
/* Команда запроса наличия движения
*/

#define CLUNET_COMMAND_MOTION_INFO 0x41
/* Сообщает о наличии движения в помещении:
	1 байт: 0 - движения нет; 1 - движение есть
*/

#define CLUNET_COMMAND_LIGHT_LEVEL 0x45
/* Команда запроса уровня освещенности:		
*/

#define CLUNET_COMMAND_LIGHT_LEVEL_INFO 0x46
/* Сообщает об уровне  освещенности:
	1 байт: 0 - освещение отсутствует; 1 - освещение есть
	2 байт: процент освещения
*/

#define CLUNET_COMMAND_FAN 0x50
/* Команда управления вентилятором:
	1 байт: 0 - ручное управление; 0xFF - запрос состояния
*/

#define CLUNET_COMMAND_FAN_INFO 0x51
/* Сообщает о текущем состоянии вентилятора
*/

#define CLUNET_COMMAND_DOOR 0x55
/*Команда запроса состояния дверей:
*/

#define CLUNET_COMMAND_DOOR_INFO 0x56
/* Сообщает о состоянии дверей:
	0 - дверь закрыта;
	>0 - дверь открыта;
*/

#define CLUNET_COMMAND_HEATFLOOR 0x60
/* Команда управления теплым полом:
*/

#define CLUNET_COMMAND_HEATFLOOR_INFO 0x61
/* Сообщает о состоянии теплого пола
*/

#define CLUNET_COMMAND_DEVICE_STATE 0x70
/* Команда управления состоянием произвольного устройства:
	1-ый байт: идентификатор устройства
	2-ой байт: команда для выполнения
		0xFF - запрос стостояния
*/

#define CLUNET_COMMAND_DEVICE_STATE_INFO 0x71
/* Сообщает о состоянии произвольного устройства
	1-ый байт: идентификатор устройства
	2-ой байт: состояние устройства
		0x00 - устройство выключено
		0x01 - устройство включено
*/

#define CLUNET_COMMAND_LOCK 0xA0
/* Команда блокирования (планшета)
*/

#define CLUNET_COMMAND_UNLOCK 0xA1
/* Команда разблокирования (планшета)
*/


#define CLUNET_COMMAND_SET_TIMER 0x09
/* Установка таймера. Параметр - кол-во секунд (два байта) */

#define CLUNET_COMMAND_RC_BUTTON_PRESSED 0x0A
/* Нажата кнопка на пульте. Первый байт - тип кода, далее - номер кнопки.
На данный момент 01 - самый популярный стандарт (Sony вроде?) длина кода кнопки - 4 байта.
02 - удержание кнопки его же. */

#define CLUNET_COMMAND_RC_BUTTON_PRESSED_RAW 0x0B
/* Недекодированные данные о нажатой кнопке на пульте для нестандартных пультов. Идут пачками по 4 байта. 2 из которых - длительность сигнала и 2 - длительность его отсутствия в 0.032 долях миллисекунды. (1/8000000*256 сек) */

#define CLUNET_COMMAND_RC_BUTTON_SEND 0x0C
/* Заэмулировать кнопку пульта. Формат данных аналогичен CLUNET_COMMAND_RC_BUTTON_PRESSED, плюс в конце опциональный байт длительности удержания кнопки (кол-во дополнительных сигналов), для Sony это ~30мс */

#define CLUNET_COMMAND_RC_BUTTON_SEND_RAW 0x0D
/* Заэмулировать кнопку пульта на основе сырых данных. Формат данных аналогичен CLUNET_COMMAND_RC_BUTTON_PRESSED_RAW. */

#define CLUNET_COMMAND_TIME 0x12
/* Сообщает время. Шесть байт - часы, минуты, секунды, год (от 1900), месяц (от 0), день (от 1) */

#define CLUNET_COMMAND_VOLTAGE 0x14
/* Сообщает напряжение на батарейке, первый байт - ID устройства, далее два байта - вольтаж, где 0x3FF = 5.12 */

#define CLUNET_COMMAND_INTERCOM_RING 0x16
/* Звонок в домофон */

#define CLUNET_COMMAND_INTERCOM_MESSAGE 0x17
/* Новое сообщение на автоответчике домофона, в данных 4 байта - номер сообщения */

#define CLUNET_COMMAND_INTERCOM_MODE_REQUEST 0x18
/* Запрашивает режим работы домофона */

#define CLUNET_COMMAND_INTERCOM_MODE_INFO 0x19
/* Сообщает режим работы домофона, первый байт - постоянный режим, второй - временный */

#define CLUNET_COMMAND_INTERCOM_MODE_SET 0x1A
/* Задаёт режим работы домофона, первый байт - постоянный режим (или 0xFF, чтобы не трогать), второй - временный (опционально) */

#define CLUNET_COMMAND_INTERCOM_RECORD_REQUEST 0x1B
/* Запрашивает запись у домофона, подтверждает доставку или завершает передачу
 Если 4 байта, то это номер запрашиваемой записи
 Если 1 байт, то 1 в случае подтверждения получения пакета, 0 - завершение передачи */
 
#define CLUNET_COMMAND_INTERCOM_RECORD_DATA 0x1C
/* Передаёт кусок записи с автоответчика. Первые 4 байта - номер записи, далее 4 байта - смещение от начала файла, всё далее - данные из файла */

#define CLUNET_COMMAND_WINDOW_COMMAND 0x21
/* Открывает или закрывает окно. Один байт данных. 1 - открыть, 2 - закрыть */

#define CLUNET_COMMAND_WINDOW_INFO 0x22
/* Сообщает об открытии или закрытии окна. 1 - открыто, 2 - закрыто */


#define CLUNET_PRIORITY_NOTICE 1
/* Приоритет пакета 1 - неважное уведомление, которое вообще может быть потеряно без последствий */

#define CLUNET_PRIORITY_INFO 2
/* Приоритет пакета 2 - какая-то информация, не очень важная */

#define CLUNET_PRIORITY_MESSAGE 3
/* Приоритет пакета 3 - сообщение с какой-то важной информацией */

#define CLUNET_PRIORITY_COMMAND 4
/* Приоритет пакета 4 - команда, на которую нужно сразу отреагировать */

#ifndef CLUNET_T
#define CLUNET_T ((F_CPU / CLUNET_TIMER_PRESCALER) / 31250)
#endif
#if CLUNET_T < 8
#  error Timer frequency is too small, increase CPU frequency or decrease timer prescaler
#endif
#if CLUNET_T > 24
#  error Timer frequency is too big, decrease CPU frequency or increase timer prescaler
#endif

#define CLUNET_0_T (CLUNET_T)
#define CLUNET_1_T (3*CLUNET_T)
#define CLUNET_INIT_T (10*CLUNET_T)

#define CLUNET_CONCAT(a, b)            a ## b
#define CLUNET_OUTPORT(name)           CLUNET_CONCAT(PORT, name)
#define CLUNET_INPORT(name)            CLUNET_CONCAT(PIN, name)
#define CLUNET_DDRPORT(name)           CLUNET_CONCAT(DDR, name)

#ifndef CLUNET_WRITE_TRANSISTOR
#  define CLUNET_SEND_1 CLUNET_DDRPORT(CLUNET_WRITE_PORT) |= (1 << CLUNET_WRITE_PIN)
#  define CLUNET_SEND_0 CLUNET_DDRPORT(CLUNET_WRITE_PORT) &= ~(1 << CLUNET_WRITE_PIN)
#  define CLUNET_SENDING (CLUNET_DDRPORT(CLUNET_WRITE_PORT) & (1 << CLUNET_WRITE_PIN))
#  define CLUNET_SEND_INVERT CLUNET_DDRPORT(CLUNET_WRITE_PORT) ^= (1 << CLUNET_WRITE_PIN)
#  define CLUNET_SEND_INIT { CLUNET_OUTPORT(CLUNET_WRITE_PORT) &= ~(1 << CLUNET_WRITE_PIN); CLUNET_SEND_0; }
#else
#  define CLUNET_SEND_1 CLUNET_OUTPORT(CLUNET_WRITE_PORT) |= (1 << CLUNET_WRITE_PIN)
#  define CLUNET_SEND_0 CLUNET_OUTPORT(CLUNET_WRITE_PORT) &= ~(1 << CLUNET_WRITE_PIN)
#  define CLUNET_SENDING (CLUNET_OUTPORT(CLUNET_WRITE_PORT) & (1 << CLUNET_WRITE_PIN))
#  define CLUNET_SEND_INVERT CLUNET_OUTPORT(CLUNET_WRITE_PORT) ^= (1 << CLUNET_WRITE_PIN)
#  define CLUNET_SEND_INIT { CLUNET_DDRPORT(CLUNET_WRITE_PORT) |= (1 << CLUNET_WRITE_PIN); CLUNET_SEND_0; }
#endif

#define CLUNET_READ_INIT { CLUNET_DDRPORT(CLUNET_READ_PORT) &= ~(1 << CLUNET_READ_PIN); CLUNET_OUTPORT(CLUNET_READ_PORT) |= (1 << CLUNET_READ_PIN); }
#define CLUNET_READING (!(CLUNET_INPORT(CLUNET_READ_PORT) & (1 << CLUNET_READ_PIN)))

#ifndef CLUNET_SEND_BUFFER_SIZE
#  error CLUNET_SEND_BUFFER_SIZE is not defined
#endif
#ifndef CLUNET_READ_BUFFER_SIZE
#  error CLUNET_READ_BUFFER_SIZE is not defined
#endif
#if CLUNET_SEND_BUFFER_SIZE > 255
#  error CLUNET_SEND_BUFFER_SIZE must be <= 255
#endif
#if CLUNET_READ_BUFFER_SIZE > 255
#  error CLUNET_READ_BUFFER_SIZE must be <= 255
#endif

// Инициализация
void clunet_init();

// Отправка пакета
void clunet_send(unsigned char address, unsigned char prio, unsigned char command, char* data, unsigned char size);

// Возвращает 0, если готов к передаче, иначе приоритет текущей задачи
int clunet_ready_to_send();

// Установка функций, которые вызываются при получении пакетов
// Эта - получает пакеты, которые адресованы нам
void clunet_set_on_data_received(void (*f)(unsigned char src_address, unsigned char dst_address, unsigned char command, char* data, unsigned char size));

// А эта - абсолютно все, которые ходят по сети, включая наши
void clunet_set_on_data_received_sniff(void (*f)(unsigned char src_address, unsigned char dst_address, unsigned char command, char* data, unsigned char size));

char check_crc(char* data, unsigned char size);

#endif
