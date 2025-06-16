const mongoose = require('mongoose');

const medicineSchema = new mongoose.Schema({
  name: { type: String, required: true },
  quantity: { type: Number, required: true },
  expirationDate: { type: Date, required: true },
  supplier: { type: String, required: true },
  price: { type: Number, required: true },
  storageRoomId: { type: mongoose.Schema.Types.ObjectId, ref: 'Room', required: true },
  temperatureRange: { 
    min: { type: Number, required: true },
    max: { type: Number, required: true }
  },
  humidityRange: {
    min: { type: Number, required: true },
    max: { type: Number, required: true }
  },
  storageInstructions: { type: String }
}, {
  timestamps: true
});

module.exports = mongoose.model('Medicine', medicineSchema);