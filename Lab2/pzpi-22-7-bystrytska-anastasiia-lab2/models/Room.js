const mongoose = require('mongoose');
const roomSchema = new mongoose.Schema({
  name: { type: String, required: true },
  description: { type: String, default: '' },
  temperature: { type: Number, default: null },
  humidity: { type: Number, default: null },
  updatedAt: { type: Date, default: Date.now },
});
module.exports = mongoose.model('Room', roomSchema);