const mongoose = require('mongoose');
const sensorDataSchema = new mongoose.Schema({
  roomId: { type: mongoose.Schema.Types.ObjectId, ref: 'Room', required: true },
  temperature: { type: Number, required: true },
  humidity: { type: Number, required: true },
  timestamp: { type: Date, default: Date.now, expires: '7d' },
});
module.exports = mongoose.model('SensorData', sensorDataSchema);