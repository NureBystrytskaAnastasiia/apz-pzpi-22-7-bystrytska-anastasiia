const SensorData = require('../models/SensorData');
const mongoose = require('mongoose');

exports.createSensorData = async (req, res) => {
  try {
    const { roomId, temperature, humidity } = req.body;
    const sensorData = new SensorData({ roomId, temperature, humidity });
    await sensorData.save();
    
    // Оновлюємо останні дані в кімнаті
    await Room.findByIdAndUpdate(roomId, { 
      temperature, 
      humidity,
      lastSensorUpdate: new Date()
    });
    
    res.status(201).json(sensorData);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

exports.getSensorDataByRoom = async (req, res) => {
  try {
    const { roomId } = req.params;
    const data = await SensorData.find({ roomId })
      .sort({ timestamp: -1 })
      .limit(100); // Обмежуємо кількість записів
    res.json(data);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

exports.getAllRoomsArchive = async (req, res) => {
  try {
    // Отримуємо дані за останні 7 днів, згруповані по кімнатам та дням
    const sevenDaysAgo = new Date();
    sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 7);

    const archiveData = await SensorData.aggregate([
      { 
        $match: { 
          timestamp: { $gte: sevenDaysAgo } 
        } 
      },
      {
        $group: {
          _id: {
            roomId: "$roomId",
            day: { $dayOfMonth: "$timestamp" },
            month: { $month: "$timestamp" },
            year: { $year: "$timestamp" }
          },
          avgTemp: { $avg: "$temperature" },
          avgHumidity: { $avg: "$humidity" },
          minTemp: { $min: "$temperature" },
          maxTemp: { $max: "$temperature" },
          count: { $sum: 1 }
        }
      },
      {
        $lookup: {
          from: "rooms",
          localField: "_id.roomId",
          foreignField: "_id",
          as: "room"
        }
      },
      {
        $unwind: "$room"
      },
      {
        $sort: { 
          "_id.year": -1, 
          "_id.month": -1, 
          "_id.day": -1 
        }
      },
      {
        $group: {
          _id: "$_id.roomId",
          roomName: { $first: "$room.name" },
          days: {
            $push: {
              date: {
                day: "$_id.day",
                month: "$_id.month",
                year: "$_id.year"
              },
              avgTemp: "$avgTemp",
              avgHumidity: "$avgHumidity",
              minTemp: "$minTemp",
              maxTemp: "$maxTemp",
              count: "$count"
            }
          }
        }
      }
    ]);

    res.json(archiveData);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};