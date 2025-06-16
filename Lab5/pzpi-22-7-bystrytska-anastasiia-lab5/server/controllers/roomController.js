const Room = require('../models/Room');
const SensorData = require('../models/SensorData');
const { mqttClient } = require('../mqtt');

// Функція для конвертації значень сенсорів
const convertSensorValues = (temp, hum, isColdRoom = false) => {
  if (typeof temp !== 'number' || typeof hum !== 'number') {
    console.warn('Invalid input values for sensor conversion');
    return { temperature: 0, humidity: 0 };
  }

  if (isColdRoom) {
    const realisticTemp = Number((-25 + Math.random() * 20).toFixed(1));
    const realisticHum = Number((60 + Math.random() * 30).toFixed(1));
    return { temperature: realisticTemp, humidity: realisticHum };
  }
  const realisticTemp = Number((temp + (Math.random() * 2 - 1)).toFixed(1));
  const realisticHum = Number((hum + (Math.random() * 5 - 2.5)).toFixed(1));
  return { temperature: realisticTemp, humidity: realisticHum };
};

// Створення нової кімнати
const createRoom = async (req, res) => {
  try {
    const { name, description } = req.body;
    const isColdRoom = description && description.toLowerCase().includes('холодне приміщення');
    
    let initialTemp, initialHum;
    
    if (isColdRoom) {
      initialTemp = -15 + Math.random() * 20;
      initialHum = 70 + Math.random() * 20;
    } else {
      initialTemp = 20 + Math.random() * 10;
      initialHum = 40 + Math.random() * 30;
    }
    
    const { temperature, humidity } = convertSensorValues(initialTemp, initialHum, isColdRoom);

    const newRoom = new Room({ 
      name, 
      description,
      temperature,
      humidity,
      isColdRoom
    });

    await newRoom.save();

    try {
      await mqttClient.publish(
        `sensors/${newRoom._id}`,
        `${temperature.toFixed(1)},${humidity.toFixed(1)}`
      );
      console.log(`MQTT: Дані відправлено для кімнати ${newRoom._id}`);
    } catch (mqttError) {
      console.error('Помилка MQTT:', mqttError);
    }

    res.status(201).json({
      ...newRoom.toObject(),
      message: "Кімнату успішно створено з даними сенсорів"
    });

  } catch (error) {
    console.error('Помилка при створенні кімнати:', error);
    res.status(500).json({ 
      message: "Помилка сервера",
      error: error.message 
    });
  }
};

// Отримання списку кімнат
const getRooms = async (req, res) => {
  try {
    const rooms = await Room.find({});
    res.json({
      count: rooms.length,
      rooms
    });
  } catch (error) {
    console.error('Помилка при отриманні кімнат:', error);
    res.status(500).json({ 
      message: "Помилка сервера",
      error: error.message 
    });
  }
};

// Отримання однієї кімнати
const getRoomById = async (req, res) => {
  try {
    const room = await Room.findById(req.params.id);
    if (!room) {
      return res.status(404).json({ message: "Кімнату не знайдено" });
    }
    res.json(room);
  } catch (error) {
    console.error('Помилка при отриманні кімнати:', error);
    res.status(500).json({ 
      message: "Помилка сервера",
      error: error.message 
    });
  }
};

// Оновлення даних кімнати
const updateRoom = async (req, res) => {
  try {
    const { name, description } = req.body;
    const isColdRoom = description && description.toLowerCase().includes('холодне приміщення');

    const updatedRoom = await Room.findByIdAndUpdate(
      req.params.id,
      { 
        name,
        description,
        isColdRoom,
        updatedAt: new Date() 
      },
      { new: true }
    );

    if (!updatedRoom) {
      return res.status(404).json({ message: "Кімнату не знайдено" });
    }

    res.json({
      ...updatedRoom.toObject(),
      message: "Дані кімнати успішно оновлено"
    });
  } catch (error) {
    console.error('Помилка при оновленні кімнати:', error);
    res.status(500).json({ 
      message: "Помилка сервера",
      error: error.message 
    });
  }
};

// Видалення кімнати
const deleteRoom = async (req, res) => {
  try {
    const deletedRoom = await Room.findByIdAndDelete(req.params.id);
    
    if (!deletedRoom) {
      return res.status(404).json({ message: "Кімнату не знайдено" });
    }

    // Видаляємо всі пов'язані дані сенсорів
    await SensorData.deleteMany({ roomId: req.params.id });

    res.json({ 
      message: "Кімнату успішно видалено",
      deletedRoom 
    });
  } catch (error) {
    console.error('Помилка при видаленні кімнати:', error);
    res.status(500).json({ 
      message: "Помилка сервера",
      error: error.message 
    });
  }
};

// Оновлення даних сенсорів кімнати
const updateRoomData = async (roomId, temperature, humidity) => {
  try {
    const updatedRoom = await Room.findByIdAndUpdate(
      roomId,
      { 
        temperature,
        humidity,
        updatedAt: new Date() 
      },
      { new: true }
    );

    if (!updatedRoom) {
      console.warn(`Кімната з ID ${roomId} не знайдена`);
      return null;
    }

    return updatedRoom;
  } catch (error) {
    console.error('Помилка при оновленні даних кімнати:', error);
    throw error;
  }
};

// Симуляція змін середовища
const simulateEnvironmentChanges = async () => {
  try {
    const rooms = await Room.find({});
    if (!rooms || rooms.length === 0) {
      console.log('No rooms found for simulation');
      return;
    }

    const now = new Date();
    
    for (const room of rooms) {
      if (!room._id) continue;

      const isColdRoom = room.description && room.description.toLowerCase().includes('холодне приміщення');
      let newTemp, newHum;

      if (isColdRoom) {
        const hour = now.getHours();
        const baseTemp = -20 + Math.sin(hour / 24 * Math.PI * 2) * 10;
        const baseHum = 75 + Math.cos(hour / 12 * Math.PI) * 15;
        newTemp = Number((baseTemp + (Math.random() * 2 - 1)).toFixed(1));
        newHum = Number((baseHum + (Math.random() * 5 - 2.5)).toFixed(1));
      } else {
        const hour = now.getHours();
        const baseTemp = 20 + Math.sin(hour / 24 * Math.PI * 2) * 5;
        const baseHum = 50 + Math.cos(hour / 12 * Math.PI) * 20;
        newTemp = Number((baseTemp + (Math.random() * 2 - 1)).toFixed(1));
        newHum = Number((baseHum + (Math.random() * 5 - 2.5)).toFixed(1));
      }
      
      await Room.findByIdAndUpdate(room._id, {
        temperature: newTemp,
        humidity: newHum,
        updatedAt: new Date()
      });
      
      await new SensorData({
        roomId: room._id,
        temperature: newTemp,
        humidity: newHum
      }).save();
      
      mqttClient.publish(
        `sensors/${room._id}`,
        `${newTemp},${newHum}`
      );
    }
  } catch (error) {
    console.error('Simulation error:', error);
  }
};
module.exports = {
  createRoom,
  getRooms,
  getRoomById,
  updateRoom,
  deleteRoom,
  updateRoomData,
  simulateEnvironmentChanges,
  convertSensorValues
};