// models/sale.model.js
const mongoose = require('mongoose');

const saleSchema = new mongoose.Schema({
  medicine: { 
    type: mongoose.Schema.Types.ObjectId, 
    ref: 'Medicine',
    required: true 
  },
  quantity: { 
    type: Number, 
    required: true,
    min: 1
  },
  price: {
    type: Number,
    required: true
  },
  date: { 
    type: Date, 
    default: Date.now 
  }
});

module.exports = mongoose.model('Sale', saleSchema);