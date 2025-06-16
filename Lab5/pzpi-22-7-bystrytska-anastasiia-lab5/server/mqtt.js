const mqtt = require('mqtt');
const Room = require('./models/Room');
const SensorData = require('./models/SensorData');

const mqttClient = mqtt.connect('mqtt://test.mosquitto.org');

mqttClient.on('connect', () => {
  console.log('Connected to MQTT broker');
  mqttClient.subscribe('sensors/#', (err) => {
    if (err) console.error('Subscription error:', err);
    else console.log('Subscribed to topics: sensors/#');
  });
});

function convertSensorValues(temp, hum) {
  if (typeof temp !== 'number' || typeof hum !== 'number') {
    console.warn('Invalid sensor values. Expected numbers, got:', temp, hum);
    return { temperature: 0, humidity: 0 };
  }
  const realisticTemp = Number((temp + (Math.random() * 2 - 1)).toFixed(1));
  const realisticHum = Number((hum + (Math.random() * 5 - 2.5)).toFixed(1));
  return { temperature: parseFloat(realisticTemp), humidity: parseFloat(realisticHum) };
}
mqttClient.on('message', async (topic, message) => {
  try {
    const topicParts = topic.split('/');
    if (topicParts[0] !== 'sensors' || topicParts.length < 2) {
      console.warn('Invalid topic format. Expected: sensors/roomId');
      return;
    }

    const roomId = topicParts[1];
    const msgStr = message.toString().trim();
    
    // Додаткова перевірка на пусте повідомлення
    if (!msgStr) {
      console.warn('Empty message received');
      return;
    }

    const [rawTemp, rawHum] = msgStr.split(',').map(Number);

    if (isNaN(rawTemp) || isNaN(rawHum)) {
      console.warn('Invalid sensor data:', msgStr);
      return;
    }

    const { temperature, humidity } = convertSensorValues(rawTemp, rawHum);

    await Room.findByIdAndUpdate(roomId, {
      temperature,
      humidity,
      updatedAt: new Date(),
    });

    await new SensorData({
      roomId,
      temperature,
      humidity,
    }).save();

    console.log(`Updated room ${roomId} → Temp: ${temperature}°C, Hum: ${humidity}%`);
  } catch (error) {
    console.error('MQTT processing error:', error);
  }
});

module.exports = {
  mqttClient,
  convertSensorValues,
};